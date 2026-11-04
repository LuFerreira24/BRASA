package br.com.brasa.entity;

import br.com.brasa.entity.enums.TipoItemOrcamento;
import jakarta.persistence.*;

import java.math.BigDecimal;

// Cada item referencia uma peca OU um servico, nunca os dois (garantido pelo CHECK do banco)
@Entity
@Table(name = "orcamento_item")
public class OrcamentoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento_item")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamento", nullable = false)
    private Orcamento orcamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 20)
    private TipoItemOrcamento tipoItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_peca")
    private Peca peca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servico")
    private Servico servico;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public TipoItemOrcamento getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(TipoItemOrcamento tipoItem) {
        this.tipoItem = tipoItem;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
