package jobs

import org.slf4j.Logger

abstract class ScheduleJob(
    private val logger: Logger
) {
    private var isRunning = false
    private var jobThread: Thread? = null

    abstract fun doWork()

    fun start() {
        logger.info("Job executing started")
        if (isRunning) return
        isRunning = true
        jobThread = Thread {
            try {
                Thread.sleep(initialDelayInMillis())
                while (isRunning) {
                    doWork()
                    Thread.sleep(intervalInMillis())
                }
            } catch (e: Exception) {
                logger.error("An error occurred while executing the job: ${e.message}", e)
            }
        }
        jobThread?.start()
    }

    fun stop() {
        logger.info("Job executing stoped")
        isRunning = false
        jobThread?.join()
    }

    abstract fun intervalInMillis(): Long
    abstract fun initialDelayInMillis(): Long
}