import java.lang.NumberFormatException
import java.util.Scanner
import kotlin.system.exitProcess

val scanner = Scanner(System.`in`)
val sb = StringBuilder()

// Define difficulty level class with set grid size and number of bombs.
enum class DifficultyLevel(val gridSize: Int, val numBombs: Int) {
    EASY(3, 1),
    MEDIUM(10, 15),
    HARD(12, 20)
}
fun main() {
    println("Welcome to Minesweeper!")
    println("Please select a difficulty level:")
    println("1. Easy (3x3, 1 bomb)")
    println("2. Medium (10x10, 15 bombs)")
    println("3. Hard (12x12, 20 bombs)")

    // Get user input for difficulty level.
    val input: Int
    try {
        input = scanner.nextInt()
        scanner.nextLine()
    } catch (e: NumberFormatException) {
        println("Invalid input. Please enter a valid integer.")
        exitProcess(0)
    }

    // Assign difficulty level
    val level = when (input) {
        1 -> DifficultyLevel.EASY
        2 -> DifficultyLevel.MEDIUM
        3 -> DifficultyLevel.HARD
        else -> {
            println("Invalid input. Exiting game.")
            exitProcess(0)
        }
    }

    // Create new game instance based on selected difficulty.
    val game = Minesweeper(level)
    game.initializeBoard()
    // Print board.
    printBoard(game.board, sb)

    // User prompt for commands to play game.
    println("Enter command a (uncover, flag, quit):")
    println("An example command would be: flag 1 1")

    // Counter variable.
    var invalidCommandCount = 0

    // Loop until game ends.
    while (!game.isGameOver) {
        // Get next move.
        println("Enter your selection: ")
        val command = scanner.nextLine().split(" ")
        // Process command based on type and argument.
        when (command[0]) {
            "uncover" -> {
                if (command.size < 3) {
                    println("Invalid command. Please try again.")
                    continue
                }
                // Convert row and column arguments to integers with an offset.
                val row = command[1].toIntOrNull()?.minus(1)
                val col = command[2].toIntOrNull()?.minus(1)

                // Check validity of input based on grid size.
                if (row == null || col == null || row < 0 || row >= game.gridSize || col < 0 || col >= game.gridSize) {
                    println("Invalid input. Please enter valid row and column (e.g. '3 4'):")
                    continue
                }
                // Uncover selected cell and update game state.
                game.uncoverCell(row, col)
                printBoard(game.board, sb)

                // Check if game is won or lost and update game state.
                if (game.isWin()) {
                    println("Congratulations! You won!")
                    game.isGameOver = true
                } else if (game.isLose()) {
                    println("Game over. You lose!")
                    printBoard(game.board, sb, revealBombs = true)
                    game.isGameOver = true
                }

                // Reset command counter.
                invalidCommandCount = 0
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

                // Call flagCell from Minesweeper method to flag selected cell.
                game.flagCell(row, col)
                printBoard(game.board, sb)
                // Check if game has been won after flagging the cell.
                if (game.isWin()) {
                    println("Congratulations! You won!")
                    game.isGameOver = true
                }

                // Reset command counter.
                invalidCommandCount = 0
            }
            "quit" -> {
                println("Exiting game.")
                exitProcess(0)
            }
            else -> {
                invalidCommandCount++
                if (invalidCommandCount >= 3) {
                    println("Too many invalid commands. Exiting.")
                    exitProcess(0)
                }
                println("Invalid command. Please try again.")
            }
        }
    }
}

fun printBoard(board: Array<Array<Cell>>, sb: StringBuilder, revealBombs: Boolean = false) {
    sb.clear()
    sb.appendLine()
    // Start counting at 1 instead of 0.
    sb.appendLine("  | ${Array(board[0].size) { it + 1 }.joinToString(" ")}")
    // Separator.
    sb.appendLine("--|-" + "-".repeat(board[0].size * 2))
    for (row in board.indices) {
        sb.append("${row + 1} | ")
        for (col in board[row].indices) {
            val cell = board[row][col]
            // Display cell symbol based on state of the cell.
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