package util;

import model.Cliente;
import model.Pet;
import model.Produto;
import model.Funcionario;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public static String clienteToJson(Cliente cliente) {
        if (cliente == null) {
            return "null";
        }
        
        StringBuilder json = new StringBuilder("{");
        json.append("\"idCliente\":").append(cliente.getIdCliente() != null ? cliente.getIdCliente() : "null").append(",");
        json.append("\"nome\":\"").append(escapeJson(cliente.getNome() != null ? cliente.getNome() : "")).append("\",");
        json.append("\"cpf\":\"").append(escapeJson(cliente.getCpf() != null ? cliente.getCpf() : "")).append("\",");
        json.append("\"telefone\":").append(cliente.getTelefone() != null ? "\"" + escapeJson(cliente.getTelefone()) + "\"" : "null").append(",");
        json.append("\"email\":").append(cliente.getEmail() != null ? "\"" + escapeJson(cliente.getEmail()) + "\"" : "null").append(",");
        json.append("\"endereco\":").append(cliente.getEndereco() != null ? "\"" + escapeJson(cliente.getEndereco()) + "\"" : "null").append(",");
        json.append("\"dataCadastro\":").append(cliente.getDataCadastro() != null ? "\"" + cliente.getDataCadastro().format(DATETIME_FORMATTER) + "\"" : "null");
        json.append("}");
        
        return json.toString();
    }
    
    public static String clientesToJson(List<Cliente> clientes) {
        if (clientes == null) {
            return "[]";
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < clientes.size(); i++) {
            if (i > 0) json.append(",");
            json.append(clienteToJson(clientes.get(i)));
        }
        json.append("]");
        
        return json.toString();
    }
    
    public static String petToJson(Pet pet) {
        if (pet == null) {
            return "null";
        }
        
        StringBuilder json = new StringBuilder("{");
        json.append("\"idPet\":").append(pet.getIdPet() != null ? pet.getIdPet() : "null").append(",");
        json.append("\"idCliente\":").append(pet.getIdCliente() != null ? pet.getIdCliente() : "null").append(",");
        json.append("\"nome\":\"").append(escapeJson(pet.getNome())).append("\",");
        json.append("\"especie\":\"").append(escapeJson(pet.getEspecie())).append("\",");
        json.append("\"raca\":").append(pet.getRaca() != null ? "\"" + escapeJson(pet.getRaca()) + "\"" : "null").append(",");
        json.append("\"dataNascimento\":").append(pet.getDataNascimento() != null ? "\"" + pet.getDataNascimento().format(DATE_FORMATTER) + "\"" : "null").append(",");
        json.append("\"peso\":").append(pet.getPeso() != null ? pet.getPeso() : "null").append(",");
        json.append("\"observacoes\":").append(pet.getObservacoes() != null ? "\"" + escapeJson(pet.getObservacoes()) + "\"" : "null").append(",");
        json.append("\"dataCadastro\":").append(pet.getDataCadastro() != null ? "\"" + pet.getDataCadastro().format(DATETIME_FORMATTER) + "\"" : "null");
        json.append("}");
        
        return json.toString();
    }
    
    public static String petsToJson(List<Pet> pets) {
        if (pets == null) {
            return "[]";
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < pets.size(); i++) {
            if (i > 0) json.append(",");
            json.append(petToJson(pets.get(i)));
        }
        json.append("]");
        
        return json.toString();
    }
    
    public static String produtoToJson(Produto produto) {
        if (produto == null) {
            return "null";
        }
        
        StringBuilder json = new StringBuilder("{");
        json.append("\"idProduto\":").append(produto.getIdProduto() != null ? produto.getIdProduto() : "null").append(",");
        json.append("\"nome\":\"").append(escapeJson(produto.getNome())).append("\",");
        json.append("\"descricao\":").append(produto.getDescricao() != null ? "\"" + escapeJson(produto.getDescricao()) + "\"" : "null").append(",");
        json.append("\"preco\":").append(produto.getPreco() != null ? produto.getPreco() : "null").append(",");
        json.append("\"estoque\":").append(produto.getEstoque() != null ? produto.getEstoque() : "null").append(",");
        json.append("\"categoria\":").append(produto.getCategoria() != null ? "\"" + escapeJson(produto.getCategoria()) + "\"" : "null").append(",");
        json.append("\"ativo\":").append(produto.getAtivo() != null ? produto.getAtivo() : "true").append(",");
        json.append("\"dataCadastro\":").append(produto.getDataCadastro() != null ? "\"" + produto.getDataCadastro().format(DATETIME_FORMATTER) + "\"" : "null");
        json.append("}");
        
        return json.toString();
    }
    
    public static String produtosToJson(List<Produto> produtos) {
        if (produtos == null) {
            return "[]";
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < produtos.size(); i++) {
            if (i > 0) json.append(",");
            json.append(produtoToJson(produtos.get(i)));
        }
        json.append("]");
        
        return json.toString();
    }
    
    public static String funcionarioToJson(Funcionario funcionario) {
        if (funcionario == null) {
            return "null";
        }
        
        StringBuilder json = new StringBuilder("{");
        json.append("\"idFuncionario\":").append(funcionario.getIdFuncionario() != null ? funcionario.getIdFuncionario() : "null").append(",");
        json.append("\"nome\":\"").append(escapeJson(funcionario.getNome())).append("\",");
        json.append("\"cpf\":\"").append(escapeJson(funcionario.getCpf())).append("\",");
        json.append("\"telefone\":").append(funcionario.getTelefone() != null ? "\"" + escapeJson(funcionario.getTelefone()) + "\"" : "null").append(",");
        json.append("\"email\":\"").append(escapeJson(funcionario.getEmail())).append("\",");
        json.append("\"cargo\":\"").append(escapeJson(funcionario.getCargo())).append("\",");
        json.append("\"salarioBase\":").append(funcionario.getSalarioBase() != null ? funcionario.getSalarioBase() : "null").append(",");
        json.append("\"salarioCalculado\":").append(funcionario.calcularSalario() != null ? funcionario.calcularSalario() : "null").append(",");
        json.append("\"dataContratacao\":").append(funcionario.getDataContratacao() != null ? "\"" + funcionario.getDataContratacao().format(DATE_FORMATTER) + "\"" : "null").append(",");
        json.append("\"ativo\":").append(funcionario.getAtivo() != null ? funcionario.getAtivo() : "true").append(",");
        json.append("\"dataCadastro\":").append(funcionario.getDataCadastro() != null ? "\"" + funcionario.getDataCadastro().format(DATETIME_FORMATTER) + "\"" : "null");
        json.append("}");
        
        return json.toString();
    }
    
    public static String funcionariosToJson(List<Funcionario> funcionarios) {
        if (funcionarios == null) {
            return "[]";
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < funcionarios.size(); i++) {
            if (i > 0) json.append(",");
            json.append(funcionarioToJson(funcionarios.get(i)));
        }
        json.append("]");
        
        return json.toString();
    }
    
    public static Map<String, String> jsonToMap(String json) {
        Map<String, String> map = new HashMap<>();
        
        if (json == null || json.trim().isEmpty() || json.trim().equals("null")) {
            return map;
        }
        
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
        }
        
        String[] pairs = splitJsonPairs(json);
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = removeQuotes(keyValue[0].trim());
                String value = removeQuotes(keyValue[1].trim());
                map.put(key, value);
            }
        }
        
        return map;
    }
    
    public static Cliente jsonToCliente(Map<String, String> jsonMap) {
        Cliente cliente = new Cliente();
        
        if (jsonMap.containsKey("idCliente") && jsonMap.get("idCliente") != null && !jsonMap.get("idCliente").equals("null")) {
            try {
                cliente.setIdCliente(Integer.parseInt(jsonMap.get("idCliente")));
            } catch (NumberFormatException e) {
                // Ignora erro de conversão
            }
        }
        
        if (jsonMap.containsKey("nome")) cliente.setNome(jsonMap.get("nome"));
        if (jsonMap.containsKey("cpf")) cliente.setCpf(jsonMap.get("cpf"));
        if (jsonMap.containsKey("telefone") && !jsonMap.get("telefone").equals("null")) cliente.setTelefone(jsonMap.get("telefone"));
        if (jsonMap.containsKey("email")) cliente.setEmail(jsonMap.get("email"));
        if (jsonMap.containsKey("endereco") && !jsonMap.get("endereco").equals("null")) cliente.setEndereco(jsonMap.get("endereco"));
        
        return cliente;
    }
    
    public static Produto jsonToProduto(Map<String, String> jsonMap) {
        Produto produto = new Produto();
        
        if (jsonMap.containsKey("idProduto") && jsonMap.get("idProduto") != null && !jsonMap.get("idProduto").equals("null")) {
            try {
                produto.setIdProduto(Integer.parseInt(jsonMap.get("idProduto")));
            } catch (NumberFormatException e) {
                // Ignora erro de conversão
            }
        }
        
        if (jsonMap.containsKey("nome")) produto.setNome(jsonMap.get("nome"));
        if (jsonMap.containsKey("descricao") && !jsonMap.get("descricao").equals("null")) produto.setDescricao(jsonMap.get("descricao"));
        if (jsonMap.containsKey("preco")) {
            try {
                produto.setPreco(Double.parseDouble(jsonMap.get("preco")));
            } catch (NumberFormatException e) {
                // Ignora erro
            }
        }
        if (jsonMap.containsKey("estoque")) {
            try {
                produto.setEstoque(Integer.parseInt(jsonMap.get("estoque")));
            } catch (NumberFormatException e) {
                // Ignora erro
            }
        }
        if (jsonMap.containsKey("categoria") && !jsonMap.get("categoria").equals("null")) produto.setCategoria(jsonMap.get("categoria"));
        
        return produto;
    }
    
    private static String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    private static String removeQuotes(String text) {
        if (text == null) {
            return null;
        }
        text = text.trim();
        if (text.startsWith("\"") && text.endsWith("\"")) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }
    
    private static String[] splitJsonPairs(String json) {
        List<String> pairs = new ArrayList<>();
        StringBuilder currentPair = new StringBuilder();
        boolean insideString = false;
        boolean escapeNext = false;
        
        for (char c : json.toCharArray()) {
            if (escapeNext) {
                currentPair.append(c);
                escapeNext = false;
                continue;
            }
            
            if (c == '\\') {
                escapeNext = true;
                currentPair.append(c);
                continue;
            }
            
            if (c == '"') {
                insideString = !insideString;
                currentPair.append(c);
                continue;
            }
            
            if (c == ',' && !insideString) {
                pairs.add(currentPair.toString());
                currentPair = new StringBuilder();
                continue;
            }
            
            currentPair.append(c);
        }
        
        if (currentPair.length() > 0) {
            pairs.add(currentPair.toString());
        }
        
        return pairs.toArray(new String[0]);
    }
}

