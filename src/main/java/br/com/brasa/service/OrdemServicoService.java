package br.com.brasa.service;

import br.com.brasa.entity.MovimentacaoEstoque;
import br.com.brasa.entity.OrdemServico;
import br.com.brasa.entity.OsPeca;
import br.com.brasa.entity.Usuario;
import br.com.brasa.entity.enums.StatusOrdemServico;
import br.com.brasa.entity.enums.TipoMovimentacao;
import br.com.brasa.exception.RegraNegocioException;
import br.com.brasa.repository.MovimentacaoEstoqueRepository;
import br.com.brasa.repository.OrdemServicoRepository;
import br.com.brasa.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               UsuarioRepository usuarioRepository,
                               MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /**
     * Coloca a OS em execucao e baixa do estoque todas as pecas incluidas (RF-014),
     * registrando uma movimentacao de SAIDA para cada uma.
     * Tudo acontece em uma unica transacao: se qualquer peca nao tiver estoque suficiente,
     * a RegraNegocioException desfaz tudo (status, baixas e movimentacoes ja feitas).
     */
    @Transactional
    public OrdemServico iniciarExecucao(Long idOs, Long idUsuario) {
        OrdemServico os = ordemServicoRepository.findById(idOs)
                .orElseThrow(() -> new RegraNegocioException("Ordem de Servico nao encontrada: " + idOs));
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado: " + idUsuario));

        os.alterarStatus(StatusOrdemServico.EM_EXECUCAO, usuario);

        for (OsPeca item : os.getPecas()) {
            item.getPeca().baixarEstoque(item.getQuantidade());
            movimentacaoRepository.save(new MovimentacaoEstoque(item.getPeca(), os, usuario,
                    TipoMovimentacao.SAIDA, item.getQuantidade(), "Baixa automatica - OS " + os.getId()));
        }
        return os;
    }
}
