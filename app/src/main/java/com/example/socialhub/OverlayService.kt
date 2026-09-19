package com.example.socialhub

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

/**
 * Foreground service that draws ONLY the draggable edge handle as a system overlay.
 * The handle persists over every other app (Samsung edge-panel behaviour).
 * Tapping or dragging the handle outward launches the floating translucent [PanelActivity],
 * which hosts the Compose feed + per-app login. The background app stays visible and
 * interactive because the panel is a floating window, not a fullscreen activity.
 */
class OverlayService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var handle: HandleView

    private var handleParams: WindowManager.LayoutParams = baseHandleParams()
    private var panelOpen = false

    private val screenW: Int get() = Resources.getSystem().displayMetrics.widthPixels
    private val screenH: Int get() = Resources.getSystem().displayMetrics.heightPixels

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }
        var foregroundOk = false
        try {
            wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            // Call startForeground FIRST, with the explicit type (Android 14 / targetSdk 35
            // requires the type to be passed for typed foreground services). Use ServiceCompat
            // so a failure here is caught instead of crashing the whole app process.
            val notif = buildNotification()
            foregroundOk = try {
                ServiceCompat.startForeground(
                    this,
                    NOTIF_ID,
                    notif,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
                true
            } catch (fse: Exception) {
                Log.e(TAG, "typed startForeground failed, falling back", fse)
                try { startForeground(NOTIF_ID, notif); true } catch (_: Exception) { false }
            }
            if (foregroundOk) {
                buildHandle()
            } else {
                // Could not become foreground — stop now so the system doesn't kill the app
                // for a missing startForeground call.
                stopSelf()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "OverlayService.onCreate failed", e)
            try { if (::wm.isInitialized && ::handle.isInitialized) wm.removeView(handle) } catch (_: Exception) {}
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return START_NOT_STICKY
        }
        // Re-assert foreground state on every start (Android 14 can redeliver).
        try {
            val notif = buildNotification()
            ServiceCompat.startForeground(
                this,
                NOTIF_ID,
                notif,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } catch (e: Exception) {
            Log.e(TAG, "onStartCommand foreground re-assert failed", e)
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Keep the overlay alive when the user swipes the launcher task away.
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
            interpolator = OvershootInterpolator(0.8f)
            addUpdateListener {
                handleParams.y = it.animatedValue as Int
                try { wm.updateViewLayout(handle, handleParams) } catch (_: Exception) {}
            }
        }
        anim.start()
    }

    // ---------- Panel launch ----------

    fun openPanel() {
        if (panelOpen) return
        panelOpen = true
        cancelPeek()
        val intent = Intent(this, PanelActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (_: Exception) {
            panelOpen = false
        }
    }

    fun closePanel() {
        // Panel closes itself (PanelActivity.finish()); just reset state.
        panelOpen = false
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
            interpolator = AccelerateDecelerateInterpolator()
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
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun dp(v: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics
    ).toInt()

    private companion object {
        const val NOTIF_ID = 4201
        const val CHANNEL_ID = "social_hub_overlay"
        const val TAG = "SocialHubOverlay"
    }
}
