package com.allenlucas.allencmdutils

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.io.InputStream

/**
 * 运行指令工具类
 */
class RunCmdUtils : LifecycleObserver {

    private val permissionUtils by lazy { RequestPermissionsUtils.instance }
    private val cmdUtils by lazy { CMDUtils.instance }
    private var index = 0  // 多段指令的下标

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        permissionUtils.addListener()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        permissionUtils.removeListener()
    }

    // 运行
    fun cmd(requestCode: Int, callback: () -> Unit) {
        if (!permissionUtils.checkPermission(requestCode)) {
            permissionUtils.callback = { code, permission ->
                if (permission && code == requestCode) {
                    callback.invoke()
                }
            }
            return
        }
        callback.invoke()
    }

    fun cmd(cmd: String, callback: (InputStream) -> Unit = {}) {
        cmdUtils.shiZuKuCmd(cmd, callback)
    }

    fun cmdList(cmdList: ArrayList<String>, callback: () -> Unit = {}) {
        // 运行完成
        if (index >= cmdList.size) {
            index = 0
            callback.invoke()
            Log.d("lal-cmd", "多段指令运行结束")
            return
        }
        cmdUtils.shiZuKuCmd(cmdList[index]) {
            index++
            cmdList(cmdList, callback)
        }
    }
}