package com.kyhsgeekcode.fogofwarchess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kyhsgeekcode.fogofwarchess.ui.theme.FogOfWarChessTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FogOfWarChessTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Text(text = "Current Turn: ${viewModel.currentTurn.collectAsState().value}")
                        ChessBoard(
                            viewModel.boardSnapshot.collectAsState().value,
                            viewModel.possibleMoves.collectAsState().value,
                            onCLickCell = {
                                viewModel.onCellClicked(it)
                            })
                        Text(text = "Selected Piece: ${viewModel.selectedPiece.collectAsState().value}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ColumnScope.ChessCell(
    cell: Cell,
    piece: Piece?,
    possibleMoves: List<Move>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .weight(1f)
            .background(
                color = if (cell.visible) {
                    cell.color.getColor()
                } else {
                    Color.Gray
                }
            )
            .clickable { onClick() }
    )
    {
        Box(
            modifier = modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
//            Text(
//                text = cell.coordCode,
//                modifier = modifier.align(Alignment.Center),
//            )
            if (piece != null && cell.visible) {
                Text(
                    text = piece.type.name,
                    style = if (piece.color == PieceColor.WHITE) TextStyle.Default.copy(
                        drawStyle = Stroke(
                            miter = 8f,
                            width = 1f,
                            join = StrokeJoin.Round,
                        ),
                        color = piece.color.color
                    ) else TextStyle.Default,
                    modifier = modifier.align(Alignment.Center),
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
            if (possibleMoves.any { it.to == Coord(cell.x, cell.y) }) {
                Text(
                    text = "X",
                    style = TextStyle.Default,
                    modifier = modifier.align(Alignment.Center),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ChessBoard(
    board: BoardSnapshot,
    possibleMoves: List<Move>,
    onCLickCell: (Coord) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        for (row in board.cells) {
            Column(modifier = Modifier.weight(1f)) {
                for (cell in row) {
                    ChessCell(
                        cell,
                        board.pieces[cell.x to cell.y],
                        onClick = {
                            onCLickCell(Coord(cell.x, cell.y))
                        },
                        possibleMoves = possibleMoves,
                        modifier = modifier
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FogOfWarChessTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ChessBoard(Board().toSnapshot(), possibleMoves = listOf(), onCLickCell = {})
        }
    }
}