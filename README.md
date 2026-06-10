# 🏨 JR Hotel - Système de Gestion d'Hôtel

**JR Hotel** est une API REST développée avec **Spring Boot** pour gérer les réservations, les chambres, les utilisateurs et les services d'un hôtel.

---

## 📌 **Fonctionnalités**
✅ **Gestion des chambres** :
- Ajout, modification, suppression des chambres.
- Recherche par type (Simple, Double, Suite, etc.).
- Disponibilité en temps réel.

✅ **Gestion des réservations** :
- Création, annulation et modification de réservations.
- Vérification des disponibilités par date.
- Historique des réservations par client.

✅ **Gestion des utilisateurs** :
- Inscription et authentification (JWT).
- Rôles (ADMIN, USER, RECEPTIONIST).
- Profil utilisateur.

✅ **Sécurité** :
- Authentification via **JWT (JSON Web Token)**.
- Autorisations basées sur les rôles (Spring Security).

✅ **API Documentation** :
- Documentation automatique avec **SpringDoc OpenAPI (Swagger)**.
- Accès : `http://localhost:4040/swagger-ui.html`

---

## 🛠 **Technologies Utilisées**
   Technologie | Version | Usage |
 |-------------|---------|-------|
| **Java** | 21 | Langage principal |
| **Spring Boot** | 4.0.6 | Framework backend |
| **Spring Security** | - | Gestion de l'authentification |
| **Spring Data JPA** | - | Accès à la base de données |
| **PostgreSQL** | - | Base de données relationnelle |
| **Liquibase** | - | Gestion des migrations |
| **JUnit 5** | 5.10.0 | Tests unitaires et d'intégration |
| **MockMvc** | - | Tests des contrôleurs |
| **Maven** | - | Gestion des dépendances |

---

## 🚀 **Comment démarrer le projet ?**

### **Prérequis**
- [JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Maven 3.8+](https://maven.apache.org/download.cgi)
- [PostgreSQL](https://www.postgresql.org/download/)
- [IntelliJ IDEA 2024.1+](https://www.jetbrains.com/idea/download/) (recommandé)

---

### **Installation et exécution**
1. **Cloner le dépôt** :
   ```bash
   git clone https://github.com/JeanRobertLelo/gestionHotel.git
   cd jr-hotel