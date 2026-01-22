package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveValidator implements ChessMoveValidator{
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessGame.TeamColor selfColor = board.getPiece(position).getTeamColor();
        //Add the valid moves below the piece
        for (int i = 1; i < 8; i++) {
            ChessPosition potentialPosition = new ChessPosition(row, col - i);
            if(col - i < 1){
                break;
            }
            if(board.getPiece(potentialPosition)!=null){
                if(board.getPiece(potentialPosition).getTeamColor() != selfColor){
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(position, potentialPosition, null));
        }
        //Add the valid moves above the piece
        for (int i = 1; i < 8; i++) {
            ChessPosition potentialPosition = new ChessPosition(row, col + i);
            if(col + i > 8){
                break;
            }
            if(board.getPiece(potentialPosition)!=null){
                if(board.getPiece(potentialPosition).getTeamColor() != selfColor){
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(position, potentialPosition, null));
        }
        //Add the valid moves to the left of the piece
        for (int i = 1; i < 8; i++) {
            ChessPosition potentialPosition = new ChessPosition(row - i, col);
            if(row - i < 1){
                break;
            }
            if(board.getPiece(potentialPosition)!=null){
                if(board.getPiece(potentialPosition).getTeamColor() != selfColor){
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(position, potentialPosition, null));
        }
        //Add the valid moves to the right of the piece
        for (int i = 1; i < 8; i++) {
            ChessPosition potentialPosition = new ChessPosition(row + i, col);
            if(row + i > 8){
                break;
            }
            if(board.getPiece(potentialPosition)!=null){
                if(board.getPiece(potentialPosition).getTeamColor() != selfColor){
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(position, potentialPosition, null));
        }
        //Add the valid moves northeast of the piece
        for (int i = 1; i < 8; i++) {
            ChessPosition potentialPosition = new ChessPosition(row + i, col + i);
            if(col + i > 8 || row + i > 8){
                break;
            }
            if(board.getPiece(potentialPosition)!=null){
                if(board.getPiece(potentialPosition).getTeamColor() != selfColor){
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(position, potentialPosition, null));
        }
        //Add the valid moves northwest of the piece
        for (int i = 1; i < 8; i++) {
            ChessPosition potentialPosition = new ChessPosition(row + i, col - i);
            if(col - i < 1 || row + i > 8){
                break;
            }
            if(board.getPiece(potentialPosition)!=null){
                if(board.getPiece(potentialPosition).getTeamColor() != selfColor){
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(position, potentialPosition, null));
        }
        //Add the valid moves southeast of the piece
        for (int i = 1; i < 8; i++) {
            ChessPosition potentialPosition = new ChessPosition(row - i, col + i);
            if(row - i < 1 || col + i > 8){
                break;
            }
            if(board.getPiece(potentialPosition)!=null){
                if(board.getPiece(potentialPosition).getTeamColor() != selfColor){
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(position, potentialPosition, null));
        }
        //Add the valid moves southwest of the piece
        for (int i = 1; i < 8; i++) {
            ChessPosition potentialPosition = new ChessPosition(row - i, col - i);
            if(row - i < 1 || col - i < 1){
                break;
            }
            if(board.getPiece(potentialPosition)!=null){
                if(board.getPiece(potentialPosition).getTeamColor() != selfColor){
                    validMoves.add(new ChessMove(position, potentialPosition, null));
                }
                break;
            }
            validMoves.add(new ChessMove(position, potentialPosition, null));
        }
        return validMoves;
    }
}
