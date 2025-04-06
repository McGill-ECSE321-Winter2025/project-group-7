# ECSE321 Group 7🐻‍❄️

## The Project
Hi! We are group 7🤯 We are a team of 7 students consisting of:

    🪼Artimice, a U2 computer engineering student👩‍💻

    🦑Brian, a U2 software engineering student🧑‍💻

    🐈‍⬛Doddy, a U2 software engineering student🧑‍💻

    🦋Jyothsna, a U2 computer engineering student👩‍💻

    👻Mary, a U2 software engineering student👩‍💻

    🌝Shengyi, a U2 software engineering student👩‍💻

    👸🏽Snigdha, a U2 computer engineering student👩‍💻

## Board Game Sharing System – Project Scope
🎲 The Board Game Sharing System is a Java-based application designed for board game enthusiasts to connect, share games, and organize events. The application supports two types of users: Players and Game Owners. Players can browse available board games, register for events, and request to borrow games, while Game Owners can manage their game collections, approve or decline borrowing requests, and track lending history.
### Key Features:
+ **Secure Access & User Roles** – User authentication is required, and users can switch between Player and Game Owner roles.
+ **Game Library** – A centralized collection of board games where users can browse available titles and see their owners.
+ **Game Lending & Tracking** – Players can request to borrow games, and Game Owners can approve, decline, and monitor their lending history.
+ **Event Creation & Participation** – Users can schedule board game events, set details such as date and location, and allow other users to register.
+ **Community Feedback & Reviews** – Users can share their experiences by leaving reviews for board games they’ve played.
### Technology Stack:
+ **Backend:** Java, Spring Boot, Hibernate (ORM), REST API
+ **Database:** PostgreSQL
+ **Frontend:** Vue.js, HTML, CSS, JavaScript
  
This project is focused on providing a seamless experience for board game enthusiasts by enabling easy game sharing and event organization. Future enhancements may include additional social features or advanced search and filtering options.

## Installing, Building, Testing and Running the Application 

Running the app locally requires Java and PostgreSQL 

```
$ java --version
java 21.0.1 2023-10-17 LTS
Java(TM) SE Runtime Environment (build 21.0.1+12-LTS-29)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.1+12-LTS-29, mixed mode, sharing)
$ psql --version
psql (PostgreSQL) 17.2
```
This project uses java 21. Furthermore the app requires a database. **Create a database** using:
```SQL
psql --username postgres
Password for user postgres: 
postgres=# CREATE DATABASE board_game_sharing_system
```
*Note that the password for the user postgres is ecse321. Details of database configuration can be found in the app.properties file*

**Steps to build the project:**

```bash
## . is project-group7
$ cd BoardGameSharingSystem-Backend
$ ./gradlew build -xtest

```
**Steps to run the tests:**
```bash
## . is project-group7
$ cd BoardGameSharingSystem-Backend
$ ./gradlew test

```

**Steps to generate the REST API documentation:**

```bash
## . is project-group7
$ cd BoardGameSharingSystem-Backend
$ ./gradlew bootRun

```
Once the application is running, it is normal that it only reaches 80%. Click this link http://localhost:8080/swagger-ui.html to be amazed by this technology. To find the **test coverage report**, please consult [Software-Quality-Assurance-Report](https://github.com/McGill-ECSE321-Winter2025/project-group-7/wiki/Software-Quality-Plan-and-Report)


**Steps to run the frontend**
```bash
## . is project-group7
$ cd BoardGameSharingSystem-Frontend
$ npm install
$ npm run dev
```
*The frontend uses port 8090. Please ensure that the port is not taken by another process*

On windows
```
netstat -ano |find /i "8090"
```

