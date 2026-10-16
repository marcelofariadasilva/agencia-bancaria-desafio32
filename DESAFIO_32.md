# Desafio 32 | Cadastro, Autenticação e Login com JWT

## 1. Apresentação

Este projeto foi desenvolvido durante a formação Java do programa **CAIXA Verso**, utilizando Java 25 e Quarkus 3.40.1.

A aplicação consiste em uma API REST para operações bancárias, organizada em camadas, com persistência de dados, validação de informações e autenticação de usuários.

O objetivo do **Desafio 32** foi implementar o processo de autenticação e login utilizando senhas protegidas com BCrypt e tokens JWT (JSON Web Token).

O projeto foi desenvolvido a partir do material didático original do professor Rafael Neves de Oliveira.

## 2. Tecnologias utilizadas

- Java 25
- Quarkus 3.40.1
- Maven
- PostgreSQL
- H2 Database
- JDBC
- Jakarta REST
- Jakarta Validation
- CDI
- Jackson
- BCrypt
- SmallRye JWT
- JUnit
- REST Assured
- Swagger UI / OpenAPI
- Git e GitHub

## 3. Funcionalidades implementadas no Desafio 32

- Autenticação de usuários por e-mail e senha.
- Consulta do usuário cadastrado no repositório.
- Verificação de senha utilizando BCrypt.
- Verificação de usuário ativo.
- Geração de token JWT após autenticação bem-sucedida.
- Assinatura digital do token utilizando RSA com algoritmo RS256.
- Definição de validade do token em 1 hora.
- Proteção de endpoint bancário utilizando `@RolesAllowed("USER")`.
- Rejeição de requisições sem autenticação válida.
- Testes automatizados para verificar a proteção das senhas.

A implementação mantém a separação de responsabilidades entre as camadas da aplicação.

## 4. Organização do projeto

A aplicação utiliza uma arquitetura em camadas:

| Camada | Responsabilidade |
|---|---|
| Controller | Receber requisições HTTP e devolver respostas REST |
| DTO | Representar dados de entrada e saída da API |
| Service | Executar regras de negócio e autenticação |
| Repository | Realizar operações de acesso aos dados |
| Security | Gerar tokens JWT e concentrar responsabilidades de segurança |
| Entity | Representar as entidades do domínio bancário |

### Principais classes do Desafio 32

**AuthResource.java**

Disponibiliza o endpoint REST responsável pelo login.

**AuthService.java**

Executa o processo de autenticação, incluindo consulta do usuário, verificação da situação cadastral e conferência da senha.

**JwtService.java**

Responsável pela geração dos tokens JWT.

**LoginRequest.java**

Representa os dados recebidos na requisição de login.

**LoginResponse.java**

Representa a resposta da autenticação.

**UsuarioServiceTest.java**

Contém testes automatizados relacionados ao tratamento e à proteção das senhas dos usuários.

## 5. Banco de dados

O projeto possui três configurações de execução: PostgreSQL, H2 para desenvolvimento e H2 para testes.

### 5.1. PostgreSQL: ambiente padrão

O PostgreSQL é o banco de dados utilizado no ambiente normal da aplicação.

Configuração padrão:

| Parâmetro | Valor |
|---|---|
| Servidor | localhost |
| Porta | 5432 |
| Banco | agencia_bancaria |
| Usuário | postgres |
| Senha padrão de desenvolvimento | postgres |

