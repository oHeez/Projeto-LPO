# 📚 EXPLICAÇÃO DETALHADA DO PROJETO - SISTEMA PETSHOP

## 🎯 Visão Geral

Este documento explica detalhadamente como cada parte do projeto funciona, os conceitos de Programação Orientada a Objetos (POO) aplicados, e a arquitetura completa do sistema de petshop desenvolvido em Java.

---

## 📁 ESTRUTURA DO PROJETO

O projeto segue uma **arquitetura em camadas (MVC-like)** com separação clara de responsabilidades:

```
lpoProjeto/
├── src/
│   ├── db/              # Gerenciamento de conexão com banco
│   ├── model/           # Entidades do domínio (POO)
│   ├── dao/             # Acesso a dados (Data Access Object)
│   ├── server/          # API REST e servidor HTTP
│   ├── exception/       # Exceções customizadas
│   ├── util/            # Utilitários (JSON, Validações)
│   └── App.java         # Classe principal
├── web/                 # Frontend (HTML, CSS, JS)
└── lib/                 # Dependências (MySQL Connector)
```

---

## 1️⃣ PACOTE `db` - GERENCIAMENTO DE CONEXÃO

### 📄 `DatabaseConnection.java`

**Responsabilidade:** Gerenciar a conexão com o banco de dados MySQL/MariaDB.

**Conceitos Aplicados:**
- ✅ **Singleton Pattern**: Garante uma única instância da conexão
- ✅ **Encapsulamento**: Configurações privadas (URL, USER, PASSWORD)

**Como Funciona:**

```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static final String URL = "jdbc:mysql://localhost:3306/db_petshop";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    // Construtor privado - impede instanciação direta
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar driver MySQL: " + e.getMessage());
        }
    }
    
    // Método estático para obter a instância única
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    // Obtém uma conexão com o banco
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
```

**Por que usar Singleton?**
- Evita múltiplas conexões desnecessárias
- Centraliza o controle de conexão
- Facilita manutenção e configuração
- Economiza recursos do sistema

**Métodos Principais:**
- `getInstance()`: Retorna a instância única (Singleton)
- `getConnection()`: Cria e retorna uma conexão com o banco
- `closeConnection()`: Fecha a conexão de forma segura
- `testConnection()`: Testa se a conexão está funcionando

---

## 2️⃣ PACOTE `model` - ENTIDADES DO DOMÍNIO

**Responsabilidade:** Representar as entidades do negócio (Cliente, Pet, Produto, Funcionario).

### 🎓 Conceitos de POO Aplicados:

#### 1. **ENCAPSULAMENTO**
Atributos privados com acesso controlado via getters/setters, incluindo validações:

```java
public class Cliente {
    private Integer idCliente;
    private String nome;
    private String cpf;
    // ... outros atributos
    
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        if (nome.length() > 100) {
            throw new IllegalArgumentException("Nome não pode ter mais de 100 caracteres.");
        }
        this.nome = nome.trim();
    }
    
    public void setCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio.");
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
        if (!validarCPF(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido.");
        }
        this.cpf = cpfLimpo;
    }
}
```

**Benefícios:**
- Protege dados contra acesso indevido
- Valida dados antes de atribuir
- Facilita manutenção e debug
- Permite mudanças internas sem afetar código externo

#### 2. **HERANÇA**
A classe `Funcionario` é uma **classe abstrata** que serve como base para:
- `Veterinario`
- `Tosador`
- `Atendente`

```java
public abstract class Funcionario {
    protected Integer idFuncionario;
    protected String nome;
    protected String cpf;
    protected String cargo;
    protected Double salarioBase;
    // ... outros atributos
    
    // Método abstrato - DEVE ser implementado pelas subclasses
    public abstract Double calcularSalario();
    
    public abstract String getDescricaoCargo();
}
```

**Por que usar Herança?**
- Reutiliza código comum (nome, CPF, email, etc.)
- Evita duplicação de código
- Facilita manutenção (mudança em um lugar afeta todas as subclasses)
- Organiza hierarquia lógica de classes

#### 3. **POLIMORFISMO**
Cada subclasse implementa `calcularSalario()` de forma diferente:

