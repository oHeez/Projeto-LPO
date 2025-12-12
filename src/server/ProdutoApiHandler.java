package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.ProdutoDAO;
import model.Produto;
import util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ProdutoApiHandler implements HttpHandler {
    
    private ProdutoDAO produtoDAO;
    
    public ProdutoApiHandler() {
        this.produtoDAO = new ProdutoDAO();
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
        // GET /api/produtos/categoria/{categoria}
        if (path.startsWith("/api/produtos/categoria/")) {
            String categoria = path.substring("/api/produtos/categoria/".length());
            List<Produto> produtos = produtoDAO.buscarPorCategoria(categoria);
            String json = JsonUtil.produtosToJson(produtos);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
        // GET /api/produtos/{id}
        else if (path.matches("/api/produtos/\\d+")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);
            
            Produto produto = produtoDAO.buscarPorId(id);
            String json = JsonUtil.produtoToJson(produto);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
        // GET /api/produtos
        else {
            List<Produto> produtos = produtoDAO.listarTodos();
            String json = JsonUtil.produtosToJson(produtos);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
    }
    
    private void handlePost(HttpExchange exchange) throws Exception {
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Produto produto = JsonUtil.jsonToProduto(jsonMap);
        
        Produto produtoInserido = produtoDAO.inserir(produto);
        String json = JsonUtil.produtoToJson(produtoInserido);
        WebServer.sendResponse(exchange, 201, "application/json", json);
    }
    
    private void handlePut(HttpExchange exchange, String path) throws Exception {
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Produto produto = JsonUtil.jsonToProduto(jsonMap);
        produto.setIdProduto(id);
        
        Produto produtoAtualizado = produtoDAO.atualizar(produto);
        String json = JsonUtil.produtoToJson(produtoAtualizado);
        WebServer.sendResponse(exchange, 200, "application/json", json);
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws Exception {
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        
        boolean deletado = produtoDAO.deletar(id);
        if (deletado) {
            WebServer.sendResponse(exchange, 200, "application/json", 
                "{\"mensagem\":\"Produto deletado com sucesso\"}");
        } else {
            WebServer.sendResponse(exchange, 404, "application/json", 
                "{\"erro\":\"Produto nao encontrado\"}");
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

