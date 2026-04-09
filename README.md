<!-- markdownlint-disable MD033 MD041 -->
<div align="center">
  <h1>💳 Cardify  </h1>
  
  <p>
    <strong>A production-ready Modular Monolith backend for secure, structured Peer-to-Peer card trading and management.</strong>
  </p>

  <p>
    <a href="#-problem-statement">The Problem</a> •
    <a href="#-features">Features</a> •
    <a href="#-architecture">Architecture</a> •
    <a href="#-tech-stack">Tech Stack</a> •
    <a href="#-api-documentation">API Docs</a> •
    <a href="#-mobile-integration">Mobile App</a>
  </p>

  <p>
    <img src="https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk" alt="Java"/>
    <img src="https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot"/>
    <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
    <img src="https://img.shields.io/badge/JWT-Safe-000000?style=for-the-badge&logo=jsonwebtokens" alt="JWT"/>
    <img src="https://img.shields.io/badge/Architecture-Modular_Monolith-blue?style=for-the-badge" alt="Architecture"/>
  </p>
</div>

---

## 📖 The Problem Statement
...
## 📱 Cardify Mobile Application

The Cardify ecosystem is designed for mobility. While this repository powers the engine, the user experience lives in the native mobile application.

<p align="center">
<a href="https://github.com/Mahir-Agarwal/cardify_mobileapplication">
<img src="https://img.shields.io/badge/Cardify-Mobile_Application-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
</a>
</p>
...
      <h3>💬 Trade Continuity</h3>
      <ul>
        <li><strong>Real-Time Chat</strong>: WhatsApp-style messaging via <strong>WebSockets (STOMP)</strong>.</li>
        <li><strong>WebRTC Signaling</strong>: Integrated P2P handshake mechanism for direct mobile-to-mobile data sharing.</li>
        <li><strong>Soft Deletion</strong>: Cards aren't just "erased"—they are deactivated to maintain history.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🏢 Enterprise Security</h3>
      <ul>
        <li><strong>P2P DataChannels</strong>: Sensitive card codes are shared directly between devices (WebRTC), bypassing the server's database completely.</li>
        <li><strong>Global Exception Handling</strong>: Unified error codes for front-end clarity.</li>
        <li><strong>JWT Handshake</strong>: Secure WebSocket authentication using stateless tokens.</li>
      </ul>
    </td>
  </tr>
</table>

---

## 🛠️ Tech Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Framework** | Spring Boot 3.4.x | Core framework with Java 17. |
| **Real-Time** | WebSockets (STOMP) | Persistent, instant messaging layer. |
| **P2P Signaling** | WebRTC | Peer-to-Peer handshake mechanism for private data sharing. |
| **Database** | MySQL 8.0 | Reliable, local persistence for users and orders. |
| **API Layer** | Spring Web | RESTful controllers with structured DTO responses. |
| **Documentation** | Swagger / OpenAPI 3 | Automatically generated interactive API UI. |
| **Persistence** | Spring Data JPA | Repository abstraction with Hibernate. |
| **Security** | Spring Security 6 | JWT-based role protection (BUYER, OWNER). |
| **Utilities** | Lombok | Boilerplate reduction for DTOs and Entities. |

---

## 📂 Project Structure

The project follows a clean **package-by-feature** modular structure:

```bash
src/main/java/in/sp/main
├── auth/            # Registration, Login, Token generation logic
├── user/            # User profiles and role management
├── card/            # Card inventory, search, and soft-delete logic
├── order/           # The core Order state machine and lifecycle
├── payment/         # Escrow simulation and transaction logging
├── chat/            # Order-specific messaging modules
├── review/          # Buyer/Owner rating and feedback system
└── core/            # Universal logic (Security, Exceptions, BaseEntity)
```

---

## 📚 API Documentation

### 🔑 Core State Transitions

| Method | Endpoint | Transition | Role |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/orders/request` | `START` ➡️ `REQUESTED` | Buyer |
| `PUT` | `/api/orders/{id}/accept` | `REQUESTED` ➡️ `ACCEPTED` | Owner |
| `POST` | `/api/orders/{id}/pay` | `ACCEPTED` ➡️ `PAID` | Buyer |
| `PUT` | `/api/orders/{id}/place` | `PAID` ➡️ `ORDER_PLACED` | Owner |
| `PUT` | `/api/orders/{id}/confirm`| `PLACED` ➡️ `COMPLETED` | Buyer |

**Interactive Swagger UI:** 
Available locally at `http://[YOUR_IP]:8080/swagger-ui/index.html`

---

<div align="center">
  <p>
    <sub> Engineered and Developed by <a href="https://github.com/Mahir-Agarwal">Mahir Agarwal</a></sub>
  </p>
</div>
