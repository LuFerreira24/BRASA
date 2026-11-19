package br.com.brasa.exception;

/**
 * Lancada quando uma regra de negocio ou invariante do dominio e violada
 * (ex.: transicao de status invalida, estoque insuficiente, quantidade negativa).
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
