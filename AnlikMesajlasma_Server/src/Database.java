
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

public class Database {
    private Connection sql;
    
    public Database(String path) {
        try {
            File dbFile = new File(path);
            boolean yeniDb = false;
            if(!dbFile.exists())
                yeniDb = true;
            
            sql = DriverManager.getConnection("jdbc:sqlite:" + path);
            
            if(yeniDb)
                dbKur();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void dbKur() throws SQLException {
        String usersStatement = "CREATE TABLE IF NOT EXISTS users (\n"
                            + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                            + "	username text NOT NULL,\n"
                            + " password text NOT NULL,\n"
                            + "	adsoyad text\n"
                            + ");";
        
        String usersUniqueStatement = "CREATE UNIQUE INDEX unique_username ON users(username);";
        
        String messagesStatement = "CREATE TABLE IF NOT EXISTS messages (\n"
                            + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
                            + "	fromID integer NOT NULL,\n"
                            + "	toID integer NOT NULL,\n"
                            + " mesaj text NOT NULL\n"
                            + ");";
        
        Statement tableStatement = this.sql.createStatement();
        tableStatement.execute(usersStatement);
        tableStatement.execute(usersUniqueStatement);
        tableStatement.execute(messagesStatement);
    }
    
    public Client getClient(Long id) throws SQLException {
        Statement statement = this.sql.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE id = " + id);
        while (resultSet.next()) {
            Long cID = resultSet.getLong("id");
            String username = resultSet.getString("username");
            String adSoyad = resultSet.getString("adsoyad");
            
            Client client = new Client(cID, username, adSoyad);
            return client;
        }
        return null;
    }
    
    public boolean girisKontrol(String username, String password) throws SQLException {
        Statement statement = this.sql.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT password FROM users WHERE username = '" + username + "'");
        while (resultSet.next()) {
            String cPassword = resultSet.getString("password");
            
            if(!password.equals(cPassword))
                return false;
            else
                return true;
        }
        return false;
    }
    
    public Client getClientByUsername(String username) throws SQLException {
        Statement statement = this.sql.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users WHERE username = '" + username + "'");
        while (resultSet.next()) {
            Long cID = resultSet.getLong("id");
            String cUsername = resultSet.getString("username");
            String adSoyad = resultSet.getString("adsoyad");
            
            Client client = new Client(cID, username, adSoyad);
            return client;
        }
        return null;
    }
    
    public Long addClient(Client client) throws SQLException {
        PreparedStatement statement = this.sql.prepareStatement(
            "INSERT INTO users (username, adsoyad) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );
        statement.setString(1, client.getUsername());
        statement.setString(2, client.getAdSoyad());
        int sonuc = 0;
        try {
            sonuc = statement.executeUpdate();
        } catch (SQLiteException ex) {
            if(ex.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE)
                return -1L;
        }
        
        if(sonuc > 0) {
            ResultSet clientIDResultSet = statement.getGeneratedKeys();
            clientIDResultSet.next();
            return clientIDResultSet.getLong(1);
        } else {
            return -1L;
        }
    }
    
    public boolean writeMessage(Client fromClient, Client toClient, String mesaj) throws SQLException {
        PreparedStatement statement = this.sql.prepareStatement(
          "INSERT INTO messages (fromID, toID, mesaj) VALUES (?, ?, ?)"
        );
        statement.setLong(1, fromClient.getId());
        statement.setLong(2, toClient.getId());
        statement.setString(3, mesaj);
        return statement.execute();
    }
    
    public void close() throws SQLException {
        sql.close();
    }
    
}
