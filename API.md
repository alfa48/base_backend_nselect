# Documentação da API — Frontend

> **Base URL (dev):** `http://localhost:8080`  
> **Documentação interactiva (Swagger):** `http://localhost:8080/swagger-ui.html`

---

## Índice

1. [Convenções gerais](#1-convenções-gerais)
2. [Autenticação](#2-autenticação)
3. [Utilizadores](#3-utilizadores)
4. [Parceiros (Admin)](#4-parceiros-admin)
5. [Leads (Parceiro)](#5-leads-parceiro)
6. [Notas de Lead (Parceiro)](#6-notas-de-lead-parceiro)
7. [Materiais de Apoio — Parceiro](#7-materiais-de-apoio--parceiro)
8. [Materiais de Apoio — Admin](#8-materiais-de-apoio--admin)
9. [Tickets (Parceiro)](#9-tickets-parceiro)
10. [Ficheiros públicos](#10-ficheiros-públicos)
11. [Enums de referência](#11-enums-de-referência)
12. [Formato de erros](#12-formato-de-erros)

---

## 1. Convenções gerais

### Autenticação com token

Todos os endpoints marcados como **🔒 Autenticado** exigem o header:

```
Authorization: Bearer <access_token>
```

### Respostas paginadas

Quando a resposta é uma lista paginada, o formato é sempre:

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5,
  "last": false
}
```

Os parâmetros de paginação enviados na query string são:

| Parâmetro | Tipo | Padrão | Descrição |
|---|---|---|---|
| `pagina` | `int` | `0` | Número da página (começa em 0) |
| `tamanho` | `int` | `10` | Quantidade de itens por página |

### Identificadores (`publicId`)

Todos os IDs usados nos endpoints são **UUIDs** chamados `publicId`. Nunca usar o `id` numérico.

### Datas

As datas são devolvidas como `string` no formato ISO 8601:  
`"2025-04-30T10:23:00"`

Nos filtros de query string, enviar datas no formato `YYYY-MM-DD`:  
`?dataInicial=2025-01-01&dataFinal=2025-04-30`

### Multipart (upload de ficheiros)

Quando um endpoint aceita ficheiros, o `Content-Type` deve ser `multipart/form-data`.  
Os dados JSON do corpo são enviados como uma **parte chamada `dados`** (tipo `application/json`).

---

## 2. Autenticação

Base: `/api/v1/auth` — **público, sem token**

---

### `POST /api/v1/auth/login`

Autentica o utilizador e devolve os tokens.

**Body (JSON):**

```json
{
  "username": "admin@app.co.ao",
  "password": "Admin@1234"
}
```

| Campo | Tipo | Obrigatório |
|---|---|---|
| `username` | string (email) | ✅ |
| `password` | string | ✅ |

**Resposta `200`:**

```json
{
  "access_token": "eyJ...",
  "refresh_token": "eyJ...",
  "id": 1,
  "public_id": "uuid-aqui",
  "nome": "Administrador",
  "username": "admin@app.co.ao",
  "role": "ADMIN",
  "foto": "http://localhost:8080/files/usuarios/foto.jpg"
}
```

> **Nota:** `foto` pode ser `null` se o utilizador não tiver foto. Usar o `role` para decidir qual interface mostrar (`ADMIN` ou `PARCEIRO`).

---

### `POST /api/v1/auth/register`

Regista uma nova conta pública (role `ADMIN` por omissão).

**Body (JSON):**

```json
{
  "nome": "Novo Admin",
  "username": "novo@app.co.ao",
  "password": "MinhaPass1"
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---|---|
| `nome` | string | ✅ | — |
| `username` | string (email) | ✅ | Formato email válido |
| `password` | string | ✅ | Mínimo 8 caracteres |

**Resposta `201`:** Mesmo formato que o login.

---

### `POST /api/v1/auth/refresh-token`

Renova o access token usando o refresh token.

**Header:**
```
Authorization: Bearer <refresh_token>
```

**Resposta `200`:** Mesmo formato que o login com novos tokens.

---

### `POST /api/v1/auth/logout`

🔒 Invalida todos os tokens activos do utilizador.

**Header:**
```
Authorization: Bearer <access_token>
```

**Resposta `204`:** Sem corpo.

---

## 3. Utilizadores

Base: `/api/v1/usuarios` — 🔒 **Autenticado**

---

### `POST /api/v1/usuarios`

Cria um novo utilizador. O utilizador autenticado será o "criador".

**Body (JSON):**

```json
{
  "nome": "João Silva",
  "username": "joao@app.co.ao",
  "password": "Senha1234",
  "role": "ADMIN"
}
```

| Campo | Tipo | Obrigatório | Valores possíveis |
|---|---|---|---|
| `nome` | string | ✅ | — |
| `username` | string (email) | ✅ | — |
| `password` | string | ✅ | Mínimo 8 caracteres |
| `role` | string (enum) | ✅ | `ADMIN`, `PARCEIRO` |

**Resposta `201`:** String com o `publicId` do utilizador criado.

```json
"550e8400-e29b-41d4-a716-446655440000"
```

---

### `PUT /api/v1/usuarios/{publicId}`

Edita o nome e role de um utilizador.

**Body (JSON):**

```json
{
  "nome": "Novo Nome",
  "role": "ADMIN"
}
```

| Campo | Tipo | Obrigatório |
|---|---|---|
| `nome` | string | ✅ |
| `role` | string (enum) | ✅ |

**Resposta `202`:** Sem corpo.

---

### `DELETE /api/v1/usuarios/{publicId}`

Elimina permanentemente um utilizador.

**Resposta `204`:** Sem corpo.

---

### `PATCH /api/v1/usuarios/{publicId}/foto`

Upload ou substituição da foto de perfil.

**Content-Type:** `multipart/form-data`

| Parte | Tipo | Obrigatório | Formatos aceites |
|---|---|---|---|
| `file` | ficheiro | ✅ | JPEG, PNG, WEBP (máx 5 MB) |

**Resposta `202`:** URL completa da nova foto.

```json
"http://localhost:8080/files/usuarios/abc123.jpg"
```

---

### `GET /api/v1/usuarios`

Lista todos os utilizadores (paginado).

**Query params:** `pagina`, `tamanho`

**Resposta `200`:** Lista paginada de:

```json
{
  "publicId": "uuid",
  "nome": "João Silva",
  "username": "joao@app.co.ao",
  "role": "ADMIN",
  "foto": "http://...",
  "createdAt": "2025-01-10T09:00:00",
  "updatedAt": "2025-01-10T09:00:00",
  "criadorPublicId": "uuid",
  "criadorNome": "Admin"
}
```

---

### `GET /api/v1/usuarios/meus`

Lista os utilizadores criados pelo utilizador autenticado (paginado).

**Query params:** `pagina`, `tamanho`

**Resposta `200`:** Mesmo formato que `/usuarios`.

---

### `GET /api/v1/usuarios/{publicId}`

Retorna os dados de um utilizador específico.

**Resposta `200`:** Mesmo formato que o item da lista.

---

## 4. Parceiros (Admin)

Base: `/api/v1/admin/parceiros` — 🔒 **Apenas ADMIN**

---

### `POST /api/v1/admin/parceiros`

Cria um novo parceiro e a respectiva conta de acesso (role `PARCEIRO`).

**Content-Type:** `multipart/form-data`

| Parte | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `dados` | JSON | ✅ | Dados do parceiro (ver abaixo) |
| `foto` | ficheiro | ❌ | Foto do parceiro (imagem) |
| `documento` | ficheiro | ❌ | BI ou Passaporte (imagem ou PDF) |

**`dados` (JSON):**

```json
{
  "nome": "Empresa XYZ",
  "nif": "123456789",
  "iban": "AO06 0040 0000 1234 5678 9012 3",
  "telefone": "923000000",
  "email": "xyz@empresa.co.ao",
  "password": "Senha1234",
  "endereco": "Rua de Luanda, 123",
  "tipoParceiroPublicId": "uuid-tipo",
  "provinciaPublicId": "uuid-provincia"
}
```

| Campo | Obrigatório |
|---|---|
| `nome` | ✅ |
| `nif` | ✅ |
| `telefone` | ✅ |
| `email` | ✅ |
| `password` | ✅ (palavra-passe da conta do parceiro) |
| `tipoParceiroPublicId` | ✅ |
| `provinciaPublicId` | ✅ |
| `iban` | ❌ |
| `endereco` | ❌ |

**Resposta `201`:** `publicId` do parceiro criado (string).

---

### `PUT /api/v1/admin/parceiros/{publicId}`

Edita um parceiro. **Todos os campos são opcionais** — só os enviados são actualizados.

**Content-Type:** `multipart/form-data`

| Parte | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `dados` | JSON | ✅ | Campos a actualizar |
| `foto` | ficheiro | ❌ | Nova foto (substitui a anterior) |
| `documento` | ficheiro | ❌ | Novo documento BI (substitui o anterior) |

**`dados` (JSON):** Mesmos campos do criar, todos opcionais:

```json
{
  "nome": "Novo Nome",
  "telefone": "924111222",
  "provinciaPublicId": "uuid-nova-provincia"
}
```

**Resposta `202`:** Sem corpo.

---

### `DELETE /api/v1/admin/parceiros/{publicId}`

Elimina o parceiro e **todos os dados associados**: leads, notas, comprovativos, conta de acesso.

**Resposta `204`:** Sem corpo.

---

### `GET /api/v1/admin/parceiros`

Lista todos os parceiros com filtros opcionais.

**Query params:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `nome` | string | Filtro parcial por nome (case-insensitive) |
| `provinciaPublicId` | string (uuid) | Filtrar por província |
| `tipoParceiroPublicId` | string (uuid) | Filtrar por tipo de parceiro |
| `pagina` | int | — |
| `tamanho` | int | — |

**Resposta `200`:** Lista paginada de:

```json
{
  "publicId": "uuid",
  "nome": "Empresa XYZ",
  "telefone": "923000000",
  "email": "xyz@empresa.co.ao",
  "nif": "123456789",
  "fotoUrl": "http://localhost:8080/files/parceiros/fotos/abc.jpg",
  "provinciaNome": "Luanda",
  "tipoParceiroNome": "Partner",
  "createdAt": "2025-01-10T09:00:00"
}
```

> `fotoUrl` pode ser `null` se não tiver foto.

---

### `GET /api/v1/admin/parceiros/{publicId}`

Retorna todos os detalhes de um parceiro.

**Resposta `200`:**

```json
{
  "publicId": "uuid",
  "nome": "Empresa XYZ",
  "nif": "123456789",
  "iban": "AO06...",
  "telefone": "923000000",
  "email": "xyz@empresa.co.ao",
  "endereco": "Rua de Luanda, 123",
  "fotoUrl": "http://...",
  "temAnexo": true,
  "provinciaPublicId": "uuid",
  "provinciaNome": "Luanda",
  "tipoParceiroPublicId": "uuid",
  "tipoParceiroNome": "Partner",
  "usuarioPublicId": "uuid",
  "createdAt": "2025-01-10T09:00:00",
  "updatedAt": "2025-01-15T14:30:00"
}
```

> `temAnexo: true` significa que tem documento BI/Passaporte carregado. Usar para mostrar/ocultar botão de download.

---

### `GET /api/v1/admin/parceiros/{publicId}/documento`

Faz download do documento BI/Passaporte do parceiro.

**Resposta `200`:** Ficheiro binário com `Content-Disposition: attachment`.

> Retorna `404` se o parceiro não tiver documento carregado.

---

## 5. Leads (Parceiro)

Base: `/api/v1/leads` — 🔒 **Apenas PARCEIRO**

---

### `POST /api/v1/leads`

Cria um novo lead associado ao parceiro autenticado.

**Body (JSON):**

```json
{
  "nome": "Cliente Teste",
  "email": "cliente@gmail.com",
  "telemovel": "912345678",
  "nif": "987654321",
  "pacotePublicId": "uuid-pacote",
  "estado": "LEAD"
}
```

| Campo | Obrigatório | Descrição |
|---|---|---|
| `nome` | ✅ | — |
| `pacotePublicId` | ✅ | UUID do pacote pretendido |
| `email` | ❌ | — |
| `telemovel` | ❌ | — |
| `nif` | ❌ | — |
| `estado` | ❌ | Padrão: `LEAD` |

**Resposta `201`:** `publicId` do lead criado (string).

---

### `PUT /api/v1/leads/{publicId}`

Edita um lead. Quando o estado passa para `CONVERTIDO` é **obrigatório** enviar comprovativo (se ainda não tiver um).

**Content-Type:** `multipart/form-data`

| Parte | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `dados` | JSON | ✅ | Dados do lead |
| `comprovativo` | ficheiro | ⚠️ | Obrigatório se `estado=CONVERTIDO` e sem comprovativo anterior |

**`dados` (JSON):** Mesmo formato do criar.

**Resposta `202`:** Sem corpo.

---

### `DELETE /api/v1/leads/{publicId}`

Elimina um lead e todas as suas notas.

**Resposta `204`:** Sem corpo.

---

### `PATCH /api/v1/leads/{publicId}/comprovativo`

Upload ou substituição do comprovativo de pagamento.

**Content-Type:** `multipart/form-data`

| Parte | Tipo | Formatos aceites |
|---|---|---|
| `file` | ficheiro | JPEG, PNG, WEBP, PDF (máx 5 MB) |

**Resposta `202`:** Caminho relativo do ficheiro guardado (string).

---

### `GET /api/v1/leads/{publicId}`

Retorna todos os dados de um lead, incluindo as notas.

**Resposta `200`:**

```json
{
  "publicId": "uuid",
  "nome": "Cliente Teste",
  "email": "cliente@gmail.com",
  "telemovel": "912345678",
  "nif": "987654321",
  "estado": "CONVERTIDO",
  "comprovantivoUrl": "http://localhost:8080/files/comprovativos/abc.pdf",
  "pacotePublicId": "uuid",
  "pacoteNome": "Pacote Plus",
  "pacotePreco": 30000.00,
  "parceiroPublicId": "uuid",
  "parceiroNome": "Empresa XYZ",
  "usuarioPublicId": "uuid",
  "usuarioNome": "João Silva",
  "createdAt": "2025-01-10T09:00:00",
  "updatedAt": "2025-01-15T14:30:00",
  "notas": [
    {
      "publicId": "uuid",
      "nota": "Contactei o cliente por telefone.",
      "usuarioPublicId": "uuid",
      "usuarioNome": "João Silva",
      "createdAt": "2025-01-11T10:00:00",
      "updatedAt": "2025-01-11T10:00:00"
    }
  ]
}
```

> `comprovantivoUrl` é `null` se ainda não tiver comprovativo.

---

### `GET /api/v1/leads`

Lista os leads do parceiro autenticado (paginado), com filtros opcionais.

**Query params:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `estado` | string (enum) | `LEAD`, `PENDENTE`, `CONVERTIDO`, `PERDIDO` |
| `dataInicial` | date (`YYYY-MM-DD`) | Data mínima de criação |
| `dataFinal` | date (`YYYY-MM-DD`) | Data máxima de criação |
| `pagina` | int | — |
| `tamanho` | int | — |

**Resposta `200`:** Lista paginada de:

```json
{
  "publicId": "uuid",
  "nome": "Cliente Teste",
  "pacoteNome": "Pacote Plus",
  "pacotePreco": 30000.00,
  "estado": "LEAD",
  "createdAt": "2025-01-10T09:00:00"
}
```

---

### `GET /api/v1/leads/overview`

Retorna os totais para o dashboard do parceiro.

**Resposta `200`:**

```json
{
  "totalLeads": 25,
  "totalFaturacao": 750000.00
}
```

---

### `GET /api/v1/leads/recentes`

Lista os leads criados nos últimos 7 dias pelo parceiro autenticado.

**Resposta `200`:** Array (não paginado) com o mesmo formato do item da lista.

---

### `GET /api/v1/leads/admin/todos` — 🔒 Apenas ADMIN

Lista **todos** os leads de todos os parceiros.

**Query params:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `estado` | string (enum) | Filtro por estado |
| `dataInicial` | date | — |
| `dataFinal` | date | — |
| `parceiroPublicId` | string (uuid) | Filtrar por parceiro específico |
| `pagina` | int | — |
| `tamanho` | int | — |

**Resposta `200`:** Lista paginada com o mesmo formato da listagem do parceiro.

---

## 6. Notas de Lead (Parceiro)

Base: `/api/v1/leads/{leadPublicId}/notas` — 🔒 **Apenas PARCEIRO**

---

### `POST /api/v1/leads/{leadPublicId}/notas`

Adiciona uma nota ao lead.

**Body (JSON):**

```json
{
  "nota": "Cliente confirmou interesse, aguarda aprovação interna."
}
```

**Resposta `201`:** `publicId` da nota criada (string).

---

### `PUT /api/v1/leads/{leadPublicId}/notas/{publicId}`

Edita uma nota. **Só o autor da nota pode editar.**

**Body (JSON):** Mesmo formato do criar.

**Resposta `202`:** Sem corpo.  
**`400`** se tentar editar uma nota que não é sua.

---

### `DELETE /api/v1/leads/{leadPublicId}/notas/{publicId}`

Elimina uma nota. **Só o autor da nota pode eliminar.**

**Resposta `204`:** Sem corpo.  
**`400`** se tentar eliminar uma nota que não é sua.

---

### `GET /api/v1/leads/{leadPublicId}/notas`

Lista todas as notas de um lead, da mais recente para a mais antiga.

**Resposta `200`:** Array (não paginado):

```json
[
  {
    "publicId": "uuid",
    "nota": "Texto da nota",
    "usuarioPublicId": "uuid",
    "usuarioNome": "João Silva",
    "createdAt": "2025-01-11T10:00:00",
    "updatedAt": "2025-01-11T10:00:00"
  }
]
```

---

## 7. Materiais de Apoio — Parceiro

Base: `/api/v1/materiais-apoio` — 🔒 **Apenas PARCEIRO**

Cada parceiro só vê os materiais cujo alvo inclui o seu tipo de parceiro.

---

### `GET /api/v1/materiais-apoio`

Lista os materiais de apoio disponíveis para o parceiro autenticado.

**Query params:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `nome` | string | Pesquisa parcial por nome |
| `tipoConteudo` | string (enum) | `PROMOCIONAL`, `EDUCATIVO` |
| `pagina` | int | — |
| `tamanho` | int | — |

**Resposta `200`:** Lista paginada de:

```json
{
  "publicId": "uuid",
  "nome": "Brochura de Produto",
  "arquivoUrl": "http://localhost:8080/files/materiais/brochura.pdf",
  "tipoConteudo": "PROMOCIONAL",
  "createdAt": "2025-01-05T08:00:00"
}
```

> `arquivoUrl` pode ser `null` se o material não tiver ficheiro associado.

---

## 8. Materiais de Apoio — Admin

Base: `/api/v1/admin/materiais-apoio` — 🔒 **Apenas ADMIN**

---

### `POST /api/v1/admin/materiais-apoio`

Cria um novo material de apoio.

**Content-Type:** `multipart/form-data`

| Parte | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `dados` | JSON | ✅ | Metadados do material |
| `arquivo` | ficheiro | ❌ | Ficheiro (imagem ou PDF) |

**`dados` (JSON):**

```json
{
  "nome": "Brochura de Produto",
  "tiposParceiroPublicIds": ["uuid-tipo-1", "uuid-tipo-2"],
  "tipoConteudo": "PROMOCIONAL"
}
```

| Campo | Obrigatório | Descrição |
|---|---|---|
| `nome` | ✅ | — |
| `tiposParceiroPublicIds` | ✅ | Lista de UUIDs dos tipos de parceiro que verão este material |
| `tipoConteudo` | ❌ | `PROMOCIONAL` ou `EDUCATIVO` |

**Resposta `201`:** `publicId` do material criado (string).

---

### `PUT /api/v1/admin/materiais-apoio/{publicId}`

Edita um material de apoio.

**Content-Type:** `multipart/form-data`

| Parte | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `dados` | JSON | ✅ | Campos a actualizar (mesmo formato do criar) |
| `arquivo` | ficheiro | ❌ | Novo ficheiro (substitui o anterior) |

**Resposta `202`:** Sem corpo.

---

### `GET /api/v1/admin/materiais-apoio/{publicId}/ficheiro`

Faz download do ficheiro associado ao material.

**Resposta `200`:** Ficheiro binário com `Content-Disposition: attachment`.

> Retorna `404` se o material não tiver ficheiro.

---

## 9. Tickets (Parceiro)

Base: `/api/v1/tickets` — 🔒 **Apenas PARCEIRO**

> **Nota:** Nesta versão os parceiros podem consultar tickets. A criação de tickets é gerida internamente.

---

### `GET /api/v1/tickets`

Lista os tickets do parceiro autenticado (paginado).

**Query params:** `pagina`, `tamanho`

**Resposta `200`:** Lista paginada de:

```json
{
  "publicId": "uuid",
  "conteudo": "Problema ao submeter lead...",
  "tipo": "BUG",
  "estado": "ABERTO",
  "createdAt": "2025-04-20T10:30:00"
}
```

---

### `GET /api/v1/tickets/{publicId}`

Retorna os detalhes completos de um ticket.

**Resposta `200`:**

```json
{
  "publicId": "uuid",
  "conteudo": "Problema ao submeter lead...",
  "tipo": "BUG",
  "estado": "ABERTO",
  "publicadoPorPublicId": "uuid",
  "publicadoPorNome": "João Silva",
  "createdAt": "2025-04-20T10:30:00",
  "updatedAt": "2025-04-20T10:30:00"
}
```

---

## 10. Ficheiros públicos

### `GET /files/{subdir}/{filename}`

Serve qualquer ficheiro armazenado. **Público, sem token.**

Exemplos:
```
GET /files/usuarios/foto.jpg
GET /files/parceiros/fotos/logo.png
GET /files/comprovativos/doc.pdf
GET /files/materiais/brochura.pdf
```

> As URLs completas já são devolvidas directamente nos campos `fotoUrl`, `arquivoUrl`, `comprovantivoUrl`, etc. — não é necessário construir manualmente.

---

## 11. Enums de referência

### `role` — Papel do utilizador

| Valor | Descrição |
|---|---|
| `ADMIN` | Administrador com acesso total |
| `PARCEIRO` | Parceiro comercial |

### `estado` — Estado do lead

| Valor | Descrição |
|---|---|
| `LEAD` | Contacto inicial registado |
| `PENDENTE` | Em análise / negociação |
| `CONVERTIDO` | Venda concretizada (requer comprovativo) |
| `PERDIDO` | Oportunidade perdida |

### `tipoConteudo` — Tipo de material de apoio

| Valor | Descrição |
|---|---|
| `PROMOCIONAL` | Material de marketing / vendas |
| `EDUCATIVO` | Material formativo |

### `tipo` — Tipo de ticket

| Valor | Descrição |
|---|---|
| `CANCELAR` | Pedido de cancelamento |
| `BUG` | Reporte de erro |
| `RECLAMACAO` | Reclamação |
| `OUTRO` | Outro assunto |

### `estado` — Estado do ticket

| Valor | Descrição |
|---|---|
| `ABERTO` | Ticket recebido, aguarda resposta |
| `PROGRESSO` | Em tratamento |
| `RESOLVIDO` | Resolvido |

---

## 12. Formato de erros

Todos os erros seguem este formato:

```json
{
  "type": "error",
  "msg": "Descrição do problema"
}
```

### Códigos HTTP comuns

| Código | Significado |
|---|---|
| `400` | Dados inválidos ou regras de negócio violadas |
| `401` | Não autenticado (token ausente, expirado ou inválido) |
| `403` | Sem permissão (role insuficiente) |
| `404` | Recurso não encontrado |
| `500` | Erro interno do servidor |

> Para `400`, a mensagem (`msg`) descreve exactamente o problema (ex: `"Comprovativo obrigatório para o estado CONVERTIDO"`).
