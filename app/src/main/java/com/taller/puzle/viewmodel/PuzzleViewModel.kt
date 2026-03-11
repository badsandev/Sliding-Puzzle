package com.taller.Puzle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taller.puzle.model.PuzzleState
import kotlin.math.abs

class PuzzleViewModel : ViewModel() {

    private val _state = MutableLiveData<PuzzleState>()
    val state: LiveData<PuzzleState> = _state

    private val solvedBoard = listOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
    private val cols = 3

    init {
        startNewGame()
    }

    fun startNewGame() {
        val shuffled = generateSolvableBoard()
        val minMoves = estimateMinMoves(shuffled)
        _state.value = PuzzleState(
            board = shuffled,
            moveCount = 0,
            minMovesGoal = minMoves,
            isSolved = false,
            message = ""
        )
    }

    fun onCellClicked(index: Int) {
        val current = _state.value ?: return
        if (current.isSolved) return

        val board = current.board.toMutableList()
        val clickedValue = board[index]

        // No se puede tocar el hueco
        if (clickedValue == 0) return

        val emptyIndex = board.indexOf(0)

        // Solo se mueve si la ficha es adyacente al hueco
        if (areAdjacent(index, emptyIndex)) {
            board[emptyIndex] = clickedValue
            board[index] = 0

            val newMoves = current.moveCount + 1
            val solved = board == solvedBoard
            val message = if (solved) buildVictoryMessage(newMoves, current.minMovesGoal) else ""

            _state.value = current.copy(
                board = board,
                moveCount = newMoves,
                isSolved = solved,
                message = message
            )
        }
    }

    private fun areAdjacent(i: Int, j: Int): Boolean {
        val ri = i / cols; val ci = i % cols
        val rj = j / cols; val cj = j % cols
        return (abs(ri - rj) + abs(ci - cj)) == 1
    }

    private fun generateSolvableBoard(): List<Int> {
        var board: List<Int>
        do {
            board = (0..8).shuffled()
        } while (!isSolvable(board) || board == solvedBoard)
        return board
    }

    private fun isSolvable(board: List<Int>): Boolean {
        val filtered = board.filter { it != 0 }
        var inversions = 0
        for (i in filtered.indices) {
            for (j in i + 1 until filtered.size) {
                if (filtered[i] > filtered[j]) inversions++
            }
        }
        return inversions % 2 == 0
    }

    private fun estimateMinMoves(board: List<Int>): Int {
        var total = 0
        for (i in board.indices) {
            val value = board[i]
            if (value == 0) continue
            val targetIndex = value - 1
            val currentRow = i / cols;          val currentCol = i % cols
            val targetRow = targetIndex / cols; val targetCol = targetIndex % cols
            total += abs(currentRow - targetRow) + abs(currentCol - targetCol)
        }
        return maxOf(4, total)
    }

    private fun buildVictoryMessage(moves: Int, goal: Int): String {
        return when {
            moves <= goal     -> " ¡Increíble! Igualaste la meta mínima ($goal movimientos)."
            moves <= goal + 3 -> " ¡Muy bien! Estuviste cerca de la meta ($goal movimientos)."
            else              -> " ¡Resuelto! Superaste la meta. Intenta en menos de $goal movimientos."
        }
    }
}