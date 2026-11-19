package br.com.brasa.entity;

import br.com.brasa.exception.RegraNegocioException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Experimento 2 - Validacoes dentro das classes de dominio.
 * Bean Validation (anotacoes nos campos) + invariantes verificadas nos metodos da classe.
 * Testes unitarios puros: nao usam banco nem Spring.
 */
class ValidacoesDominioTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void criarValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void fecharValidator() {
        factory.close();
    }

    private static <T> Set<String> camposInvalidos(T objeto) {
        Set<ConstraintViolation<T>> violacoes = validator.validate(objeto);
        return violacoes.stream().map(v -> v.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());
    }

    private static Cliente clienteValido() {
        return new Cliente("Joao da Silva", "529.982.247-25");
    }

    @Nested
    @DisplayName("Cliente")
    class ClienteTest {

        @Test
        @DisplayName("Cliente com dados validos nao tem violacoes")
        void valido() {
            Cliente cliente = clienteValido();
            cliente.setEmail("joao@email.com");
            assertThat(camposInvalidos(cliente)).isEmpty();
        }

        @Test
        @DisplayName("CPF e gravado somente com digitos")
        void normalizaCpf() {
            assertThat(clienteValido().getCpfCnpj()).isEqualTo("52998224725");
        }

        @Test
        @DisplayName("Nome em branco, CPF invalido e e-mail invalido sao rejeitados")
        void invalido() {
            Cliente cliente = new Cliente("  ", "529.982.247-24");
            cliente.setEmail("nao-e-email");
            assertThat(camposInvalidos(cliente)).containsExactlyInAnyOrder("nome", "cpfCnpj", "email");
        }

        @Test
        @DisplayName("CPF/CNPJ e obrigatorio")
        void cpfObrigatorio() {
            assertThat(camposInvalidos(new Cliente("Maria", null))).containsExactly("cpfCnpj");
        }
    }

    @Nested
    @DisplayName("Veiculo")
    class VeiculoTest {

        @ParameterizedTest
        @ValueSource(strings = {"ABC-1234", "abc1234", "ABC1D23", "abc1d23", "BRA 2E19"})
        @DisplayName("Aceita placa no padrao antigo e no padrao Mercosul")
        void placaValida(String placa) {
            Veiculo veiculo = new Veiculo(clienteValido(), placa, "Fiat", "Strada");
            assertThat(camposInvalidos(veiculo)).isEmpty();
        }

        @Test
        @DisplayName("Placa e normalizada (maiusculas, sem traco)")
        void normalizaPlaca() {
            assertThat(new Veiculo(clienteValido(), "abc-1234", "Fiat", "Uno").getPlaca()).isEqualTo("ABC1234");
        }

        @ParameterizedTest
        @ValueSource(strings = {"AB-1234", "ABCD123", "1234ABC", "ABC12D3", "ABC-12345"})
        @DisplayName("Rejeita placa fora dos padroes")
        void placaInvalida(String placa) {
            Veiculo veiculo = new Veiculo(clienteValido(), placa, "Fiat", "Strada");
            assertThat(camposInvalidos(veiculo)).containsExactly("placa");
        }

        @Test
        @DisplayName("Veiculo sem cliente e invalido (RN-02)")
        void semCliente() {
            Veiculo veiculo = new Veiculo(null, "ABC1234", "Fiat", "Strada");
            assertThat(camposInvalidos(veiculo)).containsExactly("cliente");
        }

        @Test
        @DisplayName("Ano do veiculo no futuro lanca excecao de dominio")
        void anoNoFuturo() {
            Veiculo veiculo = new Veiculo(clienteValido(), "ABC1234", "Fiat", "Strada");
            int anoInvalido = Year.now().getValue() + 2;
            assertThatThrownBy(() -> veiculo.setAno(anoInvalido))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("futuro");
        }

        @Test
        @DisplayName("Ano-modelo do ano seguinte e aceito")
        void anoModeloSeguinte() {
            Veiculo veiculo = new Veiculo(clienteValido(), "ABC1234", "Fiat", "Strada");
            veiculo.setAno(Year.now().getValue() + 1);
            assertThat(camposInvalidos(veiculo)).isEmpty();
        }
    }

    @Nested
    @DisplayName("Peca e Servico")
    class PecaServicoTest {

        @Test
        @DisplayName("Preco de venda negativo lanca excecao de dominio")
        void precoVendaNegativo() {
            assertThatThrownBy(() -> new Peca("P-01", "Filtro", new BigDecimal("-1.00"), 5))
                    .isInstanceOf(RegraNegocioException.class);
        }

        @Test
        @DisplayName("Quantidade em estoque negativa lanca excecao de dominio")
        void estoqueNegativo() {
            Peca peca = new Peca("P-01", "Filtro", new BigDecimal("30.00"), 5);
            assertThatThrownBy(() -> peca.setQuantidadeEstoque(-1))
                    .isInstanceOf(RegraNegocioException.class);
        }

        @Test
        @DisplayName("Preco com mais de duas casas decimais e invalido (@Digits)")
        void precoComTresCasas() {
            Peca peca = new Peca("P-01", "Filtro", new BigDecimal("30.123"), 5);
            assertThat(camposInvalidos(peca)).containsExactly("precoVenda");
        }

        @Test
        @DisplayName("Peca sem codigo e sem nome e invalida")
        void pecaSemCodigo() {
            Peca peca = new Peca(" ", "", new BigDecimal("10.00"), 1);
            assertThat(camposInvalidos(peca)).containsExactlyInAnyOrder("codigo", "nome");
        }

        @Test
        @DisplayName("Servico com preco negativo lanca excecao de dominio")
        void servicoPrecoNegativo() {
            assertThatThrownBy(() -> new Servico("Alinhamento", new BigDecimal("-50")))
                    .isInstanceOf(RegraNegocioException.class);
        }
    }

    @Nested
    @DisplayName("Usuario")
    class UsuarioTest {

        @Test
        @DisplayName("E-mail invalido e rejeitado e e-mail valido e normalizado")
        void email() {
            assertThat(camposInvalidos(new Usuario("Ana", "ana@", "hash"))).containsExactly("email");
            assertThat(new Usuario("Ana", " Ana@Oficina.com ", "hash").getEmail()).isEqualTo("ana@oficina.com");
        }
    }
}
