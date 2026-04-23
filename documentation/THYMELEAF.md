# Thymeleaf: Java <-> HTML

Este documento explica como passar dados entre o Java (Backend) e o HTML (Frontend) usando o Thymeleaf.

---

## 1. Do Java para o HTML (Exibir Dados)
Para enviar dados para a página, usamos o objeto `Model` no Controller.

**No Java:**
```java
@GetMapping("/perfil")
public String perfil(Model model) {
    model.addAttribute("nomeUsuario", "Manuel Alfredo");
    model.addAttribute("admin", true);
    return "perfil";
}
```

**No HTML:**
```html
<!-- Exibir texto simples -->
<h1 th:text="${nomeUsuario}">Nome Padrão</h1>

<!-- Condicionais (se for admin, mostra o botão) -->
<button th:if="${admin}">Painel Admin</button>
```

---

## 2. Do HTML para o Java (Formulários)
Para receber dados de um formulário, usamos o vínculo de objetos (`th:object`).

**No Java (Preparação):**
```java
model.addAttribute("usuarioForm", new UsuarioDTO());
```

**No HTML (Vínculo):**
```html
<form th:action="@{/salvar}" th:object="${usuarioForm}" method="post">
    <!-- O '*' indica que o campo pertence ao objeto 'usuarioForm' -->
    <input type="text" th:field="*{nome}">
    <input type="email" th:field="*{email}">
    <button type="submit">Guardar</button>
</form>
```

**No Java (Receção):**
```java
@PostMapping("/salvar")
public String salvar(@ModelAttribute("usuarioForm") UsuarioDTO dados) {
    System.out.println("Nome recebido: " + dados.getNome());
    return "redirect:/sucesso";
}
```

---

## 3. URLs e Links
Sempre usa `@{~}` para links, para que o Spring trate os caminhos corretamente.

```html
<!-- Link estático -->
<a th:href="@{/dashboard}">Ir para Dashboard</a>

<!-- Link com parâmetro -->
<a th:href="@{/utilizadores/editar(id=${u.id})}">Editar</a>
```

---

## 4. Fragmentos (Componentes Reutilizáveis)
Podes criar pedaços de HTML que se repetem em várias páginas (ex: Menu, Modais).

**Definição (em `layout.html`):**
```html
<div th:fragment="menu">
   <nav>...</nav>
</div>
```

**Inclusão (em qualquer página):**
```html
<div th:replace="~{layout :: menu}"></div>
```

---

## 5. Upload de Arquivos (Multipart)
Para enviar ficheiros (imagens, PDFs), é obrigatório usar `enctype="multipart/form-data"`.

**No HTML:**
```html
<form th:action="@{/upload}" method="post" enctype="multipart/form-data">
    <input type="file" name="foto">
    <button type="submit">Enviar</button>
</form>
```

**No Java (Controller):**
```java
@PostMapping("/upload")
public String upload(@RequestParam("foto") MultipartFile arquivo) {
    if (!arquivo.isEmpty()) {
        System.out.println("Recebido: " + arquivo.getOriginalFilename());
    }
    return "redirect:/sucesso";
}
```

> [!TIP]
> Podes usar um input `hidden` e disparar o clique via JS para manter o design premium:
> ```html
> <div onclick="document.getElementById('file-id').click()">Fazer Upload</div>
> <input type="file" id="file-id" style="display:none">
> ```
