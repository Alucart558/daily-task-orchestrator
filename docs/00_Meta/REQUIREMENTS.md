# Requirements - Daily Task Orchestrator

## Functional Requirements

### FR1: Data Collection
- [ ] Fetch emails from Gmail
- [ ] Fetch assignments from university portal
- [ ] Fetch tasks from professor websites
- [ ] Handle authentication securely

### FR2: Data Processing
- [ ] Normalize data into Task objects
- [ ] Extract deadline dates
- [ ] Extract assignment titles and descriptions
- [ ] Handle missing/malformed data gracefully

### FR3: AI Analysis
- [ ] Call Claude API with tasks
- [ ] Get AI-generated priorities
- [ ] Get AI-generated schedule
- [ ] Get AI-generated summary

### FR4: Notification
- [ ] Generate formatted email
- [ ] Include priority order
- [ ] Include suggested schedule
- [ ] Send via Gmail

### FR5: Automation
- [ ] Trigger daily at 9 AM
- [ ] Run on AWS Lambda
- [ ] Handle errors gracefully
- [ ] Log execution for debugging

## Non-Functional Requirements

### NFR1: Code Quality
- Clean, readable code
- Proper separation of concerns
- No hardcoded values
- Comprehensive error handling

### NFR2: Architecture
- Hexagonal architecture (ports & adapters)
- Dependency injection
- Interface-based design
- No circular dependencies

### NFR3: Testing
- Unit tests for all classes
- Integration tests between layers
- Mock external dependencies
- >80% code coverage

### NFR4: Security
- Store secrets in AWS Secrets Manager
- No credentials in code
- Secure OAuth2 flows
- Validate all external data

### NFR5: Performance
- Complete execution <1 minute
- Minimal API calls
- Efficient parsing
- No unnecessary database queries

### NFR6: Maintainability
- Clear, up-to-date documentation
- Commented code for complex logic
- Proper logging
- Easy to debug

## Out of Scope (For Now)

- Web dashboard / UI
- Mobile app
- Database persistence (beyond in-memory)
- Multi-user support
- Analytics/reporting