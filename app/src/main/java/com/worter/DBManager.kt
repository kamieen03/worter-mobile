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

    fun printDeviceWorterDbFiles() {
        println("Worter db on device contents:")
        for (fName in context.fileList()) {
            println(fName)
        }
    }

    private fun fileAlreadyStored(fName: String): Boolean {
        val files = context.fileList()
        return fName in files
    }

}