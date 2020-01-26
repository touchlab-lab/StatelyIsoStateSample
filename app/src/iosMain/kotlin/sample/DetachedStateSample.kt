package sample

import co.touchlab.stately.isolate.IsolateState
import co.touchlab.stately.native.SharedDetachedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.native.concurrent.freeze

fun timeTest() {
    val times = 50_000
    val detachedTime = measureTimeMillis {
        val detachedMap = SharedDetachedObject { mutableMapOf<String, SomeData>() }
        detachedMap.freeze()
        repeat(times){c ->
            val sd = SomeData("data $c")
            detachedMap.access {
                it.put("i $c", sd)
            }
        }
    }

    println("detachedTime: $detachedTime")
}
fun testDetachedData() {
    val time = measureTimeMillis {
        push1000(make1000())
        val more1 = make1000()
        val more2 = make1000()
        runBlocking {
            val job = GlobalScope.launch(Dispatchers.Default) {
                push1000(more1)
                push1000(more2)
            }

            job.join()
        }
    }

    DetachedStateSample.detachedMap.access {
        println("Total map size: ${it.size}, time: $time")
    }
}

private fun push1000(insertMap: Map<String, SomeData>) {
    DetachedStateSample.detachedMap.access { map ->
        map.putAll(insertMap)
    }
}

object DetachedStateSample {
    val detachedMap = SharedDetachedObject { mutableMapOf<String, SomeData>() }
}