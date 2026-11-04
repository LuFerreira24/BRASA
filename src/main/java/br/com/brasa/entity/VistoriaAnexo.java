package br.com.brasa.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Referencia (URL) para fotos/arquivos da vistoria guardados no armazenamento externo
@Entity
@Table(name = "vistoria_anexo")
public class VistoriaAnexo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anexo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vistoria", nullable = false)
    private Vistoria vistoria;

    @Column(name = "url_arquivo", nullable = false, length = 500)
    private String urlArquivo;

    @Column(name = "tipo_arquivo", length = 100)
    private String tipoArquivo;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;

    @PrePersist
    protected void prePersist() {
        if (dataUpload == null) {
            dataUpload = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vistoria getVistoria() {
        return vistoria;
    }

    public void setVistoria(Vistoria vistoria) {
        this.vistoria = vistoria;
    }

    public String getUrlArquivo() {
        return urlArquivo;
    }

    public void setUrlArquivo(String urlArquivo) {
        this.urlArquivo = urlArquivo;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }
}
