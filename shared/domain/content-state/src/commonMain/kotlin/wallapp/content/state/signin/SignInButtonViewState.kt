package wallapp.content.state.signin

import androidx.compose.runtime.Immutable
import wallapp.image.Image
import wallapp.pixel.shape.ShapeSpec
import wallapp.pixel.text.Text
import wallapp.pixel.view.ViewEventHandler

@Immutable
interface SignInButtonViewState {

    @Immutable
    data class Apple(
        val viewSpec: SignInButtonViewSpec,
        val imageLight: Image,
        val imageDark: Image,
        val label: Text,
        val shapeSpec: ShapeSpec,
        val viewEventHandler: ViewEventHandler,
    ): SignInButtonViewState

    @Immutable
    data class Google(
        val viewSpec: SignInButtonViewSpec,
        val image: Image,
        val label: Text,
        val shapeSpec: ShapeSpec,
        val viewEventHandler: ViewEventHandler,
    ): SignInButtonViewState
}

