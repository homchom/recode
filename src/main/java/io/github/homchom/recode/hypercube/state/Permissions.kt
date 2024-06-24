package io.github.homchom.recode.hypercube.state

class PermissionGroup(ranks: Iterable<Rank>) {
    val ranks = ranks.toSet()

    inline operator fun <reified P> contains(rankPermission: P)
    where P : Rank, P : Comparable<P> =
        ranks.any { it is P && it >= rankPermission }
}