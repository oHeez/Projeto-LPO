package dao;

import db.DatabaseConnection;
import exception.ClienteNaoEncontradoException;
import model.Cliente;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements IDAO<Cliente> {
    
    private DatabaseConnection dbConnection;
    
    public ClienteDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public Cliente inserir(Cliente cliente) throws Exception {
        String sql = "INSERT INTO TB_CLIENTE (nome, cpf, telefone, email, endereco, data_cadastro) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getCpf());
            pstmt.setString(3, cliente.getTelefone());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setString(5, cliente.getEndereco());
            pstmt.setTimestamp(6, Timestamp.valueOf(
                cliente.getDataCadastro() != null ? cliente.getDataCadastro() : LocalDateTime.now()
            ));
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
                return cliente;
            } else {
                throw new SQLException("Falha ao inserir cliente. Nenhuma linha afetada.");
            }
            
        } catch (SQLException e) {
            throw new Exception("Erro ao inserir cliente: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public Cliente buscarPorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do cliente deve ser positivo.");
        }
        
        String sql = "SELECT id_cliente, nome, cpf, telefone, email, endereco, data_cadastro " +
                     "FROM TB_CLIENTE WHERE id_cliente = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return criarClienteDoResultSet(rs);
            } else {
                throw new ClienteNaoEncontradoException(id);
            }
            
        } catch (ClienteNaoEncontradoException e) {
            throw e;
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar cliente por ID: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public List<Cliente> listarTodos() throws Exception {
        String sql = "SELECT id_cliente, nome, cpf, telefone, email, endereco, data_cadastro " +
                     "FROM TB_CLIENTE ORDER BY nome ASC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Cliente> clientes = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                clientes.add(criarClienteDoResultSet(rs));
            }
            
            return clientes;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao listar clientes: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public Cliente atualizar(Cliente cliente) throws Exception {
        if (cliente.getIdCliente() == null || cliente.getIdCliente() <= 0) {
            throw new IllegalArgumentException("ID do cliente é obrigatório para atualização.");
        }
        
        String sql = "UPDATE TB_CLIENTE SET nome = ?, cpf = ?, telefone = ?, email = ?, endereco = ? " +
                     "WHERE id_cliente = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getCpf());
            pstmt.setString(3, cliente.getTelefone());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setString(5, cliente.getEndereco());
            pstmt.setInt(6, cliente.getIdCliente());
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                return buscarPorId(cliente.getIdCliente());
            } else {
                throw new ClienteNaoEncontradoException(cliente.getIdCliente());
            }
            
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar cliente: " + e.getMessage(), e);
        } finally {
            fecharRecursos(null, pstmt, null);
        }
    }
    
    @Override
    public boolean deletar(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do cliente deve ser positivo.");
        }
        
        String sql = "DELETE FROM TB_CLIENTE WHERE id_cliente = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao deletar cliente: " + e.getMessage(), e);
        } finally {
            fecharRecursos(null, pstmt, null);
        }
    }
    
    private Cliente criarClienteDoResultSet(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(rs.getInt("id_cliente"));
        
        // Usar métodos que não fazem validação para carregar dados do banco
        // Isso evita erros quando os dados no banco estão em formato diferente
        cliente.setNomeFromDB(rs.getString("nome"));
        cliente.setCpfFromDB(rs.getString("cpf"));
        cliente.setTelefoneFromDB(rs.getString("telefone"));
        cliente.setEmailFromDB(rs.getString("email"));
        cliente.setEnderecoFromDB(rs.getString("endereco"));
        
        Timestamp timestamp = rs.getTimestamp("data_cadastro");
        if (timestamp != null) {
            cliente.setDataCadastro(timestamp.toLocalDateTime());
        }
        
        return cliente;
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