**Veterinario:**
```java
@Override
public Double calcularSalario() {
    if (salarioBase == null) return 0.0;
    return salarioBase * 1.5; // Bônus de 50%
}
```

**Tosador:**
```java
@Override
public Double calcularSalario() {
    if (salarioBase == null) return 0.0;
    return salarioBase * 1.2; // Bônus de 20%
}
```

**Atendente:**
```java
@Override
public Double calcularSalario() {
    return salarioBase != null ? salarioBase : 0.0; // Sem bônus
}
```

**Exemplo de Polimorfismo em Ação:**
```java
List<Funcionario> funcionarios = new ArrayList<>();
funcionarios.add(new Veterinario(...));
funcionarios.add(new Tosador(...));
funcionarios.add(new Atendente(...));

// Mesmo método, comportamentos diferentes!
for (Funcionario f : funcionarios) {
    System.out.println(f.calcularSalario()); // Cada um calcula diferente!
}
```

**Por que Polimorfismo é importante?**
- Permite tratar objetos diferentes de forma uniforme
- Facilita extensão (adicionar novos tipos de funcionário)
- Reduz acoplamento entre classes
- Torna código mais flexível e manutenível

### 📋 Entidades do Model:

1. **Cliente**: Representa clientes do petshop
   - Valida CPF, email, telefone
   - Armazena dados pessoais e de contato

2. **Pet**: Representa animais de estimação
   - Relacionado com Cliente (id_cliente)
   - Calcula idade automaticamente
   - Valida peso e espécie

3. **Produto**: Representa produtos vendidos
   - Gerencia estoque
   - Valida preço e categoria
   - Métodos: `estaDisponivel()`, `estoqueBaixo()`, `emFalta()`

4. **Funcionario** (abstrata): Base para funcionários
   - **Veterinario**: Bônus de 50% no salário
   - **Tosador**: Bônus de 20% no salário
   - **Atendente**: Salário base sem bônus

---

## 3️⃣ PACOTE `dao` - ACESSO A DADOS

### 📄 `IDAO<T>` - Interface Genérica

**Responsabilidade:** Definir o contrato CRUD (Create, Read, Update, Delete) usando **Generics**.

```java
public interface IDAO<T> {
    T inserir(T entidade) throws Exception;
    T buscarPorId(Integer id) throws Exception;
    List<T> listarTodos() throws Exception;
    T atualizar(T entidade) throws Exception;
    boolean deletar(Integer id) throws Exception;
}
```

**Por que usar Interface?**
- Define contrato comum para todos os DAOs
- Garante que todos implementem os mesmos métodos
- Facilita substituição de implementações
- Permite programação orientada a interfaces

**Por que usar Generics (`<T>`)?**
- Permite reutilizar a interface para qualquer tipo
- `IDAO<Cliente>`, `IDAO<Pet>`, `IDAO<Produto>` - mesma interface!
- Type-safe: o compilador garante tipos corretos
- Evita casting e erros em tempo de execução

### 📄 Implementações dos DAOs

Cada DAO implementa `IDAO<T>` e encapsula operações SQL:

**Exemplo: ClienteDAO**

```java
public class ClienteDAO implements IDAO<Cliente> {
    private DatabaseConnection dbConnection;
    
    public ClienteDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    @Override
    public Cliente inserir(Cliente cliente) throws Exception {
        String sql = "INSERT INTO TB_CLIENTE (nome, cpf, telefone, email, endereco, data_cadastro) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // Preenche os parâmetros (previne SQL Injection)
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getCpf());
            pstmt.setString(3, cliente.getTelefone());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setString(5, cliente.getEndereco());
            pstmt.setTimestamp(6, Timestamp.valueOf(
                cliente.getDataCadastro() != null ? cliente.getDataCadastro() : LocalDateTime.now()
            ));
            
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1)); // Pega o ID gerado
                }
                return cliente;
            } else {
                throw new SQLException("Falha ao inserir cliente.");
            }
        } catch (SQLException e) {
            throw new Exception("Erro ao inserir cliente: " + e.getMessage(), e);
        } finally {
            fecharRecursos(rs, pstmt, null); // Sempre fecha recursos
        }
    }
}
```

