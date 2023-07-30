package jobs

import org.koin.java.KoinJavaComponent.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import providers.DeadlinesProvider

class Jobs(
    private val logger: Logger = LoggerFactory.getLogger(DeadlinesProvider::class.java)
) {

    private val cleanPipisaCacheJob: CleanPipisaCacheJob by inject(CleanPipisaCacheJob::class.java)

    fun startAllJobs() {
        logger.info("Starting all jobs")
        cleanPipisaCacheJob.start()
    }

    fun stopAllJobs() {
        logger.info("Stopping all jobs")
        cleanPipisaCacheJob.stop()
    }
}