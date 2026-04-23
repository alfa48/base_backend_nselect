# Implementação de Paginação

Este guia explica como funciona o sistema de paginação no projeto e como deves implementar em novas listagens.

---

## 1. O Desafio das Páginas (API vs UI)
A nossa API externa usa **páginas baseadas em 0** (0-based), ou seja:
- Página 1 na UI = `page=0` na API.
- Página 2 na UI = `page=1` na API.

---

## 2. Implementação no Controller
O Controller é responsável por fazer a ponte entre o número da página que o utilizador vê e o que a API espera.

```java
@GetMapping("/utilizadores")
public String listar(
        @RequestParam(defaultValue = "1") int page, // Padrão UI: 1
        @RequestParam(defaultValue = "10") int size,
        Model model) {
    
    // 1. Converter UI (1-based) para API (0-based)
    int apiPage = (page > 0) ? page - 1 : 0;
    
    // 2. Chamar o serviço
    PageResponse<UserDTO> response = service.getMeusUsuarios(apiPage, size);
    
    // 3. Passar os dados para o Thymeleaf
    model.addAttribute("items", response.getContent());
    model.addAttribute("currentPage", page);        // Mantém 1-based para o utilizador
    model.addAttribute("totalPages", response.getTotalPages());
    model.addAttribute("size", size);
    
    return "minha-listagem";
}
```

---

## 3. Implementação no HTML (Thymeleaf)
Deves adicionar os botões de navegação no final da tua tabela.

```html
<div class="pagination-wrapper">
    <!-- Botão Anterior -->
    <a th:if="${currentPage > 1}" 
       th:href="@{/rota(page=${currentPage - 1}, size=${size})}" 
       class="button-pagination">
        Anterior
    </a>
    
    <!-- Indicador de Página -->
    <span>Página <span th:text="${currentPage}">1</span> de <span th:text="${totalPages}">1</span></span>

    <!-- Botão Próximo -->
    <a th:if="${currentPage < totalPages}" 
       th:href="@{/rota(page=${currentPage + 1}, size=${size})}" 
       class="button-pagination">
        Próximo
    </a>
</div>
```

---

## 4. Notas Importantes
- **Comportamento da API**: Se pedires uma página que não existe (ex: página 50 num total de 2), a API retornará uma lista vazia. O nosso sistema trata isso mostrando a mensagem "Nenhum item encontrado".
- **Limites**: Mesmo que definas um `size=100`, a API só retornará os registos que realmente existem.
- **Sessão**: Lembra-te que o `BaseApiService` já anexa o Token de autenticação automaticamente, por isso não precisas de te preocupar com isso na paginação.
