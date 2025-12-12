import server.WebServer;
import java.io.IOException;

public class App {
    
    public static void main(String[] args) {
        try {
            WebServer server = new WebServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
