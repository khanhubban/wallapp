Instructions for running on Android, Desktop, and iOS.

## Pre-requisites (Android, Desktop)

1. A Mac.
2. A Git client. [Github Desktop](https://desktop.github.com/) goes a very long way. 
3. Install [Android Studio](https://developer.android.com/studio) (even if running iOS).
    * Assuming your Mac has an Apple Silicon chip, make sure to download the "Mac with Apple chip" version.
    * "Android Studio Otter | 2025.2.2" is the most recent version that has been verified to work.
4. Install [Homebrew](https://brew.sh/).  
5. [optional] Must have Java 17 installed. Android Studio comes bundled with it but you can also [install it separately via Homebrew](https://stuetzpunkt.wordpress.com/2022/04/07/install-temurin-jdk-with-homebrew-on-macos/) if you plan on running from the Terminal or building iOS.

## Pre-requisites (iOS)
All of the above, plus:
1. Install [Xcode](https://apps.apple.com/au/app/xcode/id497799835?mt=12) from the Mac App Store (including command line tools)
2. Follow all the steps in [this guide](https://kotlinlang.org/docs/multiplatform-mobile-setup.html). You should be able to run the `kdoctor` command via the Terminal successfully.
3. Install [Cocoapods](https://guides.cocoapods.org/using/getting-started.html)

### Android

1. Open Android Studio and select "Open an existing project" and select the root folder (the same one this README is located in).
2. Grab a coffee and wait for the initial project sync. The footer at the bottom of Android Studio will 
3. Change the project view in the top left from "Android" to "Project".
4. Set "app.android" as the current Run / Debug configuration (on the toolbar at the top right of the screen). This will be selected by default first time you open the code in Android Studio.
5. Select "wallApp" as the app flavor.
6. Ensure an Android device is connected (either a physical device or emulator). See Google's doco for running on [deivce](https://developer.android.com/studio/run/device) and [emulator](https://developer.android.com/studio/run/emulator).
7. Select "Run" from the toolbar at the top right of the screen (the "Play" button). The first build will take a while, but subsequent builds will generally be much faster.


### iOS

First time setup:
1. In terminal, navigate to `<project_root>/app/ios`
2. Run `pod install` to fetch the CocoaPods dependencies
3. Ensure there is an iOS device connected to the Mac. A simulator is easiest to start with [doco](https://developer.apple.com/documentation/xcode/running-your-app-in-simulator-or-on-a-device). Configuring a physical iOS device requires more effort (see [here](https://developer.apple.com/documentation/xcode/enabling-developer-mode-on-a-device). Whether running on a simulator or physical device, [these instructions](https://developer.apple.com/documentation/xcode/running-your-app-in-simulator-or-on-a-device) are worth reading.

#### Running the iOS app via Xcode (easiest, and recommended if you're just building/running the iOS app):
1. Open `app/ios/wallApp.xcworkspace` in Xcode
2. Select the `wallApp` scheme and desired device/emulator
   - The repo does not include a signing team. Simulator builds work without signing; for device builds, set your own team in the Signing & Capabilities tab.
3. Click on the Run button or press Cmd + R

Note: iOS builds are expected to work on a simulator out of the box. Building/running on a physical device requires standard iOS provisioning (team, signing, profiles), which is not covered in this repo.

#### Running the iOS app via Android Studio (recommended if you're coding the iOS app):

First time setup:
1. Open Android Studio the same way you do for Android
2. Expand the "Configurations" dropdown on the top right toolbar. You will likely see an item labeled "wallApp" (it typically has a white "K" logo with a circular, quasi-Apple logo) - you can delete this.
3. Select "Edit Configurations", click the "+" button and select "iOS Application". Now we must populate the Configuration:
   1. Name: set to "iosApp"
   2. Xcode project file: select `<project_root>/app/ios/wallApp.xcodeproj`
   3. Xcode project scheme: select "Release" or "Debug". "Debug" is preferable if you are debugging the app.
   4. Exection target: select the desired device/iOS simulator.
   5. Select "OK" to save the configuration.

Running the app:
1. From Android Studio, select the "iosApp" Select "Run" from the toolbar at the top right of the screen (the "Play" button). The first build can take a long time (10+ minutes), but subsequent builds are much faster.


### Desktop

⚠️ Important: Do not run the desktop app via the green "Run" arrow on `fun main()` via Android Studio.  
That will not generate resources correctly and may cause runtime crashes.

Instead, always run through the Gradle task:

```bash
./gradlew :app:desktop:run
```

#### First-time setup in Android Studio
1. Open Android Studio.
2. Go to "Edit Configurations".
3. Add a new **Gradle** run configuration:
   - Name: `desktopApp`
   - Gradle project: select the project root.
   - Tasks: `:app:desktop:run`
4. Use this `desktopApp` configuration to run or debug the app.

This ensures resources are generated correctly and the app behaves consistently.


## Troubleshooting

"I get a `error: 'env pod install' command failed with an exception:` error building in Xcode"

There's a few possible fixes for this:

1. Perform a Gradle sync in Android Studio ("File" -> "Sync project with Gradle Files"). This is the easiest and most reliable way.

Alternatively, you can try the following:
1. Run `./script/build_ios [d|r]`, which will force the app to be built using the correct Ruby version. 
2. Once Step 1 is complete, click "Start the active scheme" in Xcode and the app will run as expected.

The above is an infuriating, intermittent issue. It can start happening for seemingly no good reason. 
The other fix is to:
1. Delete the contents of `~/Library/Developer/Xcode/DerivedData`
2. In Xcode, select Product -> Clean Build Folder
3. Run `./gradlew clean`.
4. Restart the Mac.

"Can I debug iOS from Android Studio?"

1. Yes! "Settings -> Advanced Settings -> Enable experimental Multiplatform IDE features" (via [this](https://twitter.com/abdulbasitgd/status/1779919638010208259) tweet).

"I can't build the iOS app for the first time"
1. Try running `pod update` from `<project_root>/app/ios`.
2. For build issues, perform a clean build (Cmd + Shift + K)


"I'm unable to run iOS via Android studio. The build output is attempting to load `null.app`"
From [here](https://stackoverflow.com/a/71759968):
1. Close Android Studio
2. Delete `build/ios`
3. Go to `~/Library/LaunchAgents/` and delete `com.jetbrains.AppCode.BridgeService.plist`
4. Reopen the project in Android Studio and try again
