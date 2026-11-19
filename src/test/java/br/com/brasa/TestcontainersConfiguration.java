package br.com.brasa;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Sobe um PostgreSQL 17 em container para os testes, criando o banco a partir do mesmo
 * script SQL usado no docker-compose. A aplicacao roda com ddl-auto=validate,
 * entao qualquer divergencia entre entidades e tabelas faz o teste falhar.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"))
                .withCopyFileToContainer(
                        MountableFile.forHostPath("BRASA_banco_de_dados.sql"),
                        "/docker-entrypoint-initdb.d/01-schema.sql");
    }
}
