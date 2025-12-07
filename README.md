# Stalingrado Battle Simulator

A turn-based battle simulation system modeling the Battle of Stalingrad between AXIS and USSR forces. This educational project demonstrates object-oriented programming principles, design patterns, multi-threading, and database integration in Java.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Database Schema](#database-schema)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Design Patterns](#design-patterns)
- [Recent Updates (2025)](#recent-updates-2025)

## Features

- **Turn-based Combat System**: Simulates battles between two armies with different unit types
- **Multiple Unit Types**: Infantry, tanks, fighter aircraft, artillery, and more
- **Customizable Armies**: Configure attack and defense bonifications for strategic advantage
- **Equipment System**: Various weapons (rifles, cannons, tanks, aircraft) and defensive equipment (vests, trenches)
- **Multi-threaded Simulation**: Each army runs in its own thread for concurrent battle execution
- **Database Integration**: Persistent storage of battle reports with MySQL
- **Observer Pattern**: Real-time battle event notifications
- **Statistical Views**: Track victories, defeats, and performance metrics

## Technologies

- **Java 17** - Modern LTS version with enhanced features
- **Maven 3.x** - Dependency management and build automation
- **MySQL 8.0** - Relational database for battle persistence
- **SLF4J + Logback** - Modern logging framework
- **JUnit 5** - Unit testing framework
- **JDBC** - Database connectivity

## Architecture

The project follows a **three-layer architecture**:

```
┌─────────────────────────────────────┐
│         View Layer (UI)             │
│  - Console-based menu system        │
│  - User input handling              │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│      Control Layer (Logic)          │
│  - Battle orchestration             │
│  - Observer pattern implementation  │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│       Model Layer (Domain)          │
│  - Ejercito (Army)                  │
│  - Soldado (Soldier)                │
│  - CampoBatalla (Battlefield)       │
│  - Weapons & Equipment              │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│    Data Access Layer (DAO)          │
│  - Database connection management   │
│  - CRUD operations                  │
└─────────────────────────────────────┘
```

## Prerequisites

- **Java Development Kit (JDK) 17 or higher**
  ```bash
  java -version  # Should show version 17+
  ```

- **Apache Maven 3.6 or higher**
  ```bash
  mvn -version
  ```

- **MySQL Server 8.0 or higher**
  ```bash
  mysql --version
  ```

## Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Stalingrado
```

### 2. Set Up MySQL Database

Start your MySQL server and create the database:

```bash
mysql -u root -p < informesBatallas.sql
```

Or manually:

```sql
mysql -u root -p
source informesBatallas.sql
```

This will create:
- Database: `batallas`
- Tables: `ejercitos`, `tipos_unidad`, `batallas`, `informes`, `soldados_batalla`
- Views: `vista_resumen_batallas`, `vista_estadisticas_ejercitos`
- Default data: USSR and AXIS armies, unit types

### 3. Install Dependencies

```bash
mvn clean install
```

This will download all required dependencies including:
- MySQL Connector/J 8.0.33
- SLF4J logging framework
- Logback for log management
- JUnit 5 for testing

## Configuration

### Database Configuration

Edit `src/main/resources/application.properties`:

```properties
db.host=localhost
db.port=3306
db.name=batallas
db.username=root
db.password=your_password_here
```

**Security Note**: For production, use environment variables instead of hardcoded credentials.

### Alternative: Environment Variables

Copy `.env.example` to `.env` and update:

```bash
cp .env.example .env
```

Edit `.env`:
```
DB_HOST=localhost
DB_PORT=3306
DB_NAME=batallas
DB_USERNAME=root
DB_PASSWORD=your_secure_password
```

## Running the Application

### Option 1: Using Maven

```bash
mvn exec:java
```

### Option 2: Build and Run JAR

```bash
# Build the JAR with dependencies
mvn clean package

# Run the application
java -jar target/stalingrado-battle-simulator-2.0.0-jar-with-dependencies.jar
```

### Option 3: From IDE

Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code) and run:
- Main class: `View.Main`

## Database Schema

### Core Tables

**ejercitos** (Armies)
- `id` - Primary key
- `nombre` - Army name (USSR, AXIS)
- `bonificacion_ataque` - Attack multiplier
- `bonificacion_defensa` - Defense multiplier
- `fecha_creacion` - Creation timestamp

**batallas** (Battles)
- `id` - Primary key
- `ejercito_ganador_id` - Winning army FK
- `ejercito_perdedor_id` - Losing army FK
- `duracion_segundos` - Battle duration
- `total_soldados_iniciales` - Initial troop count
- `soldados_sobrevivientes` - Survivors count
- `fecha_batalla` - Battle timestamp

**informes** (Battle Reports)
- `id` - Primary key
- `batalla_id` - Battle FK
- `resultado` - Battle outcome description
- `detalles_combate` - Combat details
- `fecha_creacion` - Report timestamp

**tipos_unidad** (Unit Types)
- Predefined soldier types with weapon/armor configurations

**soldados_batalla** (Battle Soldiers)
- Individual soldier tracking per battle

### Useful Queries

```sql
-- View all battle reports
SELECT * FROM vista_resumen_batallas;

-- View army statistics
SELECT * FROM vista_estadisticas_ejercitos;

-- View recent battles
SELECT * FROM informes ORDER BY fecha_creacion DESC LIMIT 10;
```

## Usage

### Main Menu Options

1. **Initialize Armies** - Set up USSR and AXIS forces with default bonifications
2. **Modify Attack Bonification** - Adjust army attack multipliers
3. **Modify Defense Bonification** - Adjust army defense multipliers
4. **Add Soldiers** - Populate armies with various unit types
5. **Start Battle** - Execute combat simulation
6. **View Battle Reports** - Display all historical battle results
7. **Exit** - Close application

### Combat Mechanics

**Damage Calculation:**
```
Attack Value = Weapon Power × Army Attack Bonus
Defense Value = Armor Power × Army Defense Bonus

If Attack > Defense: Defender is eliminated
Otherwise: Attacker is eliminated
```

**Default Bonifications:**
- **USSR**: Attack 0.95, Defense 1.95 (defensive strategy)
- **AXIS**: Attack 2.95, Defense 1.00 (offensive strategy)

### Unit Types

| Unit | Weapon | Attack | Armor | Defense |
|------|--------|--------|-------|---------|
| Infantry (Rifle + Vest) | Rifle | 15 | Vest | 30 |
| Infantry (Rifle + Trench) | Rifle | 15 | Trench | 50 |
| Tank Operator | Tank | 40 | Vest | 30 |
| Fighter Pilot | Aircraft | 50 | None | 10 |
| Artillery | Cannon | 25 | Trench | 50 |

## Project Structure

```
Stalingrado/
├── dev/src/                          # Source code
│   ├── AccesoDatos/                  # Data Access Layer
│   │   ├── connectDatabase.java      # DB connection (updated with modern practices)
│   │   └── Dao.java                  # Data Access Object
│   ├── Control/                      # Controller Layer
│   │   └── ControlBatalla.java       # Battle controller
│   ├── Interfaz/                     # Interfaces
│   │   ├── IAttack.java              # Attack strategy interface
│   │   └── IDefence.java             # Defense strategy interface
│   ├── Model/                        # Domain Models
│   │   ├── Ejercito.java             # Army (Thread-based)
│   │   ├── Soldado.java              # Soldier
│   │   ├── CampoBatalla.java         # Battlefield (Observable)
│   │   ├── InformeBatalla.java       # Battle Report DTO
│   │   ├── Fusil.java                # Rifle weapon
│   │   ├── Tanque.java               # Tank weapon
│   │   ├── AvionCombate.java         # Fighter aircraft
│   │   ├── Cañon.java                # Cannon
│   │   ├── Chaleco.java              # Vest armor
│   │   ├── Trinchera.java            # Trench defense
│   │   └── Correr.java               # Flee action
│   └── View/                         # View Layer
│       └── Main.java                 # Console UI
├── src/
│   ├── main/resources/               # Configuration files
│   │   ├── application.properties    # App configuration
│   │   └── logback.xml               # Logging configuration
│   └── test/java/                    # Unit tests (TODO)
├── logs/                             # Application logs
├── informesBatallas.sql              # Database schema (updated 2025)
├── pom.xml                           # Maven configuration
├── .gitignore                        # Git ignore rules
├── .env.example                      # Environment variables template
└── README.md                         # This file
```

## Design Patterns

### 1. Strategy Pattern
- **Interfaces**: `IAttack`, `IDefence`
- **Implementations**: Weapons (Fusil, Tanque, etc.) and Armor (Chaleco, Trinchera)
- **Purpose**: Flexible soldier equipment assignment

### 2. Observer Pattern
- **Observable**: `CampoBatalla` extends Observable
- **Observer**: `ControlBatalla` implements Observer
- **Purpose**: Real-time battle event notifications

### 3. Singleton Pattern
- **Class**: `connectDatabase`
- **Purpose**: Single database connection instance

### 4. Data Access Object (DAO)
- **Class**: `Dao` wrapper around `connectDatabase`
- **Purpose**: Abstract database operations from business logic

### 5. Template Method
- **Class**: `Ejercito` extends Thread
- **Purpose**: Thread execution template for concurrent battles

## Recent Updates (2025)

### Database Improvements
- Complete schema redesign with proper relationships
- Foreign key constraints for data integrity
- Indexes for query performance
- Statistical views for reporting
- UTF-8 character encoding support
- InnoDB engine for ACID compliance

### Code Modernization
- **Java 17** compatibility
- **Try-with-resources** for automatic resource management
- **Modern logging** with SLF4J + Logback (replacing java.util.logging)
- **Enhanced error handling** with proper exception messages
- **Configuration externalization** via properties files
- **Thread-safe singleton** with synchronized getInstance()

### Dependency Management
- **Maven** build system added
- **MySQL Connector/J 8.0.33** (upgraded from 5.1.18)
- **JUnit 5** testing framework
- **Logback** logging framework

### Security & Best Practices
- Removed hardcoded credentials (now configurable)
- Proper resource cleanup (no more resource leaks)
- SQL injection prevention with PreparedStatements
- Environment variable support via .env files
- Comprehensive .gitignore for sensitive data

### Developer Experience
- Comprehensive README documentation
- Build automation with Maven
- Executable JAR generation
- IDE-agnostic project structure
- Example configuration files

## Building from Source

```bash
# Clean previous builds
mvn clean

# Compile source code
mvn compile

# Run tests
mvn test

# Package as JAR
mvn package

# Install to local Maven repository
mvn install
```

## Troubleshooting

### MySQL Connection Issues

**Error**: `Communications link failure`
```bash
# Check MySQL is running
mysql -u root -p -e "SELECT 1"

# Verify connection settings in application.properties
```

**Error**: `Access denied for user`
```bash
# Grant privileges to database user
mysql -u root -p
GRANT ALL PRIVILEGES ON batallas.* TO 'your_user'@'localhost';
FLUSH PRIVILEGES;
```

### Maven Build Issues

**Error**: Dependencies not downloading
```bash
# Force update dependencies
mvn clean install -U

# Clear local repository cache
rm -rf ~/.m2/repository/com/mysql
mvn clean install
```

### IDE Errors (Logger not resolved)

**Solution**: Install Maven dependencies first
```bash
mvn clean install
```

Then refresh your IDE project (File > Reload Project in IntelliJ, or Maven > Update Project in Eclipse)

## Contributing

This is an educational project. Contributions are welcome:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

Educational project - check with original authors for licensing terms.

## Acknowledgments

- Original college project demonstrating OOP principles
- Updated in 2025 with modern Java practices and tooling
- Battle of Stalingrad historical context

---

**Version**: 2.0.0 (Updated 2025)
**Java Version**: 17+
**Database**: MySQL 8.0+