package br.com.brasa.entity;

import br.com.brasa.entity.enums.StatusOrdemServico;
import br.com.brasa.exception.RegraNegocioException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Ordem de Servico. As regras de negocio ficam dentro da propria classe:
 * status so muda por {@link #alterarStatus}, itens so entram por {@link #adicionarServico}
 * e {@link #adicionarPeca}, e o valor total e sempre recalculado a partir dos itens.
 * Cada alteracao e registrada em {@link HistoricoOs} (RN-15).
 */
@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    /** Itens so podem ser incluidos/removidos durante o diagnostico (RF-008). */
    private static final Set<StatusOrdemServico> STATUS_EDITAVEIS =
            EnumSet.of(StatusOrdemServico.ABERTA, StatusOrdemServico.EM_ANALISE);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_veiculo", nullable = false)
    private Veiculo veiculo;

    @NotBlank(message = "Motivo da visita e obrigatorio")
    @Column(name = "motivo_visita", nullable = false, columnDefinition = "TEXT")
    private String motivoVisita;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusOrdemServico status = StatusOrdemServico.ABERTA;

    @NotNull
    @PositiveOrZero
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
    @OrderBy("id ASC")
    private List<OsServico> servicos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<OsPeca> pecas = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico")
    private List<Vistoria> vistorias = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico")
    private List<Orcamento> orcamentos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico")
    private List<Pagamento> pagamentos = new ArrayList<>();

    @OneToMany(mappedBy = "ordemServico", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @OrderBy("dataHora ASC")
    private List<HistoricoOs> historico = new ArrayList<>();

    protected OrdemServico() {
        // exigido pelo JPA
    }

    /**
     * Abre uma OS (RF-005). O veiculo precisa pertencer ao cliente informado e estar ativo,
     * e o cliente tambem precisa estar ativo (RN-01, RN-02).
     */
    public OrdemServico(Cliente cliente, Veiculo veiculo, String motivoVisita, Usuario responsavel) {
        if (cliente == null || veiculo == null) {
            throw new RegraNegocioException("A OS deve estar vinculada a um cliente e a um veiculo");
        }
        if (!cliente.isAtivo()) {
            throw new RegraNegocioException("Cliente inativo nao pode abrir Ordem de Servico");
        }
        if (!veiculo.isAtivo()) {
            throw new RegraNegocioException("Veiculo inativo nao pode receber novo atendimento");
        }
        if (!mesmoCliente(veiculo.getCliente(), cliente)) {
            throw new RegraNegocioException("O veiculo " + veiculo.getPlaca() + " nao pertence ao cliente informado");
        }
        if (motivoVisita == null || motivoVisita.isBlank()) {
            throw new RegraNegocioException("Motivo da visita e obrigatorio");
        }
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.motivoVisita = motivoVisita;
        this.dataAbertura = LocalDateTime.now();
        registrarHistorico(responsavel, "ABERTURA", "status", null, status.name(), "Ordem de Servico aberta");
    }

    // ------------------------------------------------------------------ itens

    public OsServico adicionarServico(Servico servico, int quantidade, Usuario responsavel) {
        verificarEdicaoPermitida();
        if (servico == null || !servico.isAtivo()) {
            throw new RegraNegocioException("Servico inexistente ou inativo nao pode ser incluido na OS");
        }
        OsServico item = new OsServico(this, servico, quantidade, servico.getPrecoAtual());
        servicos.add(item);
        recalcularValorTotal();
        registrarHistorico(responsavel, "INCLUSAO_SERVICO", "servicos", null,
                quantidade + "x " + servico.getNome(), "Servico incluido na OS");
        return item;
    }

    public void removerServico(OsServico item, Usuario responsavel) {
        verificarEdicaoPermitida();
        if (!servicos.remove(item)) {
            throw new RegraNegocioException("Servico nao pertence a esta OS");
        }
        recalcularValorTotal();
        registrarHistorico(responsavel, "REMOCAO_SERVICO", "servicos",
                item.getQuantidade() + "x " + item.getServico().getNome(), null, "Servico removido da OS");
    }

    /**
     * Inclui uma peca na OS. A peca precisa estar ativa e ter estoque suficiente;
     * a baixa efetiva do estoque acontece quando a OS entra em execucao (RF-014).
     */
    public OsPeca adicionarPeca(Peca peca, int quantidade, Usuario responsavel) {
        verificarEdicaoPermitida();
        if (peca == null || !peca.isAtivo()) {
            throw new RegraNegocioException("Peca inexistente ou inativa nao pode ser incluida na OS");
        }
        if (peca.getPrecoVenda() == null) {
            throw new RegraNegocioException("Peca " + peca.getCodigo() + " sem preco de venda cadastrado");
        }
        int jaIncluida = pecas.stream()
                .filter(p -> p.getPeca() == peca)
                .mapToInt(OsPeca::getQuantidade)
                .sum();
        if (quantidade > 0 && !peca.temEstoque(jaIncluida + quantidade)) {
            throw new RegraNegocioException("Estoque insuficiente para a peca " + peca.getCodigo()
                    + ": disponivel " + peca.getQuantidadeEstoque() + ", solicitado " + (jaIncluida + quantidade));
        }
        OsPeca item = new OsPeca(this, peca, quantidade, peca.getPrecoVenda());
        pecas.add(item);
        recalcularValorTotal();
        registrarHistorico(responsavel, "INCLUSAO_PECA", "pecas", null,
                quantidade + "x " + peca.getCodigo(), "Peca incluida na OS");
        return item;
    }

    public void removerPeca(OsPeca item, Usuario responsavel) {
        verificarEdicaoPermitida();
        if (!pecas.remove(item)) {
            throw new RegraNegocioException("Peca nao pertence a esta OS");
        }
        recalcularValorTotal();
        registrarHistorico(responsavel, "REMOCAO_PECA", "pecas",
                item.getQuantidade() + "x " + item.getPeca().getCodigo(), null, "Peca removida da OS");
    }

    /** valor_total = soma dos subtotais de servicos e pecas (RN: total calculado a partir dos itens). */
    public BigDecimal recalcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OsServico s : servicos) {
            total = total.add(s.getSubtotal());
        }
        for (OsPeca p : pecas) {
            total = total.add(p.getSubtotal());
        }
        this.valorTotal = total;
        return total;
    }

    // ----------------------------------------------------------------- status

    /**
     * Altera o status validando a transicao (RF-010). Ao chegar em ENTREGUE ou CANCELADA,
     * a data de finalizacao e preenchida. A alteracao fica registrada no historico.
     */
    public void alterarStatus(StatusOrdemServico novoStatus, Usuario responsavel) {
        if (!status.podeIrPara(novoStatus)) {
            throw new RegraNegocioException("Transicao de status nao permitida: " + status + " -> " + novoStatus);
        }
        StatusOrdemServico anterior = status;
        this.status = novoStatus;
        if (novoStatus.isFinal()) {
            this.dataFinalizacao = LocalDateTime.now();
        }
        registrarHistorico(responsavel, "ALTERACAO_STATUS", "status", anterior.name(), novoStatus.name(),
                "Status alterado de " + anterior + " para " + novoStatus);
    }

    // -------------------------------------------------------------- auxiliares

    private void verificarEdicaoPermitida() {
        if (!STATUS_EDITAVEIS.contains(status)) {
            throw new RegraNegocioException("Itens so podem ser alterados durante o diagnostico (status atual: " + status + ")");
        }
    }

    private void registrarHistorico(Usuario usuario, String tipo, String campo,
                                    String anterior, String novo, String descricao) {
        historico.add(new HistoricoOs(this, usuario, tipo, campo, anterior, novo, descricao));
    }

    private static boolean mesmoCliente(Cliente a, Cliente b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.getId() != null && Objects.equals(a.getId(), b.getId());
    }

    @PrePersist
    protected void prePersist() {
        if (dataAbertura == null) {
            dataAbertura = LocalDateTime.now();
        }
    }

    // ---------------------------------------------------------- getters/setters

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
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

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public LocalDateTime getDataPrevisao() {
        return dataPrevisao;
    }

    public void setDataPrevisao(LocalDateTime dataPrevisao) {
        if (dataPrevisao != null && dataAbertura != null && dataPrevisao.isBefore(dataAbertura)) {
            throw new RegraNegocioException("Data de previsao nao pode ser anterior a data de abertura");
        }
        this.dataPrevisao = dataPrevisao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public List<OsFuncionario> getFuncionarios() {
        return funcionarios;
    }

    public List<OsServico> getServicos() {
        return Collections.unmodifiableList(servicos);
    }

    public List<OsPeca> getPecas() {
        return Collections.unmodifiableList(pecas);
    }

    public List<Vistoria> getVistorias() {
        return vistorias;
    }

    public List<Orcamento> getOrcamentos() {
        return orcamentos;
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public List<HistoricoOs> getHistorico() {
        return Collections.unmodifiableList(historico);
    }
}
