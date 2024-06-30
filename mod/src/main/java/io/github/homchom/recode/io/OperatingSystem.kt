package io.github.homchom.recode.io

import oshi.PlatformEnum
import oshi.SystemInfo

val platform: PlatformEnum get() = SystemInfo.getCurrentPlatform()