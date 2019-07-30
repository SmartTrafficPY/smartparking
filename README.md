<p align="center">
    <a href="http://smarttraffic.com.py"><img src="https://github.com/SmartTrafficPY/smartparking/blob/master/header-smartTraffic.png"        title="SmartTraffic" alt="SmartTraffic"></a>
</p>

# SmartParking: facilitating the search for parking places.

<p align="center">
  <a href="https://www.smarttraffic.com.py/SmartParking/"><img  src="https://github.com/SmartTrafficPY/smartparking/blob/master/Smartparking.png" title="SmartParking" alt="SmartParking"></a>
</p>

Repository corresponding to the development of the client part of the SmartParking tool, one of the case studies within the SmartTraffic project framework.

## Table of Contents

- [Installation](#installation)
- [Features](#features)
- [Contributing](#contributing)
- [FAQ](#faq)
- [Support](#support)
- [License](#license)

## Installation

This is the `source code` of the client side of the project.
At the time, our server is still in develop, like this application

### Clone

- Clone this repo to your local machine using `https://github.com/SmartTrafficPY/smartparking.git`

### Setup

- If you want to the app to point to your server you need to change the BASE_URL variable to your own server `<ip-address>`
- Make sure you register some data from the parking places with their spots
---

## Features
- Map with information of the availability of parking places
- Has a proximity monitoring module
- Manage parking spots and places
- User adminitration with session based authentication

## Usage
This aplication is the client side of a more complex system, where a server needs to control and have access to information about a parking place, with all the spots in it. This means that need to be register a place, with coordinates, places withs there own coordinates, and enter the data to this server.
This app get the data from the sensors mobile phone (gps, acelerometer, giroscope) and with the user position, transport mode(walking, driving...) determine if the user is occuping a spot or maybe free it. This is how the data of disponibility of the spots is recorded, then this info, is pass it to the other users.

## Documentation

All the documentation we have on this proyect is on the [SmartTraffic project page](smarttraffic.com.py), in the section of dissemination.

## Contributing

> To get started...

### Step 1

- **Option 1**
    - 🍴 Fork this repo!

- **Option 2**
    - 👯 Clone this repo to your local machine using `https://github.com/SmartTrafficPY/smartparking.git`

### Step 2

- **HACK AWAY!** 🔨🔨🔨

### Step 3

- 🔃 Create a new pull request using.

---

## FAQ

- **How do I do *specifically* so and so?**
    - No problem! Just do this.

---

## Support

This project is co-funded by CONACYT through the PROCIENCIA program with resources from the Fund for Excellence in Education and Research - FEEI, FONACIDE.

- Website at <a href="http://smarttraffic.com.py" target="_blank">`SmartTraffic`</a>
- Twitter at <a href="https://twitter.com/SmarttrafficPy" target="_blank">`@SmarttrafficPy`</a>

---

## License

[![License](https://img.shields.io/badge/License-Apache%202.0-yellowgreen.svg)](https://opensource.org/licenses/Apache-2.0)  

- **[Apache License 2.0](https://github.com/SmartTrafficPY/smartparking/blob/master/LICENSE)**
- Copyright 2019 © <a href="http://smarttraffic.com.py" target="_blank">SmartTraffic</a>.
