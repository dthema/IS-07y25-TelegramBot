import jobs.Jobs
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import utils.configureDatabase

suspend fun main() {
    startKoin {
        modules(appModule)
    }
    val jobs: Jobs by inject(Jobs::class.java)
    val botBehaviour: BotBehaviour by inject(BotBehaviour::class.java)
    configureDatabase()
    jobs.startAllJobs()
    botBehaviour.startBot()
    jobs.stopAllJobs()
}