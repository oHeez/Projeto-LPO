package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ClienteDAO;
import model.Cliente;
import util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ClienteApiHandler implements HttpHandler {
    
    private ClienteDAO clienteDAO;
    
    public ClienteApiHandler() {
        this.clienteDAO = new ClienteDAO();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        try {
            if ("OPTIONS".equals(method)) {
                WebServer.sendResponse(exchange, 200, "application/json", "");
                return;
            }
            
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    WebServer.sendResponse(exchange, 405, "text/plain", "Metodo nao permitido");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Erro desconhecido";
            WebServer.sendResponse(exchange, 500, "application/json", 
                "{\"erro\":\"" + errorMsg + "\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws Exception {
        // GET /api/clientes/{id}
        if (path.matches("/api/clientes/\\d+")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);
            
            Cliente cliente = clienteDAO.buscarPorId(id);
            String json = JsonUtil.clienteToJson(cliente);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
        // GET /api/clientes
        else {
            List<Cliente> clientes = clienteDAO.listarTodos();
            String json = JsonUtil.clientesToJson(clientes);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
    }
    
    private void handlePost(HttpExchange exchange) throws Exception {
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Cliente cliente = JsonUtil.jsonToCliente(jsonMap);
        
        Cliente clienteInserido = clienteDAO.inserir(cliente);
        String json = JsonUtil.clienteToJson(clienteInserido);
        WebServer.sendResponse(exchange, 201, "application/json", json);
    }
    
    private void handlePut(HttpExchange exchange, String path) throws Exception {
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Cliente cliente = JsonUtil.jsonToCliente(jsonMap);
        cliente.setIdCliente(id);
        
        Cliente clienteAtualizado = clienteDAO.atualizar(cliente);
        String json = JsonUtil.clienteToJson(clienteAtualizado);
        WebServer.sendResponse(exchange, 200, "application/json", json);
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws Exception {
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        
        boolean deletado = clienteDAO.deletar(id);
        if (deletado) {
            WebServer.sendResponse(exchange, 200, "application/json", 
                "{\"mensagem\":\"Cliente deletado com sucesso\"}");
        } else {
            WebServer.sendResponse(exchange, 404, "application/json", 
                "{\"erro\":\"Cliente nao encontrado\"}");
        }
    }
    
    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}

