package br.com.brasa.repository;

import br.com.brasa.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByAtivoTrueOrderByNomeAsc();
}
