package wallapp.content.model

import wallapp.content.model.Id.DesignId


val Id.RemixId.designIdCompat: DesignId
    get() = DesignId("designIdCompat~$name")

fun DesignIdCompat(name: String): DesignId = Id.RemixId(name).designIdCompat