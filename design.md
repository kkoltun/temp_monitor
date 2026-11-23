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

## Tables

| Table | Columns |
|-------|---------|
| `mqtt_message` | id, topic, device, payload (TEXT, JSON), headers (TEXT), created |
| `reading` | id, source_message_id (the `mqtt_message` it came from), value, unit, location, stamp, type |
| `device_location` | id, device (unique), location |

## Accepted for now

- **The in-memory handoff to the async thread is lost on a crash.** A message saved just before the app dies is
  not processed. If that is ever needed, a catch-up job can find raw rows with no processed row (a left join on
  `reading.source_message_id`) and process them. It would run rarely, e.g. at startup, not per reading.
