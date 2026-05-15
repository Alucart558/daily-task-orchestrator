# Daily Task Orchestrator - Project Overview

## What Are We Building?

An automated system that:
1. **Fetches** assignments from Gmail, university portal, professor websites
2. **Analyzes** them with Claude AI to prioritize and schedule
3. **Reports** via daily email what you need to do and when

**Status:** In Development
**Duration:** 10-12 weeks
**Time Commitment:** 5-10 hours/week

## Why This Project?

- Learn real software engineering (not toy projects)
- Build something useful (actual daily use)
- Understand system design patterns
- Practice clean code principles
- Deploy to production (AWS Lambda)
- Use AI APIs (Claude)

## Key Features

- ✅ Fetch from multiple data sources
- ✅ Normalize disparate data formats
- ✅ Prioritize with AI
- ✅ Send formatted daily email
- ✅ Run automatically at 9 AM
- ✅ Track progress over time

## Technology Stack

- **Language:** Java 17+
- **Build:** Maven
- **Testing:** JUnit 5 + Mockito
- **Logging:** SLF4J + Logback
- **APIs:** Gmail, Claude, University Portal
- **Deployment:** AWS Lambda + CloudWatch

## Success Criteria

By week 12, you'll have:
- [ ] A working Maven project with clean architecture
- [ ] Gmail integration (fetch and parse emails)
- [ ] Claude API integration (AI analysis)
- [ ] Daily email reports
- [ ] Running on AWS Lambda (automated 9 AM trigger)
- [ ] University portal integration (optional but likely)
- [ ] 50+ unit tests
- [ ] Complete documentation

## Who This Is For

- Java beginners (have built small programs before)
- Want to learn software engineering
- Can commit 5-10 hours/week
- Comfortable with some frustration (debugging is part of learning)