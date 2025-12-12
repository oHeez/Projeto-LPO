package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebServer {
    
    private static final int PORT = 8080;
    private static final String WEB_DIR = "web";
    private HttpServer server;
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/clientes", new ClienteApiHandler());
        server.createContext("/api/funcionarios", new FuncionarioApiHandler());
        server.createContext("/api/pets", new PetApiHandler());
        server.createContext("/api/produtos", new ProdutoApiHandler());
        server.createContext("/", new StaticFileHandler());
        
        server.setExecutor(null); // Usa thread pool padrão
        server.start();
        
        System.out.println("========================================");
        System.out.println("  Servidor HTTP iniciado com sucesso!");
        System.out.println("========================================");
        System.out.println("  URL: http://localhost:" + PORT);
        System.out.println("  Diretorio web: " + WEB_DIR);
        System.out.println("========================================");
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Servidor HTTP parado.");
        }
    }
    
    private static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }
            
            Path filePath = Paths.get(WEB_DIR + requestPath);
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                sendResponse(exchange, 404, "text/plain", "Arquivo nao encontrado");
                return;
            }
            
            byte[] fileBytes = Files.readAllBytes(filePath);
            String contentType = getContentType(requestPath);
            sendResponse(exchange, 200, contentType, new String(fileBytes, "UTF-8"));
        }
        
        private String getContentType(String filename) {
            if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
            if (filename.endsWith(".css")) return "text/css; charset=UTF-8";
            if (filename.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (filename.endsWith(".json")) return "application/json; charset=UTF-8";
            return "text/plain; charset=UTF-8";
        }
    }
    
    public static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
            return;
        }
        
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
    
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
