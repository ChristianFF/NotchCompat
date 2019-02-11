/*
 * Copyright (c) 2015-2018 BiliBili Inc.
 */

package ff.driven.self.notch

import android.graphics.Rect
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.view.Window

internal class MeizuNotchScreenSupport : INotchScreenSupport {

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun hasNotchInScreen(window: Window): Boolean {
        // 判断刘海设备
        var fringeDevice = false
        try {
            val clazz = Class.forName("flyme.config.FlymeFeature")
            val field = clazz.getDeclaredField("IS_FRINGE_DEVICE")
            fringeDevice = field.get(null) as Boolean
        } catch (e: Exception) {
        }

        if (fringeDevice) {
            // 判断隐藏刘海开关(默认关)
            val isFringeHidden = Settings.Global.getInt(window.context.contentResolver, "mz_fringe_hide", 0) == 1
            return !isFringeHidden
        }

        return false
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun getNotchSize(window: Window): List<Rect> {
        val result = ArrayList<Rect>()
        val context = window.context
        val rect = Rect()
        val displayMetrics = context.resources.displayMetrics

        // 获取刘海⾼度（51px）
        var notchHeight = 0
        val fhid = context.resources.getIdentifier("fringe_height", "dimen", "android")
        if (fhid > 0) {
            notchHeight = context.resources.getDimensionPixelSize(fhid)
        }
        // 获取刘海宽度（534px）
        var notchWidth = 0
        val fwid = context.resources.getIdentifier("fringe_width", "dimen", "android")
        if (fwid > 0) {
            notchWidth = context.resources.getDimensionPixelSize(fwid)
        }

        rect.left = (displayMetrics.widthPixels - notchWidth) / 2
        rect.right = rect.left + notchWidth
        rect.top = 0
        rect.bottom = notchHeight
        result.add(rect)

        return result
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun setWindowLayoutAroundNotch(window: Window) {
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility or 0x00000080
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun setWindowLayoutBlockNotch(window: Window) {
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility and 0x00000080.inv()
        window.decorView.systemUiVisibility = systemUiVisibility
    }
}
