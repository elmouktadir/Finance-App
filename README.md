# 🏦 Finance App Refactoring - Version 2.0

Application bancaire refactorisée avec design patterns et pipeline CI/CD complet.

## 📋 Description

Ce projet est une refactorisation complète d'une application bancaire "spaghetti" en une architecture modulaire et maintenable utilisant des design patterns fondamentaux.

## 🎨 Design Patterns Implémentés

### 1. Strategy Pattern
- `DepositStrategy` - Gestion des dépôts
- `WithdrawStrategy` - Gestion des retraits
- `TransferStrategy` - Gestion des transferts

### 2. Factory Pattern
- `AccountFactory` - Création de comptes (CHECKING, SAVINGS, BUSINESS)
- `UserFactory` - Création d'utilisateurs (STANDARD, PREMIUM, ADMIN)

### 3. Observer Pattern
- `AuditLogger` - Enregistrement des transactions dans un fichier log
- `NotificationService` - Notifications et alertes pour les utilisateurs

## 🔧 Prérequis

- Java JDK 11+
- Maven 3.6+
- Jenkins 2.300+ (optionnel pour CI/CD)
- SonarQube 8.0+ (optionnel pour analyse qualité)

## 📥 Installation

### 1. Cloner le projet
```bash
git clone https://github.com/university/finance-refactoring.git
cd finance-refactoring
```

### 2. Compiler
```bash
mvn clean compile
```

### 3. Exécuter les tests
```bash
mvn test
```

### 4. Générer le rapport de couverture
```bash
mvn jacoco:report
```
Rapport disponible dans: `target/site/jacoco/index.html`

### 5. Créer le JAR exécutable
```bash
mvn package
```

### 6. Exécuter l'application
```bash
java -jar target/refactored-finance-2.0.0-jar-with-dependencies.jar
```

## 🚀 Utilisation

### Comptes de test

L'application initialise deux utilisateurs par défaut:

- **alice** / password123 (Compte CHECKING, solde: 1000.0)
- **bob** / password456 (Compte SAVINGS, solde: 500.0)

### Fonctionnalités

1. Connexion / Inscription
2. Affichage des comptes
3. Opérations:
    - Dépôts
    - Retraits
    - Transferts
4. Historique des transactions
5. Création de nouveaux comptes
6. Statistiques

## 🧪 Tests

### Exécuter tous les tests
```bash
mvn test
```

### Statistiques des tests
- **Tests unitaires**: 67+
- **Couverture**: > 80%
- **Patterns testés**: Strategy, Factory, Observer, Services

## 📊 Métriques de Qualité

### SonarQube
```bash
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000
```

### Objectifs
- Coverage: > 80%
- Bugs: 0
- Vulnerabilities: 0
- Code Smells: < 10
- Technical Debt: < 5%

## 🔄 Pipeline CI/CD

Le projet inclut un `Jenkinsfile` avec les étapes suivantes:

1. **Checkout** - Récupération du code
2. **Build** - Compilation Maven
3. **Unit Tests** - Tests JUnit
4. **Code Coverage** - Rapport JaCoCo
5. **Quality Analysis** - Analyse SonarQube
6. **Quality Gate** - Vérification des seuils
7. **Package** - Création du JAR

## 📁 Structure du Projet

```
src/
├── main/java/com/university/finance/
│   ├── model/           (Account, User, Transaction)
│   ├── pattern/
│   │   ├── strategy/    (4 fichiers)
│   │   ├── factory/     (2 fichiers)
│   │   └── observer/    (3 fichiers)
│   ├── service/         (BankingService, TransactionService)
│   └── MainApp.java
└── test/java/           (5 fichiers de tests)
```

## 👥 Contributeurs

- **Équipe ENSA Marrakech**
- **Module**: Ingénierie Logicielle
- **Responsable**: BOUARIFI Walid
- **Année**: 2024-2025

## 📝 Licence

Projet académique - ENSA Marrakech
