package br.com.brasa.entity;

import br.com.brasa.entity.enums.StatusVistoriaItem;
import jakarta.persistence.*;

// Resultado de cada item do checklist em uma vistoria (VISTORIA N:N ITEM_CHECKLIST)
@Entity
@Table(name = "vistoria_item")
public class VistoriaItem {

    @EmbeddedId
    private VistoriaItemId id = new VistoriaItemId();

    @MapsId("idVistoria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vistoria")
    private Vistoria vistoria;

    @MapsId("idItem")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_item")
    private ItemChecklist item;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusVistoriaItem status;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    public VistoriaItemId getId() {
        return id;
    }

    public void setId(VistoriaItemId id) {
        this.id = id;
    }

    public Vistoria getVistoria() {
        return vistoria;
    }

    public void setVistoria(Vistoria vistoria) {
        this.vistoria = vistoria;
    }

    public ItemChecklist getItem() {
        return item;
    }

    public void setItem(ItemChecklist item) {
        this.item = item;
    }

    public StatusVistoriaItem getStatus() {
        return status;
    }

    public void setStatus(StatusVistoriaItem status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
