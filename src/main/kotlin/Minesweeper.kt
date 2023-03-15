import kotlin.random.Random

class Minesweeper(private val difficultyLevel: DifficultyLevel) {
    val gridSize = difficultyLevel.gridSize
    val board = Array(gridSize) { Array(gridSize) { Cell() } }
    private var uncoveredCount = 0
    var isGameOver = false

    fun initializeBoard() {
        placeBombs(difficultyLevel.numBombs)
        calculateNeighborBombs()
    }

    private fun placeBombs(numBombs: Int) {
        var bombsLeft = numBombs
        while (bombsLeft > 0) {
            val row = Random.nextInt(gridSize)
            val col = Random.nextInt(gridSize)
            if (!board[row][col].isBomb) {
                board[row][col] = Cell(isBomb = true)
                bombsLeft--
            }
        }
    }

    private fun calculateNeighborBombs() {
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val cell = board[row][col]
                if (!cell.isBomb) {
                    val neighborBombs = getNeighborBombs(row, col)
                    cell.neighborBombs = neighborBombs
                }
            }
        }
    }

    private fun getNeighborBombs(row: Int, col: Int): Int {
        var count = 0
        for (r in row - 1..row + 1) {
            for (c in col - 1..col + 1) {
                if (r in 0 until gridSize && c in 0 until gridSize) {
                    if (board[r][c].isBomb) {
                        count++
                    }
                }
            }
        }
        return count
    }

    fun uncoverCell(row: Int, col: Int) {
        if (!isGameOver) {
            val cell = board[row][col]
            if (cell.isCovered) {
                cell.uncover()
                uncoveredCount++
                if (cell.isBomb) {
                    isGameOver = true
                } else if (uncoveredCount == gridSize * gridSize - difficultyLevel.numBombs) {
                    isGameOver = true
                } else if (cell.neighborBombs == 0) {
                    uncoverNeighborCells(row, col)
                }
            }
        }
    }

    private fun uncoverNeighborCells(row: Int, col: Int) {
        for (r in row - 1..row + 1) {
            for (c in col - 1..col + 1) {
                if (r in 0 until gridSize && c in 0 until gridSize) {
                    if (!(r == row && c == col)) {
                        uncoverCell(r, c)
                    }
                }
            }
        }
    }

    fun flagCell(row: Int, col: Int) {
        if (!isGameOver) {
            val cell = board[row][col]
            if (cell.isCovered) {
                cell.flag()
            }
        }
    }

    fun isWin(): Boolean {
        var numBombs = 0
        var numFlaggedCells = 0
        var allNonBombsUncovered = true

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val cell = board[row][col]
                if (cell.isBomb) {
                    numBombs++
                    if (cell.isFlagged) {
                        numFlaggedCells++
                    }
                } else {
                    if (cell.isCovered) {
                        allNonBombsUncovered = false
                    }
                }
            }
        }
        return numFlaggedCells == numBombs && (allNonBombsUncovered || numBombs == 0)
    }

    fun isLose(): Boolean {
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val cell = board[row][col]
                if (cell.isBomb && cell.isExploded) {
                    return true
                }
            }
        }
        return false
    }
}