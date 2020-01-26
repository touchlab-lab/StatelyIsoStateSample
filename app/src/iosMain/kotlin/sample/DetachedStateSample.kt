package sample

import co.touchlab.stately.native.SharedDetachedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun testDetachedData() {
    val time = measureTimeMillis {
        println("d 1")
        push1000(make1000())
        println("d 2")
        val more1 = make1000()
        val more2 = make1000()
        runBlocking {
            val job = GlobalScope.launch(Dispatchers.Default) {
                println("d 3")
                push1000(more1)
                println("d 4")
                push1000(more2)
                println("d 5")
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
        println("da 1")
        map.putAll(insertMap)
        println("da 2")
    }
}

object DetachedStateSample {
    val detachedMap = SharedDetachedObject { mutableMapOf<String, SomeData>() }
}