package service

import models.UserTable.User

interface PipisaService {
    fun clean()
    fun isCooldown(userId: Long, chatId: Long): Boolean
    fun increaseDick(userId: Long, chatId: Long, name: String, username: String): User
    fun topDicks(chatId: Long): List<User>
    fun user(userId: Long, chatId: Long): User?
}