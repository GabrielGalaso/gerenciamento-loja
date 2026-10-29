# 🛒 Sistema de Gerenciamento de Loja

Sistema de gerenciamento de loja desenvolvido em **Java 21**, com interface via terminal, persistência de dados em **MySQL** e acesso ao banco utilizando **JDBC**.

## 📖 Sobre o projeto

O projeto foi desenvolvido para colocar em prática conceitos de **Programação Orientada a Objetos**, banco de dados relacional, arquitetura em camadas e tratamento de exceções.

A aplicação permite gerenciar produtos e clientes, realizar pedidos com múltiplos itens, controlar o estoque, consultar o faturamento e exportar produtos para arquivos CSV.

As vendas utilizam **transações JDBC com `commit` e `rollback`**, evitando que operações incompletas comprometam a consistência dos dados.

## 🛠️ Tecnologias utilizadas

- **Java 21** — Lógica de negócio, orientação a objetos e interface pelo terminal.
- **JDBC** — Comunicação com o MySQL e gerenciamento de transações.
- **MySQL** — Armazenamento dos dados da loja.
- **Docker** — Execução do banco de dados em contêiner.
- **Maven** — Gerenciamento de dependências e construção do projeto.
- **Java NIO.2** — Manipulação de arquivos e exportação CSV.
- **JUnit** — Testes de unidade e integração.

## ⚙️ Funcionalidades

- Cadastro, listagem, busca e remoção de produtos e clientes.
- Criação e finalização de pedidos com múltiplos produtos.
- Cálculo de subtotais e totais utilizando `BigDecimal`.
- Preservação do preço histórico dos itens vendidos.
- Atualização do estoque com verificação da quantidade disponível.
- Transações com `commit` e `rollback`.
- Validação de dados e exceções personalizadas.
- Tratamento de cadastros duplicados e exclusões com vínculos.
- Consulta do faturamento total.
- Exportação de produtos para CSV.

## 🧠 Arquitetura e conceitos aplicados

O sistema utiliza **orientação a objetos**, encapsulamento, interfaces, generics e polimorfismo.

A interface genérica `Repository<T, ID>` define operações comuns de persistência, permitindo trabalhar com implementações em memória ou JDBC.

O código é organizado em camadas, separando a interface do usuário, os modelos, os serviços e os repositórios.

Na finalização dos pedidos, uma transação garante que o pedido, seus itens e a atualização do estoque sejam processados em conjunto. A redução do estoque utiliza SQL condicional para impedir vendas acima da quantidade disponível.

Também são utilizados `BigDecimal` para cálculos monetários, `PreparedStatement` para consultas parametrizadas e `try-with-resources` para o gerenciamento de recursos.

## 📂 Estrutura do projeto

```text
gerenciamento-loja/
├── database/
│   └── schema.sql
├── src/
│   ├── main/java/com/gabriel/gerenciamentoloja/
│   │   ├── config/
│   │   ├── exception/
│   │   ├── model/
│   │   ├── repository/
│   │   │   └── jdbc/
│   │   ├── service/
│   │   ├── ui/
│   │   └── Main.java
│   └── test/
├── pom.xml
└── README.md
```

**Responsabilidades:**

- `config` — Configuração da conexão com o MySQL.
- `exception` — Exceções personalizadas.
- `model` — Entidades e regras do domínio.
- `repository` — Interface genérica e repositórios.
- `repository.jdbc` — Persistência utilizando JDBC.
- `service` — Serviços de pedidos e exportação.
- `ui` — Interface interativa pelo terminal.
- `Main` — Inicialização e configuração das dependências.

## 🗄️ Banco de dados

O banco de dados MySQL possui quatro tabelas principais:

- `produtos`
- `clientes`
- `pedidos`
- `itens_pedido`

As tabelas possuem relacionamentos por chaves estrangeiras para preservar a integridade dos registros.

O script de criação das tabelas está disponível em `database/schema.sql`.

## 🚀 Como executar

### Pré-requisitos

- Java 21
- Maven
- Docker com MySQL 8.4, ou uma instância compatível de MySQL
- IntelliJ IDEA ou outra IDE Java

### 1. Clonar o repositório

```bash
git clone [URL_DO_REPOSITORIO](https://github.com/GabrielGalaso/gerenciamento-loja)
cd gerenciamento-loja
```

Substitua `URL_DO_REPOSITORIO` pelo endereço deste projeto no GitHub.

### 2. Configurar o MySQL

Caso ainda não tenha um contêiner MySQL configurado, pode criar um com:

```bash
docker run --name mysql-loja -e MYSQL_ROOT_PASSWORD=SENHA_ROOT -e MYSQL_DATABASE=gerenciamento_loja -e MYSQL_USER=loja_app -e MYSQL_PASSWORD=SENHA_APP -p 3307:3306 -d mysql:8.4
```

Substitua `SENHA_ROOT` e `SENHA_APP` por senhas próprias.

Em seguida, conecte-se ao banco por um cliente SQL e execute o script `database/schema.sql` no banco `gerenciamento_loja`.

### 3. Configurar as variáveis de ambiente

No IntelliJ IDEA, acesse **Run → Edit Configurations → Environment Variables** e configure:

```env
LOJA_DB_URL=jdbc:mysql://127.0.0.1:3307/gerenciamento_loja
LOJA_DB_USER=loja_app
LOJA_DB_PASSWORD=SENHA_APP
```

Utilize os dados correspondentes à sua instalação do MySQL.

**Não inclua senhas reais no código ou no repositório.**

### 4. Executar a aplicação

Abra o projeto no IntelliJ IDEA, aguarde o carregamento das dependências Maven e execute a classe:

`com.gabriel.gerenciamentoloja.Main`

A aplicação iniciará o menu interativo no terminal da IDE.

## 💻 Exemplo de utilização

Menu principal:

```text
===== SISTEMA DE GERENCIAMENTO DE LOJA =====

[1] Gerenciar produtos
[2] Gerenciar clientes
[3] Gerenciar pedidos
[4] Relatorios e exportacao

[0] Sair do sistema
```

Exemplo de finalização de um pedido:

```text
Insira o ID do pedido: 100
Insira o ID do cliente: 10

Insira o ID do produto: 10
Insira a quantidade: 2
Item adicionado ao pedido!

Deseja adicionar outro item? (S/N): S

Insira o ID do produto: 11
Insira a quantidade: 1
Item adicionado ao pedido!

Deseja adicionar outro item? (S/N): N

Pedido finalizado com sucesso!
Total: R$ 418.80
```

Os valores apresentados são ilustrativos.

A exportação gera o arquivo `exportacoes/produtos.csv`, com os dados dos produtos cadastrados e campos separados por ponto e vírgula.

## 🧪 Testes

O projeto utiliza **JUnit** para validar regras de negócio e operações de integração com o banco de dados.

Os testes de integração utilizam um banco separado, `gerenciamento_loja_teste`, para evitar interferência nos dados de uso da aplicação.

## 🎯 Objetivo do projeto

Consolidar conhecimentos de **Java SE, orientação a objetos, JDBC, SQL e organização de software**, desenvolvendo uma aplicação prática para compor meu portfólio como desenvolvedor Java back-end.
