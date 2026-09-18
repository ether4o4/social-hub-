package com.example.socialhub

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Provides Lifecycle / ViewModelStore / SavedState owners so a ComposeView can be hosted
 * inside a Service attached to WindowManager.
 */
class ServiceComposeOwners : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStore = ViewModelStore()
    private val savedStateController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = viewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateController.savedStateRegistry

    fun create(savedState: Bundle? = null) {
        savedStateController.performRestore(savedState)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun start() = lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    fun resume() = lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun pause() = lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stop() = lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

    fun destroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }
}
