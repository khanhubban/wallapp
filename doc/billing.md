Instructions for developing/running billing/RevenueCat related code.

## Android

### Pre-requisites (one-time)

1. An Android device with Google Play installed. This can be either a physical device or an emulator with Google Play installed.
2. The Google Account must be registered as a tester in Google Play.
3. You must manually opt in to testing the app via the Play Store. Do so on the web here: https://play.google.com/apps/internaltest/...
4. Install the Release version of the app via the Play Store.

### Changes to make locally for testing (each time):

1. Switch to using the Release version of the app's `applicationId` by commenting out the `applicationIdSuffix = ".debug"` line in `app/android.gradle`.
2. Make sure to do a Gradle sync.
3. Modify `FactoryCommon.kt`'s `private fun canConfigureRevenueCat(scope: Scope): Boolean {` to always return `true`.
 
Once you do the above 3 steps, you can build and run the app on your device like normal (the first time you'll be warned by ADB about different app signatures - go ahead and uninstall the Play Store version and use the version you built).
You should now be able to build the app yourself in Debug mode with full logs, and run Google Play billing on your device. Attempts to purchase Plus will now show the real Google Play purchase UI. 

Note: Assuming you joined the test group correctly, Google Play's purchase screen should say "Test card" and note a "30 min" subscription time.