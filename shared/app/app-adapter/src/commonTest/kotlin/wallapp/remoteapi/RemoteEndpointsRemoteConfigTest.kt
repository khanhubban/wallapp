package wallapp.remoteapi

import wallapp.remoteconfig.data.RemoteConfigDataDefaultsProviderDefault
import wallapp.remoteconfig.data.RemoteConfigDataMock
import wallapp.remoteconfig.data.RemoteConfigEntry
import kotlin.test.Test
import kotlin.test.assertEquals

class CatalogVersionConfigTest {

    @Test
    fun catalogVersionEntry_hasStableKeyAndDefault() {
        assertEquals("catalog_version", RemoteConfigEntry.CatalogVersion.key)
        assertEquals("99999999", RemoteConfigEntry.CatalogVersion.default)
    }

    @Test
    fun mockData_exposesCatalogVersionDefault() {
        val data = RemoteConfigDataMock(RemoteConfigDataDefaultsProviderDefault)
        assertEquals("99999999", data.catalogVersion.value)
    }
}
