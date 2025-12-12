package dao;

import db.DatabaseConnection;
import exception.ProdutoNaoEncontradoException;
import model.Produto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO implements IDAO<Produto> {
    
    private DatabaseConnection dbConnection;
    
    public ProdutoDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public Produto inserir(Produto produto) throws Exception {
        String sql = "INSERT INTO TB_PRODUTO (nome, descricao, preco, estoque, categoria, ativo, data_cadastro) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getEstoque() != null ? produto.getEstoque() : 0);
            pstmt.setString(5, produto.getCategoria());
            pstmt.setBoolean(6, produto.getAtivo() != null ? produto.getAtivo() : true);
            pstmt.setTimestamp(7, Timestamp.valueOf(
                produto.getDataCadastro() != null ? produto.getDataCadastro() : LocalDateTime.now()
            ));
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    produto.setIdProduto(rs.getInt(1));
                }
                return produto;
            } else {
                throw new SQLException("Falha ao inserir produto. Nenhuma linha afetada.");
            }
            
        } catch (SQLException e) {
            throw new Exception("Erro ao inserir produto: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public Produto buscarPorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do produto deve ser positivo.");
        }
        
        String sql = "SELECT id_produto, nome, descricao, preco, estoque, categoria, ativo, data_cadastro " +
                     "FROM TB_PRODUTO WHERE id_produto = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return criarProdutoDoResultSet(rs);
            } else {
                throw new ProdutoNaoEncontradoException(id);
            }
            
        } catch (ProdutoNaoEncontradoException e) {
            throw e;
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar produto por ID: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public List<Produto> listarTodos() throws Exception {
        String sql = "SELECT id_produto, nome, descricao, preco, estoque, categoria, ativo, data_cadastro " +
                     "FROM TB_PRODUTO ORDER BY nome ASC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Produto> produtos = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                produtos.add(criarProdutoDoResultSet(rs));
            }
            
            return produtos;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao listar produtos: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public Produto atualizar(Produto produto) throws Exception {
        if (produto.getIdProduto() == null || produto.getIdProduto() <= 0) {
            throw new IllegalArgumentException("ID do produto é obrigatório para atualização.");
        }
        
        String sql = "UPDATE TB_PRODUTO SET nome = ?, descricao = ?, preco = ?, estoque = ?, " +
                     "categoria = ?, ativo = ? WHERE id_produto = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, produto.getNome());
            pstmt.setString(2, produto.getDescricao());
            pstmt.setDouble(3, produto.getPreco());
            pstmt.setInt(4, produto.getEstoque() != null ? produto.getEstoque() : 0);
            pstmt.setString(5, produto.getCategoria());
            pstmt.setBoolean(6, produto.getAtivo() != null ? produto.getAtivo() : true);
            pstmt.setInt(7, produto.getIdProduto());
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                return buscarPorId(produto.getIdProduto());
            } else {
                throw new ProdutoNaoEncontradoException(produto.getIdProduto());
            }
            
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar produto: " + e.getMessage(), e);
        } finally {
            fecharRecursos(null, pstmt, null);
        }
    }
    
    @Override
    public boolean deletar(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do produto deve ser positivo.");
        }
        
        String sql = "DELETE FROM TB_PRODUTO WHERE id_produto = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao deletar produto: " + e.getMessage(), e);
        } finally {
            fecharRecursos(null, pstmt, null);
        }
    }
    
    public List<Produto> buscarPorCategoria(String categoria) throws Exception {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria não pode ser vazia.");
        }
        
        String sql = "SELECT id_produto, nome, descricao, preco, estoque, categoria, ativo, data_cadastro " +
                     "FROM TB_PRODUTO WHERE categoria = ? ORDER BY nome ASC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Produto> produtos = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, categoria.trim().toUpperCase());
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                produtos.add(criarProdutoDoResultSet(rs));
            }
            
            return produtos;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    public Produto atualizarEstoque(Integer idProduto, Integer quantidade) throws Exception {
        if (idProduto == null || idProduto <= 0) {
            throw new IllegalArgumentException("ID do produto deve ser positivo.");
        }
        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        }
        
        Produto produto = buscarPorId(idProduto);
        produto.setEstoque(quantidade);
        return atualizar(produto);
    }
    
    private Produto criarProdutoDoResultSet(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setIdProduto(rs.getInt("id_produto"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setPreco(rs.getDouble("preco"));
        produto.setEstoque(rs.getInt("estoque"));
        produto.setCategoria(rs.getString("categoria"));
        produto.setAtivo(rs.getBoolean("ativo"));
        
        Timestamp timestamp = rs.getTimestamp("data_cadastro");
        if (timestamp != null) {
            produto.setDataCadastro(timestamp.toLocalDateTime());
        }
        
        return produto;
    }
    
    private void fecharRecursos(ResultSet rs, PreparedStatement pstmt, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar recursos: " + e.getMessage());
        }
    }
}

