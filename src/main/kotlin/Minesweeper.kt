import kotlin.random.Random

class Minesweeper(private val difficultyLevel: DifficultyLevel) {
    val gridSize = difficultyLevel.gridSize
    val board = Array(gridSize) { Array(gridSize) { Cell() } }
    private var uncoveredCount = 0
    var isGameOver = false

    // Initialize board by placing the bombs and calculating neighbor bombs for each cell.
    fun initializeBoard() {
        placeBombs(difficultyLevel.numBombs)
        calculateNeighborBombs()
    }

    // Method to place bombs randomly on the board.
    private fun placeBombs(numBombs: Int) {
        var bombsLeft = numBombs
        // Run as long as there are bombs that need to be placed.
        while (bombsLeft > 0) {
            // Chose random row and column to place a bomb.
            val row = Random.nextInt(gridSize)
            val col = Random.nextInt(gridSize)
            // Check if cell already has a bomb
            if (!board[row][col].isBomb) {
                // Place bomb and decrease bomb count.
                board[row][col] = Cell(isBomb = true)
                bombsLeft--
            }
        }
    }

    // Method to calculate the number of neighboring bombs for each non-bomb cell on the game board.
    private fun calculateNeighborBombs() {
        // Iterate over each cell of the board.
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val cell = board[row][col]
                // If current cell is not a bomb, calculate number of neighboring bombs.
                if (!cell.isBomb) {
                    val neighborBombs = getNeighborBombs(row, col)
                    // Set the number of neighboring bombs.
                    cell.neighborBombs = neighborBombs
                }
            }
        }
    }

    // Method to calculate the number of bombs in the neighboring cells of a given cell.
    private fun getNeighborBombs(row: Int, col: Int): Int {
        var count = 0
        // Iterate over the neighboring cells of the specified cells.
        for (r in row - 1..row + 1) {
            for (c in col - 1..col + 1) {
                // Check if the neighboring cell is within bounds.
                if (r in 0 until gridSize && c in 0 until gridSize) {
                    // If the neighboring cell has a bomb, increment the count of neighboring bombs.
                    if (board[r][c].isBomb) {
                        count++
                    }
                }
            }
        }
        return count
    }

    // Method to uncover the cell at the specified row and column.
    fun uncoverCell(row: Int, col: Int) {
        // Check game is still running.
        if (!isGameOver) {
            // Get specified cell.
            val cell = board[row][col]
            // Check is cell is covered.
            if (cell.isCovered) {
                // Uncover cell and increment the count of uncovered cell.
                cell.uncover()
                uncoveredCount++
                // If uncovered cell is a bomb, end game.
                if (cell.isBomb) {
                    // If all non-bomb cells are uncovered, end game.
                    isGameOver = true
                } else if (uncoveredCount == gridSize * gridSize - difficultyLevel.numBombs) {
                    isGameOver = true
                } else if (cell.neighborBombs == 0) {
                    // If the uncovered cell has no neighboring bombs, recursively uncover the neighboring cells.
                    uncoverNeighborCells(row, col)
                }
            }
        }
    }

    // Method to uncover all neighboring cells of the specified cell recursively.
    private fun uncoverNeighborCells(row: Int, col: Int) {
        // Iterate over each neighboring cell of the specified cell.
        for (r in row - 1..row + 1) {
            for (c in col - 1..col + 1) {
                // Check if the neighboring cell is within bounds.
                if (r in 0 until gridSize && c in 0 until gridSize) {
                    // Check if the neighboring cell is not the specified cell.
                    if (!(r == row && c == col)) {
                        // Uncover neighboring cell.
                        uncoverCell(r, c)
                    }
                }
            }
        }
    }

    // Method to flag a specified cell if covered.
    fun flagCell(row: Int, col: Int) {
        // Check if the game is ongoing.
        if (!isGameOver) {
            val cell = board[row][col]
            // Check if cell is covered.
            if (cell.isCovered) {
                // Flag cell.
                cell.flag()
            }
        }
    }

    // Method to check if the player has won the game.
    fun isWin(): Boolean {
        // Counters
        var numFlaggedBombs = 0
        var numFlaggedNonBombs = 0
        var numUncoveredNonBombs = 0

        // Iterate over eah cell of the board.
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val cell = board[row][col]
                // Count number of flagged cells.
                if (cell.isBomb && cell.isFlagged) {
                    numFlaggedBombs++
                }
                // Count number of flagged non-bombs.
                if (!cell.isBomb && cell.isFlagged) {
                    numFlaggedNonBombs++
                }
                // Count number of uncovered non-bombs.
                if (!cell.isBomb && !cell.isCovered) {
                    numUncoveredNonBombs++
                }
            }
        }
        // Check win condition.
        return (numFlaggedBombs == difficultyLevel.numBombs || numFlaggedNonBombs == numUncoveredNonBombs)
                && numFlaggedBombs + numFlaggedNonBombs <= difficultyLevel.numBombs
    }

    // Method to check if the player has lost the game.
    fun isLose(): Boolean {
        // Iterate over each cell.
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val cell = board[row][col]
                // If the cell is a bomb and has exploded, return true to indicate that the player lost.
                if (cell.isBomb && cell.isExploded) {
                    return true
                }
            }
        }
        return false
    }
}