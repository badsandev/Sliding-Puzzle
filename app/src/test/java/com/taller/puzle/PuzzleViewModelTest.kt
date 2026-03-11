package com.taller.puzle

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.taller.puzle.model.PuzzleState
import com.taller.puzle.viewmodel.PuzzleViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PuzzleViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PuzzleViewModel

    @Before
    fun setUp() {
        viewModel = PuzzleViewModel()
    }

    // Test 1: Verifica que el tablero inicia correctamente
    @Test
    fun `el tablero inicial tiene 9 celdas y moveCount es 0`() {
        val state = viewModel.state.value

        assertNotNull(state)
        assertEquals(9, state!!.board.size)
        assertEquals(0, state.moveCount)
        assertFalse(state.isSolved)
    }

    // Test 2: Verifica que una ficha adyacente se mueve correctamente
    @Test
    fun `mover una ficha adyacente al hueco incrementa moveCount`() {
        setState(PuzzleState(
            board = listOf(1, 2, 3, 4, 5, 6, 7, 8, 0),
            moveCount = 0,
            minMovesGoal = 4,
            isSolved = false,
            message = ""
        ))

        viewModel.onCellClicked(7)

        val state = viewModel.state.value!!
        assertEquals(1, state.moveCount)
        assertEquals(0, state.board[7])
        assertEquals(8, state.board[8])
    }

    // Test 3: Verifica que se detecta la victoria
    @Test
    fun `el puzzle se marca como resuelto cuando el tablero queda en orden`() {
        setState(PuzzleState(
            board = listOf(1, 2, 3, 4, 5, 6, 7, 0, 8),
            moveCount = 0,
            minMovesGoal = 2,
            isSolved = false,
            message = ""
        ))

        viewModel.onCellClicked(8)

        val state = viewModel.state.value!!
        assertTrue(state.isSolved)
        assertTrue(state.message.isNotEmpty())
    }

    private fun setState(state: PuzzleState) {
        val field = PuzzleViewModel::class.java.getDeclaredField("_state")
        field.isAccessible = true
        val liveData = field.get(viewModel) as MutableLiveData<PuzzleState>
        liveData.value = state
    }
}