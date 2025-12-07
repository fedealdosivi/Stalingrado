# Changelog

All notable changes to the Stalingrado Battle Simulator project are documented in this file.

## [2.0.0] - 2025-12-07

### Added
- **Maven Build System**: Complete Maven project structure with pom.xml
- **Modern Dependencies**: Updated to latest stable versions
  - MySQL Connector/J 8.0.33 (from 5.1.18)
  - SLF4J 2.0.9 + Logback 1.4.14 for logging
  - JUnit 5.10.1 for testing
- **Configuration Management**: Externalized configuration via application.properties
- **Enhanced Database Schema**:
  - 5 normalized tables with foreign key constraints
  - 2 statistical views for reporting
  - Proper indexes for query performance
  - UTF-8 character encoding
  - InnoDB engine for ACID compliance
- **Unit Tests**: JUnit 5 tests for Soldado and Ejercito classes
- **Comprehensive Documentation**:
  - Detailed README with installation and usage instructions
  - Troubleshooting guide
  - Architecture diagrams
  - API documentation
- **Development Tools**:
  - .gitignore for Java/Maven projects
  - .env.example for environment configuration
  - run.sh quick start script
  - Logback configuration for structured logging

### Changed
- **Java Version**: Upgraded to Java 17 (from Java 7/8)
- **Thread Safety**:
  - Replaced ArrayList with Collections.synchronizedList
  - Added volatile keywords for shared variables
  - Implemented AtomicInteger for thread-safe ID generation
  - Switched from Random to ThreadLocalRandom
  - Added proper synchronization blocks
- **Resource Management**:
  - Implemented try-with-resources for automatic cleanup
  - Fixed resource leaks in database connections
  - Proper ResultSet, Statement, and Connection closing
- **Error Handling**:
  - Replaced printStackTrace() with proper logging
  - Added meaningful error messages
  - Implemented graceful exception handling
- **Database Connection**:
  - Removed hardcoded credentials
  - Added configuration file support
  - Improved connection pooling readiness
  - Updated JDBC URL with modern parameters

### Security
- **Credentials Management**: Removed hardcoded database passwords
- **SQL Injection Prevention**: Already using PreparedStatements (retained)
- **Environment Variables**: Support for .env file configuration
- **Sensitive Data**: Added .gitignore rules to prevent credential commits

### Fixed
- **Database Schema**: Removed DROP TABLE statement that destroyed data
- **Logger Issues**: Migrated from java.util.logging to SLF4J
- **Thread Safety**: Fixed race conditions in concurrent army operations
- **Resource Leaks**: Ensured all database resources are properly closed
- **Deprecated API**: Updated deprecated JDBC driver class name

### Deprecated
- Old MySQL connector jar in dev/driver/ (replaced by Maven dependency)

### Documentation
- Complete rewrite of README.md with modern formatting
- Added code examples and usage instructions
- Included troubleshooting section
- Documented all design patterns
- Added database schema documentation

## [1.0.0] - Original College Project

### Initial Features
- Turn-based battle simulation system
- Observer pattern for battle notifications
- Strategy pattern for weapons and equipment
- Multi-threaded army execution
- MySQL database integration
- Console-based user interface
- Support for USSR and AXIS armies
- Multiple unit types (infantry, tanks, aircraft, artillery)
- Configurable attack and defense bonifications

---

## Migration Guide (1.0 → 2.0)

### For Developers

**Before (1.0.0)**:
```bash
# Add mysql-connector-java-5.1.18-bin.jar to classpath manually
javac -cp .:mysql-connector-java-5.1.18-bin.jar dev/src/**/*.java
java -cp .:mysql-connector-java-5.1.18-bin.jar View.Main
```

**After (2.0.0)**:
```bash
# Dependencies managed by Maven
mvn clean install
mvn exec:java
# or
./run.sh
```

### Database Migration

The new schema is backward compatible with the old `informes` table. To migrate:

```sql
-- Backup existing data
CREATE TABLE informes_backup AS SELECT * FROM informes;

-- Run the new schema
source informesBatallas.sql

-- Data will be preserved through CREATE TABLE IF NOT EXISTS
```

### Configuration Migration

**Before**: Credentials in source code
```java
connection = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/batallas", "root", "123456"
);
```

**After**: Configuration file
```properties
# src/main/resources/application.properties
db.url=jdbc:mysql://localhost:3306/batallas
db.username=root
db.password=your_password
```

---

**Version Format**: [Major.Minor.Patch]
- Major: Breaking changes
- Minor: New features (backward compatible)
- Patch: Bug fixes
