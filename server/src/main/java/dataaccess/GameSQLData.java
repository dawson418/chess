package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameSQLData extends SQLData implements GameDataAccess{

    public GameSQLData() throws DataAccessException {
        configureDatabase();
        Gson gson = new Gson();
    }

    private Gson gson;

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    public GameData createGame(String name) throws DataAccessException {
        var statement = "INSERT INTO game (whiteuser, blackuser, name, chessgame) VALUES (?, ?, ?, ?)";
        ChessGame game = new ChessGame();
        String jsonGame = gson.toJson(game);
        int gameID = executeUpdate(statement, null, null, jsonGame);
        return new GameData(gameID, null, null, name, game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM game WHERE gameid=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1,gameID);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()) {
                        return new GameData(
                                rs.getInt("gameid"),
                                rs.getString("whiteuser"),
                                rs.getString("blackuser"),
                                rs.getString("name"),
                                gson.fromJson(rs.getString("chess"), ChessGame.class)
                                );
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean isEmpty(ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        String column;
        if (playerColor == ChessGame.TeamColor.WHITE){
            column = "whiteuser";
        } else {
            column = "blackuser";
        }
        String statement = String.format("SELECT %s FROM game WHERE gameid=?", column);
        try(Connection conn = DatabaseManager.getConnection()){
            try(PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()){
                    if (rs.next()){
                        return  rs.getString(column) == null;
                    } else {
                        throw new BadRequestException();
                    }
                }
            }
        } catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }

    }

    @Override
    public void joinGame(ChessGame.TeamColor playerColor, int gameID, String username) throws DataAccessException {
        String column;
        if (playerColor == ChessGame.TeamColor.WHITE){
            column = "whiteuser";
        } else {
            column = "blackuser";
        }
        String statement = String.format("UPDATE game SET %s=? WHERE gameid=?", column);
        executeUpdate(statement, username, gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()){
            String statement = "SELECT * FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()){
                        games.add(new GameData(
                                rs.getInt("gameid"),
                                rs.getString("whiteuser"),
                                rs.getString("blackuser"),
                                rs.getString("name"),
                                gson.fromJson(rs.getString("chessgame"), ChessGame.class)
                        ));
                    }
                }
            }
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        return games;
    }
}
