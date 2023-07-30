package providers

import models.Deadline
import notion.api.v1.NotionClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import utils.Config.NotionConfig.deadlinesDbId
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

class DeadlinesProvider(
    notionToken: String,
    private var notionClient: NotionClient = NotionClient(token = notionToken),
    private val logger: Logger = LoggerFactory.getLogger(DeadlinesProvider::class.java),
) {
    private val dateTimeFormatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz")

    private fun allDeadlines(): List<Deadline> {
        val deadlines = ArrayList<Deadline>()
        notionClient.use { client ->
            client.queryDatabase(deadlinesDbId)
                .results
                .stream()
                .forEach { page ->
                    val dateString = page.properties["Дата"]?.date?.start
                    val date = if (dateString == null) null else LocalDate.parse(dateString)
                    val extraInfoProperty = page.properties["Доп. Инфа"]?.richText!!
                    var extraInfo = ""
                    if (extraInfoProperty.isNotEmpty())
                        extraInfo = extraInfoProperty.first().text?.content ?: ""

                    deadlines.add(
                        Deadline(
                            page.properties["Предмет"]?.select?.name ?: "Неизвестно",
                            page.properties["Название работы"]?.title?.first()?.text?.content ?: "Неизвестно",
                            date,
                            page.properties["Тип работы"]?.multiSelect?.map { type -> type.name!! }?.toList()
                                ?: ArrayList(),
                            extraInfo,
                            LocalDateTime.parse(page.lastEditedTime, dateTimeFormatter)
                        )
                    )
                }
        }

        return deadlines.sortedBy { it.date }
    }

    fun monthDeadlines(): List<Deadline> {
        logger.info("Trying to get month deadlines")

        val currentDate = now()
        val monthFirstDay = currentDate.minusDays(currentDate.dayOfMonth.toLong())
        val monthLastDay = monthFirstDay.plusMonths(1).plusDays(1)
        return allDeadlines().stream()
            .filter { it.date != null && (it.date.isAfter(monthFirstDay) && it.date.isBefore(monthLastDay)) }
            .toList()
    }

    fun weekDeadlines(): List<Deadline> {
        logger.info("Trying to get week deadlines")

        val currentDate = now()
        val weekFirstDay = currentDate.minusDays(currentDate.dayOfWeek.value.toLong())
        val weekLastDay = weekFirstDay.plusWeeks(1).plusDays(1)
        return allDeadlines().stream()
            .filter { it.date != null && (it.date.isAfter(weekFirstDay) && it.date.isBefore(weekLastDay)) }
            .toList()
    }
}