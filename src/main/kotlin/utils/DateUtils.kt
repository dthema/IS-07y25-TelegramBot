package utils

import java.time.Duration.between
import java.time.LocalDateTime.now
import java.time.LocalDateTime.of
import java.time.LocalTime

object DateUtils {
    fun calculateInitialDelayMillis(desiredTime: LocalTime): Long {
        val now = now()
        val desiredTimeToday = of(now.toLocalDate(), desiredTime)
        val desiredTimeTomorrow = desiredTimeToday.plusDays(1)

        return if (now.isAfter(desiredTimeToday)) {
            between(now, desiredTimeTomorrow).toMillis()
        } else {
            between(now, desiredTimeToday).toMillis()
        }
    }
}