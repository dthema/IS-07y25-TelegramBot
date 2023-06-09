import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import dev.inmo.tgbotapi.utils.regular
import io.github.cdimascio.dotenv.dotenv

private const val NOTION_LINK = "https://dthema.notion.site/M32071-c7196be220d84ba5a0a6e64d4d9003ee"

suspend fun main() {
    val dotenv = dotenv()

    val bot = telegramBot(dotenv["TELEGRAM_TOKEN"])
    val deadlinesProvider = DeadlinesProvider(dotenv["NOTION_TOKEN"])

    bot.buildBehaviourWithLongPolling {
        onCommand("notion") {
            reply(it, "[Notion группы]($NOTION_LINK)", parseMode = MarkdownV2ParseMode, disableWebPagePreview = true)
        }

        onCommand("week_deadlines") {
            val text = MessagesProvider.parseWeekDeadlines(deadlinesProvider.getWeekDeadlines())
            reply(it) { regular(text) }
        }

        onCommand("month_deadlines") {
            val text = MessagesProvider.parseMonthDeadlines(deadlinesProvider.getMonthDeadlines())
            reply(it) { regular(text) }
        }

        setMyCommands(
            BotCommand("notion", "Ссылка на ноушен группы"),
            BotCommand("month_deadlines", "Дедлайны на текущий месяц"),
            BotCommand("week_deadlines", "Дедлайны на текущую неделю")
        )
    }.join()
}