package wallapp.bitmap

import android.graphics.Bitmap

interface PaletteGenerator {

    suspend fun generatePalette(bitmap: Bitmap): List<Int>?

}

