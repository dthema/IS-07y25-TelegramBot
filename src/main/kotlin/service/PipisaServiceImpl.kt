package service

import exceptions.PipisaException
import models.UserTable.User
import repositories.PipisaRepository
import kotlin.random.Random

class PipisaServiceImpl(
    private val repository: PipisaRepository,
    private val usersOnCooldown: MutableSet<User> = HashSet(),
) : PipisaService {
    override fun clean() {
        usersOnCooldown.clear()
    }

    override fun isCooldown(userId: Long, chatId: Long): Boolean {
        return usersOnCooldown.any { user -> user.chatId == chatId && user.userId == userId }
    }

    override fun increaseDick(userId: Long, chatId: Long, name: String, username: String): User {
        if (isCooldown(userId, chatId)) {
            throw PipisaException("Dick update is on cooldown")
        }

        val user = updateDickLengthRandomly(
            repository.userByChatAndUserId(userId, chatId) ?: User(
                userId,
                chatId,
                name,
                username,
                0,
                0
            )
        )
        repository.updateUser(user)
        usersOnCooldown.add(user)
        return user
    }

    override fun topDicks(chatId: Long): List<User> {
        return repository.allUsersSortedByDickLength(chatId)
    }

    override fun user(userId: Long, chatId: Long): User? {
        return repository.userByChatAndUserId(userId, chatId)
    }

    private fun updateDickLengthRandomly(user: User): User {
        val minDelta = -5
        val maxDelta = 10
        var randomDelta = 0

        while (randomDelta == 0) {
            randomDelta = Random.nextInt(minDelta, maxDelta + 1)
        }

        val newValue = (user.dickLength + randomDelta).coerceAtLeast(0)

        user.increase = randomDelta
        user.dickLength = newValue
        return user
    }
}