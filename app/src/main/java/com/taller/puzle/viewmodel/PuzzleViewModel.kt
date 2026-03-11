import com.taller.puzle.viewmodel.PuzzleViewModel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.puzle.ui.theme.ColorCanMove
import com.taller.puzle.ui.theme.ColorHueco
import com.taller.puzle.ui.theme.ColorInPlace
import com.taller.puzle.ui.theme.ColorNormal
import com.taller.puzle.ui.theme.ColorResuelto
import kotlin.math.abs

class MainActivity : ComponentActivity() {

    private val viewModel: PuzzleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF1A1A2E)
            ) {
                PuzzleScreen(viewModel)
            }
        }
    }
}

@Composable
fun PuzzleScreen(viewModel: PuzzleViewModel) {
    val state by viewModel.state.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = " Puzzle Deslizante",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFE0E0FF),
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Toca una ficha para deslizarla al hueco",
                fontSize = 14.sp,
                color = Color(0xFF9090BB),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        state?.let { s ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF16213E))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(label = "Movimientos", value = "${s.moveCount}", emoji = "🎯")
                Divider(
                    modifier = Modifier.height(40.dp).width(1.dp),
                    color = Color(0xFF2A2A5A)
                )
                StatCard(label = "Meta mínima", value = "${s.minMovesGoal}", emoji = "⭐")
            }
        }

        state?.let { s ->
            PuzzleBoard(
                board = s.board,
                isSolved = s.isSolved,
                onCellClick = { viewModel.onCellClicked(it) }
            )
        }

        state?.let { s ->
            if (s.message.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F3460))
                        .padding(16.dp)
                ) {
                    Text(
                        text = s.message,
                        color = Color(0xFFFFD700),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(52.dp))
            }
        }

        Button(
            onClick = { viewModel.startNewGame() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF533483))
        ) {
            Text(
                text = "  Nuevo Juego",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun PuzzleBoard(
    board: List<Int>,
    isSolved: Boolean,
    onCellClick: (Int) -> Unit
) {
    val emptyIndex = board.indexOf(0)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F3460))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        val value = board[index]
                        val isEmpty = value == 0
                        val isInPlace = !isEmpty && value == index + 1
                        val ri = index / 3; val ci = index % 3
                        val re = emptyIndex / 3; val ce = emptyIndex % 3
                        val canMove = !isEmpty &&
                                (abs(ri - re) + abs(ci - ce)) == 1

                        PuzzleCell(
                            value = value,
                            isEmpty = isEmpty,
                            isInPlace = isInPlace && !isSolved,
                            canMove = canMove && !isSolved,
                            isSolved = isSolved,
                            modifier = Modifier.weight(1f),
                            onClick = { onCellClick(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PuzzleCell(
    value: Int,
    isEmpty: Boolean,
    isInPlace: Boolean,
    canMove: Boolean,
    isSolved: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val targetColor = when {
        isEmpty   -> Color(0xFF0A0A1A)
        isSolved  -> ColorResuelto
        isInPlace -> ColorInPlace
        canMove   -> ColorCanMove
        else      -> ColorNormal
    }

    val bgColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 180),
        label = "cellColor"
    )

    val borderColor = when {
        isEmpty   -> ColorHueco
        isSolved  -> ColorResuelto
        canMove   -> ColorCanMove
        isInPlace -> ColorInPlace
        else      -> ColorNormal
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .shadow(if (canMove) 6.dp else 2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (canMove) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = canMove) { onClick() }
    ) {
        if (!isEmpty) {
            Text(
                text = "$value",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFE8EAF6),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatCard(label: String, value: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 20.sp)
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFE0E0FF)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF8080BB)
        )
    }
}