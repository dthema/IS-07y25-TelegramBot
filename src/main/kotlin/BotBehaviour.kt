import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.message.MarkdownV2ParseMode
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.bold
import dev.inmo.tgbotapi.utils.regular

import exceptions.PipisaException
import providers.DeadlinesProvider
import providers.MessagesProvider.parseIncreaseDick
import providers.MessagesProvider.parseMonthDeadlines
import providers.MessagesProvider.parseTopDicks
import providers.MessagesProvider.parseWeekDeadlines
import service.PipisaService
import utils.Config.NotionConfig.notionUrl

class BotBehaviour(
    private val bot: TelegramBot,
    private val deadlinesProvider: DeadlinesProvider,
    private val pipisaService: PipisaService,
) {
    @OptIn(RiskFeature::class)
    suspend fun startBot() {
        bot.buildBehaviourWithLongPolling {
            onCommand("notion") {
                reply(it, "[Notion группы]($notionUrl)", parseMode = MarkdownV2ParseMode, disableWebPagePreview = true)
            }

            onCommand("week_deadlines") {
                val text = parseWeekDeadlines(deadlinesProvider.weekDeadlines())
                reply(it) { regular(text) }
            }

            onCommand("month_deadlines") {
                val text = parseMonthDeadlines(deadlinesProvider.monthDeadlines())
                reply(it) { bold(text) }
            }

            onCommand("week_from_today_deadlines") {
                val text = parseWeekDeadlines(deadlinesProvider.weekFromTodayDeadlines())
                reply(it) { regular(text) }
            }

            onCommand("month_from_today_deadlines") {
                val text = parseMonthDeadlines(deadlinesProvider.monthFromTodayDeadlines())
                reply(it) { bold(text) }
            }

            onCommand("top_dicks") {
                val text = parseTopDicks(pipisaService.topDicks(it.chat.id.chatId))
                reply(it) { bold(text) }
            }

            onCommand("increase_dick") {
                val text = parseIncreaseDick(it.from ?: throw PipisaException("tg user not found"), it.chat.id.chatId)
                reply(it) { bold(text) }
            }

            setMyCommands(
                BotCommand("notion", "Ссылка на ноушен группы"),
                BotCommand("month_deadlines", "Дедлайны на текущий месяц"),
                BotCommand("week_deadlines", "Дедлайны на текущую неделю"),
                BotCommand("month_from_today_deadlines", "Дедлайны на месяц вперед"),
                BotCommand("week_from_today_deadlines", "Дедлайны на неделю вперед"),
                BotCommand("top_dicks", "Топ хуев группы"),
                BotCommand("increase_dick", "Вырастить пиписю")
            )
        }.join()
    }
}