package jobs

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import providers.DeadlinesProvider
import service.PipisaService
import utils.DateUtils.calculateInitialDelayMillis
import java.time.Duration.ofHours
import java.time.LocalTime

class CleanPipisaCacheJob(
    private val service: PipisaService,
    private val logger: Logger = LoggerFactory.getLogger(DeadlinesProvider::class.java)
) : ScheduleJob(
    logger = logger
) {
    override fun doWork() {
        logger.info("Trying to clean all pipisa caches")
        service.clean()
    }

    override fun intervalInMillis(): Long {
        return ofHours(24).toMillis()
    }

    override fun initialDelayInMillis(): Long {
        return calculateInitialDelayMillis(LocalTime.of(10, 0))
    }
}