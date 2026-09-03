package com.example.socialhub.data

data class Platform(
    val id: String,
    val name: String,
    val color: String,
    val glyph: String
)

data class FeedItem(
    val id: String,
    val platform: String,
    val type: String,
    val author: String,
    val handle: String,
    val avatarColor: String,
    val content: String,
    val image: String? = null,
    val time: String,
    val stats: ItemStats = ItemStats(),
    val link: String
)

data class ItemStats(
    val likes: Int? = null,
    val comments: Int? = null,
    val shares: Int? = null
)

val platforms = listOf(
    Platform("all", "All", "#ffffff", "✦"),
    Platform("x", "X", "#1d9bf0", "𝕏"),
    Platform("instagram", "Instagram", "#E1306C", "◎"),
    Platform("facebook", "Facebook", "#1877F2", "f"),
    Platform("tiktok", "TikTok", "#25F4EE", "♪"),
    Platform("youtube", "YouTube", "#FF0000", "▶"),
    Platform("linkedin", "LinkedIn", "#0A66C2", "in"),
    Platform("reddit", "Reddit", "#FF4500", "r"),
    Platform("snapchat", "Snapchat", "#FFFC00", "👻")
)

val feedItems = listOf(
    FeedItem("x-1", "x", "post", "Marques Brownlee", "@MKBHD", "#1d9bf0", "Just wrapped a full week with the new foldable. The hinge is genuinely the best I've felt on any device in this form factor. Full review dropping Sunday.", "https://picsum.photos/seed/foldable/600/360", "4m", ItemStats(12840, 532, 1190), "https://x.com"),
    FeedItem("ig-1", "instagram", "notification", "sora.designs", "@sora.designs", "#E1306C", "started following you and 2 others liked your recent story.", time = "9m", link = "https://instagram.com"),
    FeedItem("yt-1", "youtube", "notification", "Fireship", "@Fireship", "#FF0000", "uploaded: \"100 seconds of the new Android 16 build\". Tap to watch.", time = "23m", stats = ItemStats(likes = 42100), link = "https://youtube.com"),
    FeedItem("tt-1", "tiktok", "post", "ava.codes", "@ava.codes", "#25F4EE", "POV: you finally ship the feature at 3am and the build passes on the first try 🫠", "https://picsum.photos/seed/devlife/600/420", "41m", ItemStats(88200, 1240, 5600), "https://tiktok.com"),
    FeedItem("fb-1", "facebook", "notification", "Jordan Lee", "Jordan Lee", "#1877F2", "mentioned you in a comment: \"this is exactly what you built last month!\"", time = "1h", link = "https://facebook.com"),
    FeedItem("li-1", "linkedin", "post", "Priya Nair", "Senior PM @ Vercel", "#0A66C2", "Hot take: the best onboarding flows don't ask for anything upfront. We cut drop-off by 38%.", time = "2h", stats = ItemStats(2310, 184, 96), link = "https://linkedin.com"),
    FeedItem("rd-1", "reddit", "notification", "r/androiddev", "r/androiddev", "#FF4500", "Your post reached the top of the subreddit this week. 🎉", time = "3h", stats = ItemStats(likes = 980), link = "https://reddit.com"),
    FeedItem("ig-2", "instagram", "post", "minimal.motion", "@minimal.motion", "#C13584", "New reel is live — a 60-second study of how light bends through frosted glass. 🎧", "https://picsum.photos/seed/glass/600/450", "5h", ItemStats(15400, 320, 410), "https://instagram.com"),
    FeedItem("x-2", "x", "notification", "Dan Abramov", "@dan_abramov2", "#1d9bf0", "liked your reply about pointer events and capture. No further comment, which means you were right. 😌", time = "6h", link = "https://x.com"),
    FeedItem("sc-1", "snapchat", "notification", "maddie", "@maddie", "#FFFC00", "sent you a Snap! 👻 (tap to view — expires in 23h)", time = "7h", link = "https://snapchat.com"),
    FeedItem("yt-2", "youtube", "post", "Veritasium", "@veritasium", "#FF0000", "How do touchscreens actually know it's your finger? The answer involves quantum tunneling.", "https://picsum.photos/seed/touch/600/340", "9h", ItemStats(91200, 3100, 2200), "https://youtube.com"),
    FeedItem("tt-2", "tiktok", "notification", "trends", "@tiktok", "#25F4EE", "Your video is trending in #UIUX — 12.4k new views in the last hour.", time = "11h", link = "https://tiktok.com"),
    FeedItem("fb-2", "facebook", "post", "Android Devs United", "Group · 14k members", "#1877F2", "Weekly roundup: 5 new open-source libraries for building glassmorphic UIs on Android.", time = "14h", stats = ItemStats(640, 88, 47), link = "https://facebook.com"),
    FeedItem("li-2", "linkedin", "notification", "Recruiter · Google", "Talent Acq.", "#0A66C2", "reached out: \"Loved your work on gesture-driven overlays — would love to chat.\"", time = "1d", link = "https://linkedin.com"),
    FeedItem("rd-2", "reddit", "post", "u/glassmaker", "r/AndroidDev", "#FF4500", "After 3 months I finally nailed the draggable edge-handle that doesn't conflict with system back gestures.", time = "1d", stats = ItemStats(4200, 510, 230), link = "https://reddit.com"),
    FeedItem("x-3", "x", "post", "Natalie", "@natbuilds", "#1d9bf0", "Reminder: half-screen overlays should never block the primary action behind them.", time = "2d", stats = ItemStats(7600, 410, 1800), link = "https://x.com")
)
