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

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import io.github.aakira.napier.Napier

private const val TAG = "FirebaseAnalyticsHelperAndroid"

/**
 * Implementation of `AnalyticsHelper` which logs events to a Firebase backend.
 */
class FirebaseAnalyticsHelper(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.type) {
            for (extra in event.extras) {
                // Truncate parameter keys and values according to firebase maximum length values.
                param(
                    key = extra.key.take(40),
                    value = extra.value.take(100),
                )
            }
        }

        Napier.d(message = "Received analytics event: $event", tag = TAG)
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
        Napier.d(message = "Set user property: $name = $value", tag = TAG)
    }
}
