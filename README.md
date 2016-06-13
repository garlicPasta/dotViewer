# dotViewer

DotViewer is part of a client-server system  for displaying and exploring 
huge pointclouds on android devices.

The corresponding server u can find [here](https://github.com/garlicPasta/dotServer).

![example_picture1](https://raw.githubusercontent.com/garlicPasta/AndroidPointCloudVisualizer/master/readme/img/bstelle_mid4.png)
![example_picture2](https://raw.githubusercontent.com/garlicPasta/AndroidPointCloudVisualizer/master/readme/img/close_terra1.png)

## Dependencies 

* [Google Protocolbuffer](https://developers.google.com/protocol-buffers/)

Make sure protoc is in your PATH

## Build & Deploy

Before deploying run:

    gradle generateProto

Then continue as with any Android application.

## Run

For using dotViewer your need to specify the server adresse in the settings menue.

    Actionbar -> Settings -> General

Then specify the IP togehter with the port.