**Por que usar DAOs?**
- ✅ **Separação de Responsabilidades**: Lógica de negócio separada do acesso a dados
- ✅ **Reutilização**: Interface comum para todas as entidades
- ✅ **Manutenção**: Mudanças no banco ficam isoladas nos DAOs
- ✅ **Segurança**: Uso de `PreparedStatement` previne SQL Injection
- ✅ **Testabilidade**: Fácil criar mocks para testes

**DAOs Disponíveis:**
- `ClienteDAO`: Gerencia clientes
- `PetDAO`: Gerencia pets (com método `buscarPorCliente()`)
- `ProdutoDAO`: Gerencia produtos (com métodos `buscarPorCategoria()`, `atualizarEstoque()`)
- `FuncionarioDAO`: Gerencia funcionários (com método `buscarPorCargo()`)

---

## 4️⃣ PACOTE `exception` - TRATAMENTO DE ERROS

**Responsabilidade:** Exceções customizadas para erros específicos do domínio.

**Exemplo:**
```java
public class ClienteNaoEncontradoException extends Exception {
    public ClienteNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public ClienteNaoEncontradoException(Integer idCliente) {
        super("Cliente com ID " + idCliente + " não encontrado.");
    }
}
```

**Exceções Disponíveis:**
- `ClienteNaoEncontradoException`
- `PetNaoEncontradoException`
- `ProdutoNaoEncontradoException`
- `FuncionarioNaoEncontradoException`

**Por que Exceções Customizadas?**
- ✅ **Mensagens Claras**: Erros específicos e informativos
- ✅ **Tratamento Diferenciado**: Pode tratar cada tipo de erro de forma diferente
- ✅ **Legibilidade**: Código mais fácil de entender
- ✅ **Debugging**: Facilita identificar problemas

**Uso no DAO:**
```java
public Cliente buscarPorId(Integer id) throws Exception {
    // ... código SQL ...
    if (rs.next()) {
        return criarClienteDoResultSet(rs);
    } else {
        throw new ClienteNaoEncontradoException(id); // Exceção específica!
    }
}
```

---

## 5️⃣ PACOTE `server` - CAMADA DE API REST

### 📄 `WebServer.java` - Servidor HTTP

**Responsabilidade:** Servidor HTTP simples usando `com.sun.net.httpserver` (biblioteca padrão do Java).

```java
public class WebServer {
    private static final int PORT = 8080;
    private static final String WEB_DIR = "web";
    private HttpServer server;
    
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Registra handlers para cada rota da API
        server.createContext("/api/clientes", new ClienteApiHandler());
        server.createContext("/api/funcionarios", new FuncionarioApiHandler());
        server.createContext("/api/pets", new PetApiHandler());
        server.createContext("/api/produtos", new ProdutoApiHandler());
        server.createContext("/", new StaticFileHandler()); // Arquivos estáticos
        
        server.setExecutor(null); // Usa thread pool padrão
        server.start();
        
        System.out.println("Servidor HTTP iniciado na porta " + PORT);
    }
}
```

**Funcionalidades:**
- ✅ Servidor HTTP na porta 8080
- ✅ Roteamento de requisições para handlers específicos
- ✅ Servir arquivos estáticos (HTML, CSS, JS)
- ✅ Suporte a CORS (Cross-Origin Resource Sharing)

### 📄 `*ApiHandler.java` - Handlers REST

**Responsabilidade:** Processar requisições HTTP para cada entidade.

**Exemplo: ClienteApiHandler**

