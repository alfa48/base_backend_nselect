# Criar Serviços de API

Este guia ensina a criar novos serviços para comunicar com a API externa seguindo a arquitetura do projeto.

---

## 1. A Classe Mãe: `BaseApiService`
Todos os teus serviços de API **devem** estender a classe `BaseApiService`. Ela já trata de:
- Autenticação (Token Bearer).
- API Key.
- Logs automáticos de requisição e resposta.
- Tratamento de erros HTTP.
- Refresh automático de Token.

---

## 2. Como Criar um Novo Serviço

### Passo 1: Definir a Classe
```java
@Service
public class ClienteService extends BaseApiService {
    // Teus métodos aqui
}
```

### Passo 2: Fazer um pedido GET
Para listar dados ou buscar um item específico:
```java
public PageResponse<ClienteDTO> listarClientes(int page, int size) {
    String url = "/clientes?page=" + page + "&size=" + size;
    return get(url, new ParameterizedTypeReference<PageResponse<ClienteDTO>>() {});
}
```

### Passo 3: Fazer um pedido POST
Para criar ou enviar dados:
```java
public String criarCliente(ClienteCreateRequest dados) {
    return post("/clientes", dados, String.class);
}
```

---

## 3. Melhores Práticas
1. **Não faças Hardcode de URLs**: Usa caminhos relativos (ex: `/clientes`), o `BaseApiService` já sabe qual é a URL base.
2. **Usa DTOs**: Nunca passes entidades ou mapas genéricos. Cria classes Java que representam exatamente o que a API espera.
3. **Logs**: Não precisas de fazer `System.out.println`. O `BaseApiService` já faz log automático de tudo o que envias e recebes.
