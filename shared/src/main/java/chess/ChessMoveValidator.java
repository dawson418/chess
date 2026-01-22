package chess;

import java.util.Collection;

public interface ChessMoveValidator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}