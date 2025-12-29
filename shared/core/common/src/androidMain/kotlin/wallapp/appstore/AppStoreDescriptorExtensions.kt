package wallapp.appstore

import android.content.Intent
import android.net.Uri


fun AppStoreDescriptor.getViewInStoreIntent(applicationId: String): Intent =
    Intent().apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(getStoreUrl(applicationId))
        setPackage(storeApplicationId)
    }