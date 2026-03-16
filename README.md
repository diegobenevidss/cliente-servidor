# Sistema Distribuído com Tolerância a Falhas e Eleição de Líder

Este projeto é uma implementação de uma arquitetura de microsserviços distribuída em Java. Ele simula um ambiente de alta disponibilidade com replicação de dados, roteamento inteligente, tolerância a falhas (*failover*) e recuperação automática (*self-healing*).

## 🚀 Arquitetura e Componentes

O sistema é composto por 5 atores principais que se comunicam via API REST (HTTP/JSON):

1. **Cliente (`ClientApp.java`):** Interface de linha de comando para envio de dados (Nome e E-mail).
2. **API Gateway (Porta `8000`):** Ponto de entrada único. Gerencia o roteamento e executa a Eleição de Líder caso o servidor primário caia.
3. **Servidor Primário (Porta `8080`):** Nó líder padrão. Recebe requisições, persiste os dados e coordena a replicação.
4. **Servidor Réplica 1 (Porta `8081`):** Nó subordinado e candidato número 1 a assumir a liderança em caso de falha.
5. **Servidor Réplica 2 (Porta `8082`):** Nó subordinado de contingência secundária.

## 🛠️ Tecnologias Utilizadas

* **Java 17+**
* **Spring Boot 3** (Web, Data JPA)
* **Banco de Dados H2** (Persistência relacional nativa e independente para cada nó)
* **Gradle** (Gerenciador de dependências)

## ✨ Funcionalidades Implementadas (Critérios de Avaliação)

* ✅ **Arquitetura Correta:** Separação clara entre cliente, roteador e nós de processamento.
* ✅ **Nova Réplica Implementada:** Duas réplicas ativas rodando em portas isoladas.
* ✅ **Replicação Funcionando:** O Primário salva e propaga os dados em tempo real para as réplicas.
* ✅ **Gateway Implementado:** Balanço, roteamento e encapsulamento da rede interna.
* ✅ **Eleição de Líder:** Se o Primário cai, o Gateway elege automaticamente a Réplica 1 como nova líder sem derrubar o sistema.
* ✅ **Sincronização da Primária:** Ao ser religada após uma queda, a velha Primária detecta a nova líder, faz o download do histórico perdido, atualiza seu banco de dados e assume o papel de réplica subordinada.

---

## ⚙️ Como Executar o Projeto

Abra **5 abas independentes** no seu terminal para orquestrar a inicialização da rede.

### 1. Inicializando os Servidores
* **Réplica 1:** `cd server-replica` ➔ `./gradlew bootRun`
* **Réplica 2:** `cd server-replica2` ➔ `./gradlew bootRun`
* **Primária:** `cd server-primary` ➔ `./gradlew bootRun`
* **Gateway:** `cd gateway-service` ➔ `./gradlew bootRun`

*(Aguarde todos os servidores travarem em `80% EXECUTING`)*

### 2. Inicializando o Cliente
Vá para a pasta raiz do projeto, compile e execute:
```
javac ClientApp.java
java ClientApp
```
## 🧪 Roteiro de Testes de Validação
Este roteiro prova o funcionamento de todos os requisitos do sistema.

### Teste 1: Sistema Normal (O Caminho Feliz)
1. No Cliente, digite um nome (ex: Alice) e um e-mail válido.

2. Observe que o Gateway encaminha para a Primária (8080).

3. O Primário salva e emite logs de replicação bem-sucedida.

4. As Réplicas (8081 e 8082) acusam o recebimento e sincronização.

5. Comprovação de Persistência: Acesse http://localhost:8081/all-data no navegador para ver o dado salvo no banco físico.

### Teste 2: Derrubando o Líder (Caos)
1. Abra uma aba limpa no terminal e simule uma falha crítica no Servidor Primário matando o processo dono da porta 8080:
``` 
kill -9 $(lsof -t -i:8080 -sTCP:LISTEN)
```
2. O servidor Primário será desligado imediatamente.

### Teste 3: Prova de Resiliência (Failover)
1. Volte ao Cliente e envie um novo dado (ex: Bob).

2. O Gateway tentará acessar o Primário, detectará a falha e imprimirá: "⚠️ Primário offline! Elegendo Réplica 1 como nova líder...".

3. A Réplica 1 assumirá a carga e registrará o dado "Bob" com sucesso. O sistema permanece 100% online para o cliente final.

### Teste 4: Ressurreição e Sincronização Automática (Self-Healing)
1. Volte ao terminal do Servidor Primário (que estava morto) e ligue-o novamente com ./gradlew bootRun.

2. Assim que iniciar, ele disparará um evento automático interceptando a nova líder (Réplica 1).

3. O log do Primário mostrará que ele recuperou os registros (incluindo "Bob") e assumiu o papel de Réplica Subordinada.