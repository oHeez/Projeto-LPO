package model;

import java.time.LocalDateTime;


public class Produto {

    private Integer idProduto;
    private String nome;
    private String descricao;
    private Double preco;
    private Integer estoque;
    private String categoria;
    private Boolean ativo;
    private LocalDateTime dataCadastro;

    public Produto() {
        this.estoque = 0;
        this.ativo = true;
    }
    
    public Produto(String nome, String descricao, Double preco, Integer estoque, String categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque != null ? estoque : 0;
        this.categoria = categoria;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }
    
    public Produto(Integer idProduto, String nome, String descricao, Double preco, 
                   Integer estoque, String categoria, Boolean ativo, LocalDateTime dataCadastro) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque != null ? estoque : 0;
        this.categoria = categoria;
        this.ativo = ativo != null ? ativo : true;
        this.dataCadastro = dataCadastro;
    }

    public Integer getIdProduto() {
        return idProduto;
    }
    
    public void setIdProduto(Integer idProduto) {
        if (idProduto != null && idProduto <= 0) {
            throw new IllegalArgumentException("ID do produto deve ser positivo.");
        }
        this.idProduto = idProduto;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio.");
        }
        if (nome.length() > 100) {
            throw new IllegalArgumentException("Nome do produto não pode ter mais de 100 caracteres.");
        }
        this.nome = nome.trim();
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao != null ? descricao.trim() : null;
    }
    
    public Double getPreco() {
        return preco;
    }
    
    public void setPreco(Double preco) {
        if (preco == null) {
            throw new IllegalArgumentException("Preço não pode ser nulo.");
        }
        if (preco < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo.");
        }
        if (preco > 999999.99) {
            throw new IllegalArgumentException("Preço não pode ser maior que R$ 999.999,99.");
        }
        this.preco = preco;
    }
    
    public Integer getEstoque() {
        return estoque;
    }
    
    public void setEstoque(Integer estoque) {
        if (estoque == null) {
            this.estoque = 0;
            return;
        }
        if (estoque < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo.");
        }
        this.estoque = estoque;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        if (categoria != null && categoria.length() > 50) {
            throw new IllegalArgumentException("Categoria não pode ter mais de 50 caracteres.");
        }
        this.categoria = categoria != null ? categoria.trim().toUpperCase() : null;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo != null ? ativo : true;
    }
    
    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
    
    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean validarPreco() {
        return preco != null && preco >= 0 && preco <= 999999.99;
    }
    
    public boolean validarEstoque() {
        return estoque != null && estoque >= 0;
    }
    
    public boolean estaDisponivel() {
        return ativo != null && ativo && estoque != null && estoque > 0;
    }
    
    public boolean estoqueBaixo() {
        return estoque != null && estoque < 10 && estoque > 0;
    }
    
    public boolean emFalta() {
        return estoque == null || estoque == 0;
    }

    public void adicionarEstoque(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        this.estoque = (this.estoque != null ? this.estoque : 0) + quantidade;
    }
    
    public void removerEstoque(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        int estoqueAtual = this.estoque != null ? this.estoque : 0;
        if (estoqueAtual < quantidade) {
            throw new IllegalArgumentException(
                String.format("Estoque insuficiente. Disponível: %d, Solicitado: %d", estoqueAtual, quantidade));
        }
        this.estoque = estoqueAtual - quantidade;
    }
    
    @Override
    public String toString() {
        return String.format("Produto [ID: %d, Nome: %s, Preço: R$ %.2f, Estoque: %d, Categoria: %s, Ativo: %s]", 
                idProduto, nome, preco != null ? preco : 0.0, estoque != null ? estoque : 0, 
                categoria, ativo ? "Sim" : "Não");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Produto produto = (Produto) obj;
        return idProduto != null ? idProduto.equals(produto.idProduto) : produto.idProduto == null;
    }
    
    @Override
    public int hashCode() {
        return idProduto != null ? idProduto.hashCode() : 0;
    }
}

