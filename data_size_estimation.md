#### Example measurement data from logs

```json
{
  "battery": 100,
  "comfort_humidity_max": 60,
  "comfort_humidity_min": 40,
  "comfort_temperature_max": 27,
  "comfort_temperature_min": 19,
  "humidity": 46.1,
  "humidity_calibration": 0,
  "linkquality": 48,
  "temperature": 25.4,
  "temperature_calibration": 0,
  "temperature_units": "celsius",
  "update": {
    "installed_version": 8960,
    "latest_release_notes": null,
    "latest_source": "https://raw.githubusercontent.com/Koenkk/zigbee-OTA/master/images/Sonoff/snzb-02d_v2.3.0.ota",
    "latest_version": 8960,
    "state": "idle"
  }
}
```

#### Measurement table

* id INT 8 bytes
* created INT 8 bytes
* sensor_name VARCHAR (max 50) 50 bytes (ascii)
* data VARCHAR (around 500 chars) 500 bytes (ascii)

#### Temperature/humidity table

* id INT 8 bytes
* measurement_id INT 8 bytes
* stamp INT 8 bytes
* value INT 8 bytes
* room VARCHAR (max 50) 50 bytes (ascii)

#### Totals

* One row in measurment table: 566 B -> 600 B.
* One row in temperature/humidity table: 82 B -> 100 B.
* So one measurement takes around 800 B (one measurement row + temp row + humidity row).
* 4 rooms and one measurement per minute creates 5760 measurements a day -> 2,102,400 a year.
* Total is 1,681,920,000 bytes which makes around 1,7 gb per year.


