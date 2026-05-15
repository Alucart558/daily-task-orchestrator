# PHASE 3: Data Parsing & Normalization
## EXPLANATION PROMPT FOR CLAUDE HAIKU

**COPY THIS ENTIRE PROMPT AND GIVE IT TO CLAUDE HAIKU**

**Use this AFTER Phase 3 code is complete and tests pass.**

---

You are a software teacher explaining domain modeling and data transformation at scale.

## CONTEXT

Student has implemented Task domain model, normalization, and parsing. Now explain the concepts.

## YOUR TASK

Explain:
1. What domain models are
2. Why we normalize data
3. How parsing unstructured data works
4. Why multiple data sources need different handling
5. The danger of tight coupling to API models
6. Testing complex transformations

## STRUCTURE

### 1. DOMAIN MODELS (5 min read)

A domain model represents "what matters to our business":

```
Gmail API gives us:
  - messageId
  - threadId
  - labels
  - historyId
  - snippet
  - internalDate
  - mimeType
  - ... 50 more fields

But WE ONLY CARE ABOUT:
  - Who (from)
  - What (title, description)
  - When (deadline)
  - How urgent (priority)
  - Status (done or not)

So we create Task model with ONLY fields we need.

Benefits:
  - Simpler to work with
  - Doesn't change if Gmail API changes
  - Forces us to think about what matters
  - Domain experts (professors, students) understand Task
    but don't understand Gmail API Message
```

The Task model is "ubiquitous language" - what business talks about.

### 2. NORMALIZATION PROBLEM (4 min read)

Real problem we're solving:

```
Gmail email about assignment:
  From: prof@university.edu
  Subject: "Project 3 Due Friday"
  Body: "Due Friday, 11:59 PM..."

University Portal assignment:
  Title: "Project 3"
  Description: "Details here..."
  Due Date: 2025-05-25 23:59

Professor's Website:
  "Assignment: Project 3"
  "Deadline: May 25, 2025, 11:59 PM"

THE PROBLEM:
  Three different formats
  Three different data structures
  Three different quality levels
```

Without normalization:
```
if (source == "Gmail") {
  deadline = parseEmailDate(body);
  title = subject;
}
if (source == "University") {
  deadline = apiResponse.dueDate;
  title = apiResponse.title;
}
if (source == "Professor") {
  deadline = scrapeWebsite(html);
  title = extractFromWebsite(html);
}
// 3 * 10 = 30 code paths for 10 features
// Nightmare to test and maintain
```

With normalization (our approach):
```
RawTask rawTask = fetch(source);  // Any source
Task task = normalizer.normalize(rawTask);  // Single path
// One code path for 10 features
// Easy to test and maintain
```

The pattern:
- **Source API** → source-specific data
- **RawTask** → generic "raw from somewhere" format
- **Task** → normalized domain model

### 3. PARSING UNSTRUCTURED DATA (4 min read)

Emails are chaos. They're free-form text:

```
Good case:
  "Project 3 Due Friday"
  → deadline: next Friday

Hard cases:
  "Project 3 due Friday 2pm in room 123"
  → deadline: next Friday 2 PM
  → location: room 123 (we ignore for now)

  "Due in 1 week"
  → deadline: 1 week from now

  "Final exam: Friday May 25, 2-4 PM"
  → deadline: May 25 at 2 PM
  → also: exam is 2 hours long

  "Due ASAP"
  → deadline: today (risky assumption!)

  No deadline mentioned at all
  → deadline: null
```

Our DeadlineParser:
- Tries multiple regex patterns
- Tries natural language parsing
- Falls back to null if unsure
- Logs what it found
- Never crashes on weird input

Why this is hard:
- No standard format
- Ambiguous (1/5 = January 5 or May 1?)
- Multiple possible patterns
- Localization (Friday vs Viernes)

Why we handle it:
- Without deadline, can't prioritize
- 80% of emails have deadline (good enough)
- 20% without deadline get default (OK)

### 4. THE NORMALIZATION PIPELINE (4 min read)

Visualize the data transformation:

```
┌──────────────────────────────────────────┐
│         Multiple Sources                 │
│  Gmail  University  Professor  Email     │
└────┬──────────┬──────────┬────────┘
     │          │          │
     ▼          ▼          ▼
┌─────────────────────────────────┐
│  GmailDataSource                │
│  UniversityDataSource (Phase 7) │
│  Extract to RawTask objects     │
└────┬──────────────────────────┘
     │ List<RawTask>
     ▼
┌──────────────────────────┐
│  SimpleTaskNormalizer    │
│  Parse titles, deadlines │
│  Extract what we can     │
└────┬────────────────────┘
     │ List<Task>
     ▼
┌──────────────────────────┐
│  TaskAnalyzer (Phase 4)  │
│  Use Claude for priority │
│  Schedule generation     │
└────┬────────────────────┘
     │ AnalyzedTasks
     ▼
┌──────────────────────────┐
│  Email Formatter (Phase 5)
│  Generate daily report   │
└──────────────────────────┘
```

Each step:
- Takes previous output
- Adds value (parsing, analyzing, formatting)
- Passes to next step
- Can be tested independently

### 5. WHEN PARSING FAILS (3 min read)

Real data is messy. What if parsing fails?

