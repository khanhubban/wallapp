package wallapp.instantapp

import android.content.Context
import android.content.Intent
import com.google.android.gms.instantapps.InstantApps
import com.google.android.gms.instantapps.InstantApps.getPackageManagerCompat
import wallapp.log.Log
import wallapp.system.ui.controller.UiController
import wallapp.system.ui.controller.activity


class InstantAppManagerSystem(
    private val context: Context,
) : InstantAppManager {

    private val packageManagerCompat by lazy { getPackageManagerCompat(context) }

    override val isInstantApp: Boolean
        get() = packageManagerCompat.isInstantApp

    init {
        Log.d("isInstantApp: %b", isInstantApp)
    }

    override fun showInstallPrompt(uiController: UiController, requestCode: Int): Boolean {
        val postInstallIntent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setPackage(context.packageName)

        // The request code is passed to startActivityForResult().
        return InstantApps.showInstallPrompt(uiController.activity, postInstallIntent, requestCode, null)
    }
}


