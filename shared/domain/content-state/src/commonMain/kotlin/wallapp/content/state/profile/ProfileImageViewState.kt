package wallapp.content.state.profile

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import wallapp.image.Image
import wallapp.pixel.image.ImageViewSpec
import wallapp.pixel.image.ImageViewState
import wallapp.pixel.menu.MenuItem
import wallapp.pixel.menu.MenuItem.MenuItemImage
import wallapp.pixel.view.ViewEventHandler
import wallapp.pixel.view.ViewState

@Immutable
data class ProfileImageViewState(
    val image: MenuItem,
    val showBorder: Boolean = false,
    val indicator: ProfileImageIndicatorViewState? = null,
    val eventHandler: ViewEventHandler? = null,
) : ViewState {

    val imageViewState: ImageViewState
        get() = (image as MenuItemImage).imageViewState
    val imageViewSpec: ImageViewSpec
        get() = (image as MenuItemImage).imageViewState.viewSpec

    constructor(
        image: Image,
        imageViewSpec: ImageViewSpec,
        showBorder: Boolean = false,
        verified: Boolean = false,
        indicator: ProfileImageIndicatorViewState? = null,
        eventHandler: ViewEventHandler? = null,
    ) : this(
        imageViewState = ImageViewState(
            image = image,
            viewSpec = imageViewSpec,
            imageSize = null,
        ),
        showBorder = showBorder,
        indicator = indicator,
        eventHandler = eventHandler,
    )

    constructor(
        imageViewState: ImageViewState,
        showBorder: Boolean = false,
        indicator: ProfileImageIndicatorViewState? = null,
        eventHandler: ViewEventHandler? = null,
    ) : this(
        image = MenuItemImage(imageViewState = imageViewState),
        showBorder = showBorder,
        indicator = indicator,
        eventHandler = eventHandler,
    )

    val borderSize: Dp
        get() = if (showBorder) 3.dp else 0.dp

    init {
        require(image is MenuItemImage) {
            "image must be MenuItemImage, not ${image::class.simpleName}"
        }
    }

    companion object {
        val Preset = ProfileImageViewState(
            image = MenuItemImage(imageViewState = ImageViewState.Preset),
        )
    }
}

fun ProfileImageViewState(
    profileImage: Image,
    imageViewSpec: ImageViewSpec,
): ProfileImageViewState {
    return ProfileImageViewState(
        image = profileImage,
        imageViewSpec = imageViewSpec,
    )
}

fun ProfileImageViewState(
    profileImage: Image,
    indicator: ProfileImageIndicatorViewState,
    imageViewSpec: ImageViewSpec,
    eventHandler: ViewEventHandler,
): ProfileImageViewState {
    return ProfileImageViewState(
        image = profileImage,
        imageViewSpec = imageViewSpec,
        indicator = indicator,
        eventHandler = eventHandler,
    )
}

fun ProfileImageViewState.id(): String? {
    return (image as? MenuItemImage)?.imageViewState?.image?.id
}