## Team Management
**Please refer to the [wiki page](https://github.com/McGill-ECSE321-Winter2025/project-group-7/wiki) to see project report and meeting minutes for each deliverable.**


Table 1 : Roles of each team member Deliverable 1
Name | Role | Tasks | Number of Hours 
--- | --- | --- | ---
Artimice |  <ul><li>Full Stack Developer | <ul><li>Work on class diagram</li><li>Persistence Layer and Testing</li><li>Validate backlog in GitHub Projects</li></ul> | 20
Brian |  <ul><li>Technical Lead</li><li>GitHub Repository Manager</li><li>Full Stack Developer | <ul><li>Work on class diagram</li><li>Transfer class diagram to draw.io</li><li>Refine class diagram</li><li>Persistence Layer and Testing</li><li>Maintain backlog in GitHub Projects</li><li>Provide project deliverable on wiki</li></ul> | 20
Doddy | <ul><li>GitHub Backlog Contributor</li><li>Full Stack Developer | <ul><li>Work on class diagram</li><li>Refine class diagram</li><li>Persistence Layer and Testing</li><li>Validate backlog in GitHub Projects</li></ul> | 20
Jyothsna | <ul><li>Assistant Task Coordinator</li><li>Full Stack Developer | <ul><li>Work on class diagram</li><li>Outline scope of project</li><li>Persistence Layer and Testing</li><li>ReadME: welcome page</li></ul> | 20
Mary | <ul><li>Project Coordinator</li><li>Meeting Organizer</li><li>Full Stack Developer  | <ul><li>Work on class diagram</li><li>Persistence Layer and Testing</li><li>Lab report: design decisions recorded</li><li>Arrange weekly meetings and task deadlines</li></ul> | 20
Shengyi | <ul><li>Full Stack Developer | <ul><li>Work on class diagram</li><li>Persistence Layer and Testing</li><li>Lab report: design decisions recorded</li></ul> | 20
Snigdha | <ul><li>Assistant Task Coordinator</li><li>Full Stack Developer| <ul><li>Work on class diagram</li><li>Persistence Layer and Testing</li><li>Lab report: minutes of meeting recorded</li><li>Arrange task deadlines</li></ul> | 22


Table 2 : Roles of each team member Deliverable 2
Name | Role | Tasks | Number of Hours 
--- | --- | --- | ---
Artimice |  <ul><li>Full Stack Developer | <ul><li>Work on class diagram</li><li>Persistence Layer and Testing</li><li>Validate backlog in GitHub Projects</li><li>Implement and test REST API endpoints for Review</li><li>Implement and test serive methods to manage Reviews</li></ul> | 50
Brian |  <ul><li>Technical Lead</li><li>GitHub Repository Manager</li><li>Full Stack Developer | <ul><li>Design of updated persistence, service, RESTAPI </li><li>Implement and test service methods to manage Game Collection</li><li>Implement and test REST API endpoints for Game owner</li><li>Persistence Layer and Testing</li><li>Maintain backlog in GitHub Projects</li><li>Provide project deliverable on wiki</li></ul> | 60
Doddy | <ul><li>GitHub Backlog Contributor</li><li>Full Stack Developer | <ul><li>Implement and test service methods to manage Events, EventGames, and Registrations</li><li>Implement and test REST API endpoints for Events, EventGames, and Registrations</li><li>Maintain backlog in GitHub Projects</li><li>Coding Style Documented on Wiki</li></ul> | 50
Jyothsna | <ul><li>Assistant Task Coordinator</li><li>Full Stack Developer | <ul><li>Implement and test service methods to manage UserAccounts</li><li>Implement and test REST API endpoints for UserAccounts</li><li>General Testing Standards and Test Coverage Report Documented on Wiki</li><li>Maintain backlog in GitHub Projects</li><li>Assist in arrangement of task deadlines and assignees</li></ul> | 52
Mary | <ul><li>Project Coordinator</li><li>Meeting Organizer</li><li>Full Stack Developer  | <ul><li>Work on class diagram</li><li>Persistence Layer and Testing</li><li>Lab report: design decisions recorded</li><li>Arrange weekly meetings and task deadlines</li></ul> | 50
Shengyi | <ul><li>Full Stack Developer | <ul><li>Implement and test service methods to manage GameOwning</li><li>Implement and test REST API endpoints for GameCopies and RequestAnswer</li><li>Work on class diagram</li><li>Persistence Layer and Testing</li><li>Lab report: design decisions recorded</li></ul> | 50
Snigdha | <ul><li>Task Coordinator</li><li>Full Stack Developer| <ul><li>Implement and test service methods to manage Game Collection</li><li>Implement and test REST API endpoints for Game </li><li>Meeting minutes recorded and documented</li><li>Maintain backlog in GitHub Projects</li><li>Arrange task deadlines and assignees</li></ul> | 52

Table 3 : Roles of each team member Deliverable 3
Name | Role | Tasks | Number of Hours 
--- | --- | --- | ---
Artimice |  <ul><li>Full Stack Developer | <ul><li>Game Owner page implementation</li> <li>Specific Game page implementation</ul> | 
Brian |  <ul><li>Technical Lead</li><li>GitHub Repository Manager</li><li>Full Stack Developer |<ul> <li>Games Page Implementation</li> <li>Initiate Vue Project</li><li>Configured Build System</li> <li>GitHub Issues</i></ul> | 20
Doddy | <ul><li>GitHub Backlog Contributor</li><li>Full Stack Developer | <ul></ul> | 
Jyothsna | <ul><li> Head UI designer</li><li>Assistant Task Coordinator</li><li>Full Stack Developer</li><li> Login Page Implementation | <ul></ul> | 64
Mary | <ul><li>Project Coordinator</li><li>Meeting Organizer</li><li>Full Stack Developer  | <ul></ul> | 25
Shengyi | <ul><li>Full Stack Developer | <ul></ul> | 
Snigdha | <ul><li> UI designer</li><li>Task Coordinator</li><li>Full Stack Developer</li><li> Login Page Implementation| <ul></ul> | 64

