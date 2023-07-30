package repositories

import models.UserTable
import models.UserTable.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PipisaRepository {
    fun updateUser(user: User) {
        if (userByChatAndUserId(user.userId, user.chatId) == null) {
            return transaction {
                UserTable.insert {
                    it[userId] = user.userId
                    it[chatId] = user.chatId
                    it[name] = user.name
                    it[dickLength] = user.dickLength
                    it[username] = user.username
                }
            }
        }
        return transaction {
            UserTable.update({ (UserTable.userId eq user.userId) and (UserTable.chatId eq user.chatId) }) {
                it[name] = user.name
                it[dickLength] = user.dickLength
                it[username] = user.username
            }
        }
    }

    fun userByChatAndUserId(userId: Long, chatId: Long): User? {
        return transaction {
            UserTable.select {
                (UserTable.userId eq userId) and
                        (UserTable.chatId eq chatId)
            }.map { row ->
                User(
                    userId = row[UserTable.userId],
                    name = row[UserTable.name],
                    dickLength = row[UserTable.dickLength],
                    increase = 0,
                    username = row[UserTable.username],
                    chatId = row[UserTable.chatId],
                )
            }.singleOrNull()
        }
    }

    fun allUsers(): List<User> {
        return transaction {
            UserTable.selectAll().map { row ->
                User(
                    userId = row[UserTable.userId],
                    name = row[UserTable.name],
                    dickLength = row[UserTable.dickLength],
                    increase = 0,
                    username = row[UserTable.username],
                    chatId = row[UserTable.chatId],
                )
            }
        }
    }

    fun allUsersSortedByDickLength(chatId: Long): List<User> {
        return transaction {
            UserTable.select {
                UserTable.chatId eq chatId
            }
                .orderBy(UserTable.dickLength, SortOrder.DESC)
                .map { row ->
                    User(
                        userId = row[UserTable.userId],
                        name = row[UserTable.name],
                        dickLength = row[UserTable.dickLength],
                        increase = 0,
                        username = row[UserTable.username],
                        chatId = row[UserTable.chatId],
                    )
                }
        }
    }
}