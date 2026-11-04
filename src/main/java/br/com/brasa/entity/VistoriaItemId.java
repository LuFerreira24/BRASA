package br.com.brasa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

// Chave primaria composta de vistoria_item: (id_vistoria, id_item)
@Embeddable
public class VistoriaItemId implements Serializable {

    @Column(name = "id_vistoria")
    private Long idVistoria;

    @Column(name = "id_item")
    private Long idItem;

    public VistoriaItemId() {
    }

    public VistoriaItemId(Long idVistoria, Long idItem) {
        this.idVistoria = idVistoria;
        this.idItem = idItem;
    }

    public Long getIdVistoria() {
        return idVistoria;
    }

    public void setIdVistoria(Long idVistoria) {
        this.idVistoria = idVistoria;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VistoriaItemId that)) return false;
        return Objects.equals(idVistoria, that.idVistoria) && Objects.equals(idItem, that.idItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVistoria, idItem);
    }
}
