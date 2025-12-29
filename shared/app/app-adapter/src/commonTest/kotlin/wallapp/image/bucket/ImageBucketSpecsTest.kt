package wallapp.image.bucket

import wallapp.string.quote
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageBucketSpecsTest {

    @Test fun `all keys are unique`() {
        ImageBucketSpecs.All.forEach { spec ->
            val key = spec.key
            val count = ImageBucketSpecs.All.count { it.key == key }
            assertEquals(1, count, "Duplicate ImageBucketSpec key: ${key.quote()}")
        }
    }
}