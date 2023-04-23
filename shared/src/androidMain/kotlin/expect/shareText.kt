package expect

import android.content.Intent
import ui.components.appContextForImagesMP

actual fun shareText(text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    appContextForImagesMP.startActivity(shareIntent)
}
