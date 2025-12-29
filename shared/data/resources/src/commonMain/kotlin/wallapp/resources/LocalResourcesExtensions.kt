package wallapp.resources

import wallapp.resource.Resource


expect suspend fun Resource.readBytes(): ByteArray?

expect suspend fun Resource.readString(): String?