package com.kyhsgeekcode.fogofwarchess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
                            viewModel.visibleCoords.collectAsState().value,
                            onCLickCell = {
                                viewModel.onCellClicked(it, applicationContext)
                            })
                        Text(text = "Selected Piece: ${viewModel.selectedPiece.collectAsState().value}")
                        Text(text = "Game phase: ${viewModel.gamePhase.collectAsState().value}")
                    }
                    if (viewModel.promotingPawn.collectAsState().value != null) {
                        PromotionDialog(color = viewModel.currentTurn.collectAsState().value) {
                            viewModel.promotePiece(it)
                        }
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
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .weight(1f)
            .background(
                color = if (isVisible) {
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
            if (piece != null && isVisible) {
                Image(
                    painter =
                    painterResource(
                        id = if (piece.color == PieceColor.BLACK) piece.type.blackResId else piece.type.whiteResId
                    ),
                    contentDescription = piece.type.name,
                    modifier = modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
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
fun PromotionDialog(color: PieceColor, onClick: (PieceType) -> Unit) {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Surface(
            modifier = Modifier.size(400.dp, 300.dp),
            color = Color.White,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column {
                Text(text = "Promote to")
                for (type in arrayOf(
                    PieceType.QUEEN,
                    PieceType.ROOK,
                    PieceType.BISHOP,
                    PieceType.KNIGHT
                )) {
                    Row(modifier = Modifier
                        .clickable { onClick(type) }
                        .fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = if (color == PieceColor.BLACK) type.blackResId else type.whiteResId),
                            contentDescription = type.name,
                        )
                        Text(text = type.name)
                    }
                }
            }
        }
    }
}

@Composable
fun ChessBoard(
    board: BoardSnapshot,
    possibleMoves: List<Move>,
    visibleCoords: List<Coord>,
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
                        isVisible = visibleCoords.contains(Coord(cell.x, cell.y)),
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
            ChessBoard(
                Board().toSnapshot(),
                possibleMoves = listOf(),
                visibleCoords = listOf(),
                onCLickCell = {})
        }
    }
}