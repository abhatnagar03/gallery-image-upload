# gallery-image-upload

The app provides a gallery with feature to upload an image by clicking through device camera or selecting an image from device gallery. 

- The main screen is a gallery view which shows all the uploaded pictures
- Options to select image form gallery or click using camera
- Preview screen after image is selected/clicked with options to rotate, save cropped or save original image
- Full image view screen on tap of any thumbnail from gallery view with option to download the image to device's gallery
- Images are stored locally in the exterdir with scope to the app lifetime

## Features

- Custom LayoutManager to suport orientation change
- RecyclerView.Adapter
- RecyclerView.ItemDecoration
- MVP design pattern
- Clean code
- Responsive design techniques
- OpenCV library integration 

## Libraries used in the sample project

Butterknife - For view injection
Glide - For lazy load and caching for images
OpenCV - For cropping the image

## Prerequisites

- Android Studio
- SDK and JDK
- Clone the repository in local system using below git commands
```
git init
git clone *Project Path*
```
- Build the project and run the app
