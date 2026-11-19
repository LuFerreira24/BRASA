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
@Table(name = "servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico")
    private Long id;

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150)
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    @Column(name = "preco_atual", nullable = false, precision = 12, scale = 2)
    private BigDecimal precoAtual;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    public Servico() {
    }

    public Servico(String nome, BigDecimal precoAtual) {
        this.nome = nome;
        setPrecoAtual(precoAtual);
    }

    public void inativar() {
        this.ativo = false;
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrecoAtual() {
        return precoAtual;
    }

    public void setPrecoAtual(BigDecimal precoAtual) {
        if (precoAtual != null && precoAtual.signum() < 0) {
            throw new RegraNegocioException("Preco do servico nao pode ser negativo");
        }
        this.precoAtual = precoAtual;
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
