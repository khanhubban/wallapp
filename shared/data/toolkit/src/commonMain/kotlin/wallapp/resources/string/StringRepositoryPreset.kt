package wallapp.resources.string

import wallapp.resources.translation.TranslationRepository
import wallapp.resources.translation.TranslationRepositoryPreset

fun StringRepositoryPreset(
    translationRepository: TranslationRepository = TranslationRepositoryPreset(),
    stringArbitrator: StringArbitrator = StringArbitratorPreset(),
) : StringRepositoryDefault = StringRepositoryDefault(
    translationRepository = translationRepository,
    stringArbitrator = stringArbitrator,
)