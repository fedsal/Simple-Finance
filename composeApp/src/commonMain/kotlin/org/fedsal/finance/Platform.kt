package org.fedsal.finance

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform