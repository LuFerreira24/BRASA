package br.com.brasa.entity;

import br.com.brasa.entity.enums.FormaPagamento;
import br.com.brasa.entity.enums.StatusPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_os", nullable = false)
    private OrdemServico ordemServico;

    @Column(name = "valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 30)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "comprovante", length = 500)
    private String comprovante;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getComprovante() {
        return comprovante;
    }

    public void setComprovante(String comprovante) {
        this.comprovante = comprovante;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
