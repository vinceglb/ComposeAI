package analytics

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GeneratingTokens
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ebfstudio.appgpt.common.BuildConfig
import com.ebfstudio.appgpt.common.BuildKonfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import ui.components.AnimatedCounter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
actual fun AdMobButton(
    coins: Int,
    onRewardEarned: (Int) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val analyticsHelper = LocalAnalyticsHelper.current

    val adId = when (BuildConfig.DEBUG) {
        true -> "ca-app-pub-3940256099942544/5224354917"
        else -> BuildKonfig.ADMOB_REWARDED_AD_ID
    }

    var ad: RewardedAd? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        load(context, adId).onSuccess { ad = it }
    }

    TextButton(
        enabled = ad != null,
        onClick = {
            ad?.let { adReward ->
                show(
                    context = context,
                    ad = adReward,
                    resetRewardAd = { ad = null },
                    loadNewRewardAd = {
                        coroutineScope.launch {
                            load(context, adId).onSuccess { ad = it }
                        }
                    },
                    onAdImpression = {
                        analyticsHelper.logRewardedAdImpression()
                    },
                    onRewardEarned = {
                        onRewardEarned(it)
                        analyticsHelper.logRewardedAdReward()
                    },
                )
                ad = null
            }
        },
        modifier = modifier,
    ) {
        Icon(
            Icons.Rounded.GeneratingTokens,
            contentDescription = null,
        )
        Spacer(Modifier.width(4.dp))
        AnimatedCounter(
            count = coins,
            fontWeight = FontWeight.Bold
        )
    }
}

suspend fun load(
    context: Context,
    adId: String,
): Result<RewardedAd> =
    suspendCoroutine { continuation ->
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

fun show(
    context: Context,
    ad: RewardedAd,
    resetRewardAd: () -> Unit,
    loadNewRewardAd: () -> Unit,
    onAdImpression: () -> Unit,
    onRewardEarned: (Int) -> Unit,
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
            onAdImpression()
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
        onRewardEarned(rewardAmount)
    }
}

