package com.example.socialhub

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.example.socialhub.ui.SocialHubTheme
import com.example.socialhub.ui.PanelRoot

class OverlayService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var handle: HandleView
    private lateinit var panel: ComposeView
    private lateinit var owners: ServiceComposeOwners

    private var handleParams: WindowManager.LayoutParams = baseHandleParams()
    private var panelParams: WindowManager.LayoutParams = basePanelParams()
    private var panelOpen = false
    private var panelWidthPx = 0

    private val screenW: Int get() = Resources.getSystem().displayMetrics.widthPixels
    private val screenH: Int get() = Resources.getSystem().displayMetrics.heightPixels

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        panelWidthPx = ((screenW * 0.62).toInt()).coerceAtLeast(dp(320))

        startForeground(NOTIF_ID, buildNotification())
        buildHandle()
        buildPanel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    // ---------- Handle ----------

    private fun baseHandleParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.RIGHT or Gravity.TOP
            x = 0
            y = (screenH / 2) - dp(42)
        }
    }

    private fun buildHandle() {
        handle = HandleView(this)
        handle.setOnTouchListener(HandleTouchListener())
        handleParams = baseHandleParams()
        try {
            wm.addView(handle, handleParams)
        } catch (e: Exception) {
            stopSelf()
            return
        }
        startPeek()
    }

    private inner class HandleTouchListener : View.OnTouchListener {
        private var downRawX = 0f
        private var downRawY = 0f
        private var startY = 0
        private var moved = false
        private var openedViaDrag = false

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downRawX = event.rawX
                    downRawY = event.rawY
                    startY = handleParams.y
                    moved = false
                    openedViaDrag = false
                    cancelPeek()
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - downRawX
                    val dy = event.rawY - downRawY
                    if (kotlin.math.abs(dy) > 10 || kotlin.math.abs(dx) > 10) moved = true
                    if (moved) {
                        val newY = (startY + dy).toInt()
                            .coerceIn(0, (screenH - handle.height).coerceAtLeast(0))
                        handleParams.y = newY
                        try { wm.updateViewLayout(handle, handleParams) } catch (_: Exception) {}
                    }
                    // drag outward (leftward on right edge) opens the panel
                    if (!panelOpen && !openedViaDrag && dx < -dp(36)) {
                        openedViaDrag = true
                        openPanel()
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        if (panelOpen) closePanel() else openPanel()
                    } else if (!openedViaDrag) {
                        snapToThird()
                    }
                    if (!panelOpen) startPeek()
                }
            }
            return true
        }
    }

    private fun snapToThird() {
        val h = handle.height.coerceAtLeast(1)
        val top = 0
        val mid = (screenH / 2) - (h / 2)
        val bottom = screenH - h
        val cur = handleParams.y
        val nearest = listOf(top, mid, bottom).minByOrNull { kotlin.math.abs(it - cur) } ?: mid
        val start = cur
        val anim = android.animation.ValueAnimator.ofInt(start, nearest).apply {
            duration = 220
            interpolator = android.view.animation.OvershootInterpolator(0.8f)
            addUpdateListener {
                handleParams.y = it.animatedValue as Int
                try { wm.updateViewLayout(handle, handleParams) } catch (_: Exception) {}
            }
        }
        anim.start()
    }

    // ---------- Panel ----------

    private fun basePanelParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            panelWidthPx,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.RIGHT or Gravity.TOP
            x = 0
            y = 0
        }
    }

    private fun buildPanel() {
        owners = ServiceComposeOwners()
        panel = ComposeView(this).apply {
            ViewTreeLifecycleOwner.set(this, owners)
            ViewTreeViewModelStoreOwner.set(this, owners)
            ViewTreeSavedStateRegistryOwner.set(this, owners)
            setContent {
                SocialHubTheme {
                    PanelRoot(onClose = { closePanel() })
                }
            }
            translationX = panelWidthPx.toFloat()
        }
        panelParams = basePanelParams()
        try {
            wm.addView(panel, panelParams)
            owners.create(null)
            owners.start()
            owners.resume()
        } catch (e: Exception) {
            stopSelf()
        }
    }

    fun openPanel() {
        if (panelOpen) return
        panelOpen = true
        cancelPeek()
        panel.animate().translationX(0f).setDuration(280)
            .setInterpolator(android.view.animation.OvershootInterpolator(0.6f)).start()
    }

    fun closePanel() {
        if (!panelOpen) return
        panelOpen = false
        panel.animate().translationX(panelWidthPx.toFloat()).setDuration(240).start()
        startPeek()
    }

    // ---------- Peek luxury ----------

    private var peekAnim: android.animation.ObjectAnimator? = null

    private fun startPeek() {
        if (peekAnim != null) return
        peekAnim = android.animation.ObjectAnimator.ofFloat(handle, "translationX", 0f, -dp(10).toFloat(), 0f).apply {
            duration = 1100
            startDelay = 3500
            repeatMode = android.animation.ValueAnimator.REVERSE
            repeatCount = android.animation.ValueAnimator.INFINITE
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun cancelPeek() {
        peekAnim?.cancel()
        peekAnim = null
        handle.translationX = 0f
    }

    // ---------- Notification ----------

    private fun buildNotification(): Notification {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.overlay_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.overlay_channel_desc)
                setShowBadge(false)
            }
            nm.createNotificationChannel(channel)
        }
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.overlay_notification_title))
            .setContentText(getString(R.string.overlay_notification_text))
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setColor(Color.parseColor("#8B5CF6"))
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelPeek()
        try { wm.removeView(handle) } catch (_: Exception) {}
        try {
            owners.pause()
            owners.stop()
            owners.destroy()
            wm.removeView(panel)
        } catch (_: Exception) {}
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun dp(v: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics
    ).toInt()

    private companion object {
        const val NOTIF_ID = 4201
        const val CHANNEL_ID = "social_hub_overlay"
    }
}
