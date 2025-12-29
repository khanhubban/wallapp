package wallapp.view

import wallapp.pixel.view.ViewAlignment

interface ViewAlignmentFactory {

    val default: ViewAlignment

    val center: ViewAlignment

    val parallaxVertical: ViewAlignment
}