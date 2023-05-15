import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

/**
 * Napier proxy for iOS.
 * https://github.com/AAkira/Napier#ios
  */
fun debugBuild() {
    Napier.base(DebugAntilog())
}
