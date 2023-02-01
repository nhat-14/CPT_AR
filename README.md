# Chemical Plume Tracing in Augmentation Reality in Android Studio
## Introduction

A device equipped with 8 gas sensors will measure the gas concentration and send the processed data to the smartphone via the **MQTT protocol**. 
The application will suggest the next moving direction for the user to catch the plume. 
Following the sequence of suggestions, the user may reach the gas source. 
The suggestion is visualized as arrows shown on the environment surface captured by the phone camera (Augmentation Reality).

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://github.com/Nhat-Luong14/CPT_AR)

## Features

- Connect to a MQTT server.
- Lister to any gas data coming at assinged topic.
- Calculate the best next move for the source searcher.
- Visulize the suggestion by showing the arrow headed to the next destination.

## Installation

Application requires [Node.js](https://nodejs.org/) v10+ to run.
Application runs well on Samsung.

## Development

IP and PORT of the MQTT server is fixed in the source code in the *MainActivity* file. User should modified for their application. 

## Inspiration

Basic introduction to ARCore in Android

[![N|Solid](https://cldup.com/dTxpPi9lDf.thumb.png)](https://medium.com/@codemaker2016/develop-your-helloar-app-in-android-studio-using-arcore-and-sceneform-ae9e1b7a1b5b)

![demo](https://github.com/codemaker2015/ar-object-augmentation-android-studio/blob/master/demo/demo.gif)

## License

MIT
