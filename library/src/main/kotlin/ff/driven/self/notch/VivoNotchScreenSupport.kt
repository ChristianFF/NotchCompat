package ff.driven.self.notch

import android.graphics.Rect
import android.support.annotation.RequiresApi
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import java.util.*

/**
 * Created by feifan on 2018/5/15.
 * Contacts me:404619986@qq.com
 */
internal class VivoNotchScreenSupport : INotchScreenSupport {

    @RequiresApi(api = 26)
    override fun hasNotchInScreen(window: Window): Boolean {
        return try {
            if (vivoFtFeature == null) {
                val cl = window.context.classLoader
                vivoFtFeature = cl.loadClass("android.util.FtFeature")
            }
            val get = vivoFtFeature!!.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
            get.invoke(vivoFtFeature, VIVO_HAS_NOTCH_DISPLAY) as Boolean
        } catch (e: Exception) {
            false
        }
    }

    //vivo刘海大小固定
    @RequiresApi(api = 26)
    override fun getNotchSize(window: Window): List<Rect> {
        val result = ArrayList<Rect>()
        val rect = Rect()
        val displayMetrics = window.context.resources.displayMetrics
        val notchWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, displayMetrics).toInt()
        val notchHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27f, displayMetrics).toInt()
        rect.left = (displayMetrics.widthPixels - notchWidth) / 2
        rect.right = rect.left + notchWidth
        rect.top = 0
        rect.bottom = notchHeight
        result.add(rect)
        return result
    }

    @RequiresApi(api = 26)
    override fun setWindowLayoutAroundNotch(window: Window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    @RequiresApi(api = 26)
    override fun setWindowLayoutBlockNotch(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.inv()
        systemUiVisibility = systemUiVisibility and View.SYSTEM_UI_FLAG_LAYOUT_STABLE.inv()
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    companion object {
        private var vivoFtFeature: Class<*>? = null
        //表示是否有凹槽
        private const val VIVO_HAS_NOTCH_DISPLAY = 0x00000020
    }
}
