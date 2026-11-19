package br.com.brasa.validation;

/**
 * Funcoes utilitarias para CPF/CNPJ: normalizacao e conferencia dos digitos verificadores.
 */
public final class Documentos {

    private static final int[] PESOS_CNPJ_1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CNPJ_2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private Documentos() {
    }

    /** Remove pontos, tracos, barras e espacos, mantendo apenas os digitos. */
    public static String somenteDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    public static boolean isCpfOuCnpjValido(String valor) {
        String digitos = somenteDigitos(valor);
        if (digitos == null) {
            return false;
        }
        return switch (digitos.length()) {
            case 11 -> isCpfValido(digitos);
            case 14 -> isCnpjValido(digitos);
            default -> false;
        };
    }

    public static boolean isCpfValido(String valor) {
        String cpf = somenteDigitos(valor);
        if (cpf == null || cpf.length() != 11 || todosIguais(cpf)) {
            return false;
        }
        int dv1 = digitoCpf(cpf, 9);
        int dv2 = digitoCpf(cpf, 10);
        return dv1 == cpf.charAt(9) - '0' && dv2 == cpf.charAt(10) - '0';
    }

    public static boolean isCnpjValido(String valor) {
        String cnpj = somenteDigitos(valor);
        if (cnpj == null || cnpj.length() != 14 || todosIguais(cnpj)) {
            return false;
        }
        int dv1 = digitoCnpj(cnpj, PESOS_CNPJ_1);
        int dv2 = digitoCnpj(cnpj, PESOS_CNPJ_2);
        return dv1 == cnpj.charAt(12) - '0' && dv2 == cnpj.charAt(13) - '0';
    }

    // CPF: pesos decrescentes a partir de (tamanho + 1); resto < 2 vira 0
    private static int digitoCpf(String cpf, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (cpf.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static int digitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    // Sequencias como 111.111.111-11 passam no calculo, mas nao sao documentos validos
    private static boolean todosIguais(String digitos) {
        return digitos.chars().allMatch(c -> c == digitos.charAt(0));
    }
}
