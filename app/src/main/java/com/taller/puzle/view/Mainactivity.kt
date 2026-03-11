
package com.taller.puzle

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
import com.taller.Puzle.PuzzleViewModel
import com.taller.puzle.ui.theme.ColorCanMove
import com.taller.puzle.ui.theme.ColorHueco
import com.taller.puzle.ui.theme.ColorInPlace
import com.taller.puzle.ui.theme.ColorNormal
import com.taller.puzle.ui.theme.ColorResuelto

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
                StatCard(label = "Movimientos", value = "${s.moveCount}", emoji = "")
                Divider(
                    modifier = Modifier.height(40.dp).width(1.dp),
                    color = Color(0xFF2A2A5A)
                )
                StatCard(label = "Meta mínima", value = "${s.minMovesGoal}", emoji = "")
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
                text = " Nuevo Juego",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

5 KB