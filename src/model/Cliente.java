package model;

import java.time.LocalDateTime;


public class Cliente {
    
    private Integer idCliente;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String endereco;
    private LocalDateTime dataCadastro;
    
    public Cliente() {
    }
    
    public Cliente(String nome, String cpf, String telefone, String email, String endereco) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.dataCadastro = LocalDateTime.now();
    }
    
    public Cliente(Integer idCliente, String nome, String cpf, String telefone, String email, String endereco, LocalDateTime dataCadastro) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.dataCadastro = dataCadastro;
    }
    
    public Integer getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Integer idCliente) {
        if (idCliente != null && idCliente <= 0) {
            throw new IllegalArgumentException("ID do cliente deve ser positivo.");
        }
        this.idCliente = idCliente;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        }
        if (nome.length() > 100) {
            throw new IllegalArgumentException("Nome do cliente não pode ter mais de 100 caracteres.");
        }
        this.nome = nome.trim();
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio.");
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
        if (!validarCPF(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        this.cpf = cpfLimpo;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        if (telefone != null && !telefone.trim().isEmpty()) {
            String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
            if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
                throw new IllegalArgumentException("Telefone deve conter 10 ou 11 dígitos.");
            }
            this.telefone = telefoneLimpo;
        } else {
            this.telefone = null;
        }
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio.");
        }
        if (!validarEmail(email)) {
            throw new IllegalArgumentException("Email inválido.");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email não pode ter mais de 100 caracteres.");
        }
        this.email = email.trim().toLowerCase();
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        if (endereco != null && endereco.length() > 255) {
            throw new IllegalArgumentException("Endereço não pode ter mais de 255 caracteres.");
        }
        this.endereco = endereco != null ? endereco.trim() : null;
    }
    
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    // Métodos para carregar dados do banco sem validação (uso do DAO)
    // Estes métodos permitem carregar dados do banco mesmo que estejam em formato inválido
    public void setCpfFromDB(String cpf) {
        this.cpf = cpf != null ? cpf : null;
    }
    
    public void setEmailFromDB(String email) {
        this.email = email != null && !email.trim().isEmpty() ? email.trim().toLowerCase() : null;
    }
    
    public void setNomeFromDB(String nome) {
        // Permite nome nulo apenas para carregamento do banco
        // Se for nulo, define como string vazia para evitar problemas
        this.nome = nome != null ? nome.trim() : "";
    }
    
    public void setTelefoneFromDB(String telefone) {
        this.telefone = telefone != null && !telefone.trim().isEmpty() ? telefone : null;
    }
    
    public void setEnderecoFromDB(String endereco) {
        this.endereco = endereco != null && !endereco.trim().isEmpty() ? endereco.trim() : null;
    }
    
    private boolean validarCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return false;
        }
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        try {
            int[] numeros = new int[11];
            for (int i = 0; i < 11; i++) {
                numeros[i] = Integer.parseInt(cpf.substring(i, i + 1));
            }
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += numeros[i] * (10 - i);
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) digito1 = 0;
            
            if (digito1 != numeros[9]) {
                return false;
            }
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += numeros[i] * (11 - i);
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) digito2 = 0;
            
            return digito2 == numeros[10];
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
    
    @Override
    public String toString() {
        return String.format("Cliente [ID: %d, Nome: %s, CPF: %s, Email: %s]", 
                idCliente, nome, cpf, email);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cliente cliente = (Cliente) obj;
        return cpf != null ? cpf.equals(cliente.cpf) : cliente.cpf == null;
    }
    
    @Override
    public int hashCode() {
        return cpf != null ? cpf.hashCode() : 0;
    }
}

