# dotViewer

DotViewer is an Android Application for displaying and exploring 
huge Pointclouds.

DotViewer and dotServer form a client-server system which is able
to manage pointcloud with more than 30 million points.

![example_picture]("https://raw.githubusercontent.com/garlicPasta/AndroidPointCloudVisualizer/master/readme/img/bstelle_mid4.png")
![example_picture2]("https://raw.githubusercontent.com/garlicPasta/AndroidPointCloudVisualizer/master/readme/img/close_terra1.png")


## Dependencies 

* [Google Protobuffer]("https://developers.google.com/protocol-buffers/")

Make sure protoc in in your PATH

## Build & Deploy

Before deploying run:

    gradle generateProto

Then continue as with any Android application.

