package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveValidator implements ChessMoveValidator{
    public Collection<ChessMove> pieceMoves (ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}, {1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessGame.TeamColor selfColor = board.getPiece(myPosition).getTeamColor();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int[] dir : directions) {
            for (int i = 1; i < 8; i++) {
                if (row + (dir[0] * i) < 1|| 8 < row + (dir[0] * i)|| col + (dir[1] * i) < 1 || 8 < col + (dir[1] * i)) {
                    break;
                }
                ChessPosition potentialPos = new ChessPosition(row + (dir[0] * i), col + (dir[1] * i));
                if (board.getPiece(potentialPos) != null) {
                    if(board.getPiece(potentialPos).getTeamColor() != selfColor) {
                        validMoves.add(new ChessMove(myPosition, potentialPos, null));
                    }
                    break;
                }
                validMoves.add(new ChessMove(myPosition, potentialPos, null));
            }
        }
        return validMoves;
    }
}