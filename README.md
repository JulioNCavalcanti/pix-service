# Pix Service

Microserviço de carteira digital com suporte a transferências Pix, focado em consistência, concorrência, idempotência e auditabilidade.

## 🛠 Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.4.1
* **Banco de Dados:** PostgreSQL 15 (Docker) / H2 (Testes)
* **Migração de Dados:** Flyway
* **Containerização:** Docker & Docker Compose
* **Testes:** JUnit 5, Mockito, MockMvc

---

## 🚀 Como Executar

### Pré-requisitos
* Docker e Docker Compose instalados.

### Passo a Passo (Docker)

Esta é a forma recomendada para subir o ambiente completo (Aplicação + Banco de Dados).

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/JulioNCavalcanti/pix-service
    cd pix-service
    ```

2.  **Limpe o ambiente (garantir que não há volumes antigos):**
    ```bash
    docker-compose down -v
    ```

3.  **Suba a aplicação:**
    ```bash
    docker-compose up --build
    ```
    *A aplicação estará disponível em: `http://localhost:8080`*

### 🧪 Como Rodar os Testes

Os testes foram configurados para rodar com banco em memória (H2), não dependendo do Docker estar ativo.

```bash
./mvnw clean package
```

## 🧪 Guia de Teste Rápido (Postman)

A Collection inclusa ([pix-service.postman_collection.json](./pix-service.postman_collection.json)) já contém os payloads prontos. Siga apenas esta ordem de execução:

1.  **Importe a Collection** no Postman.
2.  **Crie a Carteira A (Origem):**
    * Execute `POST /wallets`.
    * *Copie o `id` retornado (ex: `uuid-carteira-a`).*
3.  **Deposite na Carteira A:**
    * Execute `POST /deposit`.
    * *Use o `uuid-carteira-a` na URL.*
4.  **Crie a Carteira B (Destino):**
    * Execute `POST /wallets`.
    * *Copie o `id` retornado (ex: `uuid-carteira-b`).*
5.  **Crie a Chave Pix para B:**
    * Execute `POST /pix-keys`.
    * *Use o `uuid-carteira-b` na URL.*
6.  **Faça a Transferência (A -> B):**
    * Execute `POST /pix/transfers`.
    * *No Body, coloque o `fromWalletId` (A) e a `toPixKey` (B).*
    * *Copie o `endToEndId` da resposta (Status: PENDING).*
7.  **Confirme o Pagamento (Webhook):**
    * Execute `POST /pix/webhook`.
    * *Cole o `endToEndId` no corpo da requisição com status `CONFIRMED`.*
    * **Resultado:** O saldo de B deve aumentar.
8.  **Teste de Estorno (Rejeição do Bacen):**
    * Repita o passo 6 (inicie uma nova transferência) e copie o novo `endToEndId`.
    * Execute `POST /pix/webhook` novamente.
    * *Desta vez, mude o `eventType` no Body para `REJECTED`.*
    * **Resultado:** O valor que foi debitado da Carteira A será devolvido (o saldo volta ao original).

## 🏗 Arquitetura e Decisões de Design
O projeto segue os princípios da Clean Architecture, isolando o domínio (regras de negócio) de detalhes de infraestrutura (banco de dados, frameworks web). Abaixo detalho as decisões críticas tomadas para atender aos requisitos funcionais e não-funcionais.

### 1. Escopo e Abstração de Usuário
Conforme interpretação do objetivo do microserviço: Optou-se por não modelar a entidade "Usuário" explicitamente dentro deste microserviço, focando apenas em Wallets (Carteiras).

Motivação: No contexto de transações Pix, o SLA de tempo estipulado pelo BACEN é extremamente agressivo (0,2s para 99% dos casos no contexnto do pix). Incluir validações e queries de dados cadastrais de usuários aqui aumentaria a latência desnecessariamente. Assumimos que o gerenciamento de identidade e validação de chaves Pix (DICT) são responsabilidades de serviços externos ou upstream, mantendo este serviço com contexto limitado e alta performance.

### 2. Concorrência e Race Conditions
Para garantir a consistência do saldo sob alta concorrência (ex: múltiplos saques simultâneos), utilizamos Optimistic Locking via JPA (@Version).

### 3. Idempotência (Duplo Disparo)
Para evitar que uma mesma requisição de transferência seja processada duas vezes (Double Spending):

Implementamos uma tabela idempotency_keys.

### 4. Máquina de Estados e Processamento Assíncrono
A transferência Pix não é atômica instantaneamente. Ela segue um fluxo de estados:

- PENDING: O saldo é debitado da origem (reserva de fundos) e o Pix é enviado ao Bacen (Simulador).
- CONFIRMED (via Webhook): O valor é creditado na carteira destino.
- REJECTED (via Webhook): O valor reservado é estornado (devolvido) para a carteira origem.

Isso garante que o saldo nunca fique inconsistente, mesmo que o evento de confirmação demore.

### 5. Ledger (Livro Razão) e Auditoria
Todas as movimentações financeiras geram um registro imutável na tabela ledger.
Objetivo: Permitir a rastreabilidade completa. O saldo atual é apenas um "snapshot", mas o Ledger permite reconstruir o histórico e auditar transações (Depósitos, Saques, Pix Enviado, Pix Recebido, Estornos).

## ⚖️ Trade-offs e Limitações (Tempo)

- Segurança: Não foi implementada camada de autenticação/autorização (Spring Security/OAuth2).
- Simulador Hardcoded: O PixNetworkSimulator roda na mesma JVM e tem um delay fixo. Em produção, isso seria substituído por um serviço que consulta o endpoint do Bacen para obter o lote de retorno de mensagens e, somente após essa validação, aprovar ou rejeitar a transação.

Time Tracking: 14h
