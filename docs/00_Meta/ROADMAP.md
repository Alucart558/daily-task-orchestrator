## Roadmap - Daily Task Orchestrator

## Timeline

| Phase | Name | Duration | Focus |
|-------|------|----------|-------|
| 1 | Project Setup | 1-2 weeks | Architecture, Maven, interfaces |
| 2 | Gmail Integration | 2-3 weeks | APIs, OAuth2, email fetching |
| 3 | Data Parsing | 2-3 weeks | Data models, parsing, normalization |
| 4 | Claude Integration | 2-3 weeks | LLM API, prompt engineering, analysis |
| 5 | Email Output | 1-2 weeks | Email formatting, sending |
| 6 | AWS Deployment | 1 week | Lambda, Secrets Manager, CloudWatch |
| 7 | University Portal | 2+ weeks | Web scraping, API exploration |
| **TOTAL** | **Full System** | **11-16 weeks** | **Production-ready** |

## Phase Breakdown

### Phase 1: Project Setup (Weeks 1-2)
**Goal:** Foundation & Architecture

- [x] Create Maven project
- [x] Design interfaces (DataSource, Analyzer, Notifier)
- [x] Setup manual DI
- [x] Create base test structure
- [x] Write documentation

**Learning:** Architecture, Maven, testing, interfaces

**Deliverable:** Compilable, testable project with no external dependencies

---

### Phase 2: Gmail Integration (Weeks 3-4)
**Goal:** Fetch emails via Gmail API

- [ ] Set up Gmail API credentials
- [ ] Implement OAuth2 authentication
- [ ] Build GmailDataSource
- [ ] Handle edge cases & errors
- [ ] Test with real Gmail account

**Learning:** REST APIs, OAuth2, authentication, error handling

**Deliverable:** Can fetch emails from Gmail

---

### Phase 3: Data Parsing (Weeks 5-6)
**Goal:** Parse emails into Task objects

- [ ] Design Task domain model
- [ ] Build email parser (extract deadline, title)
- [ ] Handle multiple email formats
- [ ] Normalize data
- [ ] Build integration test (email → Task)

**Learning:** Data modeling, parsing, regex, testing strategies

**Deliverable:** Can convert Gmail messages to Task objects

---

### Phase 4: Claude Integration (Weeks 7-8)
**Goal:** Analyze tasks with AI

- [ ] Set up Anthropic API
- [ ] Build Claude API client
- [ ] Implement TaskAnalyzer
- [ ] Design prompts for prioritization
- [ ] Test with real tasks

**Learning:** LLM APIs, prompt engineering, API integration

**Deliverable:** Can prioritize and schedule tasks with Claude

---

### Phase 5: Email Output (Weeks 9-10)
**Goal:** Send formatted daily email

- [ ] Design email template
- [ ] Build EmailTaskNotifier
- [ ] Format AnalyzedTasks for email
- [ ] Send via Gmail SMTP
- [ ] End-to-end test locally

**Learning:** Email formatting, SMTP, Java Mail, integration testing

**Deliverable:** Receive daily email with prioritized tasks

---

### Phase 6: AWS Deployment (Week 11)
**Goal:** Automate with Lambda

- [ ] Package as Lambda function
- [ ] Set up Secrets Manager
- [ ] Configure CloudWatch Events (9 AM trigger)
- [ ] Test deployment
- [ ] Monitor execution

**Learning:** Serverless, AWS, deployment, IAM

**Deliverable:** Automated daily execution at 9 AM

---

### Phase 7: University Portal (Week 12+)
**Goal:** Integrate university assignments

- [ ] Investigate portal (API? HTML?)
- [ ] Build UniversityDataSource
- [ ] Handle authentication
- [ ] Parse assignment data
- [ ] Multi-source aggregation

**Learning:** Web scraping / API integration, decision-making under uncertainty

**Deliverable:** Unified task list from all sources

---

## Weekly Commitment

**Assumption:** 5-10 hours/week

| Week | Phase | Expected | Actual |
|------|-------|----------|--------|
| 1 | Phase 1 | 4-5 hrs | |
| 2 | Phase 1 | 2-3 hrs | |
| 3 | Phase 2 | 4-5 hrs | |
| 4 | Phase 2 | 4-5 hrs | |
| 5 | Phase 3 | 4-5 hrs | |
| 6 | Phase 3 | 4-5 hrs | |
| 7 | Phase 4 | 5-6 hrs | |
| 8 | Phase 4 | 4-5 hrs | |
| 9 | Phase 5 | 3-4 hrs | |
| 10 | Phase 5 | 3-4 hrs | |
| 11 | Phase 6 | 3-4 hrs | |
| 12 | Phase 7 | 4-5 hrs | |

**Total: 48-58 hours**

## Milestones

- **Milestone 1 (Week 2):** Project compiles, tests pass
- **Milestone 2 (Week 4):** Can fetch Gmail
- **Milestone 3 (Week 6):** Can parse into Tasks
- **Milestone 4 (Week 8):** Can prioritize with Claude
- **Milestone 5 (Week 10):** Can send email locally
- **Milestone 6 (Week 11):** Running on Lambda
- **Milestone 7 (Week 12+):** Multi-source aggregation