```java
public class ClienteApiHandler implements HttpHandler {
    private ClienteDAO clienteDAO;
    
    public ClienteApiHandler() {
        this.clienteDAO = new ClienteDAO();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // GET, POST, PUT, DELETE
        String path = exchange.getRequestURI().getPath();
        
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange, path);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "PUT":
                    handlePut(exchange, path);
                    break;
                case "DELETE":
                    handleDelete(exchange, path);
                    break;
                default:
                    WebServer.sendResponse(exchange, 405, "text/plain", "Método não permitido");
            }
        } catch (Exception e) {
            // Tratamento de erros
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Erro desconhecido";
            WebServer.sendResponse(exchange, 500, "application/json", 
                "{\"erro\":\"" + errorMsg + "\"}");
        }
    }
    
    private void handleGet(HttpExchange exchange, String path) throws Exception {
        // GET /api/clientes/{id} - Busca um cliente específico
        if (path.matches("/api/clientes/\\d+")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);
            
            Cliente cliente = clienteDAO.buscarPorId(id);
            String json = JsonUtil.clienteToJson(cliente);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
        // GET /api/clientes - Lista todos os clientes
        else {
            List<Cliente> clientes = clienteDAO.listarTodos();
            String json = JsonUtil.clientesToJson(clientes);
            WebServer.sendResponse(exchange, 200, "application/json", json);
        }
    }
    
    private void handlePost(HttpExchange exchange) throws Exception {
        // Lê o corpo da requisição (JSON)
        String body = readRequestBody(exchange);
        Map<String, String> jsonMap = JsonUtil.jsonToMap(body);
        Cliente cliente = JsonUtil.jsonToCliente(jsonMap);
        
        // Insere no banco
        Cliente clienteInserido = clienteDAO.inserir(cliente);
        String json = JsonUtil.clienteToJson(clienteInserido);
        
        // Retorna 201 (Created) com o objeto criado
        WebServer.sendResponse(exchange, 201, "application/json", json);
    }
}
```

**Endpoints Disponíveis:**

**Clientes:**
- `GET /api/clientes` - Lista todos
- `GET /api/clientes/{id}` - Busca por ID
- `POST /api/clientes` - Cria novo
- `PUT /api/clientes/{id}` - Atualiza
- `DELETE /api/clientes/{id}` - Deleta

**Funcionários:**
- `GET /api/funcionarios` - Lista todos
- `GET /api/funcionarios/{id}` - Busca por ID
- `GET /api/funcionarios/cargo/{cargo}` - Busca por cargo
- `POST /api/funcionarios` - Cria novo
- `PUT /api/funcionarios/{id}` - Atualiza
- `DELETE /api/funcionarios/{id}` - Deleta

**Pets:**
- `GET /api/pets` - Lista todos
- `GET /api/pets/{id}` - Busca por ID
- `GET /api/pets/cliente/{idCliente}` - Busca pets de um cliente
- `POST /api/pets` - Cria novo
- `PUT /api/pets/{id}` - Atualiza
- `DELETE /api/pets/{id}` - Deleta

**Produtos:**
- `GET /api/produtos` - Lista todos
- `GET /api/produtos/{id}` - Busca por ID
- `GET /api/produtos/categoria/{categoria}` - Busca por categoria
- `POST /api/produtos` - Cria novo
- `PUT /api/produtos/{id}` - Atualiza
- `DELETE /api/produtos/{id}` - Deleta

### 🔄 Fluxo Completo de uma Requisição

```
1. Cliente (Frontend) → Requisição HTTP (GET /api/clientes)
                          ↓
2. WebServer → Roteia para ClienteApiHandler
                          ↓
3. ClienteApiHandler.handleGet() → Chama clienteDAO.listarTodos()
                          ↓
4. ClienteDAO → Usa DatabaseConnection para obter conexão
                          ↓
5. Executa SQL: SELECT * FROM TB_CLIENTE
                          ↓
6. Converte ResultSet → List<Cliente>
                          ↓
7. Retorna List<Cliente> para o Handler
                          ↓
8. Handler → Converte para JSON via JsonUtil
                          ↓
9. WebServer → Retorna resposta HTTP 200 com JSON
                          ↓
10. Frontend → Recebe e exibe os dados
```

---

## 6️⃣ PACOTE `util` - UTILITÁRIOS

### 📄 `JsonUtil.java` - Conversão JSON

**Responsabilidade:** Serializar e deserializar objetos Java ↔ JSON (sem dependências externas).

**Métodos Principais:**

```java
// Serialização (Objeto → JSON)
public static String clienteToJson(Cliente cliente)
public static String clientesToJson(List<Cliente> clientes)
public static String petToJson(Pet pet)
public static String produtoToJson(Produto produto)
public static String funcionarioToJson(Funcionario funcionario)

// Deserialização (JSON → Objeto)
public static Map<String, String> jsonToMap(String json)
public static Cliente jsonToCliente(Map<String, String> jsonMap)
public static Produto jsonToProduto(Map<String, String> jsonMap)
```

