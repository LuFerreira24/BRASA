package br.com.brasa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import br.com.brasa.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "peca")
public class Peca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_peca")
    private Long id;

    @NotBlank(message = "Codigo e obrigatorio")
    @Size(max = 50)
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150)
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Size(max = 100)
    @Column(name = "marca", length = 100)
    private String marca;

    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    @Column(name = "preco_custo", precision = 12, scale = 2)
    private BigDecimal precoCusto;

    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    @Column(name = "preco_venda", precision = 12, scale = 2)
    private BigDecimal precoVenda;

    @NotNull
    @PositiveOrZero
    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque = 0;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    public Peca() {
    }

    public Peca(String codigo, String nome, BigDecimal precoVenda, int quantidadeEstoque) {
        this.codigo = codigo;
        this.nome = nome;
        setPrecoVenda(precoVenda);
        setQuantidadeEstoque(quantidadeEstoque);
    }

    public boolean temEstoque(int quantidade) {
        return quantidadeEstoque != null && quantidadeEstoque >= quantidade;
    }

    /** Baixa do estoque (RF-014). Lanca excecao se a quantidade disponivel for insuficiente. */
    public void baixarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("Quantidade a baixar deve ser maior que zero");
        }
        if (!temEstoque(quantidade)) {
            throw new RegraNegocioException("Estoque insuficiente para a peca " + codigo
                    + ": disponivel " + quantidadeEstoque + ", solicitado " + quantidade);
        }
        quantidadeEstoque -= quantidade;
    }

    /** Entrada ou devolucao ao estoque. */
    public void reporEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("Quantidade a repor deve ser maior que zero");
        }
        quantidadeEstoque += quantidade;
    }

    public void inativar() {
        this.ativo = false;
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }

    private static void validarPreco(BigDecimal preco, String tipo) {
        if (preco != null && preco.signum() < 0) {
            throw new RegraNegocioException("Preco de " + tipo + " nao pode ser negativo");
        }
    }

    @PrePersist
    protected void prePersist() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(BigDecimal precoCusto) {
        validarPreco(precoCusto, "custo");
        this.precoCusto = precoCusto;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        validarPreco(precoVenda, "venda");
        this.precoVenda = precoVenda;
    }

    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        if (quantidadeEstoque == null || quantidadeEstoque < 0) {
            throw new RegraNegocioException("Quantidade em estoque nao pode ser negativa");
        }
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
