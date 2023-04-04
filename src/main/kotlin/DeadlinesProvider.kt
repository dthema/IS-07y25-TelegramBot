import notion.api.v1.NotionClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

class DeadlinesProvider(notionToken: String) {
    private var notionClient: NotionClient
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz")

    init {
        notionClient = NotionClient(token = notionToken)
    }

    private fun getAllDeadlines(): List<Deadline> {
        val deadlines = ArrayList<Deadline>()
        notionClient.use { client ->
            client.queryDatabase(databaseId = "efe920cb822b4e6ba3344834f406dd7b")
                .results
                .stream()
                .forEach { page ->
                    val dateString = page.properties["Дата"]?.date?.start
                    val date = if (dateString == null) null else LocalDate.parse(dateString)
                    val extraInfoProperty = page.properties["Доп. Инфа"]?.richText!!
                    var extraInfo = ""
                    if (extraInfoProperty.isNotEmpty())
                        extraInfo = extraInfoProperty.first().text?.content ?: ""

                    deadlines.add(Deadline(
                        page.properties["Предмет"]?.select?.name ?: "Неизвестно",
                        page.properties["Название работы"]?.title?.first()?.text?.content ?: "Неизвестно",
                        date,
                        page.properties["Тип работы"]?.multiSelect?.map { type -> type.name!! }?.toList() ?: ArrayList(),
                        extraInfo,
                        LocalDateTime.parse(page.lastEditedTime, dateTimeFormatter)))
                }
        }

        return deadlines
    }

    fun getMonthDeadlines(): List<Deadline> {
        val currentDate = LocalDate.now()
        val monthFirstDay = currentDate.minusDays(currentDate.dayOfMonth.toLong())
        val monthLastDay = monthFirstDay.plusMonths(1).plusDays(1)
        return getAllDeadlines().stream()
            .filter { it.date != null && (it.date.isAfter(monthFirstDay) && it.date.isBefore(monthLastDay)) }
            .toList()
    }

    fun getWeekDeadlines(): List<Deadline> {
        val currentDate = LocalDate.now()
        val weekFirstDay = currentDate.minusDays(currentDate.dayOfWeek.value.toLong())
        val weekLastDay = weekFirstDay.plusWeeks(1).plusDays(1)
        return getAllDeadlines().stream()
            .filter { it.date != null && (it.date.isAfter(weekFirstDay) && it.date.isBefore(weekLastDay)) }
            .toList()
    }
}