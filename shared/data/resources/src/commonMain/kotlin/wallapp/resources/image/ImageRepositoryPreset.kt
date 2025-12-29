package wallapp.resources.image

import wallapp.resources.string.Strings
import wallapp.resources.string.StringsNoOp

fun ImageRepositoryPreset(
    strings: Strings = StringsNoOp,
): ImageRepository = ImageRepositoryDefault(strings)