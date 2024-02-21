package com.github.trueddd.data

import org.jetbrains.exposed.sql.Table

object ActionsTable : Table("actions") {
    val value = text("value")
    override val primaryKey = PrimaryKey(value)
}
