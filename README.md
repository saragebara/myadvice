# myAdvice
**COMP2800 Group Project**
An undergraduate CS advising system

## Tech Stack
- Java 17+
- Spring Boot 4.0.3
- MariaDB 12.2.2
- Hibernate/Spring Data JPA
- Maven

## Project Structure
```
myadvice/
  src/main/java/com/sad/myadvice/
    entity/           # Shared JPA entities (User, Course, Transcript, etc). Java classes that map to DB tables through Hibernate
    repository/       # Spring Data JPA repositories - interfaces that generate SQL queries 
    advising/         # Sara: Curriculum Advising module
      service/          # Logic
      controller/       # Wiring UI to services
      ui/               # JavaFX UI
    booking/          # Sara: Bookings module
      service/          
      controller/       
      ui/               
    scheduling/       # Yousif: Scheduling module (to be added)
    admin/            # Garrett: System Administration module (to be added)
    reports/          # Zahra: Reports module (to be added)
  src/main/resources/
    application.properties          # DB config and app settings
  src/test/java/com/sad/myadvice/   # Tests (mirrors the main structure)
  README.md
  pom.xml            # Includes versions/dependencies
```


## Setup Guide
Before cloning, make sure you have the following prerequisites:
- **Java 17** or higher (check with `java -version` in terminal)
- **Git** (check with `git --version`)
- **VSCode** with the Java Extension Pack
- **MariaDB 12.2.2**: Follow the instructions below for local setup

### Step 1: Local MariaDB Installation
1. Download the installer from: https://mariadb.org/download
   - Version: **12.2.2**
   - OS: **Windows**
   - Architecture: **x86_64**
   - Package Type: **MSI Package**
2. Run the installer. Leave everything the same. The port will be 3306.
   You will be prompted for a root password - this can be anything, just be sure to **note it down** since you will need it.
3. After installation, open HeidiSQL. A shortcut should have been added to your desktop.

### Step 2: Creating Local DB with HeidiSQL 
1. Open HeidiSQL and click “New” on the bottom left.

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img width="250" height="188" alt="image" src="https://github.com/user-attachments/assets/7be89b32-0022-458f-808e-80a7c2936b51" />

2. You can name your session if you wish. Enter your root password you set up earlier. Then, press "Open".
&nbsp;&nbsp;&nbsp;<img width="250" height="188" alt="image" src="https://github.com/user-attachments/assets/9a1191fd-b1d4-4754-a7fc-80abb635121d" />

3. To create the database, at the top, go to **Query → New Query Tab**.
4. Write the following SQL (replace the blank with your database name):

```sql
CREATE DATABASE ____;
```

For example,
```sql
CREATE DATABASE gebaras_myadvice;
```

5. Click the play button at the top to run the command.
&nbsp;&nbsp;&nbsp;<img width="519" height="145" alt="image" src="https://github.com/user-attachments/assets/1ffc8e9e-521e-4e91-9b62-e06c175eb056" />

## Step 3: Clone Repo
In your terminal, run:
```
git clone https://github.com/saragebara/myadvice.git
cd myadvice
```

## Step 4: Connecting the Local Database
In order to connect the database to the code, you need to modify the application.properties file.
Open `src/main/resources/application.properties` and fill in your database name and password:
```properties
# MariaDB (LOCAL) Database Connection
spring.datasource.url=jdbc:mariadb://localhost:3306/[YOUR DB NAME]
spring.datasource.username=root
spring.datasource.password= [YOUR PASSWORD]
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Turn off web application type (no web server needed long term)
spring.main.web-application-type=none
```

## Step 5: Run App
In your terminal, run:
```
.\mvnw.cmd clean
.\mvnw.cmd spring-boot:run
```
> Make sure you run these from the **project root** (`C:\Users\yourname\myadvice`) and NOT from inside the `src` folder.

It should say BUILD SUCCESS. To check that the database tables were created properly, check on HeidiSQL by querying ```SHOW TABLES;```.
Currently, it should show `course`, `users`, `transcript`, `prerequisite`, `course_plan`, and `course_plan_item`.
