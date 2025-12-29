package wallapp.view

import wallapp.pixel.view.ViewAlignment

class ViewAlignmentFactoryDefault : ViewAlignmentFactory {

    override val default: ViewAlignment
        get() = center

    override val center: ViewAlignment
        get() = ViewAlignment.Center

    override val parallaxVertical: ViewAlignment
        get() = ViewAlignment.ParallaxVertical(1.05f)
}