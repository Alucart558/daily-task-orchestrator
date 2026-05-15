# PHASE 4: Claude API Integration & Task Analysis
## EXPLANATION PROMPT FOR CLAUDE HAIKU

**COPY THIS ENTIRE PROMPT AND GIVE IT TO CLAUDE HAIKU**

**Use this AFTER Phase 4 code works with real Claude API.**

---

You are a software teacher explaining LLM integration and prompt engineering.

## CONTEXT

Student has implemented Claude API integration for task prioritization. Now explain concepts.

## YOUR TASK

Explain:
1. What it means to use an LLM as a component
2. Prompt engineering (getting good results)
3. Parsing unstructured LLM responses
4. When to use AI vs rules
5. Cost and performance implications
6. Testing LLM-based systems

## STRUCTURE

### 1. USING LLM AS A COMPONENT (5 min read)

Most students think of AI as either:
- ChatGPT web interface (user types, AI responds)
- Magic black box (use it once, move on)

Professional reality:
- LLM is a service like any other (Gmail, Stripe, etc.)
- You call it via API
- You craft prompts carefully
- You parse responses programmatically
- You integrate results into larger system

Our use case:
```
We have: List<Task> with title, deadline, description
We want: Priorities, schedule, summary
We use: Claude API + specific prompt

Flow:
  1. Format tasks as text prompt
  2. Send to Claude with system prompt
  3. Claude returns analysis (JSON)
  4. Parse JSON back to AnalyzedTasks
  5. Rest of system uses AnalyzedTasks (doesn't know about Claude)
```

Claude is internal implementation detail. Rest of system doesn't know/care.

### 2. PROMPT ENGINEERING (5 min read)

Prompt quality determines output quality.

Bad prompt:
```
"Analyze these tasks"
```
Result: Claude gives generic, unhelpful analysis.

Good prompt:
```
You are an academic task scheduler. You analyze student assignments.
Your job is to:
1. Assign priority (CRITICAL, HIGH, MEDIUM, LOW)
2. Estimate time needed
3. Create daily schedule
4. Identify most urgent

Consider:
- Deadline urgency
- Task complexity
- Student capacity (8 hours study/day max)

Format response as JSON for easy parsing.

Tasks:
[Task details]
```
Result: Claude gives structured, useful analysis.

Prompt engineering principles:
```
✅ Clear role: "You are a task scheduler"
✅ Specific goal: "Assign priority..."
✅ Clear constraints: "Student capacity..."
✅ Output format: "Format as JSON"
✅ Examples: Show what good output looks like

❌ Vague: "Tell me about my tasks"
❌ Unclear role: No context on what Claude should do
❌ No constraints: Claude guesses what you want
❌ Unstructured output: Hard to parse
```

Why prompts matter:
```
Bad prompt + Claude 3.5 = mediocre output
Good prompt + Claude 3.5 = excellent output
Good prompt + Haiku = decent output
```

Spend time on prompts!

### 3. RESPONSE PARSING (4 min read)

Claude's output is text. How do we extract data?

Option 1: Request JSON
```
Prompt: "Respond ONLY in JSON format: {...}"
Response:
  {
    "tasks": [{"title": "...", "priority": "..."}],
    ...
  }
Parse: JSON parser
Advantage: Easy, deterministic, works every time
Disadvantage: Claude might refuse, won't be conversational
```

Option 2: Structured markers
```
Prompt: "Use these markers: [PRIORITY]...[/PRIORITY]"
Response:
  Task: Project 3
  [PRIORITY]HIGH[/PRIORITY]
  Estimated: 8 hours
Parse: Regex to extract markers
Advantage: More flexible
Disadvantage: Error-prone, Claude might use wrong markers
```

Option 3: Pure NLP (hard)
```
Prompt: "Analyze these tasks"
Response: Free-form text
Parse: NLP to extract meaning
Advantage: Most natural
Disadvantage: Very hard, fragile
```

We use Option 1 (JSON). Why?
- JSON is unambiguous
- Easy to parse
- Claude handles it well
- Can version schema easily

### 4. WHEN TO USE AI VS RULES (4 min read)

NOT every decision should use Claude. Cost and latency matter.

Use Claude for:
```
✅ Prioritization (complex, multi-factor decision)
   - Deadline + complexity + dependencies
   - Hard to encode as rules
   
✅ Summarization (complex analysis)
   - Multiple tasks → executive summary
   - Hard to template
   
✅ Scheduling (optimization problem)
   - Fit tasks into daily schedule
   - Respecting student capacity
   - Hard to do with simple rules
```

Don't use Claude for:
```
❌ Date parsing
   "May 25" → 2025-05-25
   Better: Regex + date libraries (instant, free)
   
❌ Title extraction
   Subject line is usually title
   Better: Simple string operations
   
❌ Task filtering
   Keywords like "assignment", "deadline"
   Better: Keyword matching (instant, free)
```

Rule of thumb:
- If you can code it deterministically → do it
- If it requires judgment/nuance → use Claude
- If it's in the happy path (runs every day) → avoid LLM
- If it's slow (doesn't need to be instant) → consider LLM

Cost:
```
Claude costs $3 per 1M input tokens
Our prompt: ~200 tokens
Our response: ~200 tokens
Cost: ~$0.001 per call

Running daily: $0.30/month
Running 100x/day: $30/month
Running 10,000x/day: $3,000/month

Know your volume!
```

