package analytics

//import cocoapods.FirebaseAnalytics.FIRAnalytics
import io.github.aakira.napier.Napier

private const val TAG = "FirebaseAnalyticsHelperIos"

class FirebaseAnalyticsHelper : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
//        FIRAnalytics.logEventWithName(
//            name = event.type.take(40),
//            parameters = event.extras.associate {
//                // Truncate parameter keys and values according to firebase maximum length values.
//                it.key.take(40) to it.value.take(100)
//            },
//        )

        Napier.d(message = "Received analytics event: $event", tag = TAG)
    }

    override fun setUserProperty(name: String, value: String) {
//        FIRAnalytics.setUserPropertyString(value = value, forName = name)
        Napier.d(message = "Set user property: $name = $value", tag = TAG)
    }
}
