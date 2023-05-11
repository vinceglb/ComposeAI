package data.repository.util

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
suspend fun <T> suspendRunCatching(
    dispatcher: CoroutineDispatcher,
    block: suspend () -> T
): Result<T> = try {
    withContext(dispatcher) {
        Result.success(block())
    }
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Napier.i(
        message = "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        throwable = exception,
    )
    Result.failure(exception)
}