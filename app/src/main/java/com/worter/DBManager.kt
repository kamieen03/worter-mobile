package com.worter

import android.content.Context
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class RecordModel(val poleng_list: Array<String>,
                       val ger_list: Array<String>,
                       var hardness: Int)

    // serializing lists
    //val jsonList = JSON.stringify(MyModel.serializer().list, listOf(MyModel(42)))
    //println(jsonList) // [{"a": 42, "b": "42"}]

class DBManager(private val context: Context) {

    fun decodeJsonFile(fileName: String): List<RecordModel> {
        val jsonString = File(context.filesDir, fileName).readText()
        val jsonArray = Json.parseToJsonElement(jsonString) as JsonArray
        return jsonArray.map { Json.decodeFromJsonElement(it) }
    }

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
        decodeJsonFile("1.json")
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