**Exemplo de Uso:**
```java
// Objeto → JSON
Cliente cliente = new Cliente("João", "12345678901", ...);
String json = JsonUtil.clienteToJson(cliente);
// Resultado: {"idCliente":null,"nome":"João","cpf":"12345678901",...}

// JSON → Objeto
String json = "{\"nome\":\"João\",\"cpf\":\"12345678901\"}";
Map<String, String> map = JsonUtil.jsonToMap(json);
Cliente cliente = JsonUtil.jsonToCliente(map);
```

**Por que implementar manualmente?**
- ✅ Sem dependências externas (Gson, Jackson, etc.)
- ✅ Controle total sobre o formato
- ✅ Projeto mais leve
- ✅ Aprendizado de como JSON funciona

### 📄 `ValidacaoUtil.java` - Validações

**Responsabilidade:** Métodos estáticos para validações comuns.

**Métodos Disponíveis:**
```java
public static boolean validarCPF(String cpf)
public static boolean validarEmail(String email)
public static boolean validarTelefone(String telefone)
public static String formatarCPF(String cpf)
public static String formatarTelefone(String telefone)
public static boolean validarStringNaoVazia(String texto)
public static boolean validarIntervalo(int numero, int min, int max)
```

**Exemplo:**
```java
if (ValidacaoUtil.validarCPF("12345678901")) {
    String cpfFormatado = ValidacaoUtil.formatarCPF("12345678901");
    // Resultado: "123.456.789-01"
}
```

**Por que classe utilitária?**
- ✅ Métodos estáticos reutilizáveis
- ✅ Não precisa instanciar objeto
- ✅ Centraliza validações comuns
- ✅ Facilita manutenção

---

## 🏗️ ARQUITETURA COMPLETA

```
┌─────────────────────────────────────────┐
│         FRONTEND (web/)                │
│    HTML + CSS + JavaScript              │
│    - Interface do usuário               │
│    - Faz requisições AJAX               │
└──────────────┬──────────────────────────┘
               │ HTTP Requests (GET/POST/PUT/DELETE)
               │ JSON
               ▼
┌─────────────────────────────────────────┐
│         SERVER (server/)                │
│    WebServer + *ApiHandler             │
│    - Recebe requisições HTTP            │
│    - Roteia para handlers corretos     │
│    - Converte JSON ↔ Objetos            │
│    - Trata erros                        │
└──────────────┬──────────────────────────┘
               │ Chama métodos do DAO
               ▼
┌─────────────────────────────────────────┐
│         DAO (dao/)                       │
│    ClienteDAO, PetDAO, etc.            │
│    - Implementa IDAO<T>                  │
│    - Executa SQL no banco               │
│    - Converte ResultSet → Objetos Model│
│    - Trata exceções específicas         │
└──────────────┬──────────────────────────┘
               │ Usa conexão do DatabaseConnection
               ▼
┌─────────────────────────────────────────┐
│         DB (db/)                         │
│    DatabaseConnection (Singleton)      │
│    - Gerencia conexão única             │
│    - Fornece Connection para DAOs      │
└──────────────┬──────────────────────────┘
               │ JDBC
               ▼
┌─────────────────────────────────────────┐
│         BANCO DE DADOS                  │
│    MySQL/MariaDB                        │
│    - Armazena dados persistentes        │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│         MODEL (model/)                   │
│    Cliente, Pet, Produto, Funcionario  │
│    - Representa entidades do negócio    │
│    - Validações e regras de negócio     │
│    - Herança e Polimorfismo             │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│         UTIL (util/)                     │
│    JsonUtil, ValidacaoUtil             │
│    - Funções auxiliares reutilizáveis  │
│    - Conversão de dados                 │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│         EXCEPTION (exception/)          │
│    Exceções customizadas               │
│    - Tratamento de erros específicos    │
│    - Mensagens claras                   │
└─────────────────────────────────────────┘
```

---

## 🎓 CONCEITOS DE POO APLICADOS

### 1. **ENCAPSULAMENTO**
- ✅ Atributos privados
- ✅ Acesso via getters/setters
- ✅ Validações nos setters
- ✅ Proteção de dados

