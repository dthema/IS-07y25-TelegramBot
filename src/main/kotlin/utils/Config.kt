package utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager.Companion.defaultDatabase
import org.koin.java.KoinJavaComponent.inject

object Config {
    private val dotEnv: Dotenv by inject(Dotenv::class.java)

    object TelegramConfig {
        val telegramToken: String =
            dotEnv["TELEGRAM_TOKEN"] ?: error("TELEGRAM_TOKEN not found in environment variables.")
    }

    object NotionConfig {
        val notionToken: String = dotEnv["NOTION_TOKEN"] ?: error("NOTION_TOKEN not found in environment variables.")
        val notionUrl: String = dotEnv["NOTION_URL"] ?: error("NOTION_URL not found in environment variables")
        val deadlinesDbId: String =
            dotEnv["DEADLINES_DB_ID"] ?: error("DEADLINES_DB_ID not found in environment variables")
    }

    object PipisaConfig {
        val pipisaEnable: Boolean = dotEnv["PIPISA_ENABLE"]?.toBoolean() ?: error("PIPISA_ENABLE not found in environment variables")
    }
}

fun configureDatabase() {
    val hikariConfig = HikariConfig("/db.properties")
    val dataSource = HikariDataSource(hikariConfig)
    defaultDatabase = Database.connect(dataSource)

    val flyway = Flyway.configure().dataSource(dataSource).load()
    flyway.migrate()
}