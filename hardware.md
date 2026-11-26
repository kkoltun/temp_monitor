**Everything here is created by AI.**

# Hardware – starter kit

Goal: get one sensor working end to end before buying the rest.

## Shopping list

| # | Item | Notes | Approx. cost |
|---|------|-------|--------------|
| 1 | **Sonoff SNZB-02D** (or **SNZB-02DR2**) ✅ arrived 2026-11-17, paired | Zigbee temperature/humidity sensor with LCD. Runs on a **CR2450** coin cell. | €10–15 |
| 2 | **Sonoff Zigbee 3.0 USB Dongle Plus (ZBDongle-P)** ✅ arrived 2026-11-17 | Zigbee coordinator. Listing should mention **TI CC2652P + CP2102N**, aluminium case, SMA antenna. | ~€25 |
| 3 | ~~USB extension cable, ~1 m~~ ✅ already owned | Keeps the dongle away from USB 3 ports and the host to avoid interference. | €0 |
| 4 | Spare **CR2450** battery ✅ arrived 2026-11-17 | Less common than CR2032, so worth having one in stock. | €2–3 |
| 5 | Host machine (if not owned) | Raspberry Pi 4/5 with a good SD card/SSD, or any always-on Linux machine. | €0–80 |

Total without the host: about **€40–50**.

## The dongle on Linux

Checked on the notebook on 2026-11-18:

- Linux recognises the dongle out of the box: its USB-to-serial chip (CP2102N) uses the `cp210x` driver.
- It shows up as `/dev/ttyUSB0`. That name can change after replugging or when another USB-serial device is connected.
- The stable name is the link under `/dev/serial/by-id/`. The compose file takes it from `DONGLE_PATH` in `docker/.env`. The link's name contains the dongle's serial number, which is why it stays in the ignored `.env`.
- Inside the container the dongle is mapped to `/dev/ttyACM0`, the port named in Zigbee2MQTT's config. The name only matters inside the container.

## The sensor after a restart

Seen on 2026-11-19: after Zigbee2MQTT restarted, the SNZB-02D had no wireless icon on its screen and sent no readings, even when its readings changed. The pairing was still there. **One short press of its button** made it rejoin. Holding the button for 5 seconds would start a new pairing, which isn't needed here.

The coordinator firmware is Z-Stack 20210708, which is old for the ZBDongle-P. An update is worth a later slice.

## The boiler and its thermostat

Seen on 2026-11-24 (from photos, which are not in the repository):

- The boiler is **heating only**: it heats the radiators and no tap water. The water pipe going into it only tops up the circuit when the pressure drops.
- The thermostat is an **Auraton 2005 TX/RX**. The battery transmitter sends on/off by radio. The receiver ("AURATON RX") plugs into a wall socket, and a cable runs from it to the boiler's room-thermostat input. It works as a simple on/off contact.
- The boiler's power cord seems to go to the same wall socket, so the boiler plugs in rather than being hard-wired (to confirm).

### Ways to tell when the heating is on

Because the boiler heats nothing but the radiators, every option below measures heating only.

1. **A temperature probe clamped to the heating flow pipe** (chosen on 2026-11-24, plan below). The copper pipes under the boiler are bare, and the arrows on the mounting bracket show which outlet is which. It shows the burner's real cycles, about a minute late.
2. **A Zigbee plug that measures power**, if the boiler really does plug in. The pump's power use jumps when heating runs. The pump may keep running for a short while after the burner stops. The plug also strengthens the Zigbee mesh near the boiler.
3. **Listening to the Auraton's radio** with an RTL-SDR and `rtl_433`. Support for this model is not confirmed, so the signal may need decoding by hand. It shows when the thermostat asks for heat, not when the burner fires.
4. **Later, for control:** a Zigbee dry-contact relay in place of the Auraton receiver.

Options 3 and 4 are bigger projects. Option 3 means decoding an unknown radio protocol. Option 4 makes the app the thermostat, with schedules, hysteresis and a safe fallback for when it goes down.

### Plan: the flow-pipe sensor (agreed on 2026-11-24)

**The sensor:** a **Sonoff SNZB-02LD**, the Zigbee version with a probe.

**Ordered on 2026-11-25: two** (61.10 zł each), one for the flow pipe and one for the return pipe. The flow alone shows when the boiler heats and how hot the water it sends is. The return adds the difference between them (ΔT): how much heat the radiators gave off, whether the circuit is balanced, and whether the water coming back to this non-condensing boiler is cold enough to make the flue gases condense. The range test in step 1 now only decides whether a repeater is needed.