**Exemplo:**
```java
private String cpf; // Privado - não pode ser acessado diretamente

public void setCpf(String cpf) {
    // Valida antes de atribuir
    if (!validarCPF(cpf)) {
        throw new IllegalArgumentException("CPF inválido");
    }
    this.cpf = cpf;
}
```

### 2. **HERANÇA**
- ✅ Classe abstrata `Funcionario`
- ✅ Subclasses: `Veterinario`, `Tosador`, `Atendente`
- ✅ Reutilização de código comum
- ✅ Hierarquia lógica

**Exemplo:**
```java
// Classe base
public abstract class Funcionario {
    protected String nome;
    protected String cpf;
    // ... atributos comuns
}

// Subclasse
public class Veterinario extends Funcionario {
    // Herda nome, cpf, etc.
    // Adiciona comportamentos específicos
}
```

### 3. **POLIMORFISMO**
- ✅ Método `calcularSalario()` implementado diferente em cada subclasse
- ✅ Tratamento uniforme de objetos diferentes
- ✅ Flexibilidade e extensibilidade

**Exemplo:**
```java
List<Funcionario> funcionarios = new ArrayList<>();
funcionarios.add(new Veterinario(...));
funcionarios.add(new Tosador(...));

// Mesmo método, comportamentos diferentes!
for (Funcionario f : funcionarios) {
    System.out.println(f.calcularSalario()); // Polimorfismo!
}
```

### 4. **ABSTRAÇÃO**
- ✅ Classe abstrata `Funcionario`
- ✅ Métodos abstratos (`calcularSalario()`, `getDescricaoCargo()`)
- ✅ Define contrato sem implementação

**Exemplo:**
```java
public abstract class Funcionario {
    // Método abstrato - DEVE ser implementado pelas subclasses
    public abstract Double calcularSalario();
}
```

### 5. **GENERICS**
- ✅ Interface `IDAO<T>` genérica
- ✅ Type-safe
- ✅ Reutilização de código

**Exemplo:**
```java
public interface IDAO<T> {
    T inserir(T entidade);
    T buscarPorId(Integer id);
    // ...
}

// Uso:
IDAO<Cliente> clienteDAO = new ClienteDAO();
IDAO<Pet> petDAO = new PetDAO();
```

### 6. **SINGLETON**
- ✅ `DatabaseConnection` - uma única instância
- ✅ Controle centralizado
- ✅ Economia de recursos

**Exemplo:**
```java
DatabaseConnection db1 = DatabaseConnection.getInstance();
DatabaseConnection db2 = DatabaseConnection.getInstance();
// db1 e db2 são a MESMA instância!
```

### 7. **INTERFACE**
- ✅ `IDAO<T>` define contrato comum
- ✅ Múltiplas implementações
- ✅ Baixo acoplamento

**Exemplo:**
```java
public interface IDAO<T> {
    T inserir(T entidade);
    // ...
}

public class ClienteDAO implements IDAO<Cliente> {
    // Implementa os métodos
}
```

---

## 🔄 FLUXO COMPLETO DE EXEMPLO

### Cenário: Listar todos os clientes

```
1. Usuário acessa http://localhost:8080
   ↓
2. Frontend (JavaScript) faz requisição:
   fetch('http://localhost:8080/api/clientes')
   ↓
3. WebServer recebe requisição GET /api/clientes
   ↓
4. WebServer roteia para ClienteApiHandler
   ↓
5. ClienteApiHandler.handleGet() é chamado
   ↓
6. Handler chama: clienteDAO.listarTodos()
   ↓
7. ClienteDAO obtém conexão:
   DatabaseConnection.getInstance().getConnection()
   ↓
8. Executa SQL: SELECT * FROM TB_CLIENTE ORDER BY nome ASC
   ↓
9. Converte ResultSet → List<Cliente>
   (método criarClienteDoResultSet())
   ↓
10. Retorna List<Cliente> para o Handler
   ↓
11. Handler converte para JSON:
    JsonUtil.clientesToJson(clientes)
   ↓
12. WebServer retorna HTTP 200 com JSON:
    [{"idCliente":1,"nome":"João",...}, {...}]
   ↓
13. Frontend recebe JSON e exibe na tela
```

### Cenário: Criar um novo cliente

