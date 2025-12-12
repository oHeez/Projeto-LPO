package model;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class Tosador extends Funcionario {
    
    private static final double BONUS_TOSADOR = 1.2;

    public Tosador() {
        super();
        this.cargo = "TOSADOR";
    }
    
    public Tosador(String nome, String cpf, String telefone, String email, 
                  Double salarioBase, LocalDate dataContratacao) {
        super(nome, cpf, telefone, email, "TOSADOR", salarioBase, dataContratacao);
    }
    
    public Tosador(Integer idFuncionario, String nome, String cpf, String telefone, 
                  String email, Double salarioBase, LocalDate dataContratacao, 
                  Boolean ativo, LocalDateTime dataCadastro) {
        super(idFuncionario, nome, cpf, telefone, email, "TOSADOR", salarioBase, 
              dataContratacao, ativo, dataCadastro);
    }

    @Override
    public Double calcularSalario() {
        if (salarioBase == null) {
            return 0.0;
        }
        return salarioBase * BONUS_TOSADOR;
    }
    
    @Override
    public String getDescricaoCargo() {
        return "Tosador - Responsável pela tosa e cuidados estéticos dos animais.";
    }

    public String realizarTosa(String nomePet, String tipoTosa) {
        return String.format("%s realizou tosa %s no(a) pet %s.", nome, tipoTosa, nomePet);
    }
    
    @Override
    public String toString() {
        return String.format("Tosador [ID: %d, Nome: %s, CPF: %s, Salário: R$ %.2f, Ativo: %s]", 
                idFuncionario, nome, cpf, calcularSalario(), ativo ? "Sim" : "Não");
    }
}

