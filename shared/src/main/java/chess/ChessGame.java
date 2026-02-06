package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor teamTurn;
    ChessBoard mainBoard;


    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        mainBoard = new ChessBoard();
        mainBoard.resetBoard();
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
        if (mainBoard.getPiece(startPosition) != null){
            ArrayList<ChessMove> outMoves = new ArrayList<>();
            for (ChessMove move : mainBoard.getPiece(startPosition).pieceMoves(mainBoard, startPosition)) {
                if (isMoveSafe(move, mainBoard.getPiece(move.getStartPosition()).getTeamColor())){
                    outMoves.add(move);
                }
            }
            return outMoves;
        } else {
            return null;
        }
    }


    private ChessPosition findKingPosition(TeamColor teamColor, ChessBoard board){
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(pos);
                if(piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //Check if a friendly piece is at the position and if the move is valid
        if (mainBoard.getPiece(move.getStartPosition()) == null ||
                !mainBoard.getPiece(move.getStartPosition()).pieceMoves(mainBoard, move.getStartPosition()).contains(move)||
                mainBoard.getPiece(move.getStartPosition()).getTeamColor() != teamTurn
        ) {
            throw new InvalidMoveException();
        }
        //Make the move
        ChessPiece pieceMoving = mainBoard.getPiece(move.getStartPosition());
        boolean isPawn = false;
        if (move.getPromotionPiece() != null){
            pieceMoving = new ChessPiece(pieceMoving.getTeamColor(), move.getPromotionPiece());
            isPawn = true;
        }
        ChessPiece targetPiece = mainBoard.getPiece(move.getEndPosition());
        mainBoard.addPiece(move.getEndPosition(), pieceMoving);
        mainBoard.addPiece(move.getStartPosition(), null);
        //Reverse the move if it puts you in check
        if (isInCheck(pieceMoving.getTeamColor())) {
            if (isPawn) {
                mainBoard.addPiece(move.getStartPosition(), new ChessPiece(pieceMoving.getTeamColor(), ChessPiece.PieceType.PAWN));
            } else {
                mainBoard.addPiece(move.getStartPosition(), pieceMoving);
            }
            mainBoard.addPiece(move.getEndPosition(), targetPiece);
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
        ChessPosition kingPos = findKingPosition(teamColor, mainBoard);
        //Loop through each square on the board
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);

                //If there is no piece on the square or if the piece is on the team in question, skip this iteration.
                if (mainBoard.getPiece(currPosition) == null || mainBoard.getPiece(currPosition).getTeamColor() == teamColor) {
                    continue;
                }
                ChessPiece currPiece = mainBoard.getPiece(currPosition);
                Collection<ChessMove> currPossMoves = currPiece.pieceMoves(mainBoard, currPosition);

                //Check if each move for the current enemy piece could target the king
                for (ChessMove move : currPossMoves) {
                    if (kingPos.equals(move.getEndPosition())) {
                        return true;
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
                if (mainBoard.getPiece(currPosition) == null || mainBoard.getPiece(currPosition).getTeamColor() != teamColor) {
                    continue;
                }
                ChessPiece currPiece = mainBoard.getPiece(currPosition);
                Collection<ChessMove> currPossMoves = currPiece.pieceMoves(mainBoard, currPosition);
                //Check if each move for the current piece would leave the king in check
                for (ChessMove move : currPossMoves) {
                    if (isMoveSafe(move, teamColor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isMoveSafe(ChessMove move, TeamColor teamColor) {
        ChessBoard tempBoard = new ChessBoard(mainBoard);
        ChessPiece piece = tempBoard.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(teamColor, move.getPromotionPiece());
        }
        tempBoard.addPiece(move.getEndPosition(), piece);
        tempBoard.addPiece(move.getStartPosition(), null);
        ChessPosition kingPos = findKingPosition(teamColor, tempBoard);
        if (kingPos == null) return false;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition pos = new ChessPosition(i, j);
                ChessPiece enemyPiece = tempBoard.getPiece(pos);
                if (enemyPiece != null && enemyPiece.getTeamColor() != teamColor) {
                    for (ChessMove enemyMove : enemyPiece.pieceMoves(tempBoard, pos)) {
                        if (enemyMove.getEndPosition().equals(kingPos)) {
                            return false;
                        }
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
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                //If there is no piece on the square or if the piece is not on the team in question, skip this iteration.
                if (mainBoard.getPiece(currPosition) == null || mainBoard.getPiece(currPosition).getTeamColor() != teamColor) {
                    continue;
                }
                for (ChessMove move: mainBoard.getPiece(currPosition).pieceMoves(mainBoard, currPosition)) {
                    if(isMoveSafe(move, teamColor)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        mainBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return mainBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(mainBoard, chessGame.mainBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, mainBoard);
    }
}
