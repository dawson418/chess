package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardUI {
    public ChessBoard chessBoard;

    public BoardUI(ChessBoard board){
        chessBoard = board;
    }

    public String drawBoard(ChessGame.TeamColor perspective){
        StringBuilder sb = new StringBuilder();

        String[] headers = {EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY};

        if (perspective == ChessGame.TeamColor.BLACK) {
            headers = new String[]{EMPTY, " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", EMPTY};
        }
        drawHeader(sb, headers);
        int startRow = 1;
        int endRow = 8;
        int stepR = 1;
        if(perspective.equals(ChessGame.TeamColor.WHITE)){
            startRow = 8;
            endRow = 1;
            stepR = -1;
        }
        for (int r = startRow; (stepR > 0 ? r <= endRow : r >= endRow); r += stepR) {
            sb
                    .append(SET_BG_COLOR_BLACK)
                    .append(SET_TEXT_COLOR_WHITE)
                    .append(" ")
                    .append(r)
                    .append(" ");
            int startCol = 8;
            int endCol = 1;
            int stepC = -1;
            if (perspective.equals(ChessGame.TeamColor.WHITE)) {
                startCol = 1;
                endCol = 8;
                stepC = 1;
            }
            for (int c = startCol; (stepC > 0 ? c <= endCol : c >= endCol); c += stepC) {
                if ((r + c) % 2 != 0) {
                    sb.append(SET_BG_COLOR_WHITE);
                } else {
                    sb.append(SET_BG_COLOR_BLACK);
                }
                sb.append(getPieceRep(r, c));
            }
            sb
                    .append(SET_BG_COLOR_BLACK)
                    .append(SET_TEXT_COLOR_WHITE)
                    .append(" ")
                    .append(r)
                    .append(" ")
                    .append(RESET_BG_COLOR)
                    .append(RESET_TEXT_COLOR)
                    .append("\n");
        }
        drawHeader(sb, headers);
        return sb.toString();
    }

    private void drawHeader(StringBuilder sb, String[] headers){
        sb
                .append(SET_BG_COLOR_BLACK)
                .append(SET_TEXT_COLOR_WHITE);
        for (String h: headers){
            sb.append(h);
        }
        sb
                .append(RESET_BG_COLOR)
                .append(RESET_TEXT_COLOR)
                .append("\n");
    }

    private String getPieceRep(int r, int c){
        ChessPiece piece = chessBoard.getPiece(new ChessPosition(r, c));
        if (piece == null){
            return "   ";
        }
        ChessPiece.PieceType pieceType = piece.getPieceType();
        String outpiece;
        ChessGame.TeamColor color = piece.getTeamColor();
        if (color.equals(ChessGame.TeamColor.BLACK)){
            outpiece = SET_TEXT_COLOR_BLUE;
        } else {
            outpiece = SET_TEXT_COLOR_RED;
        }
        switch(pieceType){
            case PAWN -> outpiece += BLACK_PAWN;
            case BISHOP -> outpiece += BLACK_BISHOP;
            case KING -> outpiece += BLACK_KING;
            case QUEEN -> outpiece += BLACK_QUEEN;
            case KNIGHT -> outpiece += BLACK_KNIGHT;
            case ROOK -> outpiece += BLACK_ROOK;
            default -> outpiece += EMPTY;
        }
        return outpiece;
    }
}
