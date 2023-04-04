import java.time.LocalDate
import java.time.LocalDateTime

data class Deadline(
    val subject: String,
    val name: String,
    val date: LocalDate?,
    val types: List<String>,
    val extraInfo: String,
    var lastEditedTime: LocalDateTime
)
