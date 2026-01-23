package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveValidator implements ChessMoveValidator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int[][] potentialMoves = {{0,1},{1,0},{0,-1},{-1,0}, {1,1},{1,-1},{-1,1},{-1,-1}};
        int row = position.getRow();
        int col = position.getColumn();
        ChessGame.TeamColor selfColor = board.getPiece(position).getTeamColor();
        for (int [] move : potentialMoves) {
            ChessPosition potentialPosition = new ChessPosition(row + move[0], col + move[1]);
            if (row + move[0] < 9 &&
                    0 < row + move[0] &&
                    col + move[1] < 9 &&
                    0 < col + move[1] &&
                    (board.getPiece(potentialPosition) == null ||
                            board.getPiece(potentialPosition).getTeamColor() != selfColor)
            ) {
                validMoves.add(new ChessMove(position, potentialPosition, null));
            }
        }
        return validMoves;
    }
}
