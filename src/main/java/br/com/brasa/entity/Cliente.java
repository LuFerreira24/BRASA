package br.com.brasa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import br.com.brasa.validation.CpfCnpj;
import br.com.brasa.validation.Documentos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long id;

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150)
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    // Gravado somente com digitos (11 para CPF, 14 para CNPJ) para que a restricao UNIQUE
    // nao seja burlada por mascaras diferentes do mesmo documento.
    @NotBlank(message = "CPF/CNPJ e obrigatorio")
    @CpfCnpj
    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 18)
    private String cpfCnpj;

    @Size(max = 20)
    @Column(name = "telefone", length = 20)
    private String telefone;

    @Email(message = "E-mail invalido")
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    @Size(max = 255)
    @Column(name = "endereco", length = 255)
    private String endereco;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "cliente")
    private List<Veiculo> veiculos = new ArrayList<>();

    @OneToOne(mappedBy = "cliente")
    private Usuario usuario;

    public Cliente() {
    }

    public Cliente(String nome, String cpfCnpj) {
        setNome(nome);
        setCpfCnpj(cpfCnpj);
    }

    /** Inativacao logica (soft delete): o registro continua no banco e ligado as OS antigas. */
    public void inativar() {
        this.ativo = false;
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }

    @PrePersist
    protected void prePersist() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = Documentos.somenteDigitos(cpfCnpj);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
