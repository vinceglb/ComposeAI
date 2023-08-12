/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package analytics

import analytics.AnalyticsEvent.Param
import analytics.AnalyticsEvent.ParamKeys
import analytics.AnalyticsEvent.Types
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

/**
 * Classes and functions associated with analytics events for the UI.
 */
fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
                Param(ParamKeys.SCREEN_CLASS, screenName),
            ),
        ),
    )
}

fun AnalyticsHelper.logMessageSent(isRetry: Boolean = false) {
    val eventType = if (isRetry) "ai_message_sent_retry" else "ai_message_sent"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}

fun AnalyticsHelper.logMessageReceived(
    receivedSuccessfully: Boolean,
    promptTokens: Int? = null,
    completionTokens: Int? = null,
    totalTokens: Int? = null,
) {
    val eventType = if (receivedSuccessfully) "ai_message_successful" else "ai_message_failed"

    // Report request tokens stats if available
    val extras = mutableListOf<Param>()
    promptTokens?.let { extras.add(Param("prompt_tokens", it.toString())) }
    completionTokens?.let { extras.add(Param("completion_tokens", it.toString())) }
    totalTokens?.let { extras.add(Param("total_tokens", it.toString())) }

    // Count the number of user that did not report tokens stats
    if (receivedSuccessfully && extras.isEmpty()) {
        extras.add(Param("missing", "1"))
    }

    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = extras,
        ),
    )
}

fun AnalyticsHelper.logCreateNewConversation() {
    logEvent(
        AnalyticsEvent(type = "create_new_conversation"),
    )
}

fun AnalyticsHelper.logConversationSelected() {
    logEvent(
        AnalyticsEvent(type = "conversation_selected"),
    )
}

fun AnalyticsHelper.logMessageCopied() {
    logEvent(
        AnalyticsEvent(type = "message_copied"),
    )
}

fun AnalyticsHelper.logMessageShared() {
    logEvent(
        AnalyticsEvent(type = "message_shared"),
    )
}

fun AnalyticsHelper.logWelcomeSeen() {
    logEvent(
        AnalyticsEvent(type = "welcome_complete"),
    )
}

fun AnalyticsHelper.logInAppReviewComplete() {
    logEvent(
        AnalyticsEvent(type = "in_app_review_complete"),
    )
}

fun AnalyticsHelper.logInAppReviewError() {
    logEvent(
        AnalyticsEvent(type = "in_app_review_error"),
    )
}

fun AnalyticsHelper.logRewardedAdImpression() {
    logEvent(
        AnalyticsEvent(type = "ad_ra_impression"),
    )
}

fun AnalyticsHelper.logRewardedAdReward() {
    logEvent(
        AnalyticsEvent(type = "ad_ra_reward_earned"),
    )
}

fun AnalyticsHelper.setUserTotalTokens(totalTokens: Int) {
    setUserProperty("tokens_total", totalTokens.toString())
}

fun AnalyticsHelper.setUserTotalMessages(totalMessages: Int) {
    setUserProperty("messages_total", totalMessages.toString())
}

/**
 * A side-effect which records a screen view event.
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}
