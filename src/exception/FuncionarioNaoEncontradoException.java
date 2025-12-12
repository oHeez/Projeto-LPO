package exception;

public class FuncionarioNaoEncontradoException extends Exception {
    
    public FuncionarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public FuncionarioNaoEncontradoException(Integer idFuncionario) {
        super("Funcionário com ID " + idFuncionario + " não encontrado.");
    }
}

