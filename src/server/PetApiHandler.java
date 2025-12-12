package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PetDAO;
import model.Pet;
import util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PetApiHandler implements HttpHandler {
    
    private PetDAO petDAO;
    
    public PetApiHandler() {
        this.petDAO = new PetDAO();
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
        // GET /api/pets/cliente/{idCliente}
        if (path.startsWith("/api/pets/cliente/")) {
            String[] parts = path.split("/");
            int idCliente = Integer.parseInt(parts[parts.length - 1]);
            
            List<Pet> pets = petDAO.buscarPorCliente(idCliente);
            String json = JsonUtil.petsToJson(pets);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
        // GET /api/pets/{id}
        else if (path.matches("/api/pets/\\d+")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);
            
            Pet pet = petDAO.buscarPorId(id);
            String json = JsonUtil.petToJson(pet);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
        // GET /api/pets
        else {
            List<Pet> pets = petDAO.listarTodos();
            String json = JsonUtil.petsToJson(pets);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
    }
    
    private void handlePost(HttpExchange exchange) throws Exception {
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Pet pet = criarPetDoJson(jsonMap);
        
        Pet petInserido = petDAO.inserir(pet);
        String json = JsonUtil.petToJson(petInserido);
        WebServer.sendResponse(exchange, 201, "application/json", json);
    }
    
    private void handlePut(HttpExchange exchange, String path) throws Exception {
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Pet pet = criarPetDoJson(jsonMap);
        pet.setIdPet(id);
        
        Pet petAtualizado = petDAO.atualizar(pet);
        String json = JsonUtil.petToJson(petAtualizado);
        WebServer.sendResponse(exchange, 200, "application/json", json);
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws Exception {
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        
        boolean deletado = petDAO.deletar(id);
        if (deletado) {
            WebServer.sendResponse(exchange, 200, "application/json", 
                "{\"mensagem\":\"Pet deletado com sucesso\"}");
        } else {
            WebServer.sendResponse(exchange, 404, "application/json", 
                "{\"erro\":\"Pet nao encontrado\"}");
        }
    }
    
    private Pet criarPetDoJson(Map<String, String> jsonMap) {
        Pet pet = new Pet();
        
        if (jsonMap.containsKey("idCliente")) {
            try {
                pet.setIdCliente(Integer.parseInt(jsonMap.get("idCliente")));
            } catch (NumberFormatException e) {
                // Ignora erro de conversão
            }
        }
        
        if (jsonMap.containsKey("nome")) pet.setNome(jsonMap.get("nome"));
        if (jsonMap.containsKey("especie")) pet.setEspecie(jsonMap.get("especie"));
        if (jsonMap.containsKey("raca") && !jsonMap.get("raca").equals("null")) {
            pet.setRaca(jsonMap.get("raca"));
        }
        
        if (jsonMap.containsKey("dataNascimento") && !jsonMap.get("dataNascimento").equals("null")) {
            try {
                pet.setDataNascimento(LocalDate.parse(jsonMap.get("dataNascimento")));
            } catch (Exception e) {
                // Ignora erro de conversão
            }
        }
        
        if (jsonMap.containsKey("peso")) {
            try {
                pet.setPeso(Double.parseDouble(jsonMap.get("peso")));
            } catch (NumberFormatException e) {
                // Ignora erro de conversão
            }
        }
        
        if (jsonMap.containsKey("observacoes") && !jsonMap.get("observacoes").equals("null")) {
            pet.setObservacoes(jsonMap.get("observacoes"));
        }
        
        return pet;
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
