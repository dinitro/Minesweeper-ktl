import java.util.Scanner
import kotlin.system.exitProcess

val scanner = Scanner(System.`in`)

enum class DifficultyLevel(val gridSize: Int, val numBombs: Int) {
    EASY(3, 1),
    MEDIUM(10, 15),
    HARD(12, 20)
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