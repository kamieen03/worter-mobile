package com.worter

import android.content.Context
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import java.lang.ref.WeakReference
import java.io.Serializable as javaSerializable

@Serializable
data class RecordModel(val poleng_list: Array<String>,
                       val ger_list: Array<String>,
                       var hardness: Int,
                       var show_as_red_in_word_list: Boolean = false) : javaSerializable


object DBManager {
    private var db = mutableMapOf<String, List<RecordModel>>()
    private var readTexts = mutableMapOf<String, Boolean>()
    private lateinit var context: WeakReference<Context>
    private lateinit var textsDir: File

    fun setContext(ctx: Context) {
        context = WeakReference(ctx)
        textsDir = File(context.get()!!.filesDir, "text")
        copyDbFromAssetsToDevice()
        readDbFromDevice()
        readReadTexts()
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

    fun getWordsFileNames(): List<String> {
        val worterFileList = context.get()!!.fileList()
                                            .filter{it != "text"}
                                            .map { trimJson(it) }
        val numericFiles =  worterFileList.filter { it[0].isDigit() }.sortedBy {it.toInt() }
        val nonNumericFiles = worterFileList.filter { !it[0].isDigit() }
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
        for (fName in getWordsFileNames()) {
            if (db.contains(fName)) {
                continue
            }
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


    @Serializable
    data class ReadTextsModel(val map: Map<String, Boolean>)

    fun getTextsFileNames(): List<String> {
        return textsDir.list()
                       .filter{it != "read_texts.json"}
                       .map{trimTxt(it)}
                       .sortedBy { it.toInt() }
    }

    private fun readReadTexts() {
        if (!textsDir.list().contains("read_texts.json")) {
            saveReadTexts()
        }
        readTexts = run {
            val jsonString = File(textsDir, "read_texts.json").readText()
            Json.decodeFromString<ReadTextsModel>(jsonString).map.toMutableMap()
        }
        val textsList = getTextsFileNames()
        for (textName in textsList) {
            if (!readTexts.containsKey(textName)) {
                readTexts[textName] = false
            }
        }
    }

    fun getText(textName: String) : String {
        return File(textsDir, addTxt(textName)).readText()
    }

    fun isTextRead(textName: String) : Boolean {
        return readTexts[textName]!!
    }

    fun flipTextRead(textName: String) {
        readTexts[textName] = !readTexts[textName]!!
    }

    fun saveReadTexts() {
        val jsonString = Json.encodeToString(ReadTextsModel(readTexts))
        File(textsDir, "read_texts.json").writeText(jsonString)
    }
}

fun trimJson(s: String) : String {
    return s.dropLast(5)
}

fun addJson(s: String) : String {
    return "$s.json"
}

fun trimTxt(s: String) : String {
    return s.dropLast(4)
}

fun addTxt(s: String) : String {
    return "$s.txt"
}

