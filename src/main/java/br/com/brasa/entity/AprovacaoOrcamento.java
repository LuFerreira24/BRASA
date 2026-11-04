package br.com.brasa.entity;

import br.com.brasa.entity.enums.DecisaoOrcamento;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "aprovacao_orcamento")
public class AprovacaoOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aprovacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_orcamento", nullable = false)
    private Orcamento orcamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_cliente", nullable = false)
    private Usuario usuarioCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "decisao", nullable = false, length = 20)
    private DecisaoOrcamento decisao;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "data_resposta", nullable = false)
    private LocalDateTime dataResposta;

    @PrePersist
    protected void prePersist() {
        if (dataResposta == null) {
            dataResposta = LocalDateTime.now();
        }
    }

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

    public Usuario getUsuarioCliente() {
        return usuarioCliente;
    }

    public void setUsuarioCliente(Usuario usuarioCliente) {
        this.usuarioCliente = usuarioCliente;
    }

    public DecisaoOrcamento getDecisao() {
        return decisao;
    }

    public void setDecisao(DecisaoOrcamento decisao) {
        this.decisao = decisao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(LocalDateTime dataResposta) {
        this.dataResposta = dataResposta;
    }
}
