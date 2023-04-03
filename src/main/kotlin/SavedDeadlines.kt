import java.time.LocalDate

class SavedDeadlines(
    deadlines: List<Deadline> = ArrayList(),
    lastUpdateDate: LocalDate = LocalDate.now().minusDays(1)
) {
    var lastUpdateDate = lastUpdateDate
        private set
    var deadlines = deadlines
        set(value) {
            field = value
            lastUpdateDate = LocalDate.now()
        }
}
