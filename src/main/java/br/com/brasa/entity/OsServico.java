package br.com.brasa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import br.com.brasa.exception.RegraNegocioException;
import java.math.RoundingMode;

import java.math.BigDecimal;

// Servicos vinculados a uma OS (resolve ORDEM_SERVICO N:N SERVICO)
@Entity
@Table(name = "os_servico")
public class OsServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os_servico")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_os", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servico", nullable = false)
    private Servico servico;

    @NotNull
    @Positive
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    protected OsServico() {
    }

    /** Criado por OrdemServico.adicionarServico(): o valor unitario e copiado do cadastro no momento da inclusao. */
    OsServico(OrdemServico ordemServico, Servico servico, int quantidade, BigDecimal valorUnitario) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("Quantidade deve ser maior que zero");
        }
        if (valorUnitario == null || valorUnitario.signum() < 0) {
            throw new RegraNegocioException("Valor unitario invalido");
        }
        this.ordemServico = ordemServico;
        this.servico = servico;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.subtotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdemServico getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServico ordemServico) {
        this.ordemServico = ordemServico;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
