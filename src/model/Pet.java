package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;


public class Pet {

    private Integer idPet;
    private Integer idCliente;
    private Cliente cliente;
    private String nome;
    private String especie;
    private String raca;
    private LocalDate dataNascimento;
    private Double peso;
    private String observacoes;
    private LocalDateTime dataCadastro;

    public Pet() {
    }
    
    public Pet(Integer idCliente, String nome, String especie, String raca, LocalDate dataNascimento, Double peso, String observacoes) {
        this.idCliente = idCliente;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.peso = peso;
        this.observacoes = observacoes;
        this.dataCadastro = LocalDateTime.now();
    }
    
    public Pet(Integer idPet, Integer idCliente, String nome, String especie, String raca, 
               LocalDate dataNascimento, Double peso, String observacoes, LocalDateTime dataCadastro) {
        this.idPet = idPet;
        this.idCliente = idCliente;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.peso = peso;
        this.observacoes = observacoes;
        this.dataCadastro = dataCadastro;
    }

    public Integer getIdPet() {
        return idPet;
    }
    
    public void setIdPet(Integer idPet) {
        if (idPet != null && idPet <= 0) {
            throw new IllegalArgumentException("ID do pet deve ser positivo.");
        }
        this.idPet = idPet;
    }
    
    public Integer getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(Integer idCliente) {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("ID do cliente deve ser informado e positivo.");
        }
        this.idCliente = idCliente;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null && cliente.getIdCliente() != null) {
            this.idCliente = cliente.getIdCliente();
        }
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do pet não pode ser vazio.");
        }
        if (nome.length() > 100) {
            throw new IllegalArgumentException("Nome do pet não pode ter mais de 100 caracteres.");
        }
        this.nome = nome.trim();
    }
    
    public String getEspecie() {
        return especie;
    }
    
    public void setEspecie(String especie) {
        if (especie == null || especie.trim().isEmpty()) {
            throw new IllegalArgumentException("Espécie do pet não pode ser vazia.");
        }
        if (especie.length() > 50) {
            throw new IllegalArgumentException("Espécie do pet não pode ter mais de 50 caracteres.");
        }
        this.especie = especie.trim().toUpperCase();
    }
    
    public String getRaca() {
        return raca;
    }
    
    public void setRaca(String raca) {
        if (raca != null && raca.length() > 50) {
            throw new IllegalArgumentException("Raça do pet não pode ter mais de 50 caracteres.");
        }
        this.raca = raca != null ? raca.trim() : null;
    }
    
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser futura.");
        }
        this.dataNascimento = dataNascimento;
    }
    
    public Double getPeso() {
        return peso;
    }
    
    public void setPeso(Double peso) {
        if (peso != null && peso < 0) {
            throw new IllegalArgumentException("Peso não pode ser negativo.");
        }
        if (peso != null && peso > 1000) {
            throw new IllegalArgumentException("Peso não pode ser maior que 1000 kg.");
        }
        this.peso = peso;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes != null ? observacoes.trim() : null;
    }
    
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Integer calcularIdade() {
        if (dataNascimento == null) {
            return null;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
    
    public boolean validarPeso() {
        if (peso == null) {
            return true;
        }
        if (peso < 0 || peso > 1000) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        Integer idade = calcularIdade();
        String idadeStr = idade != null ? idade + " anos" : "idade não informada";
        return String.format("Pet [ID: %d, Nome: %s, Espécie: %s, Raça: %s, Idade: %s, Peso: %.2f kg]", 
                idPet, nome, especie, raca, idadeStr, peso != null ? peso : 0.0);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pet pet = (Pet) obj;
        return idPet != null ? idPet.equals(pet.idPet) : pet.idPet == null;
    }
    
    @Override
    public int hashCode() {
        return idPet != null ? idPet.hashCode() : 0;
    }
}

