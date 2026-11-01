# Bearly.ai QA Exercise

QA material for Bearly.

| File | What it is |
|---|---|
| `docs/01-bearly-overview.md` | What Bearly is: surfaces, features, plans, user-feedback themes |
| `docs/02-bearly-test-cases.md` | v2: 205 test cases across 19 modules, rewritten after testing the live product (see "What changed from v1") |
| `docs/03-test-execution-report.md` | Live execution report (2026-10-31, Pro account): all 205 cases, 43 bugs, 34 improvement suggestions |
| `docs/Bearly-QA-Review.xlsx` | Review workbook: all bugs (with video links and stills), suggestions, 205 test cases with results, UI inventory, test data; yellow columns for your review |
| `docs/Bearly-QA-Submission-Pranav-Kumar.xlsx` | **Submission version for Bearly**: Summary, one row per bug in the requested format (steps, expected, actual, device & browser, recording link), Top 3 annoyances, then appendix tabs |
| `docs/04-bug-reports.md` | 43 bug reports with first-time-user reproduction steps, screenshots and 40 screen recordings (`docs/evidence/video/`) |
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
