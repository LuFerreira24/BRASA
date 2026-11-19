package br.com.brasa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import br.com.brasa.exception.RegraNegocioException;
import br.com.brasa.validation.Placas;
import java.time.Year;
import java.util.Locale;

import java.time.LocalDateTime;

@Entity
@Table(name = "veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veiculo")
    private Long id;

    @NotNull(message = "Veiculo deve estar vinculado a um cliente")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    // Gravada normalizada: maiusculas e sem traco (ABC1234 ou ABC1D23)
    @NotBlank(message = "Placa e obrigatoria")
    @Pattern(regexp = Placas.REGEX, message = "Placa invalida (use ABC-1234 ou ABC1D23)")
    @Column(name = "placa", nullable = false, unique = true, length = 10)
    private String placa;

    @NotBlank(message = "Modelo e obrigatorio")
    @Size(max = 100)
    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @NotBlank(message = "Marca e obrigatoria")
    @Size(max = 100)
    @Column(name = "marca", nullable = false, length = 100)
    private String marca;

    @Min(1886)
    @Column(name = "ano")
    private Integer ano;

    @Size(max = 50)
    @Column(name = "cor", length = 50)
    private String cor;

    @Size(max = 50)
    @Column(name = "chassi", unique = true, length = 50)
    private String chassi;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public Veiculo() {
    }

    public Veiculo(Cliente cliente, String placa, String marca, String modelo) {
        this.cliente = cliente;
        setPlaca(placa);
        this.marca = marca;
        this.modelo = modelo;
    }

    /**
     * Inativacao logica: o veiculo deixa de aparecer para novos atendimentos,
     * mas continua vinculado as Ordens de Servico antigas.
     */
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = Placas.normalizar(placa);
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        // Ano-modelo pode ser o ano seguinte ao atual (carros lancados no fim do ano)
        if (ano != null && ano > Year.now().getValue() + 1) {
            throw new RegraNegocioException("Ano do veiculo nao pode estar no futuro: " + ano);
        }
        this.ano = ano;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getChassi() {
        return chassi;
    }

    public void setChassi(String chassi) {
        this.chassi = chassi == null ? null : chassi.trim().toUpperCase(Locale.ROOT);
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
}
