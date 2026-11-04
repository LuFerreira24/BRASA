package br.com.brasa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

// Chave primaria composta de os_funcionario: (id_os, id_usuario)
@Embeddable
public class OsFuncionarioId implements Serializable {

    @Column(name = "id_os")
    private Long idOs;

    @Column(name = "id_usuario")
    private Long idUsuario;

    public OsFuncionarioId() {
    }

    public OsFuncionarioId(Long idOs, Long idUsuario) {
        this.idOs = idOs;
        this.idUsuario = idUsuario;
    }

    public Long getIdOs() {
        return idOs;
    }

    public void setIdOs(Long idOs) {
        this.idOs = idOs;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OsFuncionarioId that)) return false;
        return Objects.equals(idOs, that.idOs) && Objects.equals(idUsuario, that.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOs, idUsuario);
    }
}
