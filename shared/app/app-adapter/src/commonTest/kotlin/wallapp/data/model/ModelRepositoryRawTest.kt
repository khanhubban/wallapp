package wallapp.data.model

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import wallapp.di.resolveDependency
import wallapp.test.WaeTest
import wallapp.test.waeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue


internal class ModelRepositoryRawTest : WaeTest {

    private suspend fun ModelRepositoryRaw.waitUntilNotEmpty() =
        allWallpaperItems.filter { it.isNotEmpty() }.first()

    private suspend fun getWallpaperRepository(): ModelRepositoryRaw {
        return resolveDependency<ModelRepositoryRaw>().also {
            it.waitUntilNotEmpty()
        }
    }

    @Test
    @Ignore
    fun wallpaperDataRepositoryRaw() = waeTest {
        val modelRepositoryRaw: ModelRepositoryRaw = getWallpaperRepository()
        val allItems = modelRepositoryRaw.allWallpaperItems.first()
        println("allItems: ${allItems.size}")
        assertTrue(allItems.isNotEmpty())

        val allRemixes = modelRepositoryRaw.allRemixes.first()
        assertTrue(allRemixes.isNotEmpty())

        val allCategories = modelRepositoryRaw.allCategories.first()
        assertTrue(allCategories.isNotEmpty())

        val allFolders = modelRepositoryRaw.allFolders.first()
        assertTrue(allFolders.isNotEmpty())
    }
}