package service

import exceptions.PipisaException
import java.util.*

class PipisaCooldownCacheServiceImpl(
    private val cooldownById: MutableMap<String, Boolean> = TreeMap()
) : PipisaCooldownCacheService {

    override fun changeCooldownFlag(userId: String, isCooldown: Boolean) {
        cooldownById[userId] = isCooldown
    }

    override fun cooldownFlag(userId: String): Boolean {
        return cooldownById[userId] ?: throw PipisaException("pipisa not found")
    }

    override fun clean() {
        cooldownById.clear()
    }
}