package io.github.homchom.recode.util

import oshi.PlatformEnum
import oshi.SystemInfo

val platform: PlatformEnum get() = SystemInfo.getCurrentPlatform()