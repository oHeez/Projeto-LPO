package exception;

public class PetNaoEncontradoException extends Exception {
    
    public PetNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public PetNaoEncontradoException(Integer idPet) {
        super("Pet com ID " + idPet + " não encontrado.");
    }
}

