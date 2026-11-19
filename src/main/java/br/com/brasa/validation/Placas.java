package br.com.brasa.validation;

import java.util.Locale;

/**
 * Placas de veiculo: padrao antigo (ABC-1234) e padrao Mercosul (ABC1D23).
 * A placa e gravada normalizada (maiusculas, sem traco), ex.: "ABC1234" ou "ABC1D23".
 */
public final class Placas {

    /** Tres letras, um digito, uma letra ou digito, dois digitos (cobre os dois padroes ja normalizados). */
    public static final String REGEX = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$";

    private Placas() {
    }

    public static String normalizar(String placa) {
        return placa == null ? null : placa.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
    }
}
