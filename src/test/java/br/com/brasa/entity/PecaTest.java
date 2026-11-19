package br.com.brasa.entity;

import br.com.brasa.exception.RegraNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Experimento 4 - baixa de estoque na classe Peca (RF-014). */
class PecaTest {

    private final Peca peca = new Peca("PST-01", "Pastilha de freio", new BigDecimal("120.00"), 5);

    @Test
    @DisplayName("Baixa de estoque reduz a quantidade disponivel")
    void baixarEstoque() {
        peca.baixarEstoque(3);
        assertThat(peca.getQuantidadeEstoque()).isEqualTo(2);
    }

    @Test
    @DisplayName("Baixar exatamente o disponivel zera o estoque")
    void baixarTudo() {
        peca.baixarEstoque(5);
        assertThat(peca.getQuantidadeEstoque()).isZero();
    }

    @Test
    @DisplayName("Estoque insuficiente lanca excecao e nao altera a quantidade")
    void estoqueInsuficiente() {
        assertThatThrownBy(() -> peca.baixarEstoque(6))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Estoque insuficiente");
        assertThat(peca.getQuantidadeEstoque()).isEqualTo(5);
    }

    @Test
    @DisplayName("Quantidade menor ou igual a zero e rejeitada na baixa e na reposicao")
    void quantidadeInvalida() {
        assertThatThrownBy(() -> peca.baixarEstoque(0)).isInstanceOf(RegraNegocioException.class);
        assertThatThrownBy(() -> peca.reporEstoque(-2)).isInstanceOf(RegraNegocioException.class);
    }

    @Test
    @DisplayName("Reposicao aumenta o estoque")
    void reporEstoque() {
        peca.reporEstoque(10);
        assertThat(peca.getQuantidadeEstoque()).isEqualTo(15);
    }
}
