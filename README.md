# **📚 Library Management System - Java 17**

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge)
![Progress](https://img.shields.io/badge/Progress-Chapter%205%2F15-4CAF50?style=for-the-badge)

# Library Management System — Java SE 17 OCP Practice Project

A comprehensive, modular library management system developed as a hands-on project to master the **Oracle Certified
Professional Java SE 17 Developer (1Z0-829)** exam topics.  
Every chapter of the official study guide is reflected in the code, demonstrating real‑world usage of modern Java
features.

---

## 📚 Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [How to Run](#how-to-run)
    - [Using IntelliJ IDEA](#using-intellij-idea)
    - [From Command Line](#from-command-line)
- [Project Structure](#project-structure)
- [Implemented Features by Chapter](#implemented-features-by-chapter)
- [Key Concepts Demonstrated](#key-concepts-demonstrated)
- [Notes](#notes)
- [License](#license)

---

## Overview

This project simulates a real library with books, magazines, DVDs, reference books, members, and borrowing
transactions.  
It is intentionally built **without any frameworks** (Spring, Hibernate, etc.) to stay focused on core Java features and
the exam objectives.

The code is fully modular (Java Platform Module System), thread‑safe, and includes a complete JDBC integration with an
H2 in‑memory database.

---

## Prerequisites

- **Java SE 17 or higher** (tested with JDK 17, 21, and 25)
- **H2 Database JAR** (provided in the `lib/`
  folder – [download](https://repo1.maven.org/maven2/com/h2database/h2/2.2.224/h2-2.2.224.jar) if missing)
- **IntelliJ IDEA** (recommended) or any IDE with Java support

> **Note:** If you use JDK 25 (early access) and encounter module‑related issues, either switch to a stable LTS
> release (17/21) or temporarily remove `module-info.java` and run on the classpath.

---

## How to Run

### Using IntelliJ IDEA

1. Open the project in IntelliJ.
2. Ensure the H2 JAR is added as a library:
    - **File → Project Structure → Libraries** → `+` → **Java** → select `lib/h2-2.2.224.jar`
3. Run the `main.Main` class.

### From Command Line

#### Modular Execution (with `module-info.java`)
```bash
# Compile
javac -d out --module-source-path src -cp "lib/h2-2.2.224.jar" $(find src -name "*.java")

# Run
java --module-path out:lib/h2-2.2.224.jar -m library.management.system/main.Main
```

#### Non‑modular Execution (if you remove module-info.java)

# Compile

javac -cp "lib/h2-2.2.224.jar" -d out src/**/*.java

# Run (Windows)

java -cp "out;lib/h2-2.2.224.jar" main.Main

# Run (Linux/macOS)

java -cp "out:lib/h2-2.2.224.jar" main.Main

# Project Structure

library-management-system/
├── src/
│ ├── main/
│ │ └── Main.java # Entry point – demonstrates all chapters
│ ├── entities/ # Core domain classes
│ │ ├── items/ # Sealed hierarchy (LibraryItem, Book, Magazine, DVD, ReferenceBook, AudioBook)
│ │ ├── people/ # Member
│ │ └── transactions/ # BorrowRecord, Reservation
│ ├── enums/ # LibraryItemType, MemberStatus, MovieGenre, ItemStatus
│ ├── exceptions/ # Custom exceptions (BorrowException, ItemNotAvailableException, …)
│ ├── interfaces/ # LoanPolicy, ReservationPolicy, ReportGenerator
│ ├── services/ # BorrowingService, LocalizationService, LibraryTaskExecutor, …
│ ├── jdbc/ # DAO classes (ItemDAO, MemberDAO, BorrowRecordDAO) + DatabaseManager
│ ├── io/ # FileHandler, SerializationHandler, LibraryDataManager, FileWatcher
│ └── module-info.java # Module descriptor
├── lib/ # External JARs (H2 database driver)
├── resources/ # Resource bundles for localization (messages_*.properties)
├── library_data/ # Runtime folder for I/O operations (created automatically)
├── out/ # Compiled classes (generated)
├── .gitignore
├── LICENSE
└── README.md

## Implemented Features by Chapter

| Chapter | Topic                           | Implementation Highlights                                                                                                  |
|---------|---------------------------------|----------------------------------------------------------------------------------------------------------------------------|
| 1–6     | Basics, OOP, Class Design       | Sealed classes (`LibraryItem`), records (`LibraryStatistics`), enums with fields/methods, nested/inner classes             |
| 7       | Beyond Classes                  | Sealed hierarchy, `LibraryStatistics` record, enhanced enums (`MovieGenre` with age rating)                                |
| 8       | Lambdas & Functional Interfaces | Custom `Predicate<LibraryItem>`, `Consumer<LibraryItem>`, `Function<LibraryItem,R>`; method references                     |
| 9       | Collections & Generics          | Generic methods (`getItemsByType`, `findItemByTypeAndId`), `EnumMap`, `groupingBy`, `partitioningBy`, wildcards            |
| 10      | Streams                         | Extensive stream pipelines, parallel streams, `Collectors`, `DoubleSummaryStatistics`                                      |
| 11      | Exceptions & Localization       | Custom exception hierarchy, try‑with‑resources, multi‑catch, `LocalizationService` with EN/FA bundles                      |
| 12      | Modules                         | `module-info.java` with exports/requires/uses/provides, `ServiceLoader` demo                                               |
| 13      | Concurrency                     | Thread‑safe collections (`CopyOnWriteArrayList`), `ExecutorService`, `CompletableFuture`, `ReentrantLock`, atomic counters |
| 14      | I/O                             | File handling (`FileHandler`), object serialization (`SerializationHandler`), CSV export/import, NIO.2, file watching      |
| 15      | JDBC                            | H2 in‑memory database, DAO pattern, transactions, metadata, prepared statements                                            |

# Key Concepts Demonstrated

    Sealed Classes – LibraryItem permits only specific subclasses.

    Records – LibraryStatistics as an immutable data carrier.

    Enhanced Enums – LibraryItemType with loan information, MovieGenre with age‑appropriateness logic.

    Functional Interfaces – Using Predicate, Consumer, Function instead of custom ones.

    Stream API – Filtering, mapping, grouping, partitioning, parallel processing.

    Generics – Type‑safe methods, wildcards (? extends, ? super).

    Concurrency – Thread safety, CompletableFuture, ExecutorService, locks, atomic variables.

    Exception Handling – Custom checked/unchecked exceptions, multi‑catch, suppressed exceptions.

    Localization – Resource bundles for English and Persian.

    I/O (NIO.2) – Path, Files, BufferedReader, WatchService.

    JDBC – Connection management, DAO, transactions, metadata.

    Modules (JPMS) – Explicit module descriptors, service loading.

# Notes

    The project is learning‑oriented and deliberately avoids external frameworks.

    All code is compatible with Java SE 17 and covers the exam objectives.

    H2 is used as an in‑memory database – no installation required.

    File I/O writes to the library_data/ folder (created automatically).

    If you encounter module path issues with JDK 25, use a stable JDK 17/21 or run in non‑modular mode.

# License

This project is for educational purposes only. Feel free to use, modify, and learn from it.

# Happy Coding!

A complete Java SE 17 OCP practice project – from the basics to the database.