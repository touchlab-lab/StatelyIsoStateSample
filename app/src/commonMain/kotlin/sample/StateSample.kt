package sample

import co.touchlab.stately.collections.frozenHashMap
import co.touchlab.stately.isFrozen
import co.touchlab.stately.isolate.IsolateState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object StateSample {
    val cacheMap = IsolateState { mutableMapOf<String, SomeData>() }
}

data class SomeData(val s: String)

fun doStuff(){
    StateSample.cacheMap.access {map ->
        map.put("Hello", SomeData("World"))
    }

}

fun readStuff(){
    val sd = StateSample.cacheMap.access {
        val resultVal = it.get("Hello")
        println("got result $resultVal")
        resultVal
    }
    println("sd: $sd, isFrozen: ${sd.isFrozen()}")
}

fun perfCheck1(){
    val times = 50_000
    val v1Time = measureTimeMillis {
        val fmap = frozenHashMap<String, SomeData>()
        repeat(times){c ->
            fmap.put("i $c", SomeData("data $c"))
        }
    }

    println("v1Time: $v1Time")

    val isoTime = measureTimeMillis {
        val isomap = IsolateState{ mutableMapOf<String, SomeData>()}
        repeat(times){c ->
            isomap.access { it.put("i $c", SomeData("data $c")) }
        }
    }

    println("isoTime: $isoTime")

    val normalTime = measureTimeMillis {
        val map = mutableMapOf<String, SomeData>()
        repeat(times){c ->
            map.put("i $c", SomeData("data $c"))
        }
    }

    println("normalTime: $normalTime")

    val isoBlockTime = measureTimeMillis {
        val isomap = IsolateState{ mutableMapOf<String, SomeData>()}
        val outerTimes = times / 1000
        repeat(outerTimes){ c ->
            val blockMap = mutableMapOf<String, SomeData>()
            repeat(1000){inner ->
                val putCount = (c * 1000) + inner
                blockMap.put("i $putCount", SomeData("data $putCount"))
            }

            isomap.access { it.putAll(blockMap) }
        }
    }

    println("isoBlockTime: $isoBlockTime")

    val v1BlockTime = measureTimeMillis {
        val fmap = frozenHashMap<String, SomeData>()
        val outerTimes = times / 1000
        repeat(outerTimes){ c ->
            val blockMap = mutableMapOf<String, SomeData>()
            repeat(1000){inner ->
                val putCount = (c * 1000) + inner

                blockMap.put("i $putCount", SomeData("data $putCount"))
            }

            fmap.putAll(blockMap)
        }
    }

    println("v1BlockTime: $v1BlockTime")
}

fun racyInABadWay(){
    val fmap = frozenHashMap<String, SomeData>()
    val size = fmap.size
    fmap.put("i $size", SomeData("data $size")) // <- may result in missed values
}

fun atomicOperations(isoMap: IsolateState<MutableMap<String, SomeData>>){
    isoMap.access { map ->
        map.put("i ${map.size}", SomeData("data ${map.size}"))
    }
}

fun testData() {
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

    StateSample.cacheMap.access {
        println("Total map size: ${it.size}, time: $time")
    }
}

private fun push1000(insertMap: Map<String, SomeData>) {
    StateSample.cacheMap.access { map ->
        map.putAll(insertMap)
    }
}

internal fun make1000():Map<String, SomeData>{
    val startCount = StateSample.cacheMap.access { map ->
        map.size
    }
    val map = mutableMapOf<String, SomeData>()
    repeat(1000) { baseCount ->
        val count = startCount + baseCount
        map.put("i $count", SomeData("data $count"))
    }
    return map
}

