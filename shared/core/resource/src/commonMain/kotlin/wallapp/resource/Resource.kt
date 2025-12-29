package wallapp.resource

expect sealed interface Resource {

    val contentDescription: String?

    val exportString: String

    companion object {
        fun fromExportString(exportString: String): Resource

        fun from(model: Any): Resource

        val Preset: Resource
    }
}

