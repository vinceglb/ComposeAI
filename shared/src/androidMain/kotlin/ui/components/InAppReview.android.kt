package ui.components

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.review.ReviewManagerFactory
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await

actual class InAppReviewState(
    context: Context,
    actual val onComplete: () -> Unit,
    actual val onError: () -> Unit,
) {
    private val manager = ReviewManagerFactory.create(context)

    var doShow: Boolean by mutableStateOf(false)

    actual fun show() {
        doShow = true
    }

    suspend fun showAndroid(activity: Activity) {
        doShow = false

        try {
            Napier.d { "InAppReview: Requesting review flow" }

            val reviewInfo = manager.requestReviewFlow().await()
            manager.launchReviewFlow(activity, reviewInfo).await()
            onComplete()

            Napier.d { "InAppReview: Review flow completed" }
        } catch (e: Exception) {
            onError()
            Napier.w { "InAppReview failed: $e" }
        }
    }
}

@Composable
actual fun rememberInAppReviewState(
    onComplete: () -> Unit,
    onError: () -> Unit,
): InAppReviewState {
    val context = LocalContext.current
    val inAppReviewState = remember { InAppReviewState(context, onComplete, onError) }

    LaunchedEffect(inAppReviewState.doShow) {
        if (inAppReviewState.doShow) {
            inAppReviewState.showAndroid(context as Activity)
        }
    }

    return inAppReviewState
}
