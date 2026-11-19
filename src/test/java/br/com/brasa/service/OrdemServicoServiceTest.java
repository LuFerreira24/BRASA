package br.com.brasa.service;

import br.com.brasa.TestcontainersConfiguration;
import br.com.brasa.entity.Cliente;
import br.com.brasa.entity.OrdemServico;
import br.com.brasa.entity.Peca;
import br.com.brasa.entity.Usuario;
import br.com.brasa.entity.Veiculo;
import br.com.brasa.entity.enums.StatusOrdemServico;
import br.com.brasa.exception.RegraNegocioException;
import br.com.brasa.repository.ClienteRepository;
import br.com.brasa.repository.MovimentacaoEstoqueRepository;
import br.com.brasa.repository.OrdemServicoRepository;
import br.com.brasa.repository.PecaRepository;
import br.com.brasa.repository.UsuarioRepository;
import br.com.brasa.repository.VeiculoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Experimento 4 - teste de persistencia com @Transactional.
 * O teste NAO roda dentro de uma transacao propria: so a transacao do servico existe,
 * entao da para conferir no banco que uma falha no meio desfaz tudo (rollback).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(TestcontainersConfiguration.class)
class OrdemServicoServiceTest {

    @Autowired private OrdemServicoService service;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private VeiculoRepository veiculoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PecaRepository pecaRepository;
    @Autowired private OrdemServicoRepository ordemServicoRepository;
    @Autowired private MovimentacaoEstoqueRepository movimentacaoRepository;
    @Autowired private JdbcTemplate jdbc;

    private Usuario mecanico;
    private Peca oleo;
    private Peca filtro;
    private OrdemServico os;

    @BeforeEach
    void prepararOsAguardandoAprovacao() {
        Cliente cliente = clienteRepository.save(new Cliente("Joao", "529.982.247-25"));
        Veiculo veiculo = veiculoRepository.save(new Veiculo(cliente, "ABC1D23", "Fiat", "Strada"));
        mecanico = usuarioRepository.save(new Usuario("Carlos", "carlos@oficina.com", "hash"));
        oleo = pecaRepository.save(new Peca("OLEO-5W30", "Oleo 5W30", new BigDecimal("45.90"), 10));
        filtro = pecaRepository.save(new Peca("FLT-01", "Filtro de oleo", new BigDecimal("35.50"), 5));

        OrdemServico nova = new OrdemServico(cliente, veiculo, "Troca de oleo", mecanico);
        nova.adicionarPeca(oleo, 4, mecanico);
        nova.adicionarPeca(filtro, 3, mecanico);
        nova.alterarStatus(StatusOrdemServico.EM_ANALISE, mecanico);
        nova.alterarStatus(StatusOrdemServico.AGUARDANDO_APROVACAO, mecanico);
        os = ordemServicoRepository.save(nova);
    }

    @AfterEach
    void limparBanco() {
        jdbc.execute("TRUNCATE movimentacao_estoque, historico_os, os_peca, os_servico, ordem_servico, "
                + "veiculo, usuario, cliente, peca RESTART IDENTITY CASCADE");
    }

    private int estoque(Peca peca) {
        return pecaRepository.findById(peca.getId()).orElseThrow().getQuantidadeEstoque();
    }

    @Test
    @DisplayName("Ao iniciar a execucao, todas as pecas sao baixadas e as movimentacoes registradas")
    void baixaComSucesso() {
        service.iniciarExecucao(os.getId(), mecanico.getId());

        assertThat(estoque(oleo)).isEqualTo(6);
        assertThat(estoque(filtro)).isEqualTo(2);
        assertThat(movimentacaoRepository.findByOrdemServicoId(os.getId())).hasSize(2);
        assertThat(ordemServicoRepository.findById(os.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    @DisplayName("Se a baixa de uma peca falhar, nada e gravado pela metade (rollback)")
    void rollbackQuandoEstoqueInsuficiente() {
        // outra OS consumiu o filtro depois que esta foi orcada: so resta 1 unidade
        Peca filtroAtual = pecaRepository.findById(filtro.getId()).orElseThrow();
        filtroAtual.setQuantidadeEstoque(1);
        pecaRepository.save(filtroAtual);

        assertThatThrownBy(() -> service.iniciarExecucao(os.getId(), mecanico.getId()))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Estoque insuficiente");

        // o oleo (primeira peca) chegou a ser baixado na memoria, mas a transacao foi desfeita
        assertThat(estoque(oleo)).isEqualTo(10);
        assertThat(estoque(filtro)).isEqualTo(1);
        assertThat(movimentacaoRepository.findByOrdemServicoId(os.getId())).isEmpty();
        assertThat(ordemServicoRepository.findById(os.getId()).orElseThrow().getStatus())
                .isEqualTo(StatusOrdemServico.AGUARDANDO_APROVACAO);
    }
}
