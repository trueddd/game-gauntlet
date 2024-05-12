package com.github.trueddd.data

import org.jetbrains.exposed.sql.Table

object PlayersTable : Table("players") {
    val id = text("id").uniqueIndex()
    val name = text("name")
    val twitchToken = text("twitch_token")
    val rewardId = text("twitch_reward_id").nullable()
    override val primaryKey = PrimaryKey(id)
}
