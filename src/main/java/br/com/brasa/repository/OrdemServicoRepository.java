package br.com.brasa.repository;

import br.com.brasa.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    List<OrdemServico> findByVeiculoId(Long idVeiculo);

    List<OrdemServico> findByClienteId(Long idCliente);
}
