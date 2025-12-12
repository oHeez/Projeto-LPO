package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static final String URL = "jdbc:mysql://localhost:3306/db_petshop";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar driver MySQL: " + e.getMessage());
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
    
    public boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexão com o banco de dados estabelecida com sucesso!");
                closeConnection(conn);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao conectar ao banco de dados:");
            System.err.println("   Mensagem: " + e.getMessage());
            System.err.println("   Verifique se:");
            System.err.println("   - O banco de dados 'db_petshop' existe");
            System.err.println("   - O servidor MySQL/MariaDB está rodando");
            System.err.println("   - Os dados de conexão estão corretos");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                closeConnection(conn);
            }
        }
        return false;
    }
}

