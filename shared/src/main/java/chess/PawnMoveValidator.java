package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;

public class PawnMoveValidator implements ChessMoveValidator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessGame.TeamColor selfColor = board.getPiece(position).getTeamColor();
        ChessPiece.PieceType[] promotionChoices = {QUEEN, BISHOP, ROOK, KNIGHT};
        //Add the valid moves if the pawn is white
        if (selfColor == ChessGame.TeamColor.WHITE) {
            //Try to move forward
            ChessPosition potentialPosition = new ChessPosition(row + 1, col);
            if (board.getPiece(potentialPosition) == null) {
                //Check if pawn is moving to the last row. If so, add all the potential promotion choices as moves.
                if(row == 7) {
                    for (int i = 0; i < 4; i++) {
                        validMoves.add(new ChessMove(position, potentialPosition, promotionChoices[i]));
                    }
                } else {
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                //Check for double-move opportunity
                if (row == 2 && board.getPiece(new ChessPosition(row + 2, col)) == null) {
                    validMoves.add(new ChessMove(position, new ChessPosition(row + 2, col), null));
                }
            }
            //Try to move right diagonally and capture
            potentialPosition = new ChessPosition(row + 1, col + 1);
            if(col < 8 && board.getPiece(potentialPosition) != null && board.getPiece(potentialPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                if(row == 7) {
                    for (int i = 0; i < 4; i++) {
                        validMoves.add(new ChessMove(position, potentialPosition, promotionChoices[i]));
                    }
                } else {
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
            }
            //Try to move left diagonally and capture
            potentialPosition = new ChessPosition(row + 1, col - 1);
            if(1 < col && board.getPiece(potentialPosition) != null && board.getPiece(potentialPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                if(row == 7) {
                    for (int i = 0; i < 4; i++) {
                        validMoves.add(new ChessMove(position, potentialPosition, promotionChoices[i]));
                    }
                } else {
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
            }
        }


        //Add the valid moves if the pawn is black
        if (selfColor == ChessGame.TeamColor.BLACK) {
            //Try to move forward
            ChessPosition potentialPosition = new ChessPosition(row - 1, col);
            if (board.getPiece(potentialPosition) == null) {
                //Check if pawn is moving to the last row. If so, add all the potential promotion choices as moves.
                if(row == 2) {
                    for (int i = 0; i < 4; i++) {
                        validMoves.add(new ChessMove(position, potentialPosition, promotionChoices[i]));
                    }
                } else {
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                if (row == 7 && board.getPiece(new ChessPosition(row - 2, col)) == null) {
                    validMoves.add(new ChessMove(position, new ChessPosition(row - 2, col), null));
                }
            }
            //Try to move right diagonally and capture
            potentialPosition = new ChessPosition(row - 1, col + 1);
            if(col < 8 && board.getPiece(potentialPosition) != null && board.getPiece(potentialPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                if(row == 2) {
                    for (int i = 0; i < 4; i++) {
                        validMoves.add(new ChessMove(position, potentialPosition, promotionChoices[i]));
                    }
                } else {
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
            }
            //Try to move left diagonally and capture
            potentialPosition = new ChessPosition(row - 1, col - 1);
            if(1 < col && board.getPiece(potentialPosition) != null && board.getPiece(potentialPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
                if(row == 2) {
                    for (int i = 0; i < 4; i++) {
                        validMoves.add(new ChessMove(position, potentialPosition, promotionChoices[i]));
                    }
                } else {
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
            }
        }
        return validMoves;
    }
}
