# ACE-Project-Unit-Test

**ACE-Project-Unit-Test** est un projet basé sur une architecture microservices, conçu pour gérer, générer et tester des tests unitaires pour des projets Java. Ce projet utilise différentes technologies et frameworks pour garantir évolutivité, flexibilité et facilité d'utilisation.

## Structure du Projet

Le dépôt est organisé en plusieurs répertoires comme suit :

### 1. **EurekaServer**
- Serveur de découverte de services utilisant Netflix Eureka.
- Permet la gestion de l'enregistrement et de la communication entre les microservices.

### 2. **FlaskAPI**
- Microservice développé avec Flask (Python) pour générer des tests unitaires à partir de code Java.
- Fournit des APIs REST pour intégrer les fonctionnalités de génération de tests unitaires.

### 3. **GateWay**
- Passerelle API pour router les requêtes vers les microservices appropriés.
- Gère l'authentification, l'autorisation et la transmission des requêtes.

### 4. **UnitTest**
- Interface utilisateur pour scanner les fichiers java et générer les tests unitaires.
- Contient la logique back-end pour interagir avec FlaskAPI.

### 5. **UserAuth-BackEnd**
- Microservice backend pour la gestion de l'authentification et de l'autorisation des utilisateurs.
- Assure un accès sécurisé au système.

### 6. **ace-front**
- Interface principale du projet.
- Développée avec des technologies modernes pour offrir une expérience utilisateur intuitive.

## Prérequis

- **Docker** : Assurez-vous que Docker est installé pour déployer les services avec Docker Compose.
- **Java (JDK 11 ou supérieur)** : Pour exécuter les microservices basés sur Java.
- **Node.js** : Pour gérer le développement front-end.

## Démarrage

### Cloner le Dépôt
```bash
git clone https://github.com/hossam1956/ACE-Project-Unit-Test.git
cd ACE-Project-Unit-Test
```

### Lancer le Projet avec Docker Compose
1. Vérifiez que le fichier `docker-compose.yml` est correctement configuré.
2. Exécutez la commande suivante :
   ```bash
   docker-compose up --build
   ```

### Lancer le Microservice Unit Test en Local
1. Naviguez dans le répertoire `UnitTest`.
2. Exécutez la commande suivante pour démarrer le service :
   ```bash
   ./mvnw spring-boot:run
   ```

### Lancer le Frontend en Local
1. Accédez au répertoire `ace-front` :
   ```bash
   cd ace-front
   ```
2. Installez les dépendances :
   ```bash
   npm install
   ```
3. Lancez le serveur Angular :
   ```bash
   ng serve
   ```
4. Accédez à l'application sur `http://localhost:4200`.

## Démo

### Vidéo de Démonstration


https://github.com/user-attachments/assets/a0301d61-6582-4e18-940b-7e1efcdc547e




## Licence

Ce projet est sous licence MIT.

