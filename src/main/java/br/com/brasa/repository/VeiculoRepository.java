package br.com.brasa.repository;

import br.com.brasa.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    /** Veiculos disponiveis para novos atendimentos (soft delete pelo campo ativo). */
    List<Veiculo> findByAtivoTrue();

    List<Veiculo> findByClienteIdAndAtivoTrue(Long idCliente);

    Optional<Veiculo> findByPlaca(String placa);
}
