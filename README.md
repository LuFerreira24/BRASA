# BRASA
Sistema para automatizar o processo de ordens de serviço de uma oficina mecânica.

## Tecnologias
- Java 21 + Spring Boot 4.1 (Web MVC, Data JPA, Validation, Actuator, DevTools)
- PostgreSQL 17

## Como rodar

### Tudo via Docker
```bash
docker compose up -d --build
```
- API: http://localhost:8080 (health: http://localhost:8080/actuator/health)
- O banco é criado automaticamente a partir de `BRASA_banco_de_dados.sql` na **primeira** subida.
  Para recriar do zero: `docker compose down -v`.

### Desenvolvimento local (com hot reload do DevTools)
```bash
docker compose up -d db
./mvnw spring-boot:run
```

## Testes
```bash
./mvnw test
```
Requer o Docker ligado: os testes de persistência sobem um PostgreSQL 17 em container (Testcontainers) criado pelo `BRASA_banco_de_dados.sql`.

| Experimento (seção 17.2) | Testes |
|---|---|
| 1 - Mapeamento JPA | `BrasaApplicationTests` (contexto sobe com `ddl-auto=validate`) |
| 2 - Validações no domínio | `ValidacoesDominioTest`, `DocumentosTest` |
| 3 - Persistência e integridade | `PersistenciaTest` (`@DataJpaTest`) |
| 4 - Regras da OS | `OrdemServicoTest`, `PecaTest`, `OrdemServicoServiceTest` (rollback) |
