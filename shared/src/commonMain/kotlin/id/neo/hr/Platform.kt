package id.neo.hr

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform