import kotlin.random.Random
import java.util.Scanner
import kotlin.system.exitProcess

val scanner = Scanner(System.`in`)

enum class DifficultyLevel(val gridSize: Int, val numBombs: Int) {
    EASY(3, 1),
    MEDIUM(10, 15),
    HARD(12, 20)
}

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
        var numFlaggedCells = 0
        var numUncoveredNonBombCells = 0
        val numNonBombCells = gridSize * gridSize - difficultyLevel.numBombs

        for (i in 0 until gridSize * gridSize) {
            val cell = board[i / gridSize][i % gridSize]
            if (cell.isFlagged) {
                numFlaggedCells++
            }
            if (!cell.isBomb && !cell.isCovered) {
                numUncoveredNonBombCells++
            }
        }

        return numFlaggedCells == difficultyLevel.numBombs && numUncoveredNonBombCells == numNonBombCells
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

fun main() {
    println("Welcome to Minesweeper!")
    println("Please select a difficulty level:")
    println("1. Easy (2x2, 1 bombs)")
    println("2. Medium (10x10, 15 bombs)")
    println("3. Hard (12x12, 20 bombs)")

    val input = scanner.nextInt()
    scanner.nextLine() // consume end-of-line character
    val level = when (input) {
        1 -> DifficultyLevel.EASY
        2 -> DifficultyLevel.MEDIUM
        3 -> DifficultyLevel.HARD
        else -> {
            println("Invalid input. Exiting game.")
            exitProcess(0)
        }
    }

    val game = Minesweeper(level)
    game.initializeBoard()
    printBoard(game.board)

    println("Enter command a (uncover, flag, quit):")
    println("An example of a command would be: flag 1 1")
    while (!game.isGameOver) {
        println("Enter your selection: ")
        val command = scanner.nextLine().split(" ")
        when (command[0]) {
            "uncover" -> {
                if (command.size < 3) {
                    println("Invalid command. Please try again.")
                    continue
                }
                val row = command[1].toIntOrNull()?.minus(1)
                val col = command[2].toIntOrNull()?.minus(1)
                if (row == null || col == null || row < 0 || row >= game.gridSize || col < 0 || col >= game.gridSize) {
                    println("Invalid input. Please enter valid row and column (e.g. '3 4'):")
                    continue
                }
                game.uncoverCell(row, col)
                printBoard(game.board)
                if (game.isWin()) {
                    println("Congratulations! You won!")
                    game.isGameOver = true
                } else if (game.isLose()) {
                    println("Game over. You lose!")
                    printBoard(game.board, revealBombs = true)
                    game.isGameOver = true
                }
            }
            "flag" -> {
                if (command.size < 3) {
                    println("Invalid command. Please try again.")
                    continue
                }
                val row = command[1].toIntOrNull()?.minus(1)
                val col = command[2].toIntOrNull()?.minus(1)
                if (row == null || col == null || row < 0 || row >= game.gridSize || col < 0 || col >= game.gridSize) {
                    println("Invalid input. Please enter valid row and column (e.g. '3 4'):")
                    continue
                }

                game.flagCell(row, col)
                printBoard(game.board)
                if (game.isWin()) {
                    println("Congratulations! You won!")
                    game.isGameOver = true
                }
            }
            "quit" -> {
                println("Exiting game.")
                exitProcess(0)
            }
            else -> {
                println("Invalid command. Please try again.")
            }
        }
    }
}

fun printBoard(board: Array<Array<Cell>>, revealBombs: Boolean = false) {
    val sb = StringBuilder()
    sb.appendLine()
    sb.appendLine("  | ${Array(board[0].size) { it + 1 }.joinToString(" ")}")
    sb.appendLine("--|-" + "-".repeat(board[0].size * 2))
    for (row in board.indices) {
        sb.append("${row + 1} | ")
        for (col in board[row].indices) {
            val cell = board[row][col]
            sb.append(
                when {
                    cell.isCovered && !cell.isFlagged -> "."
                    cell.isFlagged && cell.isCovered -> "F"
                    cell.isBomb && revealBombs -> "*"
                    cell.isBomb && !revealBombs -> "X"
                    cell.neighborBombs > 0 -> cell.neighborBombs.toString()
                    else -> " "
                }
            )
            sb.append(" ")
        }
        sb.appendLine()
    }
    println(sb.toString())
}