```
Strategy 1: Crash (bad)
  Missing deadline → throw exception
  User sees error
  Task is lost

Strategy 2: Ignore (bad)
  Missing deadline → skip task
  User loses task without knowing
  Silent failure

Strategy 3: Graceful degradation (good)
  Missing deadline → set to null
  Log warning: "Couldn't parse deadline from 'Project 3'"
  Task still created
  User sees task but knows deadline is missing
  Can manually add deadline later
```

Our code uses Strategy 3. Why?
- Better than crashing (user isn't blocked)
- Better than silent failure (we log it)
- User sees incomplete data is better than no data

The lesson: In data processing, "something" is usually better than "nothing".

### 6. TESTING NORMALIZATION (3 min read)

Hard to test because lots of edge cases:

```
Test cases for DeadlineParser:
  - "Due Friday" → next Friday
  - "Due May 25" → May 25 this year
  - "Due 5/25" → May 25
  - "Due in 3 days" → today + 3 days
  - "Due ASAP" → today
  - No deadline mentioned → null
  - "Due 25/5" (European format) → ???
  - "Due last Friday" (past) → handle gracefully
  - "DUE FRIDAY" (all caps) → still works
  - Empty string → null
  - Null input → handle gracefully

That's 10+ test cases for ONE parser.
Multiply by other parsers... lots of tests!
```

We use:
- Parametrized tests (one test, multiple inputs)
- Clear test names (testParsesRelativeDates, testHandlesEmptyInput)
- TestDataBuilder (create test tasks easily)

### 7. FAILURE MODES (3 min read)

What can go wrong in normalization?

```
❌ Deadline parser always returns today
  → All tasks look urgent
  → Prioritization is wrong

❌ Title extractor returns entire email body
  → Title is 10,000 characters
  → Email looks weird

❌ Normalizer crashes on missing fields
  → One bad email breaks entire batch
  → Data loss

❌ Normalizer creates duplicate IDs
  → Same task appears twice
  → Confusion

✅ Deadline parser returns null sometimes
  → Task has no deadline, that's OK
  → Handled explicitly

✅ Title extractor limits length
  → "Project 3 Due Friday..." (truncated)
  → Reasonable fallback

✅ Normalizer validates before creating Task
  → Invalid data rejected upfront
  → Clear error message

✅ Normalizer uses UUID for IDs
  → Guaranteed unique
  → No collisions
```

Our design handles these.

### 8. LESSONS LEARNED (2 min read)

By normalizing data, you learned:

1. **Domain modeling** - what matters to business
2. **Data pipeline patterns** - multi-step transformation
3. **Parsing complexity** - unstructured → structured is hard
4. **Graceful degradation** - handle messy data
5. **Factory pattern** - object creation abstraction
6. **Testing edge cases** - lots of test cases needed
7. **Logging strategy** - make failures visible
8. **Abstraction benefits** - separate concerns, reuse

---

## LEARNING TOPICS

### Domain-Driven Design
- [ ] Ubiquitous language (business terms)
- [ ] Bounded contexts (Task vs Email vs Payment)
- [ ] Domain model vs API model
- [ ] Value objects vs Entities
- [ ] Aggregates (group of entities)

### Data Pipeline
- [ ] ETL (Extract, Transform, Load)
- [ ] Data quality (valid vs invalid)
- [ ] Idempotency (same input = same output)
- [ ] Statelessness (no hidden state between calls)

### Parsing Techniques
- [ ] Regular expressions (regex)
- [ ] Natural language processing basics
- [ ] Date/time handling across timezones
- [ ] Character encoding
- [ ] Validation (check before trusting data)

### Factory Pattern
- [ ] Factory method pattern
- [ ] Abstract factory
- [ ] When to use vs constructors
- [ ] Benefits (testability, flexibility)

### Testing Complex Code
- [ ] Parametrized tests
- [ ] Property-based testing (same logic, many inputs)
- [ ] Edge case identification
- [ ] Test coverage metrics

### Java Specifics
- [ ] LocalDateTime, LocalDate APIs
- [ ] Stream API (map, filter for transformations)
- [ ] Optional (handling missing data)
- [ ] Enums (Priority, Status types)
- [ ] Value classes (immutability)

---

## QUESTIONS TO THINK ABOUT

1. **Why not just parse deadline in Phase 2?**
   - Answer: Separate concerns. Phase 2 is about fetching. Phase 3 is about parsing.

2. **What if I don't know what data sources we'll have?**
   - Answer: RawTask is generic enough for any source. Add new source in Phase 7.

3. **Should Task model have source-specific fields?**
   - Answer: No. Task is domain model. Source info in originalId only.

4. **What if parsing always fails (no deadlines)?**
   - Answer: Task is created without deadline (null). Analyzer handles it.

5. **Why use enums for Priority/Status?**
   - Answer: Type safety. Can't accidentally set priority="URGENT". Only CRITICAL, HIGH, MEDIUM, LOW allowed.

---

## WHAT COMES NEXT

Phase 4: Claude API Integration

We have normalized tasks. Now:
- Send to Claude AI with prompt
- Ask Claude to prioritize
- Get schedule back
- Create AnalyzedTasks

Claude will use the Task model to understand what matters.

---

## KEY INSIGHT

Most of a real system is data transformation. Fetching data is easy (Phase 2: 2 weeks). Normalizing it is hard (Phase 3: 2 weeks). Getting it right determines if the system is useful.

Perfect parsing is impossible. Good-enough parsing with visible failures is achievable.

---

**Read this, explore learning topics, ask questions.**