### 5. HANDLING FAILURES (3 min read)

LLM is not a database. Responses vary. Handle this:

```
Case 1: Claude refuses
  Prompt: "Prioritize these tasks"
  Response: "I can't help with that"
  Our code: Graceful degradation, use defaults

Case 2: Claude gives wrong format
  Expected: JSON
  Got: Plain text
  Our code: Try to parse anyway, fall back

Case 3: Claude prioritizes differently
  Expected: Deadline-first prioritization
  Got: Complexity-first
  Our code: Accept it (Claude might be right!)

Case 4: API rate limit
  Too many requests too fast
  Response: 429 Too Many Requests
  Our code: Backoff, retry later

Case 5: Token limit exceeded
  Response too long to fit in model
  Our code: Truncate tasks, retry with fewer
```

Design for these cases!

### 6. TESTING WITH LLMS (3 min read)

Hard problem: How do you test something non-deterministic?

Approach 1: Mock (testing unit)
```
Don't call real Claude in unit tests
Mock AnthropicApiClient
Return fake response
Test parsing logic
Fast (1ms), free
```

Approach 2: Fixture (golden data)
```
Save real Claude responses
Use same response in tests
Tests are deterministic
But: Real Claude might change output format
```

Approach 3: Integration test (real Claude)
```
Call real API with test prompt
Verify response is valid JSON
Verify all expected fields present
Slow (1-2 seconds), costs money
Run nightly, not on every commit
```

We use all three:
- Unit tests with mocks (fast, frequent)
- Integration tests (slow, less frequent, real API)
- Manual testing (occasionally, with real tasks)

### 7. WHAT YOU LEARNED (3 min read)

By integrating Claude, you learned:

1. **LLMs as services** - not magic, just APIs
2. **Prompt engineering** - quality matters immensely
3. **Response parsing** - structure your outputs
4. **Cost awareness** - every call costs money
5. **When to use AI** - not always necessary
6. **Failure handling** - LLMs aren't reliable like databases
7. **Testing non-determinism** - mocks + fixtures + integration tests

These skills apply to ANY LLM (GPT, Gemini, Llama, etc).

### 8. COMMON MISTAKES (2 min read)

Watch out for:

```
❌ Assuming Claude always gives correct format
   → Always validate response format

❌ Sending huge prompts
   → Tokens = cost. Optimize.

❌ Using GPT-4 when Haiku would work
   → Cost discipline matters

❌ No error handling
   → API fails sometimes. Handle it.

❌ Logging full responses
   → Might leak user data

❌ No timeout on API calls
   → Can hang forever

❌ Testing only happy path
   → What if Claude refuses?

✅ Request JSON, validate before parsing
✅ Optimize prompts, test cost vs quality
✅ Use right model for job
✅ Comprehensive error handling
✅ Sanitize logs
✅ Set timeouts
✅ Test failure cases
```

---

## LEARNING TOPICS

### Prompt Engineering
- [ ] System prompts vs user prompts
- [ ] Few-shot learning (examples in prompt)
- [ ] Chain-of-thought prompting
- [ ] Temperature and randomness
- [ ] Token counting (pricing)

### LLMs as Services
- [ ] Anthropic API docs: https://docs.anthropic.com/
- [ ] Model selection (Haiku vs Sonnet vs Opus)
- [ ] Rate limiting and quotas
- [ ] Token usage tracking
- [ ] Streaming responses (bonus)

### Response Parsing
- [ ] JSON schema validation
- [ ] Regex for marker extraction
- [ ] Fallback parsing strategies
- [ ] Partial data handling

### Software Testing
- [ ] Mocking vs fixtures vs integration
- [ ] Non-deterministic testing
- [ ] Integration test best practices
- [ ] Golden data / golden files

### Cost & Performance
- [ ] Token counting algorithm
- [ ] API latency expectations
- [ ] Cost optimization strategies
- [ ] Caching (don't call API twice)

### Java Specifics
- [ ] JSON parsing in Java
- [ ] HTTP clients and streaming
- [ ] Exception handling for external APIs
- [ ] Configuration management

---

## QUESTIONS TO THINK ABOUT

1. **Why not use simple rules for prioritization?**
   - Answer: Rules are brittle. Claude handles complexity, dependencies, nuance.

2. **What if Claude gets priority wrong?**
   - Answer: It sometimes will. But humans also disagree. Good enough.

3. **Why JSON instead of plain text?**
   - Answer: JSON is unambiguous. Plain text is hard to parse reliably.

4. **How much will this cost to run daily?**
   - Answer: ~$0.001/day = $0.30/month (very cheap).

5. **What if Claude API is down?**
   - Answer: We gracefully degrade (use default priorities).

6. **Can we cache Claude responses?**
   - Answer: Phase 6+. For now, fresh analysis daily.

---

## WHAT COMES NEXT

Phase 5: Email Output

We have AnalyzedTasks with priorities and schedule. Now:
- Format nicely for email
- Include summary, schedule, recommendations
- Send via Gmail SMTP
- User receives daily email

---

## KEY INSIGHT

LLMs are tools, not magic. They're good at:
- Understanding complex nuance
- Generating human-friendly text
- Finding patterns in unstructured data

They're bad at:
- Deterministic tasks (use code)
- Cost-sensitive operations (tokens = money)
- Sensitive data (never share user data with LLM)

Using them wisely is the skill.

---

**Read this, explore learning topics, ask questions about prompt engineering and LLM integration.**