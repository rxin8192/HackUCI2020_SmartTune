# TO DO

 * make a listener that takes mic input and creates a valid decibel value

 * IMPLEMENTATION IDEA: two global variables: sound_origina, sound_increment. To calculate current volume bar, add sound_original to sound_increment. sound_original will change whenever the volume button is pressed. sound_increment will change based off of mic parsing.

 * At the beginning, take data point every 300ms 10 times (about 3 seconds). After that, every 1000ms take a data point, pop end off queue, compute average.

 * adjust the volume according to decibel changes

* create a loop that enables the istener and volume adjustment

* check for bugs

* look into making a background process

* polish UI


## extra ideas
* Add a description button that has pop out text describing what the app does
* Make a widget version so you can use the feature without launching the app

  
