# Design of the app

> Written by AI (Claude), from the design agreed with the user on Thu 19 Nov 2026.

The app records the readings of the sensors and, later, shows them as graphs. This file covers recording; the graphs
get designed once readings are stored.

## The flow

```
MQTT <base-topic>/+
        │
        ▼
listener (Spring Integration MQTT thread)
  sensor name = the topic part after <base-topic>/
  save measurement in its own short transaction (commits)
  call processing.process(measurementId)
  return
        │
        ▼
processing service (@Async, another thread)
  load the one measurement
  for each postprocessor, in its own try/catch and transaction:
    ├── temperature: room lookup, save temperature row
    └── humidity:    room lookup, save humidity row
```

The listener only saves the raw reading, so its transaction is tiny. All the processing happens later on
another thread.

## Decisions

- **Orchestration, not events.** The listener calls the processing service directly. Both sides live in one app, so
  an event would only hide what happens next.
- **One subscription, one listener class.** The app subscribes to `<base-topic>/+`. The base topic comes from config
  and mirrors Zigbee2MQTT's `mqtt.base_topic`. Zigbee2MQTT's own `bridge/...` topics are two levels deep, so `+` skips
  them. Every topic is a sensor and is handled the same way, so a new sensor needs only a row in the DB, not code.
- **Friendly names without `/`.** A slash makes a deeper topic, and `+` does not match it.
- **Raw data kept.** The `measurement` table stores the whole payload, so new kinds of data can be processed later
  without changing the listener.
- **Postprocessors are independent.** Temperature and humidity each write their own table. A failure in one is logged
  and does not stop the other.
- **Room lookup in a location service**, over a sensor-to-room table in the DB. Room names stay out of the public
  repository. A reading from a sensor with no room is logged as an error and skipped; its raw row stays.
- **`stamp` = `created`.** The SNZB-02D payload has no time, and it arrives well under a second after the
  measurement.
- **Async on Boot's `applicationTaskExecutor`** (8 threads by default). `@Async` needs `@EnableAsync` and a call from
  another bean, because it works through the Spring proxy.
- **MQTT client: Spring Integration MQTT** (an inbound channel adapter that wraps Paho).
- **Database: SQLite**, one file on the device, with WAL mode and a `busy_timeout`, so that a second writer waits
  instead of failing. Boot has no SQLite support out of the box: JPA needs `hibernate-community-dialects`, while
  `JdbcClient` needs nothing extra.
- **Persistence: Spring Data JPA** with the SQLite dialect from `hibernate-community-dialects` (decided on Thu 19 Nov
  2026). The user prefers repositories and native queries to `JdbcClient`; Spring Data JDBC was dropped because it has
  no SQLite dialect. IDs are `INTEGER PRIMARY KEY` columns with `GenerationType.IDENTITY`.
- **Schema: Liquibase**, run by Boot at startup (`spring-boot-starter-liquibase`), changelogs in
  `src/main/resources/db/changelog/`.

## Tables

| Table | Columns |
|-------|---------|
| `measurement` | id, topic, sensor, payload (TEXT, JSON), created |
| `temperature` | id, room, value, stamp, measurement_id |
| `humidity` | id, room, value, stamp, measurement_id |
| sensor-to-room | sensor, room |

## Accepted for now

- **The in-memory handoff to the async thread is lost on a crash.** A measurement saved just before the app dies is
  not processed. If that is ever needed, a catch-up job can find raw rows with no processed row (a left join on
  `measurement_id`) and process them. It would run rarely, e.g. at startup, not per reading.
