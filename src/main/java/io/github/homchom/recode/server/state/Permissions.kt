package io.github.homchom.recode.server.state

import io.github.homchom.recode.util.collections.ImmutableSet

data class PermissionGroup(
    val ranks: ImmutableSet<Rank>
) {
    inline operator fun <reified P> contains(rankPermission: P) where P : Rank, P : Comparable<P> =
        ranks.any { it is P && it >= rankPermission }
}