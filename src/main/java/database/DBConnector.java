package database;

import model.Credential;

import java.sql.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {
    private final String DB_PATH = "C:\\Users\\CCC\\My Drive\\MyAppData\\PasswordManager.db";

    private final String URL = "jdbc:sqlite:" + DB_PATH;

    public DBConnector() {
        createDatabaseIfNotExist();
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void createDatabaseIfNotExist() {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            try (Connection conn = connect()) {
                System.out.println("Databaza u krijua me sukses.");
                createTable(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createTable(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS credentials ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "website TEXT NOT NULL, "
                + "username TEXT NOT NULL, "
                + "password TEXT NOT NULL, "
                + "category TEXT NOT NULL"
                + ");";

        try (PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
            System.out.println("Tabela 'credentials' u krijua me sukses.");
        }
    }

    public void saveCredential(Credential credential) {
        String sql = "INSERT INTO credentials (website, username, password, category) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, credential.getWebsite());
            stmt.setString(2, credential.getUsername());
            stmt.setString(3, credential.getPassword());
            stmt.setString(4, credential.getCategory());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Credential> loadAllCredentials() {
        List<Credential> credentials = new ArrayList<>();
        String sql = "SELECT id, website, username, password, category FROM credentials";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                credentials.add(new Credential(
                        rs.getInt("id"),
                        rs.getString("website"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("category")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return credentials;
    }

    public void deleteCredential(int id) {
        String sql = "DELETE FROM credentials WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
