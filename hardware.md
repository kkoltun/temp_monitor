**Everything here is created by AI.**

# Hardware – starter kit

Goal: get one sensor working end to end before buying the rest.

## Shopping list

| # | Item | Notes | Approx. cost |
|---|------|-------|--------------|
| 1 | **Sonoff SNZB-02D** (or **SNZB-02DR2**) ✅ ordered | Zigbee temperature/humidity sensor with LCD. Runs on a **CR2450** coin cell. | €10–15 |
| 2 | **Sonoff Zigbee 3.0 USB Dongle Plus (ZBDongle-P)** ✅ ordered | Zigbee coordinator. Listing should mention **TI CC2652P + CP2102N**, aluminium case, SMA antenna. | ~€25 |
| 3 | ~~USB extension cable, ~1 m~~ ✅ already owned | Keeps the dongle away from USB 3 ports and the host to avoid interference. | €0 |
| 4 | Spare **CR2450** battery ✅ ordered | Less common than CR2032, so worth having one in stock. | €2–3 |
| 5 | Host machine (if not owned) | Raspberry Pi 4/5 with a good SD card/SSD, or any always-on Linux machine. | €0–80 |

Total without the host: about **€40–50**.

## Host – Raspberry Pi (later; develop on the notebook first)

| # | Item | Notes | Approx. cost |
|---|------|-------|--------------|
| 1 | **Raspberry Pi 5, 4 GB** | 4 GB is enough for Zigbee2MQTT + MQTT + app + DB. | check current price |
| 2 | **Official Raspberry Pi 27 W USB-C power supply** | Cheap supplies cause random crashes on the Pi 5. | |
| 3 | Case with fan (or the official Active Cooler) | Keeps it cool when running 24/7. | |
| 4 | **64 GB "High Endurance" microSD** | Handles constant writes better than regular cards. | |
| 5 | (optional) USB SSD | More durable than microSD. Use an old one if you have it. | |

Total roughly **450–550 zł** (more with an SSD).

Approach: develop and build on the notebook, deploy to the Pi (ARM64 Docker images via `docker buildx`).
When moving the dongle, copy the Zigbee2MQTT data folder too so paired sensors stay paired.

## Avoid when buying

- **Sonoff Dongle-PZG23**: a Z-Wave stick, not Zigbee. It won't work with the SNZB-02D.
- Anything labelled **"Z-Wave"** or **"800 series"**.
- **SNZB-02P / SNZB-02**: no screen.
- **SNZB-02LD**: waterproof probe, meant for fridges and pools.
- Sensors that only work through a vendor cloud app.

Before ordering, check the exact model on https://www.zigbee2mqtt.io/supported-devices/

## Later (after the first sensor works)

- 3× more SNZB-02D for the remaining rooms.
- A Zigbee smart plug as a repeater, if a room is out of range.
- Maybe an SNZB-02WD (outdoor, IP65) for an outside temperature reading.
- For boiler control: a Zigbee relay and/or Zigbee radiator valves (TRVs). Check the boiler wiring first.
