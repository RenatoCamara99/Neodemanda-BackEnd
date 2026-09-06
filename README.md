# NeoDemanda — Back-end

API REST para **automação do cálculo normativo de demanda elétrica** da Neoenergia PE.

Este repositório contém a estrutura base do back-end, preparada para receber as
funcionalidades de cálculo normativo. No momento expõe apenas um endpoint de
health-check, usado para validar que a aplicação está operante.

## Stack

| Item | Versão / Tecnologia |
| --- | --- |
| Linguagem | Java 25 (LTS) |
| Framework | Spring Boot 4.1.1 |
| Web | `spring-boot-starter-webmvc` (Spring MVC + Tomcat embarcado) |
| Validação | `spring-boot-starter-validation` (Jakarta Bean Validation + Hibernate Validator) |
| Persistência | `spring-boot-starter-data-jpa` (Spring Data JPA + Hibernate) |
| Banco (temporário) | H2 em memória — placeholder até a definição do banco oficial |
| Dev | `spring-boot-devtools` (restart automático) |
| Testes | `spring-boot-starter-test` (JUnit 5 + Mockito + AssertJ) |
| Build | Maven (via Maven Wrapper — `mvnw`) |

## Pré-requisitos

- **JDK 25** instalado e `JAVA_HOME` apontando para ele.
- Maven **não** precisa ser instalado: o projeto usa o Maven Wrapper (`mvnw` / `mvnw.cmd`),
  que baixa a versão correta do Maven automaticamente na primeira execução.

Verifique a instalação:

```bash
java -version
```

## Como executar localmente

Clone o repositório e, na raiz do projeto:

**Linux / macOS / Git Bash**

```bash
./mvnw spring-boot:run
```

**Windows (PowerShell / CMD)**

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

### Gerar e executar o JAR

```bash
./mvnw clean package
java -jar target/neodemanda-0.0.1-SNAPSHOT.jar
```

### Executar os testes

```bash
./mvnw test
```

## Endpoints

| Método | Rota | Descrição | Resposta |
| --- | --- | --- | --- |
| `GET` | `/health` | Health-check da API | `200 OK` — `{"status":"ok"}` |

Exemplo:

```bash
curl http://localhost:8080/health
```

```json
{"status":"ok"}
```

## Estrutura de pacotes

Todo o código-fonte fica sob `com.neoenergia.neodemanda`:

```
src/main/java/com/neoenergia/neodemanda/
├── NeodemandaApplication.java   # classe principal (ponto de entrada)
├── controller/                  # camada REST — endpoints HTTP
├── service/                     # regras de negócio (cálculo normativo)
├── repository/                  # acesso a dados / persistência
├── domain/
│   ├── model/                   # entidades JPA do domínio (Projeto)
│   └── enums/                   # enums do domínio (TipoEdificacao, TensaoAtendimento, StatusProjeto)
├── dto/                         # objetos de entrada e saída da API (Bean Validation)
└── exception/                   # exceções e tratamento centralizado de erros
```

Os testes espelham essa estrutura em `src/test/java/com/neoenergia/neodemanda/`.

## Configuração

Parâmetros da aplicação ficam em [`src/main/resources/application.properties`](src/main/resources/application.properties).
A porta padrão é a `8080` e pode ser alterada por `server.port`.

O banco configurado é um **H2 em memória**, presente apenas para a aplicação
subir com o JPA ativo enquanto o banco oficial não é definido — os dados são
perdidos a cada reinício e o schema é recriado a partir das entidades
(`ddl-auto=create-drop`). Ao adotar o banco definitivo, troque o datasource e
substitua o `ddl-auto` por migração versionada (Flyway ou Liquibase).

## Tratamento de erros

`GlobalExceptionHandler` traduz as violações de Bean Validation e os recursos não
encontrados para um corpo JSON único (`ApiError`). Exceções não mapeadas seguem
para o tratamento padrão do Spring Boot, preservando os status do framework
(ex.: `404` para rotas inexistentes).

```json
{
  "timestamp": "2026-09-03T21:30:00.000-03:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validacao nos dados enviados",
  "path": "/exemplo",
  "fields": {
    "campo": "não deve ser nulo"
  }
}
```

O campo `fields` vem vazio (`{}`) em erros que não são de validação.
