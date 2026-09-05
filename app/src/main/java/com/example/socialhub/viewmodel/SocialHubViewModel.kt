package com.example.socialhub.viewmodel

import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.FeedItem
import com.example.socialhub.data.ItemStats
import com.example.socialhub.data.feedItems
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class SocialHubViewModel : ViewModel() {
    var items = mutableStateListOf<FeedItem>()
        private set
    private var _refreshMode by mutableStateOf("live")
    val refreshMode: String
        get() = _refreshMode
    var refreshing by mutableStateOf(false)
        private set
    var lastUpdated by mutableLongStateOf(System.currentTimeMillis())
        private set
    var showSettings by mutableStateOf(false)
        private set
    var selectedPlatforms = mutableStateListOf<String>()
        private set
    var showFilter by mutableStateOf(false)
        private set
    var expandedId by mutableStateOf<String?>(null)
        private set

    private var _sidebarOpen by mutableStateOf(false)
    var sidebarOpen: Boolean
        get() = _sidebarOpen
        set(value) {
            if (_sidebarOpen == value) return
            _sidebarOpen = value
            if (value && refreshMode == "on_open") refresh(2)
            if (!value) closeDropdowns()
        }

    private var liveJob: Job? = null
    private var hourlyJob: Job? = null
    private var counter = 0

    private val templates = listOf(
        FeedItem(id = "t1", platform = "x", type = "post", author = "Tech Daily", handle = "@techdaily", avatarColor = "#1d9bf0", content = "Breaking: a new Android 16 beta just dropped with on-device AI upgrades.", time = "now", stats = ItemStats(320, 41, 18), link = "https://x.com"),
        FeedItem(id = "t2", platform = "instagram", type = "post", author = "lens.life", handle = "@lens.life", avatarColor = "#E1306C", content = "Posted a new story — a new set of travel photos is live.", time = "now", stats = ItemStats(1240, 33, 12), link = "https://instagram.com"),
        FeedItem(id = "t3", platform = "tiktok", type = "post", author = "quickbites", handle = "@quickbites", avatarColor = "#25F4EE", content = "60-second recipe just went viral — 200k views in an hour. 🍳", time = "now", stats = ItemStats(22100, 540, 1900), link = "https://tiktok.com"),
        FeedItem(id = "t4", platform = "youtube", type = "post", author = "Daily Uploads", handle = "@dailyuploads", avatarColor = "#FF0000", content = "New video: \"Build a glassmorphic overlay in 10 minutes\".", time = "now", stats = ItemStats(5400, 210, 90), link = "https://youtube.com"),
        FeedItem(id = "t5", platform = "facebook", type = "post", author = "Local Events", handle = "Local Events", avatarColor = "#1877F2", content = "New event tonight — \"Indie Devs Meetup\" has 42 going.", time = "now", stats = ItemStats(88, 12, 5), link = "https://facebook.com"),
        FeedItem(id = "t6", platform = "linkedin", type = "post", author = "Hiring Now", handle = "Talent Digest", avatarColor = "#0A66C2", content = "3 new roles matching your profile were just posted.", time = "now", stats = ItemStats(140, 9, 7), link = "https://linkedin.com"),
        FeedItem(id = "t7", platform = "reddit", type = "post", author = "r/androiddev", handle = "r/androiddev", avatarColor = "#FF4500", content = "Trending: \"How I cut my app startup time in half\".", time = "now", stats = ItemStats(980, 120, 40), link = "https://reddit.com"),
        FeedItem(id = "t8", platform = "x", type = "notification", author = "you", handle = "@you", avatarColor = "#1d9bf0", content = "You were mentioned in a thread about edge-gesture UI patterns.", time = "now", link = "https://x.com")
    )

    init {
        items.addAll(feedItems)
        startLiveStream()
    }

    fun setRefreshMode(mode: String) {
        if (mode !in setOf("live", "on_open", "manual", "hourly")) return
        _refreshMode = mode
        liveJob?.cancel(); liveJob = null
        hourlyJob?.cancel(); hourlyJob = null
        when (mode) {
            "live" -> startLiveStream()
            "hourly" -> startHourly()
            "on_open", "manual" -> Unit
        }
    }

    fun toggleSettings() {
        showSettings = !showSettings
        if (showSettings) showFilter = false
    }

    fun toggleFilter() {
        showFilter = !showFilter
        if (showFilter) showSettings = false
    }

    fun closeDropdowns() {
        showSettings = false
        showFilter = false
    }

    fun togglePlatform(platformId: String) {
        if (platformId == "all") {
            selectedPlatforms.clear()
            return
        }
        if (selectedPlatforms.contains(platformId)) selectedPlatforms.remove(platformId)
        else selectedPlatforms.add(platformId)
    }

    fun clearAll() {
        items.removeAll { it.type == "notification" }
        if (expandedId != null && items.none { it.id == expandedId }) expandedId = null
    }

    fun refresh(count: Int = 2) {
        if (refreshing) return
        viewModelScope.launch {
            refreshing = true
            try {
                delay(700)
                repeat(count.coerceAtLeast(0)) { addGeneratedItem() }
                trimItems()
                lastUpdated = System.currentTimeMillis()
            } finally {
                refreshing = false
            }
        }
    }

    fun toggleExpanded(id: String) {
        expandedId = if (expandedId == id) null else id
    }

    private fun addGeneratedItem() {
        val template = templates.random()
        counter++
        items.add(0, template.copy(
            id = "new-${System.currentTimeMillis()}-$counter",
            type = if (Random.nextFloat() > 0.4f) "post" else "notification"
        ))
    }

    private fun trimItems() {
        while (items.size > 60) items.removeAt(items.lastIndex)
    }

    private fun startLiveStream() {
        liveJob = viewModelScope.launch {
            while (true) {
                delay(6000)
                if (sidebarOpen && refreshMode == "live") {
                    addGeneratedItem()
                    trimItems()
                    lastUpdated = System.currentTimeMillis()
                }
            }
        }
    }

    private fun startHourly() {
        hourlyJob = viewModelScope.launch {
            while (true) {
                delay(60 * 60 * 1000L)
                if (refreshMode == "hourly") {
                    addGeneratedItem()
                    trimItems()
                    lastUpdated = System.currentTimeMillis()
                }
            }
        }
    }

    override fun onCleared() {
        liveJob?.cancel()
        hourlyJob?.cancel()
        super.onCleared()
    }
}
