package dao;

import db.DatabaseConnection;
import exception.FuncionarioNaoEncontradoException;
import model.Funcionario;
import model.Veterinario;
import model.Tosador;
import model.Atendente;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO implements IDAO<Funcionario> {
    
    private DatabaseConnection dbConnection;
    
    public FuncionarioDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public Funcionario inserir(Funcionario funcionario) throws Exception {
        String sql = "INSERT INTO TB_FUNCIONARIO (nome, cpf, telefone, email, cargo, salario_base, " +
                     "data_contratacao, ativo, data_cadastro) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, funcionario.getNome());
            pstmt.setString(2, funcionario.getCpf());
            pstmt.setString(3, funcionario.getTelefone());
            pstmt.setString(4, funcionario.getEmail());
            pstmt.setString(5, funcionario.getCargo());
            pstmt.setDouble(6, funcionario.getSalarioBase() != null ? funcionario.getSalarioBase() : 0.0);
            
            if (funcionario.getDataContratacao() != null) {
                pstmt.setDate(7, Date.valueOf(funcionario.getDataContratacao()));
            } else {
                pstmt.setDate(7, null);
            }
            
            pstmt.setBoolean(8, funcionario.getAtivo() != null ? funcionario.getAtivo() : true);
            pstmt.setTimestamp(9, Timestamp.valueOf(
                funcionario.getDataCadastro() != null ? funcionario.getDataCadastro() : LocalDateTime.now()
            ));
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    funcionario.setIdFuncionario(rs.getInt(1));
                }
                return funcionario;
            } else {
                throw new SQLException("Falha ao inserir funcionario. Nenhuma linha afetada.");
            }
            
        } catch (SQLException e) {
            throw new Exception("Erro ao inserir funcionario: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public Funcionario buscarPorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do funcionário deve ser positivo.");
        }
        
        String sql = "SELECT id_funcionario, nome, cpf, telefone, email, cargo, salario_base, " +
                     "data_contratacao, ativo, data_cadastro FROM TB_FUNCIONARIO WHERE id_funcionario = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return criarFuncionarioDoResultSet(rs);
            } else {
                throw new FuncionarioNaoEncontradoException(id);
            }
            
        } catch (FuncionarioNaoEncontradoException e) {
            throw e;
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar funcionário por ID: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public List<Funcionario> listarTodos() throws Exception {
        String sql = "SELECT id_funcionario, nome, cpf, telefone, email, cargo, salario_base, " +
                     "data_contratacao, ativo, data_cadastro FROM TB_FUNCIONARIO ORDER BY nome ASC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Funcionario> funcionarios = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                funcionarios.add(criarFuncionarioDoResultSet(rs));
            }
            
            return funcionarios;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao listar funcionários: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public Funcionario atualizar(Funcionario funcionario) throws Exception {
        if (funcionario.getIdFuncionario() == null || funcionario.getIdFuncionario() <= 0) {
            throw new IllegalArgumentException("ID do funcionário é obrigatório para atualização.");
        }
        
        String sql = "UPDATE TB_FUNCIONARIO SET nome = ?, cpf = ?, telefone = ?, email = ?, cargo = ?, " +
                     "salario_base = ?, data_contratacao = ?, ativo = ? WHERE id_funcionario = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, funcionario.getNome());
            pstmt.setString(2, funcionario.getCpf());
            pstmt.setString(3, funcionario.getTelefone());
            pstmt.setString(4, funcionario.getEmail());
            pstmt.setString(5, funcionario.getCargo());
            pstmt.setDouble(6, funcionario.getSalarioBase() != null ? funcionario.getSalarioBase() : 0.0);
            
            if (funcionario.getDataContratacao() != null) {
                pstmt.setDate(7, Date.valueOf(funcionario.getDataContratacao()));
            } else {
                pstmt.setDate(7, null);
            }
            
            pstmt.setBoolean(8, funcionario.getAtivo() != null ? funcionario.getAtivo() : true);
            pstmt.setInt(9, funcionario.getIdFuncionario());
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                return buscarPorId(funcionario.getIdFuncionario());
            } else {
                throw new FuncionarioNaoEncontradoException(funcionario.getIdFuncionario());
            }
            
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar funcionário: " + e.getMessage(), e);
        } finally {
            fecharRecursos(null, pstmt, null);
        }
    }
    
    @Override
    public boolean deletar(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do funcionário deve ser positivo.");
        }
        
        String sql = "DELETE FROM TB_FUNCIONARIO WHERE id_funcionario = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao deletar funcionário: " + e.getMessage(), e);
        } finally {
            fecharRecursos(null, pstmt, null);
        }
    }
    
    public List<Funcionario> buscarPorCargo(String cargo) throws Exception {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo não pode ser vazio.");
        }
        
        String sql = "SELECT id_funcionario, nome, cpf, telefone, email, cargo, salario_base, " +
                     "data_contratacao, ativo, data_cadastro FROM TB_FUNCIONARIO " +
                     "WHERE cargo = ? ORDER BY nome ASC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Funcionario> funcionarios = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cargo.trim().toUpperCase());
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                funcionarios.add(criarFuncionarioDoResultSet(rs));
            }
            
            return funcionarios;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar funcionários por cargo: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    private Funcionario criarFuncionarioDoResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id_funcionario");
        String nome = rs.getString("nome");
        String cpf = rs.getString("cpf");
        String telefone = rs.getString("telefone");
        String email = rs.getString("email");
        String cargo = rs.getString("cargo");
        Double salarioBase = rs.getDouble("salario_base");
        Date dataContratacao = rs.getDate("data_contratacao");
        Boolean ativo = rs.getBoolean("ativo");
        Timestamp timestamp = rs.getTimestamp("data_cadastro");
        
        LocalDate dataContratacaoLocal = dataContratacao != null ? dataContratacao.toLocalDate() : null;
        LocalDateTime dataCadastroLocal = timestamp != null ? timestamp.toLocalDateTime() : null;
        
        if ("VETERINARIO".equalsIgnoreCase(cargo)) {
            Veterinario vet = new Veterinario();
            vet.setIdFuncionario(id);
            vet.setNome(nome);
            vet.setCpf(cpf);
            vet.setTelefone(telefone);
            vet.setEmail(email);
            vet.setSalarioBase(salarioBase);
            vet.setDataContratacao(dataContratacaoLocal);
            vet.setAtivo(ativo);
            vet.setDataCadastro(dataCadastroLocal);
            return vet;
        } else if ("TOSADOR".equalsIgnoreCase(cargo)) {
            Tosador tos = new Tosador();
            tos.setIdFuncionario(id);
            tos.setNome(nome);
            tos.setCpf(cpf);
            tos.setTelefone(telefone);
            tos.setEmail(email);
            tos.setSalarioBase(salarioBase);
            tos.setDataContratacao(dataContratacaoLocal);
            tos.setAtivo(ativo);
            tos.setDataCadastro(dataCadastroLocal);
            return tos;
        } else {
            Atendente ate = new Atendente();
            ate.setIdFuncionario(id);
            ate.setNome(nome);
            ate.setCpf(cpf);
            ate.setTelefone(telefone);
            ate.setEmail(email);
            ate.setSalarioBase(salarioBase);
            ate.setDataContratacao(dataContratacaoLocal);
            ate.setAtivo(ativo);
            ate.setDataCadastro(dataCadastroLocal);
            return ate;
        }
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

