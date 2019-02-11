package ff.driven.self.notch

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.Window
import java.util.*

/**
 * Created by feifan on 2018/6/12.
 * Contacts me:404619986@qq.com
 */
internal class MiNotchScreenSupport : INotchScreenSupport {

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun hasNotchInScreen(window: Window): Boolean {
        return try {
            "1" == SystemProperties["ro.miui.notch", null]
        } catch (ignored: Exception) {
            false
        }
    }

    //小米的状态栏高度会略高于刘海屏的高度，因此通过获取状态栏的高度来间接避开刘海屏,宽度未知，直接返回了屏幕宽度
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun getNotchSize(window: Window): List<Rect> {
        val result = ArrayList<Rect>()

        val context = window.context
        val rect = Rect()
        rect.top = 0
        rect.bottom = getStatusBarHeight(context)
        rect.left = 0
        rect.right = context.resources.displayMetrics.widthPixels
        result.add(rect)

        return result
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun setWindowLayoutAroundNotch(window: Window) {
        val flag = FLAG_NOTCH_IMMERSIVE or FLAG_NOTCH_PORTRAIT or FLAG_NOTCH_LANDSCAPE
        try {
            val method = Window::class.java.getMethod("addExtraFlags", Int::class.javaPrimitiveType)
            method.invoke(window, flag)
        } catch (ignored: Exception) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun setWindowLayoutBlockNotch(window: Window) {
        val flag = FLAG_NOTCH_IMMERSIVE or FLAG_NOTCH_PORTRAIT or FLAG_NOTCH_LANDSCAPE
        try {
            val method = Window::class.java.getMethod("clearExtraFlags", Int::class.javaPrimitiveType)
            method.invoke(window, flag)
        } catch (ignored: Exception) {
        }
    }

    private fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = context.resources.getDimensionPixelSize(resId)
        }
        return result
    }

    companion object {
        //绘制到刘海区域
        private const val FLAG_NOTCH_IMMERSIVE = 0x00000100
        //竖屏绘制到刘海区域
        private const val FLAG_NOTCH_PORTRAIT = 0x00000200
        //横屏绘制刘海区域
        private const val FLAG_NOTCH_LANDSCAPE = 0x00000400
    }
}
