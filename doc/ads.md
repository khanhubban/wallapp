Instructions for testing ad/AdMob related code.

# AdMob Privacy and Messaging / Consent

## Android

### Pre-requisites

1. An Android device with Google Play installed. This can be either a physical device or an emulator with Google Play installed.

### Changes to make locally for testing:

1. Switch to using the Release version of the app's `applicationId` by commenting out the `applicationIdSuffix = ".debug"` line in `app/android.gradle`.
2. Make sure to do a Gradle sync.
3. Modify `PrivacyMessagingManagerGoogle.android.kt` to configure `debugSettings` to be non-null. [Google's tutorial video](https://youtu.be/SysASyh9XKo?t=248) states you must call `.addTestDeviceHashedId()`, but it seems that may no longer be accurate, as it works without it.  
 
Once you do the above 3 steps, you can build and run the app on your device like normal (the first time you'll be warned by ADB about different app signatures - go ahead and uninstall the Play Store version and use the version you built).

## iOS

### Changes to make locally for testing:

1. Ensure you are running the "WallApp - [Debug|Release]" scheme in Xcode. The "Dev" scheme is not configured with AdMob so is not reliable.
2. Uncomment the `let debugSettings = UMPDebugSettings()` code in `app/ios/panels/Ads/PrivacyMessagingManagerDelegate.swift#L27-L29`.

