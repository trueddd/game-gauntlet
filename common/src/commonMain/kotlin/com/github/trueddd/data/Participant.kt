package com.github.trueddd.data

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    override val name: PlayerName,
    val displayName: String = name,
): Rollable {

    override val description: String
        get() = displayName

    override val color: Long
        get() = when (this) {
            Truetripled -> 0xFF571F6E
            Shizov -> 0xFFB2B2B2
            Adash -> 0xffd9242b
            ChilloutLatte -> 0xFFEFEFF1
            else -> throw IllegalArgumentException("Unknown participant: $this")
        }

    val backgroundUrl: String
        get() = when (this) {
            Truetripled -> "http://static-cdn.jtvnw.net/jtv_user_pictures/90fc5af1-cb62-4317-9309-32b6b120a6ca-profile_banner-480.png"
            Shizov -> "http://static-cdn.jtvnw.net/jtv_user_pictures/d11c39e4-e320-4e43-8752-9444c94df75e-profile_banner-480.png"
            Adash -> "http://static-cdn.jtvnw.net/jtv_user_pictures/91a8b1c4-db4c-429f-8586-614be7040237-profile_banner-480.png"
            ChilloutLatte -> "http://static-cdn.jtvnw.net/jtv_user_pictures/3bcbfea5-4afc-4801-b041-eb77e51fdfad-profile_banner-480.jpeg"
            else -> throw IllegalArgumentException("Unknown participant: $this")
        }

    // TODO: Actualize participants
    companion object {
        val Truetripled = Participant("truetripled")
        val Shizov = Participant("shizov")
        val Adash = Participant("adash", "Adash")
        val ChilloutLatte = Participant("chilloutlatte", "chillout_latte")
    }
}
