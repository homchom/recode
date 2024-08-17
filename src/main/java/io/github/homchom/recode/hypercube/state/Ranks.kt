package io.github.homchom.recode.hypercube.state

import io.github.homchom.recode.hypercube.DIAMOND

sealed interface Rank {
    val displayName: String
}

enum class DonorRank(override val displayName: String) : Rank {
    NOBLE("Noble"),
    EMPEROR("Emperor"),
    MYTHIC("Mythic"),
    OVERLORD("${DIAMOND}Overlord${DIAMOND}")
}