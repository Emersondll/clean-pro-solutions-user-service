# Clean Pro Solutions - User Service 👤

## 🎯 Papel no Ecossistema
O **User Service** é o responsável pela gestão de perfis dentro da plataforma. Ele lida com:
- Cadastro e manutenção de **Clientes**.
- Cadastro e manutenção de **Profissionais de Limpeza**.
- Armazenamento de preferências, endereços e perfis detalhados.
- Integração com outros serviços para fornecer dados de perfil (Ex: para o `scheduling-service`).

## 🚀 Tecnologias
- **Java 21** & **Spring Boot 3.3.4**
- **MongoDB** (Persistência de perfis e preferências)
- **RabbitMQ** (Sincronização de dados e eventos de cadastro)
- **Netflix Eureka** (Service Discovery)

## 🛠️ Como Executar

### 1. Execução Isolada (Individual)
Para rodar apenas este serviço e suas dependências:
```bash
docker-compose up -d --build
```
O serviço estará disponível em `http://localhost:8082`.

### 2. Execução Integrada
Este serviço é orquestrado pelo projeto principal [Clean Pro Platform](../README.md).

## 🧪 Qualidade
- **Cobertura de Testes**: Mínimo de 80%.
- **Build**: `mvn clean verify`.

---
© 2026 Clean Pro Solutions - Desenvolvido por Emerson.
