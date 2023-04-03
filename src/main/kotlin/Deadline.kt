import java.time.LocalDate

data class Deadline(
    val subject: String,
    val name: String,
    val date: LocalDate,
    val types: List<String>,
    val extraInfo: String
)
