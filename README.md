# Bearly.ai QA Exercise

QA prep material for the Bearly **QA Tester (Remote, India)** role.

| File | What it is |
|---|---|
| `docs/01-bearly-overview.md` | What Bearly is: surfaces, features, plans, user-feedback themes |
| `docs/02-bearly-test-cases.md` | v2: 186 test cases across 19 modules, rewritten after testing the live product (see "What changed from v1") |
| `docs/03-test-execution-report.md` | Live execution report (2026-10-31, Pro account): all 186 cases, 35 bugs, 25 improvement suggestions |
| `docs/Bearly-QA-Review.xlsx` | Review workbook: all bugs (with video links and stills), suggestions, 186 test cases with results, UI inventory, test data; yellow columns for your review |
| `docs/04-bug-reports.md` | 35 bug reports with first-time-user reproduction steps, screenshots and 33 screen recordings (34 bugs) (`docs/evidence/video/`) |
| `docs/05-improvement-suggestions.md` | 20 suggestions, each with steps to see the current behaviour |
| `docs/test-data/` | Every prompt and file used in testing, plus a script to regenerate the files |
| `docs/evidence/` | Screenshots referenced by the bug reports |
| `src/test/java/...` | Selenium 4 + TestNG smoke suite (public pages + logged-in chat) |

## Running the automation
Requirements: JDK 17+, Maven, Chrome, plus a **matching** chromedriver (or let Selenium Manager fetch one).

```bash
# Public-page checks only (no account needed)
mvn test

# Include logged-in chat tests (use a dedicated test account)
mvn test -Dbearly.email=you@example.com -Dbearly.password='***'

# Options
-Dheadless=false            # watch the browser
-Dchrome.bin=/path/to/chrome
-Dbearly.baseUrl=https://bearly.ai -Dbearly.appUrl=https://app.bearly.ai
```

The chat-test selectors are generic (`textarea`, `input[type=email]`). Inspect the
real DOM once and pin them to stable attributes before trusting the results.
