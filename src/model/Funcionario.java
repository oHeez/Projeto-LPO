package model;

import java.time.LocalDate;
import java.time.LocalDateTime;


public abstract class Funcionario {

    protected Integer idFuncionario;
    protected String nome;
    protected String cpf;
    protected String telefone;
    protected String email;
    protected String cargo;
    protected Double salarioBase;
    protected LocalDate dataContratacao;
    protected Boolean ativo;
    protected LocalDateTime dataCadastro;

    protected Funcionario() {
        this.ativo = true;
    }
    
    protected Funcionario(String nome, String cpf, String telefone, String email, 
                         String cargo, Double salarioBase, LocalDate dataContratacao) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.dataContratacao = dataContratacao;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }
    
    protected Funcionario(Integer idFuncionario, String nome, String cpf, String telefone, 
                         String email, String cargo, Double salarioBase, LocalDate dataContratacao, 
                         Boolean ativo, LocalDateTime dataCadastro) {
        this.idFuncionario = idFuncionario;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.dataContratacao = dataContratacao;
        this.ativo = ativo != null ? ativo : true;
        this.dataCadastro = dataCadastro;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }
    
    public void setIdFuncionario(Integer idFuncionario) {
        if (idFuncionario != null && idFuncionario <= 0) {
            throw new IllegalArgumentException("ID do funcionário deve ser positivo.");
        }
        this.idFuncionario = idFuncionario;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do funcionário não pode ser vazio.");
        }
        if (nome.length() > 100) {
            throw new IllegalArgumentException("Nome do funcionário não pode ter mais de 100 caracteres.");
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
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email inválido.");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email não pode ter mais de 100 caracteres.");
        }
        this.email = email.trim().toLowerCase();
    }
    
    public String getCargo() {
        return cargo;
    }
    
    protected void setCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo não pode ser vazio.");
        }
        if (cargo.length() > 50) {
            throw new IllegalArgumentException("Cargo não pode ter mais de 50 caracteres.");
        }
        this.cargo = cargo.trim().toUpperCase();
    }
    
    public Double getSalarioBase() {
        return salarioBase;
    }
    
    public void setSalarioBase(Double salarioBase) {
        if (salarioBase != null && salarioBase < 0) {
            throw new IllegalArgumentException("Salário base não pode ser negativo.");
        }
        this.salarioBase = salarioBase;
    }
    
    public LocalDate getDataContratacao() {
        return dataContratacao;
    }
    
    public void setDataContratacao(LocalDate dataContratacao) {
        if (dataContratacao != null && dataContratacao.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de contratação não pode ser futura.");
        }
        this.dataContratacao = dataContratacao;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo != null ? ativo : true;
    }
    
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public abstract Double calcularSalario();
    
    public abstract String getDescricaoCargo();

    public Integer calcularTempoServico() {
        if (dataContratacao == null) {
            return null;
        }
        return java.time.Period.between(dataContratacao, LocalDate.now()).getYears();
    }
    
    @Override
    public String toString() {
        return String.format("Funcionario [ID: %d, Nome: %s, Cargo: %s, CPF: %s, Ativo: %s]", 
                idFuncionario, nome, cargo, cpf, ativo ? "Sim" : "Não");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Funcionario that = (Funcionario) obj;
        return cpf != null ? cpf.equals(that.cpf) : that.cpf == null;
    }
    
    @Override
    public int hashCode() {
        return cpf != null ? cpf.hashCode() : 0;
    }
}

