package dao;

import db.DatabaseConnection;
import exception.PetNaoEncontradoException;
import model.Pet;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PetDAO implements IDAO<Pet> {
    
    private DatabaseConnection dbConnection;
    
    public PetDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public Pet inserir(Pet pet) throws Exception {
        String sql = "INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, " +
                     "observacoes, data_cadastro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, pet.getIdCliente());
            pstmt.setString(2, pet.getNome());
            pstmt.setString(3, pet.getEspecie());
            pstmt.setString(4, pet.getRaca());
            
            if (pet.getDataNascimento() != null) {
                pstmt.setDate(5, Date.valueOf(pet.getDataNascimento()));
            } else {
                pstmt.setDate(5, null);
            }
            
            pstmt.setDouble(6, pet.getPeso() != null ? pet.getPeso() : 0.0);
            pstmt.setString(7, pet.getObservacoes());
            pstmt.setTimestamp(8, Timestamp.valueOf(
                pet.getDataCadastro() != null ? pet.getDataCadastro() : LocalDateTime.now()
            ));
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    pet.setIdPet(rs.getInt(1));
                }
                return pet;
            } else {
                throw new SQLException("Falha ao inserir pet. Nenhuma linha afetada.");
            }
            
        } catch (SQLException e) {
            throw new Exception("Erro ao inserir pet: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public Pet buscarPorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do pet deve ser positivo.");
        }
        
        String sql = "SELECT id_pet, id_cliente, nome, especie, raca, data_nascimento, peso, " +
                     "observacoes, data_cadastro FROM TB_PET WHERE id_pet = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return criarPetDoResultSet(rs);
            } else {
                throw new PetNaoEncontradoException(id);
            }
            
        } catch (PetNaoEncontradoException e) {
            throw e;
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar pet por ID: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public List<Pet> listarTodos() throws Exception {
        String sql = "SELECT id_pet, id_cliente, nome, especie, raca, data_nascimento, peso, " +
                     "observacoes, data_cadastro FROM TB_PET ORDER BY nome ASC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Pet> pets = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                pets.add(criarPetDoResultSet(rs));
            }
            
            return pets;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao listar pets: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    @Override
    public Pet atualizar(Pet pet) throws Exception {
        if (pet.getIdPet() == null || pet.getIdPet() <= 0) {
            throw new IllegalArgumentException("ID do pet é obrigatório para atualização.");
        }
        
        String sql = "UPDATE TB_PET SET id_cliente = ?, nome = ?, especie = ?, raca = ?, " +
                     "data_nascimento = ?, peso = ?, observacoes = ? WHERE id_pet = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, pet.getIdCliente());
            pstmt.setString(2, pet.getNome());
            pstmt.setString(3, pet.getEspecie());
            pstmt.setString(4, pet.getRaca());
            if (pet.getDataNascimento() != null) {
                pstmt.setDate(5, Date.valueOf(pet.getDataNascimento()));
            } else {
                pstmt.setDate(5, null);
            }
            pstmt.setDouble(6, pet.getPeso() != null ? pet.getPeso() : 0.0);
            pstmt.setString(7, pet.getObservacoes());
            pstmt.setInt(8, pet.getIdPet());
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                return buscarPorId(pet.getIdPet());
            } else {
                throw new PetNaoEncontradoException(pet.getIdPet());
            }
            
        } catch (SQLException e) {
            throw new Exception("Erro ao atualizar pet: " + e.getMessage(), e);
        } finally {
            fecharRecursos(null, pstmt, null);
        }
    }
    
    @Override
    public boolean deletar(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do pet deve ser positivo.");
        }
        
        String sql = "DELETE FROM TB_PET WHERE id_pet = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            return linhasAfetadas > 0;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao deletar pet: " + e.getMessage(), e);
        } finally {
            fecharRecursos(null, pstmt, null);
        }
    }
    
    public List<Pet> buscarPorCliente(Integer idCliente) throws Exception {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("ID do cliente deve ser positivo.");
        }
        
        String sql = "SELECT id_pet, id_cliente, nome, especie, raca, data_nascimento, peso, " +
                     "observacoes, data_cadastro FROM TB_PET WHERE id_cliente = ? ORDER BY nome ASC";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Pet> pets = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idCliente);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                pets.add(criarPetDoResultSet(rs));
            }
            
            return pets;
            
        } catch (SQLException e) {
            throw new Exception("Erro ao buscar pets por cliente: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null);
        }
    }
    
    private Pet criarPetDoResultSet(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setIdPet(rs.getInt("id_pet"));
        pet.setIdCliente(rs.getInt("id_cliente"));
        pet.setNome(rs.getString("nome"));
        pet.setEspecie(rs.getString("especie"));
        pet.setRaca(rs.getString("raca"));
        
        Date dataNascimento = rs.getDate("data_nascimento");
        if (dataNascimento != null) {
            pet.setDataNascimento(dataNascimento.toLocalDate());
        }
        
        pet.setPeso(rs.getDouble("peso"));
        pet.setObservacoes(rs.getString("observacoes"));
        
        Timestamp timestamp = rs.getTimestamp("data_cadastro");
        if (timestamp != null) {
            pet.setDataCadastro(timestamp.toLocalDateTime());
        }
        
        return pet;
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

