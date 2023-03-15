class Cell(
    var isBomb: Boolean = false, // Whether a cell contains a bomb or not.
    var isCovered: Boolean = true, // Whether the cell has been opened or not.
    var isFlagged: Boolean = false, // Whether the user has flagged a cell or not.
    var neighborBombs: Int = 0, // Number of neighbor cells that contains bombs.
    var isExploded: Boolean = false // Whether cell has exploded or not.
) {
    // Method to uncover a cell by changing the isCovered property.
    fun uncover() {
        isCovered = false
    }

    // Method to toggle the isFlagged property.
    fun flag() {
        isFlagged = !isFlagged
    }
}