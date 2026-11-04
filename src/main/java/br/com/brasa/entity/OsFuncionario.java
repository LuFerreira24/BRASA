package br.com.brasa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Associativa ORDEM_SERVICO N:N USUARIO com atributos proprios
@Entity
@Table(name = "os_funcionario")
public class OsFuncionario {

    @EmbeddedId
    private OsFuncionarioId id = new OsFuncionarioId();

    @MapsId("idOs")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_os")
    private OrdemServico ordemServico;

    @MapsId("idUsuario")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "responsabilidade", length = 100)
    private String responsabilidade;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim")
    private LocalDateTime dataFim;

    @PrePersist
    protected void prePersist() {
        if (dataInicio == null) {
            dataInicio = LocalDateTime.now();
        }
    }

    public OsFuncionarioId getId() {
        return id;
    }

    public void setId(OsFuncionarioId id) {
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

    public String getResponsabilidade() {
        return responsabilidade;
    }

    public void setResponsabilidade(String responsabilidade) {
        this.responsabilidade = responsabilidade;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }
}
