Instructions for adding native resources (such as images) to the app.

Note these steps are required so the image is available on Android and iOS.

## Images

### Adding the resource to the project

1. Add the image to the `./shared/data/resources/src/commonMain/resources/MR/images` directory.
2. Ensure the image is named correctly. Specifically:
   1. The image filename **must** end with `@3`.
   2. The image should be named in `[ic_]drawable_name`.
3. Add an entry in `object LocalImages`
4. Add an entry in `./shared/data/resources/src/iosMain/kotlin/wallapp.resources/ResourcesExtensions.kt` in `fun Resource.LocalImage.getImageResource(): ImageResource`.

At this point, compiling the app should add the resource to the app.

### Using the resource in the app

1. Add an entry in `ImageRepository.kt`
2. Implement the previous step like this: `override val drawableName: Image by lazy { ImageOnBackground(LocalImages.DrawableName) }`
3. Use the image in the app as necessary.
