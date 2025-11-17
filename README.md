This application is a *very* rudementary fighting game, narrated by Google's Gemini AI to add flavor to events which has "samey" descriptions in traditional games of the type.

I have add a boolean 'useAi' to "easily" switch the games text from being sourced from the API to the barebones text prompt constructed by app itself. Flipping this can give a demonstration of how *dull* the game feels without the flavoring done by Ai.

The application itself keeps track of characters and stats, though with minimal descriptors as *that* is the main purpose of the AI.

According to Gemini's [Documentation](https://ai.google.dev/gemini-api/docs/structured-output?example=recipe) it should be very feasible to ask more of the AI - such as filling out templates for character types, gear, or locations - or building on to templates for establishing fleshed out role-playing characters or even scenarios.

So far game progress is not saved - though the combat balancing is terrible anyway, so you probably won't get far "into the dungeon". There are no locations, every successful encounter simply leads to another encounter with a random adversary and there is no going back.

Note that this project is not a browser-application, but instead an Android-application.

Running the application requires either an Android smartphone or Android Studio installation, and internet access.

For running through Android Studio
  1. Install Android Studio -> https://developer.android.com/studio
  2. Open Android Studio
  3. Clone this repository through Android Studio
  4. Either
     
       a. Set up a virtual phone through Android Studio
     
          1. In the right-hand side-bar there should be a tool called 'Device Manager'
          2. In the Device Manager window, click 'Create virtual device' or the '+' near the top
          3. Ensure the 'Form Factor' to the left is set to 'Phone'
          4. Select one of the device images and click 'Next' (I used Pixel 7)
          5. Click 'Finish'
          6. Start the virtual device in Device Manager
          7. Once device has started, navigate to 'Running Devices' also in right-hand side-bar
          8. In the top of Android Studio, ensure the virtual device is selected, along with 'app'

       b. Enable Developer Options on your Android Phone
     
          1. Go to 'Settings' on your device
          2. Tap 'About device' or 'About phone'
          3. Tap 'Software information'
          4. Tap 'Build number' seven times.
          5. Input PIN if prompted
          6. Navigate to 'Developer Options' now present in Settings (usually at bottom)
          7. Enable 'USB debugging' and you can now return to home
          8. Connect device to the pc with Android Studio running
          9. Allow ALL the things xD
          10. Your device should now be selectable at the top of Android Studio
          11. Ensure your device is selected, along with 'app'
      
  6. RUN the app using the run-button next to 'app'
  7. Profit.

          If you connected a device to run the app, you can now disconnect the device and the app will remain installed and functional until uninstalled.

