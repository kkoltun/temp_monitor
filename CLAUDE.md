# What this repository is

**Home temperature monitoring**: Zigbee sensors (SNZB-02D) → ZBDongle-P → Zigbee2MQTT → MQTT → an app in Kotlin with Spring Boot that stores the readings and shows them as graphs on the local network. The aims are in `project.md`, the hardware in `hardware.md`.

It is a fun project and a way to write Kotlin and Spring for real. Decisions about the project (a design choice, a hardware fact, how to run it) go into this repository's docs, e.g. `hardware.md`.

## Conventions

- **A working product first**, one slice at a time; the first slice is one sensor end to end, readings stored and shown. No Kafka.
- Develop on the notebook; later deploy to a Raspberry Pi.
- Dates are written out in full (2026-11-18 or Wed 18 Nov).
- **No DDD or hexagonal layering**, so the project stays small and gets finished: the structure is only as much as the current slice needs.
- **Idiomatic Kotlin**, not Java in Kotlin syntax: learning Kotlin is the point.
- **Local network only**: no ports opened to the internet, no cloud, until the AWS idea in `project.md` is picked up.
- **No secrets in the repository**: credentials live in ignored files (e.g. `docker/mqtt5/create_user.sh`, from its `.template`) or environment variables.
