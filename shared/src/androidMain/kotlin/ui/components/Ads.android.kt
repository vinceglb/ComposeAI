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
import com.ebfstudio.appgpt.common.BuildConfig
import com.ebfstudio.appgpt.common.BuildKonfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import io.github.aakira.napier.Napier
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class AdsState(
    actual val onRewardEarned: (Int) -> Unit,
) {
    private var ad: RewardedAd? by mutableStateOf(null)

    var doLoad: Boolean by mutableStateOf(false)
    var doShow: Boolean by mutableStateOf(false)

    actual val isLoaded: Boolean
        get() = ad != null

    internal fun load() {
        doLoad = true
    }

    actual fun show() {
        doShow = true
        Napier.d { "show" }
    }

    suspend fun loadAndroid(context: Context) {
        doLoad = false

        val result: Result<RewardedAd> = suspendCoroutine { continuation ->
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                adId,
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

        // Set the ad
        result.onSuccess { ad = it }
    }

    fun showAndroid(context: Context) {
        doShow = false

        Napier.d { "showAndroid" }

        ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Napier.d("Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Napier.d("Ad dismissed fullscreen content.")
                ad = null
                load()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Napier.e("Ad failed to show fullscreen content.")
                ad = null
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

        ad?.show(context as Activity) { rewardItem ->
            // Handle the reward.
            val rewardAmount = rewardItem.amount
            val rewardType = rewardItem.type
            Napier.d("User earned the reward. $rewardAmount - $rewardType")
            onRewardEarned(rewardAmount)
        }
    }

    companion object {
        private val adId = when (BuildConfig.DEBUG) {
            true -> "ca-app-pub-3940256099942544/5224354917"
            else -> BuildKonfig.ADMOB_REWARDED_AD_ID
        }
    }
}

@Composable
actual fun rememberAdsState(
    onRewardEarned: (Int) -> Unit
): AdsState {
    val adsState = remember { AdsState(onRewardEarned) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        adsState.load()
    }

    LaunchedEffect(adsState.doLoad) {
        if (adsState.doLoad) {
            adsState.loadAndroid(context)
        }
    }

    LaunchedEffect(adsState.doShow) {
        if (adsState.doShow) {
            adsState.showAndroid(context)
        }
    }

    return adsState
}