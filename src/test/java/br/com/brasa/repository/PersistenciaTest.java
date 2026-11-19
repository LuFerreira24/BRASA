package br.com.brasa.repository;

import br.com.brasa.TestcontainersConfiguration;
import br.com.brasa.entity.Cliente;
import br.com.brasa.entity.OrdemServico;
import br.com.brasa.entity.Peca;
import br.com.brasa.entity.Servico;
import br.com.brasa.entity.Usuario;
import br.com.brasa.entity.Veiculo;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Experimento 3 - Persistencia no PostgreSQL e restricoes de integridade.
 * Usa um PostgreSQL real (Testcontainers) criado pelo script SQL do projeto.
 * Cada teste roda em uma transacao que e desfeita ao final (@DataJpaTest).
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class PersistenciaTest {

    @Autowired private TestEntityManager em;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private VeiculoRepository veiculoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PecaRepository pecaRepository;
    @Autowired private OrdemServicoRepository ordemServicoRepository;

    private Cliente novoCliente(String cpf) {
        return clienteRepository.save(new Cliente("Cliente " + cpf, cpf));
    }

    private Veiculo novoVeiculo(Cliente cliente, String placa) {
        return veiculoRepository.save(new Veiculo(cliente, placa, "Fiat", "Strada"));
    }

    @Test
    @DisplayName("Grava e le um cliente; data de cadastro e ativo sao preenchidos automaticamente")
    void gravarELer() {
        Cliente salvo = novoCliente("529.982.247-25");
        em.flush();
        em.clear();

        Cliente lido = clienteRepository.findById(salvo.getId()).orElseThrow();
        assertThat(lido.getCpfCnpj()).isEqualTo("52998224725");
        assertThat(lido.getDataCadastro()).isNotNull();
        assertThat(lido.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("Dois clientes com o mesmo CPF/CNPJ violam a restricao UNIQUE")
    void cpfDuplicado() {
        novoCliente("529.982.247-25");
        em.flush();
        // mesmo CPF com outra mascara: a normalizacao garante que o banco detecte a duplicidade
        assertThatThrownBy(() -> clienteRepository.saveAndFlush(new Cliente("Outro", "52998224725")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Placa duplicada viola a restricao UNIQUE")
    void placaDuplicada() {
        Cliente cliente = novoCliente("529.982.247-25");
        novoVeiculo(cliente, "ABC-1234");
        em.flush();
        assertThatThrownBy(() -> veiculoRepository.saveAndFlush(new Veiculo(cliente, "abc1234", "VW", "Gol")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Chassi duplicado viola a restricao UNIQUE")
    void chassiDuplicado() {
        Cliente cliente = novoCliente("529.982.247-25");
        Veiculo v1 = new Veiculo(cliente, "ABC1234", "Fiat", "Uno");
        v1.setChassi("9BWZZZ377VT004251");
        veiculoRepository.saveAndFlush(v1);

        Veiculo v2 = new Veiculo(cliente, "XYZ9A87", "Fiat", "Uno");
        v2.setChassi("9bwzzz377vt004251");
        assertThatThrownBy(() -> veiculoRepository.saveAndFlush(v2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Codigo de peca duplicado viola a restricao UNIQUE")
    void codigoPecaDuplicado() {
        pecaRepository.saveAndFlush(new Peca("FLT-01", "Filtro", new BigDecimal("30.00"), 1));
        assertThatThrownBy(() -> pecaRepository.saveAndFlush(new Peca("FLT-01", "Outro filtro", new BigDecimal("40.00"), 1)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("E-mail de usuario duplicado viola a restricao UNIQUE")
    void emailDuplicado() {
        usuarioRepository.saveAndFlush(new Usuario("Ana", "ana@oficina.com", "hash"));
        assertThatThrownBy(() -> usuarioRepository.saveAndFlush(new Usuario("Ana 2", "ANA@oficina.com", "hash")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Veiculo sem cliente e barrado pela validacao antes de chegar ao banco")
    void veiculoSemClienteValidacao() {
        assertThatThrownBy(() -> veiculoRepository.saveAndFlush(new Veiculo(null, "ABC1234", "Fiat", "Uno")))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Chave estrangeira impede veiculo apontando para cliente inexistente")
    void veiculoComClienteInexistente() {
        assertThatThrownBy(() -> em.getEntityManager()
                .createNativeQuery("INSERT INTO veiculo (id_cliente, placa, modelo, marca) VALUES (999999, 'ZZZ9Z99', 'X', 'Y')")
                .executeUpdate())
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    @DisplayName("CPF invalido e barrado pela Bean Validation ao gravar")
    void cpfInvalidoNaoGrava() {
        assertThatThrownBy(() -> clienteRepository.saveAndFlush(new Cliente("Fulano", "123.456.789-00")))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Veiculo inativado some da listagem de ativos, mas continua nas OS antigas (soft delete)")
    void softDeleteVeiculo() {
        Cliente cliente = novoCliente("529.982.247-25");
        Veiculo veiculo = novoVeiculo(cliente, "ABC1D23");
        Veiculo outro = novoVeiculo(cliente, "XYZ1234");
        OrdemServico os = ordemServicoRepository.save(new OrdemServico(cliente, veiculo, "Revisao", null));

        veiculo.inativar();
        em.flush();
        em.clear();

        assertThat(veiculoRepository.findByAtivoTrue())
                .extracting(Veiculo::getPlaca)
                .contains("XYZ1234")
                .doesNotContain("ABC1D23");
        assertThat(veiculoRepository.findByClienteIdAndAtivoTrue(cliente.getId()))
                .extracting(Veiculo::getId).containsExactly(outro.getId());

        // o registro continua no banco e ligado a OS antiga
        OrdemServico osAntiga = ordemServicoRepository.findById(os.getId()).orElseThrow();
        assertThat(osAntiga.getVeiculo().getPlaca()).isEqualTo("ABC1D23");
        assertThat(osAntiga.getVeiculo().isAtivo()).isFalse();
    }

    @Test
    @DisplayName("Relacionamentos @ManyToOne LAZY nao sao carregados ate serem acessados")
    void carregamentoLazy() {
        Cliente cliente = novoCliente("529.982.247-25");
        Veiculo veiculo = novoVeiculo(cliente, "ABC1234");
        em.flush();
        em.clear();

        Veiculo lido = veiculoRepository.findById(veiculo.getId()).orElseThrow();
        var util = em.getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
        assertThat(util.isLoaded(lido, "cliente")).isFalse();

        assertThat(lido.getCliente().getNome()).isEqualTo(cliente.getNome());
        assertThat(util.isLoaded(lido.getCliente())).isTrue();
    }

    @Test
    @DisplayName("OS gravada em cascata com itens e historico; valor_total persistido")
    void gravarOsComItens() {
        Cliente cliente = novoCliente("529.982.247-25");
        Veiculo veiculo = novoVeiculo(cliente, "ABC1234");
        Servico servico = em.persist(new Servico("Troca de oleo", new BigDecimal("60.00")));
        Peca peca = pecaRepository.save(new Peca("OLEO-5W30", "Oleo 5W30", new BigDecimal("45.90"), 10));

        OrdemServico os = new OrdemServico(cliente, veiculo, "Troca de oleo", null);
        os.adicionarServico(servico, 1, null);
        os.adicionarPeca(peca, 4, null);
        ordemServicoRepository.save(os);
        em.flush();
        em.clear();

        OrdemServico lida = ordemServicoRepository.findById(os.getId()).orElseThrow();
        assertThat(lida.getValorTotal()).isEqualByComparingTo("243.60");
        assertThat(lida.getServicos()).hasSize(1);
        assertThat(lida.getPecas()).hasSize(1);
        assertThat(lida.getHistorico()).extracting("tipoAlteracao")
                .containsExactly("ABERTURA", "INCLUSAO_SERVICO", "INCLUSAO_PECA");
    }
}
