import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CriarBanco {
    private static final String URL = "jdbc:mysql://localhost:3306";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP padrão: sem senha
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Criando Banco de Dados Petshop");
        System.out.println("========================================");
        System.out.println();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = conn.createStatement();
            
            String sqlScript = new String(Files.readAllBytes(Paths.get("sql/criar_banco_corrigido.sql")));
            String[] statements = sqlScript.split(";");
            
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty() && !statement.startsWith("--")) {
                    try {
                        stmt.execute(statement);
                        System.out.println("✓ Executado: " + statement.substring(0, Math.min(50, statement.length())) + "...");
                    } catch (Exception e) {
                        System.err.println("✗ Erro: " + e.getMessage());
                    }
                }
            }
            
            stmt.close();
            conn.close();
            System.out.println("\n✅ Banco de dados criado com sucesso!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
