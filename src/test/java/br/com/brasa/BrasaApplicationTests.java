package br.com.brasa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Experimento 1 - Mapeamento JPA.
 * O contexto so sobe se todas as entidades baterem com as tabelas criadas pelo script SQL
 * (spring.jpa.hibernate.ddl-auto=validate).
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class BrasaApplicationTests {

    @Test
    @DisplayName("Entidades JPA conferem com o schema do script SQL (ddl-auto=validate)")
    void contextLoads() {
    }
}
