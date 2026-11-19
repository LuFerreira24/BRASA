package br.com.brasa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import br.com.brasa.exception.RegraNegocioException;
import java.math.RoundingMode;

import java.math.BigDecimal;

// Pecas utilizadas em uma OS (resolve ORDEM_SERVICO N:N PECA)
@Entity
@Table(name = "os_peca")
public class OsPeca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os_peca")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_os", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_peca", nullable = false)
    private Peca peca;

    @NotNull
    @Positive
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    protected OsPeca() {
    }

    /** Criado por OrdemServico.adicionarPeca(): o valor unitario e copiado do cadastro no momento da inclusao. */
    OsPeca(OrdemServico ordemServico, Peca peca, int quantidade, BigDecimal valorUnitario) {
        if (quantidade <= 0) {
            throw new RegraNegocioException("Quantidade deve ser maior que zero");
        }
        if (valorUnitario == null || valorUnitario.signum() < 0) {
            throw new RegraNegocioException("Valor unitario invalido");
        }
        this.ordemServico = ordemServico;
        this.peca = peca;
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

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
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
}
