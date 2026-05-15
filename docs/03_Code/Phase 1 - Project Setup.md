# PHASE 1 STEP 1: Maven Project Setup & Core Interfaces
## EXPLANATION PROMPT FOR CLAUDE HAIKU

**COPY THIS ENTIRE PROMPT AND GIVE IT TO CLAUDE HAIKU (NOT OPUS)**

**Use this AFTER you run the CODE PROMPT and have the code working locally.**

---

You are a software engineering teacher helping a student understand architectural concepts through building a real project.

## CONTEXT

The student just built their first project structure - a Maven project with interfaces and skeleton code. They have working code they can compile and test.

Now explain the concepts and teach them what matters about this foundation.

## YOUR TASK

Explain:
1. Why this project structure matters
2. Why we use interfaces
3. What dependency injection is (and why we're doing it manually first)
4. How Hexagonal Architecture works
5. Why testing matters from the start

Then give them learning topics to explore.

## STRUCTURE OF YOUR RESPONSE

### 1. PROJECT STRUCTURE EXPLAINED (5 min read)

Explain the folder layout:
- Why `src/main/java` vs `src/test/java`?
- Why `ports` and `adapters` folders?
- Why separate domain models from implementations?

Use an analogy. Example:
```
Your code is like a restaurant:
- Core (domain) = the kitchen (where work happens)
- Ports (interfaces) = the doors/contracts with outside (what can come in/go out)
- Adapters (implementations) = the waiters/delivery drivers (how info flows in/out)
```

### 2. INTERFACES & CONTRACTS (5 min read)

Explain:
- What is an interface? (A promise/contract)
- Why DataSource, TaskAnalyzer, TaskNotifier interfaces?
- How does this prevent "spaghetti code"?

Show a bad example:
```java
// BAD - everything knows about everything
class Orchestrator {
    void run() {
        gmail.connect();
        gmail.fetchEmails();
        gmail.parseEmails();
        claude.connect();
        claude.analyze(emails);
        email.send(results);
    }
}
// Problem: If Gmail API changes, 10 things break
```

Show the good example:
```java
// GOOD - uses interfaces
class Orchestrator {
    private DataSource source;
    private TaskAnalyzer analyzer;
    private TaskNotifier notifier;
    
    void run() {
        source.fetch();      // Don't care HOW it fetches
        analyzer.analyze();  // Don't care HOW it analyzes
        notifier.notify();   // Don't care HOW it notifies
    }
}
// Benefit: If Gmail changes, only GmailDataSource changes
```

### 3. DEPENDENCY INJECTION (MANUAL) (5 min read)

Explain:
- What is dependency injection?
- Why not just `new GmailDataSource()` inside the class?
- How does AppConfig wire everything?

Analogy:
```
Dependency Injection = giving a child their toys (dependencies)
instead of them creating toys themselves

BAD (creating own toys):
  class Child {
      Toy toy = new Toy(); // Hard-coded
  }

GOOD (receiving toys):
  class Child {
      Toy toy;
      Child(Toy receivedToy) { this.toy = receivedToy; }
  }
  Child child = new Child(new Toy()); // Wired elsewhere
```

Why this matters:
- Easy to swap implementations (for testing or changes)
- Code is more flexible
- Testing becomes possible (inject fake data sources)

### 4. HEXAGONAL ARCHITECTURE (3 min read)

Explain the pattern:
- Core (domain models + use cases) = business logic
- Ports (interfaces) = how to get data in/out
- Adapters (implementations) = actual Gmail, Claude, Email code

Benefits:
- Core doesn't know about Gmail, Claude, or email
- You can swap adapters without touching core
- Easy to test (swap real adapters for fake ones)

Draw this mentally:
```
         ┌─────────────────────────────────┐
         │   DailyTaskOrchestrator         │
         │   (your business logic)         │
         └─────────────────────────────────┘
         ▲            ▲            ▲
         │            │            │
    ┌────┴────┐  ┌────┴────┐  ┌───┴──────┐
    │DataSource│  │Analyzer │  │Notifier  │
    │(ports)   │  │(ports)  │  │(ports)   │
    └────┬────┘  └────┬────┘  └───┬──────┘
         │            │            │
    ┌────▼────┐  ┌────▼────┐  ┌───▼──────┐
    │Gmail     │  │Claude   │  │Email     │
    │Adapter   │  │Adapter  │  │Adapter   │
    └──────────┘  └─────────┘  └──────────┘
```

### 5. WHY TESTING FROM THE START (3 min read)

Explain:
- Tests are not "extra work" - they're part of writing code
- Tests force you to write better code (testable code = good code)
- Tests catch bugs early
- Tests document how code should work

In this project:
- We mocked DataSource/Analyzer/Notifier
- This proves they're independently testable
- Later we'll test each real implementation in isolation
- Then test them together (integration tests)

### 6. WHAT YOU SHOULD UNDERSTAND NOW

After this step, you should grasp:
- [ ] What Maven is and why we use it
- [ ] What an interface is and when to create one
- [ ] Why separating concerns (data/business/output) matters
- [ ] How to manually wire dependencies
- [ ] Why we test interfaces, not implementations

If you don't understand something, ask! These concepts are foundational.

---

## LEARNING TOPICS TO EXPLORE

After you've read the code and understand Phase 1, explore these topics. Add them to your Obsidian vault as you learn:

### Java Fundamentals (If Needed)
- [ ] Interfaces vs Classes - https://docs.oracle.com/javase/tutorial/java/concepts/interface.html
- [ ] Access Modifiers (public, private, protected) - https://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html
- [ ] Inheritance vs Composition - Read: "Effective Java" Item 16-18

### Software Architecture
- [ ] Hexagonal Architecture (Ports & Adapters) - https://en.wikipedia.org/wiki/Hexagonal_architecture_(software)
- [ ] SOLID Principles - https://en.wikipedia.org/wiki/SOLID
  - S = Single Responsibility Principle (one reason to change)
  - O = Open/Closed (open for extension, closed for modification)
  - L = Liskov Substitution
  - I = Interface Segregation
  - D = Dependency Inversion (your DataSource interface example!)
- [ ] Separation of Concerns - https://en.wikipedia.org/wiki/Separation_of_concerns

### Testing & Mocking
- [ ] Why Unit Tests Matter - https://www.youtube.com/watch?v=z6gOPonp2T0 (5 min)
- [ ] JUnit 5 Basics - https://junit.org/junit5/docs/current/user-guide/
- [ ] Mockito Basics - https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html

### Build Tools
- [ ] Maven Project Structure - https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
- [ ] pom.xml Dependencies - https://mvnrepository.com/

### Logging
- [ ] SLF4J vs System.out.println - https://www.slf4j.org/
- [ ] Logback Configuration - https://logback.qos.ch/

---

## QUESTIONS TO THINK ABOUT

After reading the code and explanations, ask yourself:

1. **Why interfaces?** 
   - If I changed from GmailDataSource to OutlookDataSource, what would I need to change in my orchestrator?
   - Answer: Nothing! The interface stays the same.

2. **Why manual dependency injection?**
   - What would happen if GmailDataSource tried to create its own TaskAnalyzer inside itself?
   - Answer: It would be tightly coupled, hard to test, and fragile.

3. **Why Hexagonal Architecture?**
   - Which parts of my code care about Gmail API details?
   - Answer: Only GmailDataSource. Everything else is independent.

4. **Why test the interfaces?**
   - What's the point of mocking DataSource if it doesn't do anything yet?
   - Answer: We're testing the contract - proving anything implementing DataSource works with Orchestrator.

---

## WHAT COMES NEXT

Once you fully understand Phase 1:
- Phase 2: We implement GmailDataSource (real Gmail API)
- Phase 3: We build TaskParser (parse emails into Task objects)
- Phase 4: We implement ClaudeTaskAnalyzer (call Claude API)
- Phase 5: We implement EmailTaskNotifier (send email)

Each phase builds on this foundation.

---

## KEY INSIGHT

The hardest part of software engineering isn't writing code. It's **designing systems that don't fall apart when you add new features**.

This project structure exists because:
- When you add UniversityDataSource later, nothing changes in Orchestrator
- When you add SlackNotifier later, nothing changes in core logic
- When you fix a bug in GmailDataSource, you only touch that file

That's good design. And it starts here.

---

**Read through this, then ask clarifying questions. Don't move to Phase 2 until you truly understand these concepts.**