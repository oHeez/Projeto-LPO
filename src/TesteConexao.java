import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Teste de Conexão com Banco de Dados");
        System.out.println("========================================");
        System.out.println();
        
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        boolean conectado = dbConnection.testConnection();
        
        if (!conectado) {
            System.exit(1);
        }
    }
}
