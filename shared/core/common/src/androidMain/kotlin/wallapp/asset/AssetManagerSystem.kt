package wallapp.asset

import android.content.Context
import java.io.InputStream

class AssetManagerSystem(context: Context) : AssetManager() {

    private val assetManager: android.content.res.AssetManager = context.assets

    override fun open(fileName: String): InputStream = assetManager.open(fileName)
}