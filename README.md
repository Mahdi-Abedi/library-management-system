# **📚 Library Management System - Java 17**

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge)
![Progress](https://img.shields.io/badge/Progress-Chapter%205%2F15-4CAF50?style=for-the-badge)

A comprehensive **Library Management System** built with **Java 17** as part of the OCP Java SE 17 certification study. This project demonstrates modern Java features, clean architecture, and professional software development practices.

## **🎯 Project Overview**

This system provides complete library management capabilities including book cataloging, member management, borrowing/returning operations, and reporting. Built following the **OCP Java SE 17 Developer Study Guide** structure, with each chapter adding new features and improvements.

## **🚀 Features**

### **📖 Book Management**
- Add, update, remove books with ISBN validation
- Search by title, author, ISBN, or keywords
- Track book status (Available, Borrowed, Reserved)
- Manage book details (publication year, page count)

### **👥 Member Management**
- Register and manage library members
- Member status tracking (Active, Suspended, Inactive)
- Membership dates and contact information
- Borrowing history

### **🔄 Borrowing System**
- Complete borrow/return transactions
- Automated due date calculation
- Overdue detection and fine calculation
- Multiple borrowing period options
- Batch operations for multiple books

### **📊 Reporting & Analytics**
- Detailed library statistics
- Overdue books tracking
- Available books listing
- Custom report generation

## **🏗️ Architecture**

### **Package Structure**
```
src/
├── entities/               # Core domain entities
│   ├── Book.java           # Book entity with ISBN-based equality
│   ├── Member.java         # Library member with status tracking
│   ├── Library.java        # Main library management class
│   ├── BorrowRecord.java   # Track borrow/return transactions
│   └── LibraryStatistics.java # Data class for library stats
├── enums/                  # Enumeration types
│   ├── BookStatus.java     # AVAILABLE, BORROWED, RESERVED
│   └── MemberStatus.java   # ACTIVE, SUSPENDED, INACTIVE
└── Main.java              # Application entry point
```

### **Core Classes**

#### **📘 Book Entity**
- Immutable ISBN (setter removed after creation)
- Automatic status management
- Proper `equals()` and `hashCode()` based on ISBN
- Formatted `toString()` with StringBuilder

#### **👤 Member Entity**
- Final ID for consistency
- Default membership date and status
- Contact information management
- Builder pattern support

#### **🏛️ Library Class**
- Thread-safe collection management
- Advanced search capabilities
- Batch operations support
- Comprehensive reporting

#### **📝 BorrowRecord**
- Date calculations with DateTime API
- Overdue detection and fine calculation
- Configurable borrowing periods
- Formatted transaction history

## **💻 Technical Highlights**

### **Java 17 Features Used**
- **Records**: `LibraryStatistics` for data carrier classes
- **Pattern Matching**: Future implementation planned
- **Sealed Classes**: Architecture ready for implementation
- **Text Blocks**: Readable string formatting

### **Modern Java APIs**
- **DateTime API**: Precise date calculations with `LocalDate`, `ChronoUnit`
- **Optional**: Null-safe returns replacing traditional null checks
- **Stream API**: Functional data processing and filtering
- **StringBuilder**: Efficient string concatenation

### **Design Patterns**
- **Builder Pattern**: For complex object creation
- **Factory Pattern**: Planned for object creation
- **Strategy Pattern**: For fine calculation algorithms
- **Observer Pattern**: For event notifications (planned)

## **📚 Learning Journey**

Following **OCP Java SE 17 Developer Study Guide**:

### **✅ Completed Chapters**
| Chapter | Topic | Status | Key Implementation |
|---------|-------|--------|-------------------|
| 1-3 | Building Blocks, Operators, Decisions | ✅ Complete | Core entities, basic operations |
| 4 | Core APIs | ✅ Complete | DateTime API, StringBuilder, Collections |
| 5 | Methods | ✅ Complete | Method overloading, Optional, Varargs |

### **🔜 Upcoming Chapters**
| Chapter | Topic | Status | Planned Features |
|---------|-------|--------|-----------------|
| 6 | Class Design | ⏳ Next | Inheritance, Interfaces, Abstract Classes |
| 7 | Beyond Classes | ⏳ Planned | Records, Sealed Classes, Enums |
| 8 | Lambdas | ⏳ Planned | Functional programming |
| 9-10 | Collections & Streams | ⏳ Planned | Advanced data processing |
| 11-15 | Advanced Topics | ⏳ Planned | Concurrency, JDBC, Modules |

## **🚀 Getting Started**

### **Prerequisites**
- Java 17 or higher
- Git (for version control)
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### **Installation**
```bash
# Clone the repository
git clone https://github.com/yourusername/library-management-system.git

# Navigate to project directory
cd library-management-system

# Compile the project
javac -d out src/**/*.java

# Run the application
java -cp out Main
```

### **Using Maven**
```xml
<!-- pom.xml -->
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.library</groupId>
    <artifactId>library-system</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
</project>
```

## **📖 Usage Examples**

### **Basic Operations**
```java
// Create library
Library library = new Library();

// Add books
library.addBook(new Book("978-0134685991", "Effective Java", "Joshua Bloch"));

// Register member
Member member = new Member(101, "John Doe", "john@example.com");
library.addMember(member);

// Borrow book
BorrowRecord record = library.borrowBook("978-0134685991", member);

// Generate report
System.out.println(library.generateLibraryReport());
```

### **Advanced Features**
```java
// Batch operations
library.borrowMultipleBooks(member, "ISBN1", "ISBN2", "ISBN3");

// Custom borrow period
library.borrowBook("978-0134685991", member, 21); // 21 days

// Optional for safe operations
Optional<Book> book = library.findBookByIsbnOptional("978-0134685991");
book.ifPresentOrElse(
    b -> System.out.println("Found: " + b.getTitle()),
    () -> System.out.println("Book not found")
);
```

## **🧪 Testing**

### **Running Tests**
```bash
# JUnit tests (planned)
mvn test

# Specific test class
mvn test -Dtest=LibraryTest
```

### **Test Coverage**
- Unit tests for all entities
- Integration tests for library operations
- Edge case testing for date calculations
- Performance testing for large datasets

## **🤝 Contributing**

This is a learning project following the OCP certification path. Contributions are welcome as educational examples:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes following conventional commits
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### **Commit Convention**
```
feat: add new feature
fix: bug correction
docs: documentation changes
refactor: code restructuring
test: adding tests
chore: maintenance tasks
```

## **📈 Project Roadmap**

### **Phase 1: Core Implementation** ✅
- Basic CRUD operations
- Entity relationships
- Basic validation

### **Phase 2: Advanced Features** 🔄
- Database integration (JDBC)
- Web interface (Spring Boot)
- Authentication system
- Advanced reporting

### **Phase 3: Production Ready** ⏳
- Performance optimization
- Security enhancements
- Docker deployment
- CI/CD pipeline

## **📄 License**

Copyright 2024 Your Name

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## **🙏 Acknowledgments**

- **Oracle** for the OCP Java SE 17 certification program
- **Scott Selikoff & Jeanne Boyarsky** for the study guide
- Java community for best practices and patterns
- All contributors who help improve this learning project

## **📬 Contact**

Project Link: [https://github.com/yourusername/library-management-system](https://github.com/yourusername/library-management-system)

---

**⭐ If you find this project helpful, please give it a star!**

---
*Built with ❤️ as part of the OCP Java SE 17 certification journey*
