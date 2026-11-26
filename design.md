# Design of the app

> Written by AI (Claude), from the design agreed with the user on Thu 19 Nov 2026.

The app records the readings of the sensors, and Grafana shows them as graphs. This file covers recording and the
choice of the graphs.

## The flow

```
MQTT <base-topic>/+
        │
        ▼
listener (Spring Integration MQTT thread)
  device name = the topic part after <base-topic>/
  save mqtt_message in its own short transaction (commits)
  call processing.process(mqttMessageId)
  return
        │
        ▼
processing service (@Async, another thread)
  load the one mqtt_message
  device name → mapper for that kind of device (SNZB-02D)
  payload → List<Reading>: temperature, humidity, battery, link quality
  location lookup in device_location
  save one reading row per Reading
```

The listener only saves the raw reading, so its transaction is tiny. All the processing happens later on
another thread.

## Decisions

- **Orchestration, not events.** The listener calls the processing service directly. Both sides live in one app, so
  an event would only hide what happens next.
- **One subscription, one listener class.** The app subscribes to `<base-topic>/+`. The base topic comes from config
  and mirrors Zigbee2MQTT's `mqtt.base_topic`. Zigbee2MQTT's own `bridge/...` topics are two levels deep, so `+` skips
  them. Every topic is a device. A new sensor of a known kind needs a location row and its name matched in code; a new
  kind of device needs a new mapper.
- **Friendly names without `/`.** A slash makes a deeper topic, and `+` does not match it.
- **Raw data kept.** The `mqtt_message` table stores the whole message (topic, payload, headers), so new kinds of
  data can be processed later without changing the listener.
- **`MqttMessage`, not `Measurement`** (decided on Sun 22 Nov 2026). The raw row is what the broker delivered, and
  not every message is a measurement (a button press, a relay switching). The measurements are the processed
  `reading` rows. Plain `Message` would clash with Spring's `org.springframework.messaging.Message`.
- **One message, several readings** (decided on Sun 22 Nov 2026, replacing per-type postprocessors). One SNZB-02D
  payload holds temperature, humidity, battery and link quality, so one mapper per kind of device parses it once into
  a list of `Reading`s. `Reading` is a sealed interface, so a `when` over it must handle every kind. The kind of
  device comes from its name, matched in code. Zigbee2MQTT sends the whole cached state each time, so battery and link
  quality are in every message, but may be stale.
- **One `reading` table with a `type` column**, not one table per kind: every reading is a value with a unit, a
  location and a time.
- **Location lookup** in the `device_location` table. Locations stay out of the public repository: the rows are added
  by hand, never in a changelog. A message from a device with no location fails processing; its raw row stays.
- **`stamp` = `created`.** The SNZB-02D payload has no time, and it arrives well under a second after the
  measurement.
- **Async on Boot's `applicationTaskExecutor`** (8 threads by default). `@Async` needs `@EnableAsync` and a call from
  another bean, because it works through the Spring proxy.
- **MQTT client: Spring Integration MQTT** (an inbound channel adapter that wraps Paho).
- **Database: PostgreSQL in Docker** (decided on Mon 23 Nov 2026, replacing SQLite). Grafana reads Postgres with
  its built-in data source, while SQLite needs a community plugin and a shared file mount. The readings already in
  SQLite (about one day) were not copied over (decided on Mon 23 Nov 2026).
- **Persistence: Spring Data JPA** (decided on Thu 19 Nov 2026). The user prefers repositories and native queries to
  `JdbcClient`. Before the move to Postgres it used the SQLite dialect from `hibernate-community-dialects`, because
  Spring Data JDBC has no SQLite dialect. IDs use `GenerationType.IDENTITY`.
- **Graphs: Grafana in Docker** (decided on Mon 23 Nov 2026), not an own frontend. A working product first: the
  graphs are configuration, not code, so the app stays the recording side. An own page can still come later on top of
  the same tables. Grafana logs in to Postgres as a read-only role.
- **Schema: Liquibase**, run by Boot at startup (`spring-boot-starter-liquibase`), changelogs in
  `src/main/resources/db/changelog/`.

## The outside temperature (planned on Thu 26 Nov 2026 by the user)

```
@Scheduled poll, every 15 minutes
        │
        ▼
IMGW-PIB synop API, one station (the station is kept out of the repository)
        │
        ▼
save the raw response into synop_service_response
        │
        ▼
ProcessingService, a new method taking that row's id
  a new mapper: response → List<Reading>
  save the readings, reading.source = EXTERNAL_TEMP_SERVICE
```

- **The raw response kept** in its own table `synop_service_response`, like `mqtt_message` for the sensors.
  The `synop` endpoint returns only the latest hour, so an old response cannot be fetched again.
- **A `source` column on `reading`** (`MQTT_MESSAGE`, `EXTERNAL_TEMP_SERVICE`) says which table `source_message_id` points
  at. The database cannot enforce that with a foreign key.
- **Every 15 minutes.** IMGW publishes one value per station per hour, some time after the full hour. A failed call
  is retried by the next run.
- **`stamp` from the response**: `data_pomiaru` and `godzina_pomiaru`, the hour in UTC. The values in the response
  are strings, numbers included.
- **The station is a secret.** It reveals roughly where the home is, so it goes into the ignored properties.

## Tables

| Table | Columns |
|-------|---------|
| `mqtt_message` | id, topic, device, payload (TEXT, JSON), headers (TEXT), created |
| `reading` | id, source_message_id (the row it came from), source (`MQTT_MESSAGE`, `EXTERNAL_TEMP_SERVICE`), value, unit, location, stamp, type |
| `synop_service_response` | planned: the raw IMGW response and its stamp |
| `device_location` | id, device (unique), location |

## Accepted for now

- **The in-memory handoff to the async thread is lost on a crash.** A message saved just before the app dies is
  not processed. If that is ever needed, a catch-up job can find raw rows with no processed row (a left join on
  `reading.source_message_id`) and process them. It would run rarely, e.g. at startup, not per reading.
- **The processing package knows every source** (noted by the user on Thu 26 Nov 2026). It holds the entities and
  mappers of both MQTT messages and synop responses, and someone adding another weather service would not know what to
  implement where. To refactor later toward clear plugging places for a new source.
- **Duplicate outside readings** (accepted by the user on Thu 26 Nov 2026). Every poll saves the response and its
  readings, but IMGW publishes once an hour, so each hour is stored about four times with the same values. Graphs and
  averages are unaffected. A fix on a lower priority: skip a response whose stamp is already processed, in the
  processing service. A longer interval alone cannot give exactly one row per hour.
