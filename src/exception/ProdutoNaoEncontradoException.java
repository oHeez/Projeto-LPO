package exception;

public class ProdutoNaoEncontradoException extends Exception {
    
    public ProdutoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public ProdutoNaoEncontradoException(Integer idProduto) {
        super("Produto com ID " + idProduto + " não encontrado.");
    }
}

