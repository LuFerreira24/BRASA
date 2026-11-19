package br.com.brasa.entity;

import br.com.brasa.entity.enums.StatusOrdemServico;
import br.com.brasa.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static br.com.brasa.entity.enums.StatusOrdemServico.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Experimento 4 - Regras de dominio da Ordem de Servico.
 * Testes unitarios puros em JUnit, sem banco de dados.
 */
class OrdemServicoTest {

    private Cliente cliente;
    private Veiculo veiculo;
    private Usuario mecanico;
    private Servico alinhamento;
    private Peca filtro;
    private OrdemServico os;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Joao da Silva", "529.982.247-25");
        veiculo = new Veiculo(cliente, "ABC1D23", "Fiat", "Strada");
        mecanico = new Usuario("Carlos", "carlos@oficina.com", "hash");
        alinhamento = new Servico("Alinhamento", new BigDecimal("80.00"));
        filtro = new Peca("FLT-01", "Filtro de oleo", new BigDecimal("35.50"), 10);
        os = new OrdemServico(cliente, veiculo, "Barulho na suspensao", mecanico);
    }

    private void avancarAte(StatusOrdemServico destino) {
        StatusOrdemServico[] fluxo = {EM_ANALISE, AGUARDANDO_APROVACAO, EM_EXECUCAO, PRONTO, ENTREGUE};
        for (StatusOrdemServico s : fluxo) {
            if (os.getStatus() == destino) {
                return;
            }
            os.alterarStatus(s, mecanico);
        }
    }

    @Nested
    @DisplayName("Abertura da OS")
    class Abertura {

        @Test
        @DisplayName("OS nasce ABERTA, com valor zero e registro de abertura no historico")
        void novaOs() {
            assertThat(os.getStatus()).isEqualTo(ABERTA);
            assertThat(os.getValorTotal()).isEqualByComparingTo("0");
            assertThat(os.getDataAbertura()).isNotNull();
            assertThat(os.getHistorico()).hasSize(1);
            assertThat(os.getHistorico().getFirst().getTipoAlteracao()).isEqualTo("ABERTURA");
        }

        @Test
        @DisplayName("Veiculo de outro cliente e rejeitado")
        void veiculoDeOutroCliente() {
            Cliente outro = new Cliente("Maria", "111.444.777-35");
            assertThatThrownBy(() -> new OrdemServico(outro, veiculo, "Revisao", mecanico))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("nao pertence");
        }

        @Test
        @DisplayName("Veiculo inativo nao pode receber novo atendimento")
        void veiculoInativo() {
            veiculo.inativar();
            assertThatThrownBy(() -> new OrdemServico(cliente, veiculo, "Revisao", mecanico))
                    .isInstanceOf(RegraNegocioException.class);
        }

        @Test
        @DisplayName("OS sem cliente ou sem veiculo e rejeitada (RN-01)")
        void semClienteOuVeiculo() {
            assertThatThrownBy(() -> new OrdemServico(null, veiculo, "Revisao", mecanico))
                    .isInstanceOf(RegraNegocioException.class);
            assertThatThrownBy(() -> new OrdemServico(cliente, null, "Revisao", mecanico))
                    .isInstanceOf(RegraNegocioException.class);
        }
    }

    @Nested
    @DisplayName("Transicoes de status")
    class Status {

        @Test
        @DisplayName("Segue o fluxo completo ABERTA -> ... -> ENTREGUE e preenche a data de finalizacao")
        void fluxoCompleto() {
            avancarAte(ENTREGUE);
            assertThat(os.getStatus()).isEqualTo(ENTREGUE);
            assertThat(os.getDataFinalizacao()).isNotNull();
        }

        @Test
        @DisplayName("ABERTA direto para ENTREGUE e rejeitada")
        void pularEtapas() {
            assertThatThrownBy(() -> os.alterarStatus(ENTREGUE, mecanico))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("ABERTA -> ENTREGUE");
            assertThat(os.getStatus()).isEqualTo(ABERTA);
            assertThat(os.getDataFinalizacao()).isNull();
        }

        @Test
        @DisplayName("Nao e permitido voltar status")
        void voltarStatus() {
            avancarAte(EM_EXECUCAO);
            assertThatThrownBy(() -> os.alterarStatus(EM_ANALISE, mecanico))
                    .isInstanceOf(RegraNegocioException.class);
        }

        @ParameterizedTest
        @EnumSource(value = StatusOrdemServico.class, names = {"ABERTA", "EM_ANALISE", "AGUARDANDO_APROVACAO", "EM_EXECUCAO", "PRONTO"})
        @DisplayName("CANCELADA e saida possivel de qualquer status nao encerrado")
        void cancelar(StatusOrdemServico origem) {
            avancarAte(origem);
            os.alterarStatus(CANCELADA, mecanico);
            assertThat(os.getStatus()).isEqualTo(CANCELADA);
            assertThat(os.getDataFinalizacao()).isNotNull();
        }

        @Test
        @DisplayName("OS ENTREGUE ou CANCELADA nao muda mais de status")
        void statusFinal() {
            os.alterarStatus(CANCELADA, mecanico);
            assertThatThrownBy(() -> os.alterarStatus(EM_ANALISE, mecanico))
                    .isInstanceOf(RegraNegocioException.class);
        }

        @Test
        @DisplayName("Cada mudanca de status fica registrada no historico")
        void historico() {
            os.alterarStatus(EM_ANALISE, mecanico);
            HistoricoOs ultimo = os.getHistorico().getLast();
            assertThat(ultimo.getTipoAlteracao()).isEqualTo("ALTERACAO_STATUS");
            assertThat(ultimo.getValorAnterior()).isEqualTo("ABERTA");
            assertThat(ultimo.getValorNovo()).isEqualTo("EM_ANALISE");
            assertThat(ultimo.getUsuario()).isSameAs(mecanico);
        }
    }

    @Nested
    @DisplayName("Itens e valor total")
    class Itens {

        @Test
        @DisplayName("valor_total acompanha a inclusao e a remocao de itens (BigDecimal)")
        void valorTotal() {
            os.adicionarServico(alinhamento, 1, mecanico);                    // 80.00
            OsPeca pecas = os.adicionarPeca(filtro, 2, mecanico);             // 71.00
            assertThat(os.getValorTotal()).isEqualByComparingTo("151.00");

            os.removerPeca(pecas, mecanico);
            assertThat(os.getValorTotal()).isEqualByComparingTo("80.00");
        }

        @Test
        @DisplayName("Valor unitario e copiado do cadastro e o subtotal e calculado")
        void subtotal() {
            OsPeca item = os.adicionarPeca(filtro, 3, mecanico);
            assertThat(item.getValorUnitario()).isEqualByComparingTo("35.50");
            assertThat(item.getSubtotal()).isEqualByComparingTo("106.50");
        }

        @Test
        @DisplayName("Somas decimais nao sofrem erro de arredondamento")
        void semErroDeArredondamento() {
            Servico s = new Servico("Teste", new BigDecimal("0.10"));
            os.adicionarServico(s, 1, mecanico);
            os.adicionarServico(s, 2, mecanico);
            assertThat(os.getValorTotal()).isEqualByComparingTo("0.30");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1})
        @DisplayName("Quantidade menor ou igual a zero e rejeitada")
        void quantidadeInvalida(int quantidade) {
            assertThatThrownBy(() -> os.adicionarServico(alinhamento, quantidade, mecanico))
                    .isInstanceOf(RegraNegocioException.class);
            assertThatThrownBy(() -> os.adicionarPeca(filtro, quantidade, mecanico))
                    .isInstanceOf(RegraNegocioException.class);
            assertThat(os.getValorTotal()).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("Peca inativa nao pode ser incluida")
        void pecaInativa() {
            filtro.inativar();
            assertThatThrownBy(() -> os.adicionarPeca(filtro, 1, mecanico))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("inativa");
        }

        @Test
        @DisplayName("Estoque insuficiente impede a inclusao da peca")
        void estoqueInsuficiente() {
            assertThatThrownBy(() -> os.adicionarPeca(filtro, 11, mecanico))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("Estoque insuficiente");
            assertThat(os.getPecas()).isEmpty();
        }

        @Test
        @DisplayName("A mesma peca incluida varias vezes soma a quantidade ao conferir o estoque")
        void estoqueSomaInclusoes() {
            os.adicionarPeca(filtro, 6, mecanico);
            assertThatThrownBy(() -> os.adicionarPeca(filtro, 5, mecanico))
                    .isInstanceOf(RegraNegocioException.class);
        }

        @Test
        @DisplayName("Itens nao podem ser alterados depois do diagnostico")
        void edicaoForaDoDiagnostico() {
            avancarAte(AGUARDANDO_APROVACAO);
            assertThatThrownBy(() -> os.adicionarServico(alinhamento, 1, mecanico))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("diagnostico");
        }

        @Test
        @DisplayName("Lista de itens nao pode ser alterada por fora dos metodos de dominio")
        void listaImutavel() {
            assertThatThrownBy(() -> os.getPecas().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
