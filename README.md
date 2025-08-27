Microsserviço responsável pelo **gerenciamento de usuários** da plataforma **CleanPro**. Ele atende tanto **clientes** (contratantes de serviços de limpeza) quanto **prestadores** (colaboradores que realizam os serviços).

Este serviço é a base da aplicação, fornecendo endpoints para cadastro, autenticação, atualização de perfil e integração com outros microsserviços (booking, payment, notification, review).

---

## 📝 Objetivo

- Criar e gerenciar perfis de usuários (clientes e prestadores).  
- Validar dados de cadastro e documentos (CPF, RG, certificados).  
- Suportar autenticação via JWT e integração futura com SSO (Google).  
- Permitir integração com os demais serviços do sistema CleanPro.  

O **diferencial do cadastro** é o campo `type`, que determina o perfil do usuário:

```json
"type": "client"   // Contratante de serviços
"type": "provider" // Prestador / colaborador
````

---

## 🚀 Endpoints Principais

| Método | Endpoint           | Descrição                                         |
| ------ | ------------------ | ------------------------------------------------- |
| POST   | /users             | Criar um novo usuário                             |
| GET    | /users/{id}        | Obter dados de usuário por ID                     |
| PUT    | /users/{id}        | Atualizar informações do usuário                  |
| DELETE | /users/{id}        | Deletar usuário                                   |
| POST   | /users/login       | Autenticação do usuário                           |
| GET    | /users/type/{type} | Listar usuários por tipo (`client` ou `provider`) |

---

## 📦 Contrato JSON

### 1️⃣ Criar Usuário (`POST /users`)

**Request**

```json
{
  "name": "Maria Silva",
  "email": "maria@email.com",
  "password": "Senha123!",
  "type": "provider", 
  "phone": "+5511999999999",
  "documents": {
    "cpf": "123.456.789-00",
    "rg": "12.345.678-9",
    "certificates": ["limpezaProfissional.pdf"]
  },
  "address": {
    "street": "Rua Exemplo",
    "number": "100",
    "city": "São Paulo",
    "state": "SP",
    "zip": "01234-567"
  }
}
```

**Response**

```json
{
  "id": "uuid-usuario-123",
  "name": "Maria Silva",
  "email": "maria@email.com",
  "type": "provider",
  "phone": "+5511999999999",
  "createdAt": "2025-08-26T22:00:00Z",
  "status": "active"
}
```

---

### 2️⃣ Autenticação (`POST /users/login`)

**Request**

```json
{
  "email": "maria@email.com",
  "password": "Senha123!"
}
```

**Response**

```json
{
  "token": "jwt.token.gerado.aqui",
  "user": {
    "id": "uuid-usuario-123",
    "name": "Maria Silva",
    "email": "maria@email.com",
    "type": "provider"
  }
}
```

---

### 3️⃣ Atualizar Usuário (`PUT /users/{id}`)

**Request**

```json
{
  "phone": "+5511988888888",
  "address": {
    "street": "Av. Paulista",
    "number": "1000",
    "city": "São Paulo",
    "state": "SP",
    "zip": "01310-100"
  }
}
```

**Response**

```json
{
  "id": "uuid-usuario-123",
  "name": "Maria Silva",
  "email": "maria@email.com",
  "phone": "+5511988888888",
  "address": {
    "street": "Av. Paulista",
    "number": "1000",
    "city": "São Paulo",
    "state": "SP",
    "zip": "01310-100"
  },
  "updatedAt": "2025-08-26T22:30:00Z",
  "status": "active"
}
```

---

### 4️⃣ Obter Usuário por ID (`GET /users/{id}`)

**Response**

```json
{
  "id": "uuid-usuario-123",
  "name": "Maria Silva",
  "email": "maria@email.com",
  "type": "provider",
  "phone": "+5511999999999",
  "address": {
    "street": "Rua Exemplo",
    "number": "100",
    "city": "São Paulo",
    "state": "SP",
    "zip": "01234-567"
  },
  "documents": {
    "cpf": "123.456.789-00",
    "rg": "12.345.678-9",
    "certificates": ["limpezaProfissional.pdf"]
  },
  "createdAt": "2025-08-26T22:00:00Z",
  "status": "active"
}
```

---

### 5️⃣ Listar Usuários por Tipo (`GET /users/type/{type}`)

**Response**

```json
[
  {
    "id": "uuid-usuario-123",
    "name": "Maria Silva",
    "email": "maria@email.com",
    "type": "provider",
    "status": "active"
  },
  {
    "id": "uuid-usuario-456",
    "name": "João Souza",
    "email": "joao@email.com",
    "type": "provider",
    "status": "active"
  }
]
```

---

## ⚙️ Observações Técnicas

* Senhas devem ser armazenadas **hash com bcrypt**.
* JWT deve expirar em tempo configurável.
* Todos os endpoints retornam **JSON**.
* Possível versionamento futuro dos endpoints (`/v1/users`).
* Integração com `booking-service`, `payment-service` e `notification-service` para fluxo completo da aplicação.

---

## Autores

- [@Emerson Lima](https://www.linkedin.com/in/stackdeveloper/)

