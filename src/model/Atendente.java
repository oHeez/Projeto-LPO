package model;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class Atendente extends Funcionario {
    public Atendente() {
        super();
        this.cargo = "ATENDENTE";
    }
    
    public Atendente(String nome, String cpf, String telefone, String email, 
                    Double salarioBase, LocalDate dataContratacao) {
        super(nome, cpf, telefone, email, "ATENDENTE", salarioBase, dataContratacao);
    }
    
    public Atendente(Integer idFuncionario, String nome, String cpf, String telefone, 
                    String email, Double salarioBase, LocalDate dataContratacao, 
                    Boolean ativo, LocalDateTime dataCadastro) {
        super(idFuncionario, nome, cpf, telefone, email, "ATENDENTE", salarioBase, 
              dataContratacao, ativo, dataCadastro);
    }

    @Override
    public Double calcularSalario() {
        return salarioBase != null ? salarioBase : 0.0;
    }
    
    @Override
    public String getDescricaoCargo() {
        return "Atendente - Responsável pelo atendimento ao público, vendas e agendamentos.";
    }

    public String realizarVenda(Double valorVenda) {
        return String.format("%s realizou uma venda no valor de R$ %.2f.", nome, valorVenda);
    }
    
    public String realizarAgendamento(String nomeCliente, String servico) {
        return String.format("%s agendou %s para o cliente %s.", nome, servico, nomeCliente);
    }
    
    @Override
    public String toString() {
        return String.format("Atendente [ID: %d, Nome: %s, CPF: %s, Salário: R$ %.2f, Ativo: %s]", 
                idFuncionario, nome, cpf, calcularSalario(), ativo ? "Sim" : "Não");
    }
}

