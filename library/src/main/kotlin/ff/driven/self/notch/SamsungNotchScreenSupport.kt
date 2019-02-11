/*
 * Copyright (c) 2015-2018 BiliBili Inc.
 */

package ff.driven.self.notch

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager

internal class SamsungNotchScreenSupport : INotchScreenSupport {

    private var mWindowInsetsWrapper: WindowInsetsWrapper? = null

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun hasNotchInScreen(window: Window): Boolean {
        try {
            checkInit(window)
            return getNotchSize(window).isNotEmpty()
        } catch (ignore: Exception) {
        }
        return false
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun hasNotchInScreenHardware(window: Window): Boolean {
        return try {
            checkInit(window)
            val res = window.context.resources
            val resId = res.getIdentifier("config_mainBuiltInDisplayCutout", "string", "android")
            val spec = if (resId > 0) res.getString(resId) else null
            spec != null && !TextUtils.isEmpty(spec)
        } catch (ignore: Exception) {
            super.hasNotchInScreenHardware(window)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun getNotchSize(window: Window): List<Rect> {
        checkInit(window)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //setWindowLayoutBlockNotch之后 DisplayCutout 可能为null
            //获取当前屏幕 notch size 需要重新建立一个新的 WindowInsetsWrapper 来实时获取 屏幕状态
            val tempWrapper = WindowInsetsWrapper(window.decorView.rootWindowInsets)
            if (tempWrapper.displayCutoutWrapper != null) {
                return tempWrapper.displayCutoutWrapper.boundingRects
            }
        }
        return ArrayList()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun getNotchSizeHardware(window: Window): List<Rect> {
        checkInit(window)
        //获取硬件层面上的 notch size
        //利用 setWindowLayoutBlockNotch() 执行之前 获取的 notch size 然后保存起来，当做硬件层面上 notch size
        return if (mWindowInsetsWrapper != null && mWindowInsetsWrapper!!.displayCutoutWrapper != null) {
            mWindowInsetsWrapper!!.displayCutoutWrapper!!.boundingRects
        } else super.getNotchSizeHardware(window)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun setWindowLayoutAroundNotch(window: Window) {
        checkInit(window)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val lp = window.attributes
            try {
                val field = lp.javaClass.getField("layoutInDisplayCutoutMode")
                field.isAccessible = true
                field.setInt(lp, 1)//LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                window.attributes = lp
            } catch (ignore: Exception) {
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun setWindowLayoutBlockNotch(window: Window) {
        checkInit(window)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val lp = window.attributes
            try {
                val field = lp.javaClass.getField("layoutInDisplayCutoutMode")
                field.isAccessible = true
                field.setInt(lp, 2)//LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
                window.attributes = lp
            } catch (ignore: Exception) {
            }
        }
    }

    /**
     *
     * hasNotchInScreen()
     * getNotchSizeHardware()
     * getNotchSize()
     * setWindowLayoutAroundNotch()
     * setWindowLayoutBlockNotch()
     *
     * 调用以上方法时，必须调用当前方法
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun checkInit(window: Window) {
        if (mWindowInsetsWrapper != null && mWindowInsetsWrapper!!.displayCutoutWrapper != null) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            val windowInsets = decorView.rootWindowInsets ?: return
            mWindowInsetsWrapper = WindowInsetsWrapper(windowInsets)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    internal class WindowInsetsWrapper constructor(mInner: WindowInsets) {
        /**
         * Returns the display cutout if there is one.
         *
         * @return the display cutout or null if there is none
         */
        val displayCutoutWrapper: DisplayCutoutWrapper?

        init {
            displayCutoutWrapper = DisplayCutoutWrapper(mInner)
        }
    }

    @SuppressLint("PrivateApi")
    @RequiresApi(api = Build.VERSION_CODES.O)
    internal class DisplayCutoutWrapper constructor(windowInsets: WindowInsets) {
        private val mSafeInsets = Rect()
        private val mBoundingRects = ArrayList<Rect>()

        /** Returns the inset from the top which avoids the display cutout inpixels.  */
        val safeInsetTop: Int
            get() = mSafeInsets.top

        /** Returns the inset from the bottom which avoids the display cutoutin pixels.  */
        val safeInsetBottom: Int
            get() = mSafeInsets.bottom

        /** Returns the inset from the left which avoids the display cutout inpixels.  */
        val safeInsetLeft: Int
            get() = mSafeInsets.left

        /** Returns the inset from the right which avoids the display cutoutin pixels.  */
        val safeInsetRight: Int
            get() = mSafeInsets.right

        /**
         * Returns a list of `Rect`s, each of which is the bounding
         * rectangle for a non-functional
         * area on the display.
         *
         * There will be at most one non-functional area per short edge of the
         * device, and none on
         * the long edges.
         *
         * @return a list of bounding `Rect`s, one for each display cutout
         * area.
         */
        val boundingRects: List<Rect>
            get() = mBoundingRects

        init {

            try {
                val method = WindowInsets::class.java.getDeclaredMethod(GET_DISPLAY_CUTOUT)
                val displayCutoutInstance = method.invoke(windowInsets)
                val cls = displayCutoutInstance.javaClass
                val top = cls.getDeclaredMethod(GET_SAFE_INSET_TOP).invoke(displayCutoutInstance) as Int
                val bottom = cls.getDeclaredMethod(GET_SAFE_INSET_BOTTOM).invoke(displayCutoutInstance) as Int
                val left = cls.getDeclaredMethod(GET_SAFE_INSET_LEFT).invoke(displayCutoutInstance) as Int
                val right = cls.getDeclaredMethod(GET_SAFE_INSET_RIGHT).invoke(displayCutoutInstance) as Int
                mSafeInsets.set(left, top, right, bottom)
                mBoundingRects.add(mSafeInsets)
            } catch (e: Exception) {
                Log.e(TAG, "DisplayCutoutWrapper init exception: " + e.message)
            }
        }

        companion object {
            private const val TAG = "SamsungNotch"
            private const val GET_DISPLAY_CUTOUT = "getDisplayCutout"
            private const val GET_SAFE_INSET_TOP = "getSafeInsetTop"
            private const val GET_SAFE_INSET_BOTTOM = "getSafeInsetBottom"
            private const val GET_SAFE_INSET_LEFT = "getSafeInsetLeft"
            private const val GET_SAFE_INSET_RIGHT = "getSafeInsetRight"
        }
    }
}


