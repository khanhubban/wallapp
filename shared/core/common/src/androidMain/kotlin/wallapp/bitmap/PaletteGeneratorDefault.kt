package wallapp.bitmap

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import wallapp.util.nonEmptyList


class PaletteGeneratorDefault : PaletteGenerator {

    override suspend fun generatePalette(bitmap: Bitmap): List<Int>? {
        val palette = Palette.from(bitmap).generate()
        return palette.swatches.map {
            it.rgb
        }.nonEmptyList()
    }
}