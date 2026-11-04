package br.com.brasa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vistoria")
public class Vistoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vistoria")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_os", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_responsavel", nullable = false)
    private Usuario usuarioResponsavel;

    @Column(name = "quilometragem")
    private Integer quilometragem;

    @Column(name = "nivel_combustivel", length = 30)
    private String nivelCombustivel;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_vistoria", nullable = false)
    private LocalDateTime dataVistoria;

    @OneToMany(mappedBy = "vistoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VistoriaItem> itens = new ArrayList<>();

    @OneToMany(mappedBy = "vistoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VistoriaAnexo> anexos = new ArrayList<>();

    @PrePersist
    protected void prePersist() {
        if (dataVistoria == null) {
            dataVistoria = LocalDateTime.now();
        }
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

    public Usuario getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(Usuario usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public Integer getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(Integer quilometragem) {
        this.quilometragem = quilometragem;
    }

    public String getNivelCombustivel() {
        return nivelCombustivel;
    }

    public void setNivelCombustivel(String nivelCombustivel) {
        this.nivelCombustivel = nivelCombustivel;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataVistoria() {
        return dataVistoria;
    }

    public void setDataVistoria(LocalDateTime dataVistoria) {
        this.dataVistoria = dataVistoria;
    }

    public List<VistoriaItem> getItens() {
        return itens;
    }

    public void setItens(List<VistoriaItem> itens) {
        this.itens = itens;
    }

    public List<VistoriaAnexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(List<VistoriaAnexo> anexos) {
        this.anexos = anexos;
    }
}
