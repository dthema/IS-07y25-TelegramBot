package jobs

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import providers.DeadlinesProvider
import service.PipisaCooldownCacheService
import service.PipisaCooldownCacheServiceImpl
import java.time.Duration

class CleanPipisaCacheJob(
    private val logger: Logger = LoggerFactory.getLogger(DeadlinesProvider::class.java),
    private val service: PipisaCooldownCacheService = PipisaCooldownCacheServiceImpl()
) : ScheduleJob(
    logger = logger
) {
    override fun doWork() {
        logger.info("Trying to clean all pipisa caches")
        service.clean()
    }

    override fun getIntervalInMillis(): Long {
        return Duration.ofSeconds(3).toMillis()
    }

    override fun getInitialDelayInMillis(): Long {
        return Duration.ofSeconds(10).toMillis()
    }
}