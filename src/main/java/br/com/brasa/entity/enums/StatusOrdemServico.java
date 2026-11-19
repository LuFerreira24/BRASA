package br.com.brasa.entity.enums;

/**
 * Ciclo de vida da Ordem de Servico (RF-010):
 * ABERTA -> EM_ANALISE -> AGUARDANDO_APROVACAO -> EM_EXECUCAO -> PRONTO -> ENTREGUE.
 * CANCELADA e uma saida possivel de qualquer status que ainda nao foi encerrado.
 */
public enum StatusOrdemServico {
    ABERTA,
    EM_ANALISE,
    AGUARDANDO_APROVACAO,
    EM_EXECUCAO,
    PRONTO,
    ENTREGUE,
    CANCELADA;

    /** ENTREGUE e CANCELADA encerram a OS: nao ha transicao a partir deles. */
    public boolean isFinal() {
        return this == ENTREGUE || this == CANCELADA;
    }

    public boolean podeIrPara(StatusOrdemServico destino) {
        if (destino == null || isFinal()) {
            return false;
        }
        if (destino == CANCELADA) {
            return true;
        }
        return proximo() == destino;
    }

    private StatusOrdemServico proximo() {
        return switch (this) {
            case ABERTA -> EM_ANALISE;
            case EM_ANALISE -> AGUARDANDO_APROVACAO;
            case AGUARDANDO_APROVACAO -> EM_EXECUCAO;
            case EM_EXECUCAO -> PRONTO;
            case PRONTO -> ENTREGUE;
            case ENTREGUE, CANCELADA -> null;
        };
    }
}
