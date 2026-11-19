package br.com.brasa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Trilha de alteracoes de uma Ordem de Servico
@Entity
@Table(name = "historico_os")
public class HistoricoOs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_os", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "tipo_alteracao", nullable = false, length = 100)
    private String tipoAlteracao;

    @Column(name = "campo_alterado", length = 100)
    private String campoAlterado;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_novo", columnDefinition = "TEXT")
    private String valorNovo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @PrePersist
    protected void prePersist() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }

    protected HistoricoOs() {
    }

    public HistoricoOs(OrdemServico ordemServico, Usuario usuario, String tipoAlteracao,
                       String campoAlterado, String valorAnterior, String valorNovo, String descricao) {
        this.ordemServico = ordemServico;
        this.usuario = usuario;
        this.tipoAlteracao = tipoAlteracao;
        this.campoAlterado = campoAlterado;
        this.valorAnterior = valorAnterior;
        this.valorNovo = valorNovo;
        this.descricao = descricao;
        this.dataHora = LocalDateTime.now();
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipoAlteracao() {
        return tipoAlteracao;
    }

    public void setTipoAlteracao(String tipoAlteracao) {
        this.tipoAlteracao = tipoAlteracao;
    }

    public String getCampoAlterado() {
        return campoAlterado;
    }

    public void setCampoAlterado(String campoAlterado) {
        this.campoAlterado = campoAlterado;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNovo() {
        return valorNovo;
    }

    public void setValorNovo(String valorNovo) {
        this.valorNovo = valorNovo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
