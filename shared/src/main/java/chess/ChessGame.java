package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard currentBoard;
    ChessPosition wKingPos;
    ChessPosition bKingPos;


    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        currentBoard = new ChessBoard();
        wKingPos = new ChessPosition(1, 5);
        bKingPos = new ChessPosition(8, 5);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (currentBoard.getPiece(startPosition) != null){
            return currentBoard.getPiece(startPosition).pieceMoves(currentBoard, startPosition);
        } else {
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //Check if a friendly piece is at the position and if the move is valid
        if (currentBoard.getPiece(move.getStartPosition()) == null ||
                !currentBoard.getPiece(move.getStartPosition()).pieceMoves(currentBoard, move.getStartPosition()).contains(move)||
                currentBoard.getPiece(move.getStartPosition()).getTeamColor() != teamTurn
        ) {
            throw new InvalidMoveException();
        }
        //Make the move
        ChessPiece pieceMoving = currentBoard.getPiece(move.getStartPosition());
        boolean isPawn = false;
        if (move.getPromotionPiece() != null){
            pieceMoving = new ChessPiece(pieceMoving.getTeamColor(), move.getPromotionPiece());
            isPawn = true;
        }
        ChessPiece targetPiece = currentBoard.getPiece(move.getEndPosition());
        currentBoard.addPiece(move.getEndPosition(), pieceMoving);
        currentBoard.addPiece(move.getStartPosition(), null);
        //Update the king position variable if the piece moving is a king
        if(pieceMoving.getPieceType() == ChessPiece.PieceType.KING){
            switch(pieceMoving.getTeamColor()) {
                case WHITE -> wKingPos = move.getEndPosition();
                case BLACK -> bKingPos = move.getEndPosition();
            }
        }
        //Reverse the move if it puts you in check
        if (isInCheck(pieceMoving.getTeamColor())) {
            if (isPawn) {
                currentBoard.addPiece(move.getStartPosition(), new ChessPiece(pieceMoving.getTeamColor(), ChessPiece.PieceType.PAWN));
            } else {
                currentBoard.addPiece(move.getStartPosition(), pieceMoving);
            }
            currentBoard.addPiece(move.getEndPosition(), targetPiece);
            //Undo the king position variable update
            if(pieceMoving.getPieceType() == ChessPiece.PieceType.KING){
                switch(pieceMoving.getTeamColor()) {
                    case WHITE -> {wKingPos = move.getStartPosition();}
                    case BLACK -> {bKingPos = move.getStartPosition();}
                }
            }
            throw new InvalidMoveException();
        }
        //Pass the turn
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //Loop through each square on the board
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                //If there is no piece on the square or if the piece is on the team in question, skip this iteration.
                if (currentBoard.getPiece(currPosition) == null || currentBoard.getPiece(currPosition).getTeamColor() == teamColor) {
                    continue;
                }
                ChessPiece currPiece = currentBoard.getPiece(currPosition);
                Collection<ChessMove> currPossMoves = currPiece.pieceMoves(currentBoard, currPosition);
                //Check if each move for the current enemy piece could target the king
                for (ChessMove move : currPossMoves) {
                    switch(teamColor){
                        case BLACK -> {
                            if (move.getEndPosition().equals(bKingPos)) {
                                return true;
                            }
                        }
                        case WHITE -> {
                            if (move.getEndPosition().equals(wKingPos)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        //Loop through each square on the board
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                //If there is no piece on the square or if the piece is not on the team in question, skip this iteration.
                if (currentBoard.getPiece(currPosition) == null || currentBoard.getPiece(currPosition).getTeamColor() != teamColor) {
                    continue;
                }
                ChessPiece currPiece = currentBoard.getPiece(currPosition);
                Collection<ChessMove> currPossMoves = currPiece.pieceMoves(currentBoard, currPosition);
                //Check if each move for the current piece would leave the king in check
                for (ChessMove move : currPossMoves) {
                    ChessPiece originalPiece = currentBoard.getPiece(move.getStartPosition());
                    try {
                        ChessPiece targetPiece = currentBoard.getPiece(move.getEndPosition());
                        ChessPiece pieceMoving = currentBoard.getPiece(move.getStartPosition());
                        makeMove(move);
                        currentBoard.addPiece(move.getStartPosition(), originalPiece);
                        currentBoard.addPiece(move.getEndPosition(), targetPiece);
                        if(pieceMoving.getPieceType() == ChessPiece.PieceType.KING){
                            switch(pieceMoving.getTeamColor()) {
                                case WHITE -> {wKingPos = move.getStartPosition();}
                                case BLACK -> {bKingPos = move.getStartPosition();}
                            }
                        }
                        TeamColor op = getTeamTurn();
                        switch(op){
                            case BLACK -> setTeamTurn(TeamColor.WHITE);
                            case WHITE -> setTeamTurn(TeamColor.BLACK);
                        }
                        return false;
                    }
                    catch (InvalidMoveException e) {

                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }
}