A configuração está definida em `src/main/resources/application.properties`.

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=${DB_URL:jdbc:postgresql://localhost:5432/agencia_bancaria}
quarkus.datasource.username=${DB_USER:postgres}
quarkus.datasource.password=${DB_PASSWORD:postgres}
```

**Pré-requisitos:**

1. Ter o PostgreSQL instalado.
2. Manter o serviço PostgreSQL em execução.
3. Criar o banco de dados `agencia_bancaria`.
4. Garantir que o usuário configurado tenha as permissões necessárias.
5. Preparar a estrutura de tabelas utilizada pela aplicação.

As credenciais e a conexão podem ser alteradas por variáveis de ambiente:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

A senha `postgres` é apenas o valor padrão da configuração didática e não deve ser utilizada como senha de produção.

### 5.2. H2: ambiente de desenvolvimento

O projeto também possui um perfil H2, que permite executar a aplicação sem depender de um servidor PostgreSQL.

Características:

- Banco de dados em memória.
- Usuário: `sa`.
- Senha: vazia.
- Compatibilidade configurada com o modo PostgreSQL.
- Inicialização utilizando `db/h2-init.sql`.
- Dados descartados quando a instância do banco é encerrada.

Esse ambiente é útil para exercícios, desenvolvimento e validação das funcionalidades.

### 5.3. H2: ambiente de testes

Os testes automatizados utilizam um banco H2 separado, também em memória.

A configuração do perfil `%test` evita a dependência de um servidor PostgreSQL local durante a execução dos testes.

O script de inicialização está localizado em:

`src/main/resources/db/h2-init.sql`

## 6. Como executar a aplicação

### 6.1. Pré-requisitos

- JDK 25 instalado e configurado.
- Maven Wrapper disponível no projeto.
- Chaves RSA configuradas para autenticação JWT.
- PostgreSQL configurado, caso seja utilizado o ambiente padrão.

Os comandos apresentados abaixo são destinados ao **Windows PowerShell**, executados na pasta raiz do projeto.

### 6.2. Executar com PostgreSQL

Com o PostgreSQL iniciado e o banco preparado:

```powershell
.\mvnw.cmd quarkus:dev
```

A aplicação utilizará as configurações padrão de conexão PostgreSQL, salvo quando substituídas pelas variáveis de ambiente.

### 6.3. Executar com H2

Para utilizar o banco H2 em memória, sem precisar iniciar PostgreSQL:

```powershell
.\mvnw.cmd quarkus:dev "-Dquarkus.profile=h2"
```

O perfil H2 utiliza o script de inicialização para preparar o banco.

### 6.4. Endereços da aplicação

A aplicação está configurada para utilizar a porta `8081`.

**Swagger UI**

http://localhost:8081/swagger-ui

**Documento OpenAPI**

http://localhost:8081/openapi

Para encerrar a execução no terminal, pressione `Ctrl + C`.

## 7. Cadastro e autenticação de usuários

A autenticação depende da existência de um usuário previamente cadastrado e ativo.

### 7.1. Login

**Método HTTP:** POST

**Endpoint:**

`/api/auth/login`

Exemplo de corpo JSON:

```json
{
  "email": "usuario@exemplo.com",
  "senha": "senha-do-usuario"
}
```

O serviço de autenticação:

1. Consulta o usuário pelo e-mail informado.
2. Verifica se o usuário existe.
3. Verifica se o cadastro está ativo.
4. Confere a senha utilizando BCrypt.
5. Gera um token JWT quando as credenciais são válidas.
6. Devolve a resposta de autenticação.

O token gerado possui validade configurada de 3.600 segundos, equivalentes a 1 hora.

### 7.2. Utilização do token JWT

Após realizar o login, o cliente deve incluir o token no cabeçalho HTTP das requisições protegidas:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

Exemplo de endpoint protegido:

`GET /api/contas/1`

A aplicação exige um token válido com o perfil `USER` para acessar esse recurso.

Uma requisição sem autenticação retorna HTTP 401.

### 7.3. Estrutura de segurança

A implementação utiliza:

- Emissor JWT: `agencia-bancaria-quarkus`.
- Algoritmo de assinatura: `RS256`.
- Perfil de acesso: `USER`.
- Validade: `3600` segundos.
- Chave privada RSA para assinatura.
- Chave pública RSA para verificação.

## 8. Configuração das chaves JWT

A aplicação utiliza um par de chaves RSA em formato PEM.

Arquivos esperados:

**Chave pública**

`src/main/resources/jwt/publicKey.pem`

**Chave privada**

`src/main/resources/jwt/privateKey.pem`

A chave pública faz parte do repositório.

A chave privada foi excluída do controle de versão para evitar sua publicação no GitHub.

### 8.1. Gerar um par de chaves RSA no Windows

Para configurar uma nova instalação, é possível utilizar o OpenSSL, desde que esteja instalado e disponível no terminal.

Na pasta raiz do projeto, execute:

```powershell
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out src/main/resources/jwt/privateKey.pem
```

Em seguida:

```powershell
openssl pkey -in src/main/resources/jwt/privateKey.pem -pubout -out src/main/resources/jwt/publicKey.pem
```

Esses comandos criam um novo par de chaves RSA de 2048 bits.

**Atenção:** os dois arquivos devem pertencer ao mesmo par de chaves. Como o repositório já contém uma chave pública, quem gerar uma nova chave privada também deverá substituir localmente a chave pública pelo arquivo correspondente.

Não execute esses comandos sobre chaves existentes sem necessidade.

A substituição das chaves invalida a verificação de tokens assinados com o par anterior.

**Nunca publique a chave privada no GitHub.**

### 8.2. Configuração no Quarkus

As propriedades utilizadas são:

```properties
mp.jwt.verify.issuer=agencia-bancaria-quarkus
mp.jwt.verify.publickey.location=jwt/publicKey.pem
smallrye.jwt.sign.key.location=jwt/privateKey.pem
smallrye.jwt.new-token.signature-algorithm=RS256
security.jwt.expiration-seconds=3600
```

Essas propriedades estão definidas no arquivo `application.properties`.

## 9. Testes automatizados

Os testes utilizam JUnit e os recursos de teste do Quarkus.

Para executar a suíte completa:

```powershell
.\mvnw.cmd clean test
```

A execução dos testes utiliza H2 em memória e não exige PostgreSQL iniciado.

### 9.1. Testes de segurança de senhas

A classe `UsuarioServiceTest.java` verifica:

- A senha original não é enviada ao repositório para armazenamento em texto puro.
- O valor enviado ao repositório corresponde a um hash BCrypt.
- O hash gerado pode ser validado utilizando a senha correta.

Esses testes ajudam a prevenir o armazenamento indevido de senhas.

### 9.2. Verificações funcionais realizadas

Durante o desenvolvimento do Desafio 32, foram realizadas as seguintes verificações:

| Operação | Resultado |
|---|---|
| Cadastro de usuário | HTTP 201 |
| Login com credenciais válidas | HTTP 200 |
| Consulta protegida sem JWT | HTTP 401 |
| Consulta protegida com JWT válido | Acesso autorizado |
| Execução da suíte de testes Maven | BUILD SUCCESS |

Esses resultados correspondem às verificações realizadas durante o desenvolvimento e não substituem testes futuros em outros ambientes.

## 10. Segurança e boas práticas

O projeto adota as seguintes práticas:

- Senhas verificadas utilizando BCrypt.
- Separação entre dados de entrada, regras de negócio e persistência.
- Tokens JWT assinados digitalmente.
- Verificação de permissões nos endpoints protegidos.
- Chave privada JWT fora do repositório.
- Configurações de conexão parametrizadas por variáveis de ambiente.
- Testes automatizados para comportamentos relacionados à segurança.

**Observação:** a configuração apresentada destina-se ao ambiente didático e de desenvolvimento. Uma implantação em produção exige configuração apropriada de credenciais, gerenciamento seguro de chaves, HTTPS e controles adicionais de segurança.

## 11. Histórico e evolução do projeto

O projeto original foi organizado como material didático incremental, começando pela construção de uma API REST em Quarkus e evoluindo para persistência e segurança.

O Desafio 32 representa a etapa de implementação da autenticação JWT e dos testes relacionados à proteção das senhas.

O arquivo `README.md` original do professor foi preservado como referência didática.

Esta documentação complementar está disponível no arquivo `DESAFIO_32.md`.

## 12. Créditos e referências

**Formação:** CAIXA Verso, trilha Java.

**Projeto didático original**

Professor: Rafael Neves de Oliveira

https://github.com/RafaelNevesdeOliveira/agencia-bancaria-quarkus

**Implementação do Desafio 32**

Marcelo Faria da Silva

Contribuições: implementação da autenticação JWT, login, proteção de endpoint bancário e testes automatizados relacionados ao BCrypt.

**Repositório do projeto**

https://github.com/marcelofariadasilva/agencia-bancaria-desafio32

---

**Desafio 32 | CAIXA Verso | Java 25 + Quarkus + PostgreSQL + H2 + BCrypt + JWT**