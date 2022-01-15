package com.worter

import android.content.Context
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.File
import java.lang.ref.WeakReference
import java.io.Serializable as javaSerializable

@Serializable
data class RecordModel(val poleng_list: Array<String>,
                       val ger_list: Array<String>,
                       var hardness: Int) : javaSerializable


object DBManager {
    private var db = mutableMapOf<String, List<RecordModel>>()
    private lateinit var context: WeakReference<Context>

    fun setContext(ctx: Context) {
        context = WeakReference(ctx)
        copyDbFromAssetsToDevice()
        readDbFromDevice()
    }

    fun getFile(fileName: String) : List<RecordModel>? {
        return db[fileName]
    }

    fun saveDb() {
        for ((fName, recordList) in db) {
            val jsonList = Json.encodeToString(recordList)
            File(context.get()!!.filesDir, addJson(fName)).writeText(jsonList)
        }
    }

    fun getFileNames(): List<String> {
        val list = context.get()!!.fileList().map { trimJson(it) }
        val numericFiles =  list.filter { it[0].isDigit() }.sortedBy {it.toInt() }
        val nonNumericFiles = list.filter { !it[0].isDigit() }
        return numericFiles + nonNumericFiles
    }

    fun getHardness(fName: String) : Double {
        return db[fName]!!.map { it.hardness }.average()
    }

    private fun copyDbFromAssetsToDevice() {
        for (fName in context.get()!!.assets.list("worter_db")!!)
        {
            if (fileAlreadyStored(fName)) {
                continue
            }
            val oStream = context.get()!!.openFileOutput(fName, Context.MODE_PRIVATE)
            context.get()!!.assets.open("worter_db/$fName").copyTo(oStream)
            println("Copied $fName to device")
        }
    }

    private fun readDbFromDevice() {
        for (fName in getFileNames()) {
            db[fName] = decodeJsonFile(addJson(fName))
        }
    }

    private fun decodeJsonFile(fileName: String): List<RecordModel> {
        val jsonString = File(context.get()!!.filesDir, fileName).readText()
        val jsonArray = Json.parseToJsonElement(jsonString) as JsonArray
        return jsonArray.map { Json.decodeFromJsonElement(it) }
    }

    private fun fileAlreadyStored(fName: String): Boolean {
        val files = context.get()!!.fileList()
        return fName in files
    }
}

fun trimJson(s: String) : String {
    return s.dropLast(5)
}

fun addJson(s: String) : String {
    return "$s.json"
}
