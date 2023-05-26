package analytics

import android.app.Activity
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
actual fun AdMobButton() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var ad: RewardedAd? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        load(context).onSuccess { ad = it }
    }

    IconButton(
        enabled = ad != null,
        onClick = {
            ad?.let { adReward ->
                show(
                    context = context,
                    ad = adReward,
                    resetRewardAd = { ad = null },
                    loadNewRewardAd = {
                        coroutineScope.launch {
                            load(context).onSuccess { ad = it }
                        }
                    }
                )
                ad = null
            }
        }
    ) {
        Icon(Icons.Rounded.Favorite, contentDescription = null)
    }
}

suspend fun load(context: Context): Result<RewardedAd> =
    suspendCoroutine { continuation ->
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Napier.d("Ad was loaded.")
                    continuation.resume(Result.success(ad))
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Napier.d("Ad failed to load. $adError")
                    continuation.resume(Result.failure(Throwable(adError.toString())))
                }
            }
        )
    }

fun show(
    context: Context,
    ad: RewardedAd,
    resetRewardAd: () -> Unit,
    loadNewRewardAd: () -> Unit
) {
    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdClicked() {
            // Called when a click is recorded for an ad.
            Napier.d("Ad was clicked.")
        }

        override fun onAdDismissedFullScreenContent() {
            // Called when ad is dismissed.
            // Set the ad reference to null so you don't show the ad a second time.
            Napier.d("Ad dismissed fullscreen content.")
            resetRewardAd()
            loadNewRewardAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            // Called when ad fails to show.
            Napier.e("Ad failed to show fullscreen content.")
            resetRewardAd()
        }

        override fun onAdImpression() {
            // Called when an impression is recorded for an ad.
            Napier.d("Ad recorded an impression.")
        }

        override fun onAdShowedFullScreenContent() {
            // Called when ad is shown.
            Napier.d("Ad showed fullscreen content.")
        }
    }

    ad.show(context as Activity) { rewardItem ->
        // Handle the reward.
        val rewardAmount = rewardItem.amount
        val rewardType = rewardItem.type
        Napier.d("User earned the reward. $rewardAmount - $rewardType")
    }
}

