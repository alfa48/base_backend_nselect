# Uso de Modais (Avisos e Confirmações)

Este documento explica como usar o sistema de modais (janelas flutuantes) para interagir com o utilizador.

---

## 1. Como Funciona
Os modais estão centralizados num fragmento Thymeleaf em `layout/fragments/modals.html` e são controlados pelo ficheiro `static/js/modals.js`.

---

## 2. Tipos de Modais (JavaScript)

Podes chamar estas funções a partir de qualquer parte do teu código JavaScript ou eventos HTML (como `onclick`).

### A. Modal de Sucesso
Usado para confirmar que uma ação correu bem.
```javascript
Modal.success("Utilizador criado com sucesso!", "Feito!");
```

### B. Modal de Erro
Usado para avisar sobre falhas ou erros da API.
```javascript
Modal.error("Não foi possível ligar ao servidor.", "Erro");
```

### C. Modal de Confirmação
Usado antes de ações perigosas (ex: Eliminar). Ele precisa de uma mensagem e da URL para onde deve redirecionar se o utilizador confirmar.
```javascript
Modal.confirm("Tens a certeza que queres eliminar?", "/utilizadores/eliminar?id=1");
```

---

## 3. Gatilhos Automáticos (Backend -> Modal)
Muitas vezes queremos mostrar um modal logo após um redirecionamento (ex: depois de salvar um formulário). 

Para isso, o Java envia uma mensagem e o JavaScript mostra o modal automaticamente ao carregar a página.

**No Java (Controller):**
```java
redirectAttrs.addFlashAttribute("successMessage", "Utilizador guardado!");
```

**O que acontece:**
O sistema detecta este atributo na página e dispara o `Modal.success()` automaticamente.

---

## 4. Requisitos
Para que os modais funcionem numa nova página, ela deve incluir:
1. O script `modals.js`.
2. O fragmento de HTML `modals`.

```html
<!-- No final da página -->
<script th:src="@{/js/modals.js}"></script>
<div th:replace="~{layout/fragments/modals :: modals}"></div>
```