- Its probe is rated −40 to 115 °C, according to retailer listings. The main unit only handles −10 to 60 °C, so it stays away from the boiler.
- Zigbee2MQTT has supported it since version 2.3.0, and the compose file pins 2.14.3.

Steps:

1. **Range test before buying anything.** Put the SNZB-02D next to the boiler and press its button once, so it rejoins. Watch whether its readings keep arriving and what `linkquality` they carry. The *Map* view in the Zigbee2MQTT frontend shows how it connects.
2. **Buy and pair** the SNZB-02LD, with a friendly name that has no `/`. Watch its payload with `mosquitto_sub`. Expect temperature, battery and link quality, but no humidity.
3. **Check how often it reports.** Warm the probe, or wait for the heating to start, and watch how often messages arrive. If they are too sparse to catch the burner switching, look at the device's reporting settings in Zigbee2MQTT.
4. **Mount them.** Tape each probe firmly to its bare copper pipe (flow and return) near the boiler, with insulation wrapped over it, so it reads the pipe and not the room. Keep the main unit away from the boiler's heat.
5. **The app.** A new kind of device means a new mapper and payload shape. The reading is still a `Temperature`. The `device_location` row (e.g. "heating flow") is added by hand.
6. **Grafana.** First a graph of the flow temperature. After a few real heating cycles, a state-timeline panel with an on/off threshold. The threshold comes from real data, not a guess.
7. **Later, a separate slice:** heating periods computed by the app into their own table, for numbers such as hours of heating per day.

### If the boiler is too far from the dongle: a repeater

- **Only mains-powered Zigbee devices relay** (routers). Battery devices are end devices and never relay. Any Zigbee smart plug works as a router.
- **The router must reach the dongle itself.** Put it somewhere between the two, not next to the boiler.
- **A battery sensor keeps the parent it joined through.** After adding a router, press the sensor's button once so it rejoins. Then check on the *Map* that it now goes through the router.
- **Cheap combination:** a power-metering Zigbee plug as the router. If it reaches the dongle from the boiler's socket, the boiler plugs into it, which also gives option 2's pump signal.
- On the Raspberry Pi, a central place for the Pi with the dongle on its USB extension may make the repeater unnecessary.

## Host – Raspberry Pi (later; develop on the notebook first)

| # | Item | Notes | Approx. cost |
|---|------|-------|--------------|
| 1 | **Raspberry Pi 5, 4 GB** | 4 GB is enough for Zigbee2MQTT + MQTT + app + DB. | check current price |
| 2 | **Official Raspberry Pi 27 W USB-C power supply** | Cheap supplies cause random crashes on the Pi 5. | |
| 3 | Case with fan (or the official Active Cooler) | Keeps it cool when running 24/7. | |
| 4 | **64 GB "High Endurance" microSD** | Handles constant writes better than regular cards. | |
| 5 | (optional) USB SSD | More durable than microSD. Use an old one if you have it. | |

Total roughly **€105–130** (450–550 zł; more with an SSD).

Approach: develop and build on the notebook, deploy to the Pi (ARM64 Docker images via `docker buildx`).
Moving the dongle to the Pi is a one-off. Two things go with it, or every sensor has to be paired again:

- `docker/zigbee2mqtt/configuration.yml`: ignored by git, it holds the Zigbee network key;
- the Docker volume `temp_monitor_zigbee2mqtt-data`: the device database and the coordinator backup.

After that, deploys leave the Zigbee state alone, as long as they update the same checkout on the Pi (`git pull`, not a fresh clone, which would come without `configuration.yml`) and nobody runs `docker compose down -v`, which deletes the volume.

## Avoid when buying

- **Sonoff Dongle-PZG23**: a Z-Wave stick, not Zigbee. It won't work with the SNZB-02D.
- Anything labelled **"Z-Wave"** or **"800 series"**.
- **SNZB-02P / SNZB-02**: no screen.
- **SNZB-02LD** for rooms: its waterproof probe is meant for fridges, pools and pipes. Here it is used only on the heating pipes (see *The boiler and its thermostat*).
- Sensors that only work through a vendor cloud app.

Before ordering, check the exact model on https://www.zigbee2mqtt.io/supported-devices/

## Later (after the first sensor works)

- 3× more SNZB-02D for the remaining rooms: ordered on 2026-11-25 (49.30 zł each).
- A Zigbee smart plug as a repeater, if a room is out of range.
- Maybe an SNZB-02WD (outdoor, IP65) for an outside temperature reading.
- For boiler control: a Zigbee relay and/or Zigbee radiator valves (TRVs). Check the boiler wiring first.
