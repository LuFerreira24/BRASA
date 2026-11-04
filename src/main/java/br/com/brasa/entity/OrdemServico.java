package br.com.brasa.entity;

import br.com.brasa.entity.enums.StatusOrdemServico;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_veiculo", nullable = false)
    private Veiculo veiculo;

    @Column(name = "motivo_visita", nullable = false, columnDefinition = "TEXT")
    private String motivoVisita;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusOrdemServico status = StatusOrdemServico.ABERTA;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "data_abertura", nullable = false, updatable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_previsao")
    private LocalDateTime dataPrevisao;

    @Column(name = "data_finalizacao")
    private LocalDateTime dataFinalizacao;

    @OneToMany(mappedBy = "ordemServico")
    private List<OsFuncionario> funcionarios = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OsServico> servicos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OsPeca> pecas = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico")
    private List<Vistoria> vistorias = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico")
    private List<Orcamento> orcamentos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico")
    private List<Pagamento> pagamentos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico")
    @OrderBy("dataHora ASC")
    private List<HistoricoOs> historico = new ArrayList<>();

    @PrePersist
    protected void prePersist() {
        if (dataAbertura == null) {
            dataAbertura = LocalDateTime.now();
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

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public String getMotivoVisita() {
        return motivoVisita;
    }

    public void setMotivoVisita(String motivoVisita) {
        this.motivoVisita = motivoVisita;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataPrevisao() {
        return dataPrevisao;
    }

    public void setDataPrevisao(LocalDateTime dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public List<OsFuncionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<OsFuncionario> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public List<OsServico> getServicos() {
        return servicos;
    }

    public void setServicos(List<OsServico> servicos) {
        this.servicos = servicos;
    }

    public List<OsPeca> getPecas() {
        return pecas;
    }

    public void setPecas(List<OsPeca> pecas) {
        this.pecas = pecas;
    }

    public List<Vistoria> getVistorias() {
        return vistorias;
    }

    public void setVistorias(List<Vistoria> vistorias) {
        this.vistorias = vistorias;
    }

    public List<Orcamento> getOrcamentos() {
        return orcamentos;
    }

    public void setOrcamentos(List<Orcamento> orcamentos) {
        this.orcamentos = orcamentos;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public List<HistoricoOs> getHistorico() {
        return historico;
    }

    public void setHistorico(List<HistoricoOs> historico) {
        this.historico = historico;
    }
}
