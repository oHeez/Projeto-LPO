package dao;

import java.util.List;

public interface IDAO<T> {
    
    T inserir(T entidade) throws Exception;
    
    T buscarPorId(Integer id) throws Exception;
    
    List<T> listarTodos() throws Exception;
    
    T atualizar(T entidade) throws Exception;
    
    boolean deletar(Integer id) throws Exception;
}

