import dev.inmo.tgbotapi.bot.ktor.telegramBot
import io.github.cdimascio.dotenv.dotenv
import jobs.CleanPipisaCacheJob
import jobs.Jobs
import org.jetbrains.exposed.sql.transactions.TransactionManager.Companion.defaultDatabase
import org.koin.dsl.module
import providers.DeadlinesProvider
import repositories.PipisaRepository
import service.PipisaService
import service.PipisaServiceImpl
import utils.Config.NotionConfig.notionToken
import utils.Config.TelegramConfig.telegramToken


val appModule = module {
    single { dotenv() }

    single { Jobs() }
    single {
        telegramBot(telegramToken)
    }
    single {
        DeadlinesProvider(notionToken)
    }

    single { PipisaRepository() }
    single<PipisaService> { PipisaServiceImpl(get()) }
    single { CleanPipisaCacheJob(get()) }

    single { BotBehaviour(get(), get(), get()) }

    single { defaultDatabase }
}

