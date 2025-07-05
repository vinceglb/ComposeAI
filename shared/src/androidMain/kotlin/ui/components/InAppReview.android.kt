package ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.tasks.await

actual class InAppReviewState(
    private val activity: Activity,
    actual val onComplete: () -> Unit,
    actual val onError: () -> Unit,
) {
    actual suspend fun show() {
        val manager = ReviewManagerFactory.create(activity)
        val reviewInfo = manager.requestReviewFlow().await()
        manager.launchReviewFlow(activity, reviewInfo).await()
        onComplete()
    }
}

@Composable
actual fun rememberInAppReviewState(
    onComplete: () -> Unit,
    onError: () -> Unit,
): InAppReviewState {
    val context = LocalContext.current
    return remember {
        InAppReviewState(context as Activity, onComplete, onError)
    }
}
