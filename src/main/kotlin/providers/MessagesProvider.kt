package providers

import models.Deadline
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.format.DateTimeFormatter

object MessagesProvider {
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val logger: Logger = LoggerFactory.getLogger(MessagesProvider::class.java)

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
}
