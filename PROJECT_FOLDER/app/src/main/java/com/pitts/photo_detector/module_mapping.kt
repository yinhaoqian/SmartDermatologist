package com.pitts.photo_detector

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class module_mapping {

    companion object {
        private var map: HashMap<Int, Pair<String, String>> = HashMap()
        var isReady: Boolean = false
        fun init(context: Context, csvFileName: String) {
            var bufferedReader: BufferedReader? = null
            try {
                bufferedReader = BufferedReader(InputStreamReader(context.assets.open(csvFileName)))
                bufferedReader.forEachLine {
                    it.split(",").run {
                        if ((this.get(0).all { it.isDigit() }) && (this.size == 3)) {
                            map.put(Integer.valueOf(this.get(0)), Pair(this.get(1), this.get(2)))
                            Log.d(
                                "Q_MODULE_MAPPING",
                                "INIT(): ENTRIES DETECTED: ${this[0]} , ${this[1]} , ${this[2]}"
                            )
                        } else {
                            throw RuntimeException("INDEX MAPPING FORMAT ERROR")
                        }
                    }
                }
                isReady = true
            } catch (ioexception: IOException) {
                ioexception.message?.let { Log.e("MODULE_MAPPING", it) }
                isReady = false
            } finally {
                bufferedReader?.close()
            }
        }

        fun getPairFromIndex(index: Int): Pair<String, String> {
            if(!isReady){
                throw RuntimeException("MODULE_MAPPING NOT READY")
            }
            Log.d("Q_MODULE_MAPPING", "GETPAIRFROMINDEX(): QUERYING INDEX $index")
            val queriedStringPair: Pair<String, String>? = map[index]
            return if (queriedStringPair == null) {
                Log.d("Q_MODULE_MAPPING", "GETPAIRFROMINDEX(): QUERY FAILED")
                Pair("ERR_ABBR", "ERR_TITL")
            } else {
                Log.d(
                    "Q_MODULE_MAPPING",
                    "GETPAIRFROMINDEX(): QUERY OBTAINED ${queriedStringPair.first} & ${queriedStringPair.second}"
                )
                Pair(queriedStringPair.first, queriedStringPair.second)
            }
        }
    }
}