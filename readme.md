# temp_monitor

## Status

* Work in progress.
* Last done: MQTT broker running.
* Next: One sensor end to end.

## General

A small solution for temperature monitoring at home.

Key ideas:

* **Have a working app**, which helps me manage the home heating.
* **Learn while creating** the solution.
* **Enjoy the ride** - have fun coding and solving the problems.

Key flow: Zigbee sensors -> Zigbee2MQTT -> MQTT -> Kotlin app.

## How to run

1. Turn the file `./docker/mqtt5/create_user.sh.template` into a ready-to-use `create_user.sh` with your credentials.
2. Run `docker compose up -d` from `./docker/` directory.
3. Run the `create_user.sh` script.

## Approach to AI

* AI helps me understand ideas and link everything.
* I author the code and solve problems (unless I really am in a pickle).
* Files created by AI are explicitly marked.