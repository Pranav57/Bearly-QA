# Bearly.ai — Test Execution & Bug Report

| Field | Value |
|---|---|
| Build / version under test | _fill in (e.g. Web 2026-10-31, Mac v_._._, iOS v_._._)_ |
| Tester | _your name_ |
| Date | _yyyy-mm-dd_ |
| Environment | _see matrix in 02-bearly-test-cases.md §0_ |
| Account / plan | _Free / Pro / Analyst_ |

## Status of this report
**No test cases have been run against Bearly yet.** This report was prepared in a
sandbox where the network policy blocked `bearly.ai` and `app.bearly.ai`, so no
real product behaviour could be seen. Every result below is **Not Run**, and no
bugs are listed. Inventing bugs would make the report worthless. Run the cases
yourself (the ★ smoke set takes about 45 min), fill in the tables, and log real
bugs using the template in §3.

## 1. Summary
| Module | Total | Pass | Fail | Blocked | Not Run |
|---|---|---|---|---|---|
| Auth | 10 | | | | 10 |
| Chat | 20 | | | | 20 |
| File upload | 15 | | | | 15 |
| Meetings | 12 | | | | 12 |
| Search | 7 | | | | 7 |
| Voice | 6 | | | | 6 |
| Extension | 10 | | | | 10 |
| Sync | 6 | | | | 6 |
| Billing | 5 | | | | 5 |
| Settings | 5 | | | | 5 |
| NFR | 8 | | | | 8 |
| **Total** | **104** | | | | **104** |

## 2. Execution log (★ daily smoke)
| ID | Surface | Result (Pass/Fail/Blocked) | Bug ID | Notes / evidence link |
|---|---|---|---|---|
| AUTH-01 | Web | Not Run | | |
| AUTH-02 | Web | Not Run | | |
| CHAT-01 | Web | Not Run | | |
| CHAT-02 | Web | Not Run | | |
| CHAT-04 | Web | Not Run | | |
| FILE-01 | Web | Not Run | | |
| FILE-02 | Web | Not Run | | |
| MEET-01 | Mac/Win | Not Run | | |
| MEET-02 | Web | Not Run | | |
| SRCH-01 | Web | Not Run | | |
| VOICE-01 | iOS/Android | Not Run | | |
| EXT-01 | Chrome | Not Run | | |
| EXT-02 | Chrome | Not Run | | |
| SYNC-01 | Mobile → Web | Not Run | | |
| BILL-01 | Web (sandbox) | Not Run | | |

## 3. Bugs found
_None logged. Testing hasn't run yet._ Use this format for each bug:

| Bug ID | Title | Surface | Severity | Priority | Linked TC | Status |
|---|---|---|---|---|---|---|
| BUG-001 | | | | | | Open |

### BUG-001 — _[Surface][Module] Short symptom_
- **Environment:** _browser/OS/app version/plan/network_
- **Severity / Priority / Frequency:** _S2 / P1 / 5 of 5_
- **Steps to reproduce:**
  1. …
  2. …
- **Expected:** …
- **Actual:** …
- **Evidence:** _screen recording link (required), screenshot, HAR / console log_
- **Notes:** _workaround, regression since version X?_

### How to judge "is this a bug?"
1. **Reproduce it twice** on a clean session. If it isn't reproducible, log it as an observation with a recording.
2. **Compare against a spec:** the docs, pricing page, in-app copy, or platform conventions. A mismatch is a bug.
3. **Rule out your own environment:** retry on another browser, device or network.
4. **AI output quality:** a wrong answer from the model is *not* automatically a bug. It is a bug when the product is at fault: the wrong file is used as context, citations don't support the claim, the wrong model answers, context is lost between turns, a transcript is invented from silence.
5. **UX friction** (confusing / slow / annoying) goes in §4, not in the bug list, unless it blocks a task.

## 4. UX feedback (confusing / slow / annoying)
| # | Area | Observation | Why it matters | Suggestion |
|---|---|---|---|---|
| 1 | | | | |

## 5. Automation run
`mvn test` (see README). Paste the TestNG summary here. Screenshots of failures are
saved to `target/screenshots/`.
