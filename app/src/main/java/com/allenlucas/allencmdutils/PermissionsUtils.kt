package com.allenlucas.allencmdutils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * 运行指令工具类
 */
class PermissionsUtils : LifecycleObserver {

    private val permissionUtils by lazy { RequestPermissionsUtils.instance }

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

}