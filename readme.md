# Smart Tune application

> Created By: Scott Buchmiller, Jason Zhu, Ryan Yoshida, and Ricky Xin for **HackUCI 2020**

## Description
Smart volume adjustment application developed for android mobile phones.

The application will adjust your volume, either higher or lower, depending on the volume of your surroundings as picked up by your device's microphone.

<br>
*Smart Tune* is designed to be run in the background while listening to some other audio input in order to adjust the volume to match the surrounding noise. 
*Smart Tune* uses the microhone permission and creates a foreground service in order to access the microphone while running in the background.

Features a simple UI with a microphone button that will enable the volume adjustment

After a short calibration delay the application will begin to increase or decrease the volume based on fluxuations in the ambient noise level picked up by your device's microphone.

Smart Tune also features a settings page where the sensitivity and range of the volume adjustment can be changed.

## Usage
### Main View
* Activate *Smart Tune* by pressing main button
* Access Settings by selecting the '?' button
* App will run in the background when not using the app to collect audio data
### Settings View
* First seek bar selects the `sensitivity`
  * Determines how sensitive to volume changes the app is
* Range seek bar selcts the minimum and maximum volume that the app will set to.
  * Utilizes a sigmoid function to create asymptotion at the bounds
* Select speaker mode setting sets whether the app will work in speaker mode
  * In speaker mode the volume will be increased when a decrease in ambient sound is recorded
  * When not in speaker mode the volume in increase when an increase in ambient sound is recorded