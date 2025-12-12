package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.FuncionarioDAO;
import model.Funcionario;
import model.Veterinario;
import model.Tosador;
import model.Atendente;
import util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class FuncionarioApiHandler implements HttpHandler {
    
    private FuncionarioDAO funcionarioDAO;
    
    public FuncionarioApiHandler() {
        this.funcionarioDAO = new FuncionarioDAO();
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
        // GET /api/funcionarios/cargo/{cargo}
        if (path.startsWith("/api/funcionarios/cargo/")) {
            String cargo = path.substring("/api/funcionarios/cargo/".length());
            List<Funcionario> funcionarios = funcionarioDAO.buscarPorCargo(cargo);
            String json = JsonUtil.funcionariosToJson(funcionarios);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
        // GET /api/funcionarios/{id}
        else if (path.matches("/api/funcionarios/\\d+")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);
            
            Funcionario funcionario = funcionarioDAO.buscarPorId(id);
            String json = JsonUtil.funcionarioToJson(funcionario);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
        // GET /api/funcionarios
        else {
            List<Funcionario> funcionarios = funcionarioDAO.listarTodos();
            String json = JsonUtil.funcionariosToJson(funcionarios);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
    }
    
    private void handlePost(HttpExchange exchange) throws Exception {
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Funcionario funcionario = criarFuncionarioDoJson(jsonMap);
        
        Funcionario funcionarioInserido = funcionarioDAO.inserir(funcionario);
        String json = JsonUtil.funcionarioToJson(funcionarioInserido);
        WebServer.sendResponse(exchange, 201, "application/json", json);
    }
    
    private void handlePut(HttpExchange exchange, String path) throws Exception {
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Funcionario funcionario = criarFuncionarioDoJson(jsonMap);
        funcionario.setIdFuncionario(id);
        
        Funcionario funcionarioAtualizado = funcionarioDAO.atualizar(funcionario);
        String json = JsonUtil.funcionarioToJson(funcionarioAtualizado);
        WebServer.sendResponse(exchange, 200, "application/json", json);
    }
    
    private void handleDelete(HttpExchange exchange, String path) throws Exception {
        String[] parts = path.split("/");
        int id = Integer.parseInt(parts[parts.length - 1]);
        
        boolean deletado = funcionarioDAO.deletar(id);
        if (deletado) {
            WebServer.sendResponse(exchange, 200, "application/json", 
                "{\"mensagem\":\"Funcionario deletado com sucesso\"}");
        } else {
            WebServer.sendResponse(exchange, 404, "application/json", 
                "{\"erro\":\"Funcionario nao encontrado\"}");
        }
    }
    
    private Funcionario criarFuncionarioDoJson(Map<String, String> jsonMap) {
        String cargo = jsonMap.get("cargo");
        String nome = jsonMap.get("nome");
        String cpf = jsonMap.get("cpf");
        String telefone = jsonMap.get("telefone");
        String email = jsonMap.get("email");
        
        Double salarioBase = null;
        if (jsonMap.containsKey("salarioBase") && jsonMap.get("salarioBase") != null) {
            try {
                salarioBase = Double.parseDouble(jsonMap.get("salarioBase"));
            } catch (NumberFormatException e) {
                // Ignora erro de conversão
            }
        }
        
        LocalDate dataContratacao = null;
        if (jsonMap.containsKey("dataContratacao") && jsonMap.get("dataContratacao") != null) {
            try {
                dataContratacao = LocalDate.parse(jsonMap.get("dataContratacao"));
            } catch (Exception e) {
                // Ignora erro de conversão
            }
        }
        
        if (cargo == null) cargo = "ATENDENTE";
        
        if ("VETERINARIO".equalsIgnoreCase(cargo)) {
            Veterinario vet = new Veterinario();
            vet.setNome(nome);
            vet.setCpf(cpf);
            vet.setTelefone(telefone);
            vet.setEmail(email);
            vet.setSalarioBase(salarioBase);
            vet.setDataContratacao(dataContratacao);
            return vet;
        } else if ("TOSADOR".equalsIgnoreCase(cargo)) {
            Tosador tos = new Tosador();
            tos.setNome(nome);
            tos.setCpf(cpf);
            tos.setTelefone(telefone);
            tos.setEmail(email);
            tos.setSalarioBase(salarioBase);
            tos.setDataContratacao(dataContratacao);
            return tos;
        } else {
            Atendente ate = new Atendente();
            ate.setNome(nome);
            ate.setCpf(cpf);
            ate.setTelefone(telefone);
            ate.setEmail(email);
            ate.setSalarioBase(salarioBase);
            ate.setDataContratacao(dataContratacao);
            return ate;
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
