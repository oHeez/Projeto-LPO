package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Veterinario extends Funcionario {
    
    private static final double BONUS_VETERINARIO = 1.5;

    public Veterinario() {
        super();
        this.cargo = "VETERINARIO";
    }
    
    public Veterinario(String nome, String cpf, String telefone, String email, 
                      Double salarioBase, LocalDate dataContratacao) {
        super(nome, cpf, telefone, email, "VETERINARIO", salarioBase, dataContratacao);
    }
    
    public Veterinario(Integer idFuncionario, String nome, String cpf, String telefone, 
                      String email, Double salarioBase, LocalDate dataContratacao, 
                      Boolean ativo, LocalDateTime dataCadastro) {
        super(idFuncionario, nome, cpf, telefone, email, "VETERINARIO", salarioBase, 
              dataContratacao, ativo, dataCadastro);
    }

    @Override
    public Double calcularSalario() {
        if (salarioBase == null) {
            return 0.0;
        }
        return salarioBase * BONUS_VETERINARIO;
    }
    
    @Override
    public String getDescricaoCargo() {
        return "Veterinário - Responsável pelo atendimento clínico e cirurgias dos animais.";
    }

    public String realizarConsulta(String nomePet) {
        return String.format("Dr(a). %s realizou consulta no(a) pet %s.", nome, nomePet);
    }
    
    @Override
    public String toString() {
        return String.format("Veterinario [ID: %d, Nome: %s, CPF: %s, Salário: R$ %.2f, Ativo: %s]", 
                idFuncionario, nome, cpf, calcularSalario(), ativo ? "Sim" : "Não");
    }
}

