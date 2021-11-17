package com.allenlucas.allencmdutils

import android.util.Log
import rikka.shizuku.Shizuku
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * 运行指令
 */
class CMDUtils private constructor() {

    companion object {
        val instance by lazy { CMDUtils() }
    }

    fun shiZuKuCmd(cmd: String, callback: (InputStream) -> Unit = {}) {
        Shizuku.newProcess(cmd.split(" ").toTypedArray(), null, "/").apply {
            callback.invoke(inputStream)
        }
    }

    fun rootCMD(cmdList: ArrayList<String>) {
        try {
            val p = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(p.outputStream)
            for (tmpCmd in cmdList) {
                os.writeBytes(
                    """
                $tmpCmd

                """.trimIndent()
                )
            }
            os.writeBytes("exit\n")
            os.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("lal-cmd", "运行指令失败")
            Log.e("lal-cmd", "失败原因${e.message}")
        }

    }
}