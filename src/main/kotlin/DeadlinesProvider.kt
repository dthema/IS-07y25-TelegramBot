import org.jsoup.Jsoup
import org.openqa.selenium.chrome.ChromeDriver
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

object DeadlinesProvider {
    private const val DEADLINES_LINK = "https://dthema.notion.site/efe920cb822b4e6ba3344834f406dd7b?v=414d6f550b4d4f53b03a0a18c9d02f27"
    private const val HTML_ELEMENT_DELIMITER = "notion-selectable notion-page-block notion-collection-item"
    private const val HTML_SECONDARY_DELIMITER = "display: block"
    private const val HTML_TYPE_DELIMITER = "white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: inline-flex; align-items: center;"

    private val savedDeadlines = SavedDeadlines()

    private fun getAllDeadlines(): List<Deadline> {
        if (savedDeadlines.lastUpdateDate.isEqual(LocalDate.now()))
            return savedDeadlines.deadlines

        val driver = ChromeDriver()
        driver.get(DEADLINES_LINK)
        while (!driver.pageSource.contains(HTML_ELEMENT_DELIMITER)) { }
        val doc = Jsoup.parse(driver.pageSource)

        val elements = doc.html().split(HTML_ELEMENT_DELIMITER)
        savedDeadlines.deadlines = elements.stream()
            .skip(1).limit((elements.size - 2).toLong())
            .map { element ->
                val subject = element.split(HTML_SECONDARY_DELIMITER)[1].split(">")[5].trim().split("\n")[0]
                val name = element.split(HTML_SECONDARY_DELIMITER)[4].split(">")[2].split("<")[0]
                val date = LocalDate.parse(
                    element.trim().split(HTML_SECONDARY_DELIMITER)[5].split(">")[2].trim().substring(0, 10),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
                val types = element.split(HTML_SECONDARY_DELIMITER)[6].split(HTML_TYPE_DELIMITER).stream()
                    .skip(1)
                    .map { it.split(">")[2].trim().split("\n")[0] }
                    .toList() ?: ArrayList()
                val extraInfo = element.split(HTML_SECONDARY_DELIMITER)[7].split(">")[2].split("<")[0]
                return@map Deadline(subject, name, date, types, extraInfo)
            }.toList()

        driver.close()
        return savedDeadlines.deadlines
    }

    fun getMonthDeadlines(): List<Deadline> {
        val currentDate = LocalDate.now()
        val monthFirstDay = currentDate.minusDays(currentDate.dayOfMonth.toLong() - 1)
        val monthLastDay = monthFirstDay.plusMonths(1)
        return getAllDeadlines().stream()
            .filter { it.date.isAfter(monthFirstDay) && it.date.isBefore(monthLastDay) }
            .toList()
    }

    fun getWeekDeadlines(): List<Deadline> {
        val currentDate = LocalDate.now()
        val weekFirstDay = currentDate.minusDays(currentDate.dayOfWeek.value.toLong() - 1)
        val weekLastDay = weekFirstDay.plusWeeks(1)
        return getAllDeadlines().stream()
            .filter { it.date.isAfter(weekFirstDay) && it.date.isBefore(weekLastDay) }
            .toList()
    }
}