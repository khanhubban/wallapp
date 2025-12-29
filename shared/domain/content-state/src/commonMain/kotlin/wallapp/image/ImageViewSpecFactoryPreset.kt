package wallapp.image

import wallapp.view.ViewAlignmentFactory
import wallapp.view.ViewAlignmentFactoryDefault
import wallapp.view.ViewSpecArbitrator
import wallapp.view.ViewSpecArbitratorDefaultPreset
import wallapp.view.shape.ShapeSpecFactory
import wallapp.view.shape.ShapeSpecFactoryDefault

fun ImageViewSpecFactoryPreset(
    viewSpecArbitrator: ViewSpecArbitrator = ViewSpecArbitratorDefaultPreset(),
    viewAlignmentFactory: ViewAlignmentFactory = ViewAlignmentFactoryDefault(),
    shapeSpecFactory: ShapeSpecFactory = ShapeSpecFactoryDefault(),
): ImageViewSpecFactory {
    return ImageViewSpecFactoryDefault(
        viewSpecArbitrator = viewSpecArbitrator,
        viewAlignmentFactory = viewAlignmentFactory,
        shapeSpecFactory = shapeSpecFactory,
    )
}