```
1. Usuário preenche formulário e clica "Salvar"
   ↓
2. Frontend envia POST /api/clientes
   Body: {"nome":"Maria","cpf":"98765432100",...}
   ↓
3. WebServer roteia para ClienteApiHandler
   ↓
4. ClienteApiHandler.handlePost() é chamado
   ↓
5. Lê corpo da requisição (JSON)
   ↓
6. Converte JSON → Cliente:
   JsonUtil.jsonToCliente(jsonMap)
   ↓
7. Validações do Model são executadas:
   cliente.setCpf("98765432100") → valida CPF
   ↓
8. Handler chama: clienteDAO.inserir(cliente)
   ↓
9. ClienteDAO executa SQL:
   INSERT INTO TB_CLIENTE (...) VALUES (...)
   ↓
10. Banco retorna ID gerado
   ↓
11. ClienteDAO atualiza objeto com ID
   ↓
12. Retorna Cliente com ID para Handler
   ↓
13. Handler converte para JSON
   ↓
14. Retorna HTTP 201 (Created) com JSON do cliente criado
   ↓
15. Frontend exibe mensagem de sucesso
```

---

## ✅ POR QUE ESSA ARQUITETURA?

### Vantagens:

1. **Separação de Responsabilidades**
   - Cada camada tem uma função específica
   - Fácil entender o que cada parte faz
   - Mudanças isoladas não afetam outras partes

2. **Manutenibilidade**
   - Código organizado e estruturado
   - Fácil encontrar e corrigir bugs
   - Fácil adicionar novas funcionalidades

3. **Reutilização**
   - DAOs seguem mesmo padrão
   - Utilitários podem ser usados em qualquer lugar
   - Interfaces definem contratos claros

4. **Testabilidade**
   - Cada camada pode ser testada separadamente
   - Fácil criar mocks para testes
   - Testes unitários e de integração

5. **Escalabilidade**
   - Fácil adicionar novas entidades
   - Fácil adicionar novos endpoints
   - Fácil migrar para frameworks (Spring, etc.)

6. **Segurança**
   - PreparedStatement previne SQL Injection
   - Validações nos Models
   - Tratamento de erros adequado

---

## 📝 RESUMO DAS RESPONSABILIDADES

| Pacote | Responsabilidade | Exemplo |
|--------|------------------|---------|
| **db** | Gerenciar conexão com banco | `DatabaseConnection` |
| **model** | Entidades do negócio | `Cliente`, `Pet`, `Produto`, `Funcionario` |
| **dao** | Acesso a dados (SQL) | `ClienteDAO`, `PetDAO` |
| **server** | API REST e servidor HTTP | `WebServer`, `ClienteApiHandler` |
| **exception** | Exceções customizadas | `ClienteNaoEncontradoException` |
| **util** | Funções auxiliares | `JsonUtil`, `ValidacaoUtil` |

---

## 🚀 COMO USAR O PROJETO

1. **Configurar Banco de Dados:**
   - Criar banco `db_petshop`
   - Ajustar credenciais em `DatabaseConnection.java`

2. **Executar:**
   ```bash
   java App
   ```

3. **Acessar:**
   - Frontend: `http://localhost:8080`
   - API: `http://localhost:8080/api/clientes`

4. **Testar API:**
   ```bash
   # Listar clientes
   curl http://localhost:8080/api/clientes
   
   # Criar cliente
   curl -X POST http://localhost:8080/api/clientes \
     -H "Content-Type: application/json" \
     -d '{"nome":"João","cpf":"12345678901","email":"joao@email.com"}'
   ```

---

## 📚 CONCEITOS APRENDIDOS

Este projeto demonstra:
- ✅ Arquitetura em camadas
- ✅ Padrão DAO (Data Access Object)
- ✅ Padrão Singleton
- ✅ REST API
- ✅ POO (Encapsulamento, Herança, Polimorfismo, Abstração)
- ✅ Generics
- ✅ Interfaces
- ✅ Tratamento de Exceções
- ✅ Validação de Dados
- ✅ Conversão JSON manual

---

**Desenvolvido por:** Felipe  
**Data:** 2024  
**Tecnologias:** Java, MySQL/MariaDB, HTTP Server (Java SE)

