package service

interface PipisaCooldownCacheService {
    fun changeCooldownFlag(userId: String, isCooldown: Boolean)
    fun cooldownFlag(userId: String) : Boolean
    fun clean()
}