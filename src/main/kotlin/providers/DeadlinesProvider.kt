package providers

import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

import notion.api.v1.NotionClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import models.Deadline
import utils.Config.NotionConfig.deadlinesDbId

class DeadlinesProvider(
    notionToken: String,
    private var notionClient: NotionClient = NotionClient(token = notionToken),
    private val logger: Logger = LoggerFactory.getLogger(DeadlinesProvider::class.java),
) {
    fun monthDeadlines(): List<Deadline> {
        logger.info("Get month deadlines")

        val currentDate = now()
        val monthFirstDay = currentDate.minusDays(currentDate.dayOfMonth.toLong())
        val monthLastDay = monthFirstDay.plusMonths(1).plusDays(1)
        return deadlinesByDates(monthFirstDay, monthLastDay)
    }

    fun weekDeadlines(): List<Deadline> {
        logger.info("Get week deadlines")

        val currentDate = now()
        val weekFirstDay = currentDate.minusDays(currentDate.dayOfWeek.value.toLong())
        val weekLastDay = weekFirstDay.plusWeeks(1).plusDays(1)
        return deadlinesByDates(weekFirstDay, weekLastDay)
    }

    fun monthFromTodayDeadlines(): List<Deadline> = deadlinesByDates(now(), now().plusMonths(1).plusDays(1))

    fun weekFromTodayDeadlines(): List<Deadline> = deadlinesByDates(now(), now().plusWeeks(1).plusDays(1))

    private fun deadlinesByDates(after: LocalDate, before: LocalDate): List<Deadline> {
        return allDeadlines().stream()
            .filter { it.date != null && (it.date.isAfter(after) && it.date.isBefore(before)) }.toList()
    }

    private val dateTimeFormatter = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz")

    private fun allDeadlines(): List<Deadline> {
        val deadlines = ArrayList<Deadline>()
        notionClient.use { client ->
            client.queryDatabase(deadlinesDbId).results.stream().forEach { page ->
                val dateString = page.properties["Дата"]?.date?.start
                val date = if (dateString == null) null else LocalDate.parse(dateString)
                val extraInfoProperty = page.properties["Доп. Инфа"]?.richText!!
                var extraInfo = ""
                if (extraInfoProperty.isNotEmpty()) extraInfo = extraInfoProperty.first().text?.content ?: ""

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
}