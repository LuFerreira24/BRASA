package br.com.brasa.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/** Experimento 2 - validador proprio de CPF/CNPJ (digitos verificadores). */
class DocumentosTest {

    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-25", "52998224725", "111.444.777-35"})
    @DisplayName("Aceita CPF valido, com ou sem mascara")
    void cpfValido(String cpf) {
        assertThat(Documentos.isCpfOuCnpjValido(cpf)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"11.222.333/0001-81", "11222333000181"})
    @DisplayName("Aceita CNPJ valido, com ou sem mascara")
    void cnpjValido(String cnpj) {
        assertThat(Documentos.isCpfOuCnpjValido(cnpj)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "529.982.247-24",      // digito verificador errado
            "111.111.111-11",      // sequencia repetida
            "123",                 // tamanho errado
            "11.222.333/0001-80",  // DV do CNPJ errado
            "00.000.000/0000-00",
            "abc.def.ghi-jk"
    })
    @DisplayName("Rejeita CPF/CNPJ invalido")
    void documentoInvalido(String valor) {
        assertThat(Documentos.isCpfOuCnpjValido(valor)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Valor nulo ou vazio nao e documento valido")
    void nuloOuVazio(String valor) {
        assertThat(Documentos.isCpfOuCnpjValido(valor)).isFalse();
    }

    @Test
    @DisplayName("Normaliza removendo a mascara")
    void normaliza() {
        assertThat(Documentos.somenteDigitos("529.982.247-25")).isEqualTo("52998224725");
        assertThat(Documentos.somenteDigitos("11.222.333/0001-81")).isEqualTo("11222333000181");
    }
}
