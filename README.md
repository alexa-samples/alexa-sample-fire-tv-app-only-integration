# Readme

This sample-fire-tv-app-only-integration project contains a basic Android TV app that responds to voice requests from users to find and play content. Users can say "Watch [movie title]" or "Find [TV show]" and the app will respond appropriately, without the need to use a remote control.

To accomplish this, the app incorporates the VSK Agent Client Library to connect to the on-device VSK Agent, which reports your app’s capabilities to Alexa and routes directives from Alexa to your app. The app follows the "App-only integration" method described in the Amazon developer documentation here: [App-only Integration Overview](https://developer.amazon.com/docs/video-skills-fire-tv-apps/integration-overview-app-only.html). 

The app requires some setup and configuration. For example, you need to configure the sample app with your own package name, as well as perform other steps to configure a security profile for the app. Once configured, the app plays up a sample video when you ask Alexa for the video by title. (Note that the code doesn't play the actual content asked for, just some test videos.)

The sample app will allow you to see a directive pushed to the app. This preview can give you more of a sense of how Alexa interacts with your app without actually going through the entire implementation with your own app. The sample app does not provide complex logic about how to handle the directives to perform specific actions in the app -- you'll need to work out much of the logic on your own based on your own unique code.

Additionally, this sample app is meant as a companion to the documentation, not as a starting point or template for your own app (though it could be used as the basis for your app if desired). Almost every video partner that integrates the VSK already has a custom-developed app. The sample app simply provides some context for some of the integration instructions.

For detailed instructions on setting up the sample app, see the [Step 3: Set Up the Sample App](https://developer.amazon.com/docs/video-skills-fire-tv-apps/set-up-sample-app-app-only.html), [Step 9: Sign Your App and Configure a Security Profile](https://developer.amazon.com/docs/video-skills-fire-tv-apps/sign-your-app-and-configure-security-profile-app-only.html), and [Step 10: Test Utterances and Observe Logs](https://developer.amazon.com/docs/video-skills-fire-tv-apps/test-utterances-and-observe-logs-app-only.html) in the app-only implementation documentation. (Other steps are not necessary if you just want to get the sample app set up and configured.)

An abbreviated version of the instructions are as follows:

1. Clone this app to your local workspace using `git clone https://github.com/alexa/alexa-sample-fire-tv-app-only-integration.git`.
2. Open the project code in Android Studio and replace the default package name `com.example.vskfiretv.company` in such a way that it is unique. Retain the prefix `com.example.vskfiretv` and replace only the last level `company` with your own unique name. The prefix `com.example.vskfiretv` is mandatory so that we can associate a default catalog for your sample app in the backend.
3. Rebuild the project and make sure the build is fine.
4. Sign your app with your custom key in Android Studio.
5. Get the MD5 and SHA-256 values from your custom key by running "signingReport" in Android Studio.
6. Create a security profile in the Amazon developer console (Settings > Security Profiles) and enter the MD5 and SHA-256 values of your customer key in "Android/Kindle Settings." Then generate a new API key.
7. Enter the API key in your sample app by going to `api_key.txt` file in the asset folder and inserting your API key. This will allow your app to be successfully authenticated by the VSK Agent.
8. Rebuild the project and make sure the build is fine.
9. In the developer console, create a new app and complete all the submission tabs (but don't submit the app). 
10. Generate a release APK file (signed with your custom key) and upload it into the Amazon developer console.
11. Attach the security profile to your app so that your app is authorized on the Fire TV. 
12. In the developer console, submit your app into Live App Testing (LAT) to allow for backend mapping of your app's ASIN and title to the catalog.
13. Get a Fire TV device and connect your computer to it through adb. (See [Connect to Fire TV through adb](https://developer.amazon.com/docs/fire-tv/connecting-adb-to-device.html).)
14. Inside Android Studio, run the app on your connected Fire TV device. You can monitor adb logs using the following commands when running the app. You will see that the sample app's capabilities are successfully reported in the adb logs.
    * `adb logcat | grep "AppAgent" -i`
    * `adb logcat | grep "DynamicCapabilityReporter" -i`
15. Open a terminal and run `adb logcat | grep "AlexaDirectiveReceiver" -i`. Then say the test utterance "Alexa, watch Superman" and look for a `SearchAndPlay` directive to get pushed to your app. You will see a random video being played in the sample app, and you can look at the directive payload using adb logs. 
16. You can test other utterances such as "Alexa, search for comedies," "Alexa, pause" (while the video is playing), "Alexa, rewind," "Alexa fast forward by 25 seconds," and so on.