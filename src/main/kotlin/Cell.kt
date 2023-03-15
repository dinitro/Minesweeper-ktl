class Cell(
    var isBomb: Boolean = false,
    var isCovered: Boolean = true,
    var isFlagged: Boolean = false,
    var neighborBombs: Int = 0,
    var isExploded: Boolean = false
) {
    fun uncover() {
        isCovered = false
    }

    fun flag() {
        isFlagged = !isFlagged
    }
}