package br.com.brasa.repository;

import br.com.brasa.entity.Peca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PecaRepository extends JpaRepository<Peca, Long> {

    List<Peca> findByAtivoTrueOrderByNomeAsc();

    Optional<Peca> findByCodigo(String codigo);
}
