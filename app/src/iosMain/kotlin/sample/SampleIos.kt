package sample

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

actual class Sample {
    actual fun checkMe() = 7
}

actual object Platform {
    actual val name: String = "iOS"
}

internal actual fun <T> runBlocking(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> T
): T = kotlinx.coroutines.runBlocking(context, block)

internal actual fun measureTimeMillis(block: () -> Unit): Long = kotlin.system.measureTimeMillis(block)