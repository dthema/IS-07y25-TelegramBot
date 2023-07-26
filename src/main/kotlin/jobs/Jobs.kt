package jobs

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import providers.DeadlinesProvider

class Jobs(
    private val logger: Logger = LoggerFactory.getLogger(DeadlinesProvider::class.java)
) {
    private val jobsList = mutableListOf<ScheduleJob>(
        CleanPipisaCacheJob()
    )

    fun startAllJobs() {
        logger.info("Starting all jobs")
        for (job in jobsList) {
            job.start()
        }
    }

    fun stopAllJobs() {
        logger.info("Stopping all jobs")
        for (job in jobsList) {
            job.stop()
        }
    }
}