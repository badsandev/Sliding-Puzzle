package com.taller.puzle.model

data class PuzzleState( val board: List<Int>,
                        val moveCount: Int,
                        val minMovesGoal: Int,
                        val isSolved: Boolean,
                        val message: String)
