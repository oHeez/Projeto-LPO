package exception;

public class ClienteNaoEncontradoException extends Exception {
    
    public ClienteNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public ClienteNaoEncontradoException(Integer idCliente) {
        super("Cliente com ID " + idCliente + " não encontrado.");
    }
}

