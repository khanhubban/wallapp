package wallapp.system.navigation

import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import wallapp.log.Log

fun openUrlInBrowser(url: String) {
    Log.d("[SystemNavigatorIos] openUrlInBrowser: $url")
    val nsUrl = NSURL(string = url)
    UIApplication.sharedApplication.openURL(nsUrl)
}

fun openUrlInSafariViewController(url: String, presentingViewController: UIViewController) {
    Log.d("[SystemNavigatorIos] openUrlInSafariViewController: $url")
    val nsUrl = NSURL(string = url)
    val safariViewController = SFSafariViewController(nsUrl)
    presentingViewController.presentViewController(safariViewController, animated = true, completion = null)
}
