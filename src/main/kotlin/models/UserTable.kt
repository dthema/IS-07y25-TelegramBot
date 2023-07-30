package models

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserTable : Table() {
    val userId: Column<Long> = long("user_id")
    val chatId: Column<Long> = long("chat_id")
    val name: Column<String> = varchar("name", 100)
    val username: Column<String> = varchar("username", 100)
    val dickLength: Column<Int> = integer("dick_length")
    override val primaryKey = PrimaryKey(chatId, userId, name = "PK_User_Id")

    data class User(
        var userId: Long,
        var chatId: Long,
        var name: String,
        var username: String,
        var dickLength: Int,
        var increase: Int,
    )
}