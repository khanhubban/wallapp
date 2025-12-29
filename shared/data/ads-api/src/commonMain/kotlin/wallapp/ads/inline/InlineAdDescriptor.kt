package wallapp.ads.inline

import wallapp.navigation.AppUiLocation


interface InlineAdDescriptor {

    val reuseAdHandle: Boolean

    val appUiLocation: AppUiLocation
}


