package providers

import dev.inmo.tgbotapi.types.chat.User
import dev.inmo.tgbotapi.types.userLink
import exceptions.PipisaException
import models.Deadline
import models.UserTable
import org.koin.java.KoinJavaComponent.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import service.PipisaService
import java.time.format.DateTimeFormatter

object MessagesProvider {
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val logger: Logger = LoggerFactory.getLogger(MessagesProvider::class.java)
    private val pipisaService: PipisaService by inject(PipisaService::class.java)

    private fun parseDeadlines(deadlines: List<Deadline>): String {
        logger.info("Trying to parse deadlines")
        val stringBuilder = StringBuilder()
        deadlines.forEach { deadline ->
            stringBuilder.append("\n${deadline.subject}: \t${deadline.name}. Дедлайн: ${deadline.date?.format(formatter) ?: "Неизвестен"}")
            if (deadline.extraInfo.isNotBlank())
                stringBuilder.append(". Доп. инфа: ${deadline.extraInfo}")
        }
        return stringBuilder.toString()
    }

    fun parseWeekDeadlines(deadlines: List<Deadline>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Дедлайны на текущую неделю:\n")
        stringBuilder.append(parseDeadlines(deadlines))
        return stringBuilder.toString()
    }

    fun parseMonthDeadlines(deadlines: List<Deadline>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Дедлайны на текущий месяц:\n")
        stringBuilder.append(parseDeadlines(deadlines))
        return stringBuilder.toString()
    }

    fun parseTopDicks(dicks: List<UserTable.User>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Топ 10 пипис группы:\n")
        dicks.take(10).forEachIndexed { index, user ->
            stringBuilder.append("${index + 1} | ${user.name}  -   ${user.dickLength} см.\n")
        }
        return stringBuilder.toString()
    }

    fun parseIncreaseDick(tgUser: User, chatId: Long): String {
        return if (pipisaService.isCooldown(tgUser.id.chatId, chatId)) {
            increaseOnCooldown(tgUser, chatId)
        } else {
            increaseNotOnCooldown(tgUser, chatId)
        }
    }

    private fun increaseNotOnCooldown(tgUser: User, chatId: Long): String {
        val user = pipisaService.increaseDick(
            userId = tgUser.id.chatId,
            username = tgUser.username?.username ?: throw PipisaException("username is not defined"),
            name = tgUser.firstName,
            chatId = chatId
        )

        val position = pipisaService.topDicks(chatId).indexOfFirst { it.userId == user.userId } + 1

        val increaseMessage = if (user.increase >= 0) {
            "вырос на ${user.increase} см."
        } else {
            "уменьшился на ${-user.increase} см."
        }
        return buildString {
            append("${tgUser.username!!.username}, твой писюн $increaseMessage\n")
            append("Теперь он равен ${user.dickLength} см.\n")
            append("Ты занимаешь $position место в топе.\n")
            append("Следующая попытка завтра!")
        }
    }

    private fun increaseOnCooldown(tgUser: User, chatId: Long): String {
        val user = pipisaService.user(tgUser.id.chatId, chatId) ?: throw PipisaException("user not found")
        val position = pipisaService.topDicks(chatId).indexOfFirst { it.userId == user.userId } + 1
        return buildString {
            append("${tgUser.username!!.username}, ты уже играл.\n")
            append("Сейчас он равен ${user.dickLength} см.\n")
            append("Ты занимаешь $position место в топе.\n")
            append("Следующая попытка завтра!")
        }
    }
}
