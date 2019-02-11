package ff.driven.self.notch

import android.graphics.Rect
import android.view.Window

/**
 * Created by feifan on 2018/6/15.
 * Contacts me:404619986@qq.com
 */
internal interface INotchScreenSupport {
    fun hasNotchInScreen(window: Window): Boolean

    /**
     * 硬件上屏幕是否存在凹口，[.hasNotchInScreen]用判断当前状态是否存在凹口，
     * Android p在调用了[.setWindowLayoutBlockNotch]认为当前状态不存在凹口
     *
     * @param window window
     * @return false, 不存在凹口，true，存在凹口
     */
    fun hasNotchInScreenHardware(window: Window): Boolean {
        return hasNotchInScreen(window)
    }

    fun getNotchSize(window: Window): List<Rect>

    /**
     * 获取硬件上屏幕凹口size，[.getNotchSize],获取当前状态凹口size
     * Android p在调用了[.setWindowLayoutBlockNotch]认为当前状态不存在凹口
     * 所以获取不到size
     *
     * @param window window
     * @return Rect
     */
    fun getNotchSizeHardware(window: Window): List<Rect> {
        return getNotchSize(window)
    }

    fun setWindowLayoutAroundNotch(window: Window)

    fun setWindowLayoutBlockNotch(window: Window)
}
