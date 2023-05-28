package analytics

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class CrashlyticsAntilog : Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        // send only error log
        if (priority < LogLevel.ERROR) return

        throwable?.let {
            when (it) {
                // e.g. http exception, add a customized your exception message
//                is KtorException -> {
//                    FirebaseCrashlytics.getInstance()
//                        .log("${priority.ordinal}, HTTP Exception, ${it.response?.errorBody}")
//                }
                else -> Firebase.crashlytics.recordException(it)
            }
        }
    }
}
