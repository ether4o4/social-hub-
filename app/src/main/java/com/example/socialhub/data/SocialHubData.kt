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
    FeedItem(
        id = "x-1", platform = "x", type = "post",
        author = "Marques Brownlee", handle = "@MKBHD", avatarColor = "#1d9bf0",
        content = "Just wrapped a full week with the new foldable. The hinge is genuinely the best I've felt on any device in this form factor. Full review dropping Sunday.",
        image = "https://picsum.photos/seed/foldable/600/360",
        time = "4m", stats = ItemStats(12840, 532, 1190), link = "https://x.com"
    ),
    FeedItem(
        id = "ig-1", platform = "instagram", type = "notification",
        author = "sora.designs", handle = "@sora.designs", avatarColor = "#E1306C",
        content = "started following you and 2 others liked your recent story.",
        time = "9m", link = "https://instagram.com"
    ),
    FeedItem(
        id = "yt-1", platform = "youtube", type = "notification",
        author = "Fireship", handle = "@Fireship", avatarColor = "#FF0000",
        content = "uploaded: \"100 seconds of the new Android 16 build\". Tap to watch.",
        time = "23m", stats = ItemStats(likes = 42100), link = "https://youtube.com"
    ),
    FeedItem(
        id = "tt-1", platform = "tiktok", type = "post",
        author = "ava.codes", handle = "@ava.codes", avatarColor = "#25F4EE",
        content = "POV: you finally ship the feature at 3am and the build passes on the first try 🫠",
        image = "https://picsum.photos/seed/devlife/600/420",
        time = "41m", stats = ItemStats(88200, 1240, 5600), link = "https://tiktok.com"
    ),
    FeedItem(
        id = "fb-1", platform = "facebook", type = "notification",
        author = "Jordan Lee", handle = "Jordan Lee", avatarColor = "#1877F2",
        content = "mentioned you in a comment: \"this is exactly what you built last month!\"",
        time = "1h", link = "https://facebook.com"
    ),
    FeedItem(
        id = "li-1", platform = "linkedin", type = "post",
        author = "Priya Nair", handle = "Senior PM @ Vercel", avatarColor = "#0A66C2",
        content = "Hot take: the best onboarding flows don't ask for anything upfront. We cut drop-off by 38%.",
        time = "2h", stats = ItemStats(2310, 184, 96), link = "https://linkedin.com"
    ),
    FeedItem(
        id = "rd-1", platform = "reddit", type = "notification",
        author = "r/androiddev", handle = "r/androiddev", avatarColor = "#FF4500",
        content = "Your post reached the top of the subreddit this week. 🎉",
        time = "3h", stats = ItemStats(likes = 980), link = "https://reddit.com"
    ),
    FeedItem(
        id = "ig-2", platform = "instagram", type = "post",
        author = "minimal.motion", handle = "@minimal.motion", avatarColor = "#C13584",
        content = "New reel is live — a 60-second study of how light bends through frosted glass. 🎧",
        image = "https://picsum.photos/seed/glass/600/450",
        time = "5h", stats = ItemStats(15400, 320, 410), link = "https://instagram.com"
    ),
    FeedItem(
        id = "x-2", platform = "x", type = "notification",
        author = "Dan Abramov", handle = "@dan_abramov2", avatarColor = "#1d9bf0",
        content = "liked your reply about pointer events and capture. No further comment, which means you were right. 😌",
        time = "6h", link = "https://x.com"
    ),
    FeedItem(
        id = "sc-1", platform = "snapchat", type = "notification",
        author = "maddie", handle = "@maddie", avatarColor = "#FFFC00",
        content = "sent you a Snap! 👻 (tap to view — expires in 23h)",
        time = "7h", link = "https://snapchat.com"
    ),
    FeedItem(
        id = "yt-2", platform = "youtube", type = "post",
        author = "Veritasium", handle = "@veritasium", avatarColor = "#FF0000",
        content = "How do touchscreens actually know it's your finger? The answer involves quantum tunneling.",
        image = "https://picsum.photos/seed/touch/600/340",
        time = "9h", stats = ItemStats(91200, 3100, 2200), link = "https://youtube.com"
    ),
    FeedItem(
        id = "tt-2", platform = "tiktok", type = "notification",
        author = "trends", handle = "@tiktok", avatarColor = "#25F4EE",
        content = "Your video is trending in #UIUX — 12.4k new views in the last hour.",
        time = "11h", link = "https://tiktok.com"
    ),
    FeedItem(
        id = "fb-2", platform = "facebook", type = "post",
        author = "Android Devs United", handle = "Group · 14k members", avatarColor = "#1877F2",
        content = "Weekly roundup: 5 new open-source libraries for building glassmorphic UIs on Android.",
        time = "14h", stats = ItemStats(640, 88, 47), link = "https://facebook.com"
    ),
    FeedItem(
        id = "li-2", platform = "linkedin", type = "notification",
        author = "Recruiter · Google", handle = "Talent Acq.", avatarColor = "#0A66C2",
        content = "reached out: \"Loved your work on gesture-driven overlays — would love to chat.\"",
        time = "1d", link = "https://linkedin.com"
    ),
    FeedItem(
        id = "rd-2", platform = "reddit", type = "post",
        author = "u/glassmaker", handle = "r/AndroidDev", avatarColor = "#FF4500",
        content = "After 3 months I finally nailed the draggable edge-handle that doesn't conflict with system back gestures.",
        time = "1d", stats = ItemStats(4200, 510, 230), link = "https://reddit.com"
    ),
    FeedItem(
        id = "x-3", platform = "x", type = "post",
        author = "Natalie", handle = "@natbuilds", avatarColor = "#1d9bf0",
        content = "Reminder: half-screen overlays should never block the primary action behind them.",
        time = "2d", stats = ItemStats(7600, 410, 1800), link = "https://x.com"
    )
)
