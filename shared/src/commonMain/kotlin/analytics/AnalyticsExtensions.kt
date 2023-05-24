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

fun AnalyticsHelper.logMessageReceived(receivedSuccessfully: Boolean, errorName: String? = null) {
    val eventType = if (receivedSuccessfully) "ai_message_successful" else "ai_message_failed"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = errorName?.let {
                listOf(Param(ParamKeys.AI_ERROR_NAME, errorName))
            } ?: emptyList()
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
