package com.allenlucas.allencmdutils

import android.content.pm.PackageManager
import android.util.Log
import rikka.shizuku.Shizuku

/**
 * 请求权限工具类
 */
class RequestPermissionsUtils private constructor() {

    companion object {
        val instance by lazy { RequestPermissionsUtils() }
    }

    private var hasListener = false

    var callback: ((Int, Boolean) -> Unit)? = null

    private val binderCallback by lazy {
        Shizuku.OnBinderReceivedListener {
            log(if (rikka.shizuku.Shizuku.isPreV11()) "v11 is not supported" else "binder received")
        }
    }

    private val bindDeadCallback by lazy {
        Shizuku.OnBinderDeadListener { log("binder dead") }
    }

    private fun log(msg: String) {
        Log.e("lal-cmd", msg)
    }

    // 添加申请权限回调
    fun addListener() {
        if (hasListener) return
        hasListener = true
        Shizuku.addBinderReceivedListener(binderCallback)
        Shizuku.addBinderDeadListener(bindDeadCallback)
        Shizuku.addRequestPermissionResultListener { requestCode, grantResult ->
            onRequestPermissionsResult(requestCode, grantResult)
        }
    }

    // 移除申请权限回调
    fun removeListener() {
        if (!hasListener) return
        hasListener = false
        Shizuku.removeBinderReceivedListener(binderCallback)
        Shizuku.removeBinderDeadListener(bindDeadCallback)
        Shizuku.removeRequestPermissionResultListener { requestCode, grantResult ->
            onRequestPermissionsResult(requestCode, grantResult)
        }
    }

    /**
     * 检查权限
     */
    fun checkPermission(code: Int) = when {
        // pre-v11 不支持
        Shizuku.isPreV11() -> false
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED -> true
        // 用户选择了不再询问
        Shizuku.shouldShowRequestPermissionRationale() -> false
        else -> {
            Shizuku.requestPermission(code)
            false
        }
    }

    private fun onRequestPermissionsResult(requestCode: Int, grantResult: Int) {
        callback?.invoke(requestCode, grantResult == PackageManager.PERMISSION_GRANTED)
    }
}