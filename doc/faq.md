## FAQ

### Why do images take a long time to load?
Image performance is far worse in this demo than in a proper production app.  

The biggest reason is that the original app used a dedicated image hosting service, which offered numerous optimizations. For example, it served optimal formats per platform (WebP for Android, AVIF for iOS), which have a considerably smaller file size than the PNGs used here.
The production app also supported loading images sized specifically for their UI elements. To simplify this open-source release, that functionality was disabled, and "worst case" images are used instead. 
You'll see many warnings similar to the following caused by these changes made for this open source release:
`WallpaperMedia: Warning: SizedImage "WallpaperFeedSingle" mapped ImageViewSpecs do not match.`

Finally, Google Cloud Storage is simply not a fast image hosting service.

### What is the status of the desktop version of the app?
It’s a proof of concept. Many core UI features work, but many are not implemented — chiefly anything related to Sign in, Firebase, AdMob, or Lottie.

### Did you ever try to make a Kotlin/Wasm version of the app?
No, but community contributions are very welcome.

### Can I easily run iOS in "Compose for iOS" mode?
The first versions of the app on iOS used Compose for rendering, but over time nearly all UI was replaced with AppKit/SwiftUI. Enabling Compose for iOS again is be possible, but support would need to be reintroduced.

### How can I build and run the iOS app?
See [./doc/setup.md](./doc/setup.md) for detailed instructions on running the app on Android, iOS, and Desktop.  
As with all iOS apps, you’ll need to rename the bundle identifier to your own and set up a provisioning profile in Xcode.

### Why does iOS build to a `/.gradle-xcode` folder?
The Xcode build phase sets `GRADLE_USER_HOME` to a local `/.gradle-xcode` folder to keep Gradle’s cache isolated from your main dev cache. This avoids toolchain conflicts and classfile version mismatches between Xcode’s environment and your shell setup.

### How was the API data generated?
A custom backend service was built to construct the API data. This service has not been open-sourced.

### Is the API data documented anywhere?
No, but it's very straightforward to inspect. The models for all network data are in `./shared/data/content-network`.

### Is the Firebase usage documented anywhere?
See [./doc/firebase-usage.md](./doc/firebase-usage.md).

### How is API data updated?
For production apps, the API data should be uploaded for Firebase Storage. See `./service/service-export-remoteapi` and `./firebase-backend/README.md` for more information.

The `bundled-assets` branch supports the use of API data bundled with the application. This code was added for the open source release to make it easier to run the project without needing to create and configure a Firebase project and upload data to Firebase Storage.

### What's the testing situation like in the project?
Most tests are located in the `./shared/app/app-adapter` module. Running these tests on the Desktop platform is recommended.
Run all unit tests with:

```bash
./script/test_unit
```
Note: the majority of tests were removed for the open-source release for various reasons, so the test coverage is limited.

If you want to run a focused subset, use Gradle directly:

```bash
./gradlew :shared:app:app-adapter:test
```

### Some of this code is not the best...

Agreed! Some key architectural systems were designed in 2023, when the KMP ecosystem was less mature. Certainly there's areas of the code that could be improved by late-2025 standards. 

A few rough edges in particular worth noting:
* ViewModel system — implemented when the prevailing opinion was that ViewModels should *not* be ported to KMP. Since then, Jetpack's ViewModel solutions have become standard, but this project predates them.
* Lack of pagination 
* iOS resource duplication — some resources are included multiple times in the iOS build, which could be cleaned up.

But in a project of >130k lines of code, there's bound to be some warts right? Contributions welcome! 

### How can I get in touch? 
Reach out at [panels.app.code@gmail.com](mailto:panels.app.code@gmail.com).
