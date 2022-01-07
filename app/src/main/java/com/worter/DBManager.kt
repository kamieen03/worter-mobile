package com.worter

import android.content.Context

class DBManager(private val context: Context) {
    fun copyDbFromAssetsToDevice() {
        for (fName in context.assets.list("worter_db")!!)
        {
            if (fileAlreadyStored(fName)) {
                continue
            }
            val oStream = context.openFileOutput(fName, Context.MODE_PRIVATE)
            context.assets.open("worter_db/$fName").copyTo(oStream)
            println("Copied $fName to device")
        }
    }

    fun getFileNames(): List<String> {
       val list = context.fileList().map { trimJson(it) }
       val numericFiles =  list.filter { it[0].isDigit() }.sortedBy {it.toInt() }
       val nonNumericFiles = list.filter { !it[0].isDigit() }
       return numericFiles + nonNumericFiles
    }

    fun printDeviceWorterDbFiles() {
        println("Worter db on device contents:")
        for (fName in getFileNames()) {
            println(fName)
        }
    }

    private fun fileAlreadyStored(fName: String): Boolean {
        val files = context.fileList()
        return fName in files
    }
}


fun trimJson(s: String) : String {
    return s.dropLast(5)
}

fun addJson(s: String) : String {
    return "$s.json"
}
