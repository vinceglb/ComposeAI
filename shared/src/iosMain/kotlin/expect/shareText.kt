package expect

import mainUIViewController
import platform.UIKit.UIActivityViewController

actual fun shareText(text: String) {
    val activityViewController = UIActivityViewController(
        activityItems = listOf(text),
        applicationActivities = null
    )
    mainUIViewController.presentViewController(
        activityViewController,
        animated = true,
        completion = null
    )
}
