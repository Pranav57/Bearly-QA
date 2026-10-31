# Bearly.ai — Test Execution & Bug Report

| Field | Value |
|---|---|
| Build / version under test | Public web, live on 2026-10-31. Desktop installers served: v5.2.11 |
| Tester | Pranav |
| Date | 2026-10-31 |
| Environment | Headless Chromium 153 (Playwright), Linux. Viewports 1366×768 desktop, 375×812 iPhone UA, 412×915 Android UA |
| Account / plan | **None.** Sign-in could not complete (see below) |

## Status of this report: read this first
- **The logged-in product was not reachable.** The web app at `bearly.ai/dashboard`
  loads, but its API (`backend.bearly.ai`, `exec.bearly.ai`) was blocked by the
  network policy of the test sandbox. The sign-in screen renders, but nothing behind
  it can be exercised. Chat, files, meetings, search, voice, sync, billing and settings
  are therefore **Blocked**, not passed or failed.
- **No mobile devices, desktop apps or Chrome extension were available** in this run.
- **What was tested:** the whole public surface (206 sitemap URLs, 427 internal
  links, 20 key pages on desktop and mobile), the sign-in screen's client-side
  behaviour, pricing page logic, and the published docs, which were used as the spec.
- **Every bug below was observed directly and is reproducible.** Nothing is inferred
  from behaviour I couldn't see. Where a finding is partial, it says so.

## 1. Summary
| Module | Total | Pass | Fail | Partial | Blocked |
|---|---|---|---|---|---|
| Auth | 10 | | | 1 | 9 |
| Chat | 20 | | | | 20 |
| File upload | 15 | | | | 15 |
| Meetings | 12 | | | | 12 |
| Search | 7 | | | | 7 |
| Voice | 6 | | | | 6 |
| Extension | 10 | | | | 10 |
| Sync | 6 | | | | 6 |
| Billing | 5 | | 1 | | 4 |
| Settings | 5 | | | | 5 |
| NFR | 8 | | 1 | | 7 |
| **Total** | **104** | **0** | **2** | **1** | **101** |

Plus exploratory testing of the public site: **10 bugs logged** (§3), **9 UX
suggestions** (§4), **8 test-plan corrections** found by checking the cases against
Bearly's own docs (§5).

## 2. Execution log
### ★ Daily smoke
| ID | Surface | Result | Bug ID | Notes / evidence |
|---|---|---|---|---|
| AUTH-01 | Web | Blocked | | Bearly has no password sign-up; it's email-code or Google. The case needs rewriting (§5). Sending the code needs `backend.bearly.ai` |
| AUTH-02 | Web | Blocked | | "Continue with Google" button present and keyboard-reachable. OAuth needs the backend |
| CHAT-01 | Web | Blocked | | Needs login |
| CHAT-02 | Web | Blocked | | Needs login + Pro |
| CHAT-04 | Web | Blocked | | Needs login |
| FILE-01 | Web | Blocked | | Needs login |
| FILE-02 | Web | Blocked | | Needs login |
| MEET-01 | Mac/Win | Blocked | | No desktop app in sandbox |
| MEET-02 | Web | Blocked | | Needs login |
| SRCH-01 | Web | Blocked | | Needs login |
| VOICE-01 | iOS | Blocked | | No device. Bearly has no Android app (§5) |
| EXT-01 | Chrome | Blocked | | Chrome Web Store not reachable |
| EXT-02 | Chrome | Blocked | | Same |
| SYNC-01 | Mobile → Web | Blocked | | Needs login + device |
| BILL-01 | Web | Blocked | | Needs login |

### Non-smoke cases with a result
| ID | Result | Bug ID | Notes |
|---|---|---|---|
| AUTH-05 | Partial | BUG-006 | "Continue" stays enabled for empty, `abc@`, `a b@x.com`, `abc`, and whitespace. No inline validation while typing, and the input has no `required`. Submitting wasn't possible, so it's unconfirmed whether the click is blocked |
| BILL-05 | **Fail** | BUG-001, 002, 003 | Pricing page contradicts the docs' plan table in three places |
| NFR-03 | **Fail** | BUG-004, 008 | Primary sign-in button fails contrast. Billing toggle doesn't expose its state. Rest of NFR-03 (screen-reader pass in the app) blocked |
| NFR-05 | Blocked | (BUG-007) | App at 375 px untestable past sign-in. Sign-in screen itself is fine at 375 px; the marketing site has a mobile-nav gap |
| NFR-06 | Blocked | (Obs. S-1) | Cookies/tokens need login. Headers checked: HSTS preload ✔, `nosniff` ✔, referrer-policy ✔, `security.txt` ✔, no CSP or frame-ancestors on app shell (§3, S-1) |

All other cases: **Blocked**, for the reasons in the status section.

### What passed in exploratory checks
- All **206 sitemap URLs return 200**; **0 dead internal links** out of 427 unique ones.
- Every key page has a title, meta description, canonical URL, OG image, a single
  `<h1>`, `lang="en"`, and alt text on every image. No console errors on any public page.
- Unknown URLs return a real **404** with a branded "Page Not Found" page.
- Platform-aware CTA works: a Linux UA gets "Download for Linux", an iPhone UA gets
  "Get Bearly for iOS", and an Android UA gets "Continue in browser" (correct, since
  there is no Android app).
- Download links 302 to GitHub releases v5.2.11 (`.dmg` universal, `.exe`, `.AppImage`).
- Yearly price = 10 × monthly ($200 / $600 / $1000), which matches the docs ("pay
  for ten months, get twelve") and the "Save 17%" badge.
- Public pages load in 0.2–1.6 s (desktop, `load` event).

## 3. Bugs found
| Bug ID | Title | Surface | Sev | Pri | Linked TC | Status |
|---|---|---|---|---|---|---|
| BUG-001 | Pro card advertises "deep research", docs say Deep Research needs Analyst | Web | S2 | P1 | BILL-05 | Open |
| BUG-002 | Pricing FAQ says paid tiers differ only in credits + Fable; Analyst/Research tab shows identical bullets | Web | S3 | P2 | BILL-05 | Open |
| BUG-003 | Free card lists "Claude, GPT, Gemini, and Grok models"; docs say Free gets low-cost models only | Web | S3 | P2 | BILL-05 | Open |
| BUG-004 | Sign-in "Continue" button contrast about 2.4:1, below WCAG AA 4.5:1 | Web | S3 | P2 | NFR-03 | Open |
| BUG-005 | Sign-in email input is 13 px on mobile, so iOS Safari will zoom on focus | Mobile web | S3 | P2 | NFR-05 | Open |
| BUG-006 | Sign-in email field: no validation, no `required`, no `autocomplete` | Web | S3 | P3 | AUTH-05 | Open |
| BUG-007 | Mobile header hides Product / Privacy / Enterprise / Pricing, with no menu button | Mobile web | S3 | P2 | — | Open |
| BUG-008 | Monthly/Yearly toggle doesn't expose its on/off state to assistive tech | Web | S3 | P2 | NFR-03 | Open |
| BUG-009 | Dead outbound link on the comparison page (support.claude.com → 404) | Web | S4 | P3 | — | Open |
| BUG-010 | Sign-in page `<title>` is "Bearly.ai \| AI at your fingertips", off-brand and not noindexed | Web | S4 | P3 | — | Open |

---

### BUG-001 — [Web][Pricing] Pro plan card advertises "deep research", but Deep Research requires Analyst
- **Environment:** bearly.ai/pricing, Chromium 153, 1366×768, logged out
- **Severity / Priority / Frequency:** S2 / P1 / Always
- **Steps to reproduce:**
  1. Open https://bearly.ai/pricing.
  2. Read the bullets on the **Pro ($20/mo)** card.
  3. Open https://bearly.ai/docs/manual/plans-and-credits/ → "What each plan unlocks".
- **Expected:** The Pro card lists only features Pro unlocks.
- **Actual:** The Pro card says **"Cowork, deep research, code"**. The docs say:
  *"Deep Research, standard tier — Minimum plan: Analyst. Run Research opens the
  upgrade flow."* The plans table also lists Deep Research under Analyst.
- **Evidence:** `docs/evidence/pricing_desktop.png`; docs page quoted above.
- **Notes:** This is the purchase decision screen. A Pro buyer who wants deep research
  hits an upgrade wall straight after paying, which leads to refund requests and lost
  trust. Fix one side: either the card says "Deep Research on Analyst+", or the docs and
  the gate are wrong.

### BUG-002 — [Web][Pricing] FAQ says paid tiers only differ by credits + Fable; docs list more differences, and the Research tab hides them
- **Environment:** as BUG-001
- **Severity / Priority / Frequency:** S3 / P2 / Always
- **Steps to reproduce:**
  1. On /pricing, expand FAQ **"What's the difference between the paid tiers?"**
  2. On the third card, switch the **Analyst / Research** tab to **Research**.
  3. Compare with the docs plan table.
- **Expected:** Each tier lists what it adds. The Research tab shows what $100 buys
  over $60.
- **Actual:** The FAQ says *"Credit budget, plus Claude Fable from Analyst up. Otherwise
  every paid plan has the same features."* The docs say Analyst adds **Deep Research and
  People Search**, and Research adds **the Ultra tier of Deep Research**. The Research tab
  switches the price to $100 but keeps Analyst's exact bullets ("Everything in Pro · Adds
  Claude Fable · Built for all-day use"), so the only visible reason to pay $40 more is
  more credits.
- **Evidence:** `docs/evidence/pricing_desktop.png`, `pricing_yearly.png`; docs page.

### BUG-003 — [Web][Pricing] Free card implies the flagship models; docs say Free gets low-cost models only
- **Severity / Priority / Frequency:** S3 / P2 / Always
- **Steps:** Read the Free card on /pricing, then the docs plan table.
- **Expected:** Free card is accurate about model access.
- **Actual:** The Free card says **"Claude, GPT, Gemini, and Grok models"**. The docs say
  Free is *"Light daily use of low-cost models"*, and the same pricing page says further
  down *"Opus needs Pro, and Fable needs Analyst."* The "personal email required" rule for
  Free also appears only in the "Short answer" block, not on the card.
- **Notes:** A user who signs up expecting Opus or GPT-6 on Free and gets an upgrade
  prompt will feel baited. Suggested copy: "Fast models from Claude, GPT, Gemini and Grok".

### BUG-004 — [Web][Auth] Sign-in "Continue" button text fails WCAG AA contrast and looks disabled
- **Environment:** bearly.ai/dashboard/focus, Chromium, 1366×768 and 375×667
- **Severity / Priority / Frequency:** S3 / P2 / Always
- **Steps:** Open the sign-in page and look at the primary **Continue** button.
- **Expected:** At least 4.5:1 text contrast (WCAG 1.4.3) for 13 px text, and an obvious
  primary action.
- **Actual:** The text is `rgb(253,108,44)` on a 10 % tint of the same orange (≈ `#F6E7E1`),
  which measures **≈ 2.4:1**. The pale-on-pale style reads as a *disabled* button even
  though it's enabled (see BUG-006), so users may wait instead of clicking.
- **Evidence:** `docs/evidence/login_desktop.png`, `login_mobile.png`; computed styles
  read via Playwright.

### BUG-005 — [Mobile web][Auth] Email input font-size is 13 px, so iOS Safari zooms on focus
- **Environment:** 375×667, mobile emulation
- **Severity / Priority / Frequency:** S3 / P2 / Always (measured value)
- **Steps:** Open /dashboard/focus on a phone and tap the Email field.
- **Expected:** Inputs at ≥ 16 px so iOS doesn't auto-zoom the page.
- **Actual:** Computed `font-size: 13px`. iOS Safari zooms any input under 16 px on
  focus, so the layout jumps and the user has to pinch back out. This is the first
  screen for every Android user and for iPhone users without the app.
- **Notes:** The 13 px value is measured. The zoom itself is standard iOS Safari
  behaviour and should be confirmed on a real iPhone. Fix: `font-size: 16px` on the input
  (or `max(16px, 1em)`).

### BUG-006 — [Web][Auth] Email field has no client-side validation, no `required`, no `autocomplete`
- **Severity / Priority / Frequency:** S3 / P3 / Always
- **Steps:** On the sign-in page, type each of `""`, `"   "`, `abc`, `abc@`, `a b@x.com`
  into Email.
- **Expected:** (AUTH-05) Inline validation, and Continue disabled or blocked for
  invalid input.
- **Actual:** Continue stays **enabled** for every value. No inline message appears.
  The input has `type=email` but no `required` (empty counts as valid) and no
  `autocomplete="email"`, so browser and password-manager autofill is weaker on a
  passwordless flow that depends on typing the email correctly.
- **Notes:** Partial result. I couldn't submit (backend blocked), so the server-side
  response to an invalid email is unverified. Re-test on submit.

### BUG-007 — [Mobile web][Marketing] At 375 px the header hides Product, Privacy, Enterprise and Pricing, with no menu button
- **Environment:** 375×812, iPhone UA; also 412×915 Android UA
- **Severity / Priority / Frequency:** S3 / P2 / Always
- **Steps:** Open https://bearly.ai on a phone and look at the header.
- **Expected:** All primary nav reachable, usually via a hamburger menu.
- **Actual:** The header shows only the logo, **News**, and **Get Bearly**. There's no menu
  button. Pricing, Product, Privacy and Enterprise can only be reached from the footer,
  thousands of pixels down. Pricing is the page a mobile evaluator most wants.
- **Evidence:** `docs/evidence/home_mobile_375.png`, `pricing_mobile_375.png`.

### BUG-008 — [Web][Pricing][a11y] Monthly/Yearly toggle doesn't expose its state
- **Severity / Priority / Frequency:** S3 / P2 / Always
- **Steps:** Inspect the toggle on /pricing, or tab to it with a screen reader.
- **Expected:** `role="switch"` + `aria-checked`, or `aria-pressed`, so the state is
  announced (WCAG 4.1.2).
- **Actual:** A plain `<button aria-label="Toggle between monthly and yearly billing">`
  with no state attribute. Only the CSS class changes, so a screen-reader user can't
  tell which prices they are reading. The theme buttons in the footer do use
  `aria-pressed`, so the pattern already exists in the codebase.

### BUG-009 — [Web][Compare] Dead outbound link on /ai-assistant-comparison
- **Severity / Priority / Frequency:** S4 / P3 / Always
- **Steps:** Open https://bearly.ai/ai-assistant-comparison and follow the link to
  `https://support.claude.com/en/articles/11049762-choose-a-claude-plan`.
- **Actual:** **404**. A comparison page is only credible if its citations resolve.
- **Notes:** Only this one outbound link could be checked from the sandbox; others
  (x.ai, perplexity.ai, etc.) weren't resolvable from there and are **unverified**, not
  broken. Suggest a scheduled link-check job in CI.

### BUG-010 — [Web][Auth] Sign-in page title is off-brand and not marked noindex
- **Severity / Priority / Frequency:** S4 / P3 / Always
- **Actual:** The `<title>` of /dashboard/focus is **"Bearly.ai | AI at your
  fingertips"**, an older tagline. Every other page uses "Bearly: Private AI with
  Claude, GPT…". The page has no `meta robots noindex`, so the app shell can show up in
  search results with the old title.

### Security observation S-1 (not a confirmed vulnerability)
`/dashboard/focus` (the app shell) is served without a `Content-Security-Policy` or
`X-Frame-Options` / `frame-ancestors` header (HSTS, `nosniff` and referrer-policy are
present). A product whose main promise is "not even we can read your chats" benefits
from a strict CSP (it limits the damage of any XSS in rendered model output; see
CHAT-18) and from `frame-ancestors 'none'` against clickjacking. **Not exploited or
tested further.** Hand it to the security owner to confirm whether it's set elsewhere,
e.g. by a meta tag after login.

### How to judge "is this a bug?"
1. **Reproduce it twice** on a clean session. If it isn't reproducible, log it as an observation with a recording.
2. **Compare against a spec:** the docs, pricing page, in-app copy, or platform conventions. A mismatch is a bug.
3. **Rule out your own environment:** retry on another browser, device or network.
4. **AI output quality:** a wrong answer from the model is *not* automatically a bug. It is a bug when the product is at fault: the wrong file is used as context, citations don't support the claim, the wrong model answers, context is lost between turns, a transcript is invented from silence.
5. **UX friction** (confusing / slow / annoying) goes in §4, not in the bug list, unless it blocks a task.

## 4. UX feedback and suggestions
| # | Area | Observation | Why it matters | Suggestion |
|---|---|---|---|---|
| 1 | Pricing hero | Headline "Cancel the other four." never says which four | Clever lines that need decoding cost conversions on the most commercial page | "Cancel ChatGPT, Claude, Gemini and Grok." or a logo strip of the four |
| 2 | Pricing FAQ | "Wait, so Bearly is free? Essentially, yes." sits next to $20–$100 plans | Reads as contradictory and slightly evasive | "You pay for AI usage at cost; the app itself is free." |
| 3 | Pricing, yearly | Footnote says "credits equal to the subscription cost", but yearly is $200 for $240 of credits | Undersells the yearly deal, and the statement is false for yearly | "Yearly: 12 months of credits for the price of 10" |
| 4 | Pricing | No way to see what $20 of credits buys (messages per model, minutes of transcription) | Credits are abstract; that's the #1 question for a credit-based plan | Add a calculator or example table ("≈ N Opus messages / M meeting hours") |
| 5 | Sign-in | No hint of what happens after "Continue" (a 6-digit code by email, valid for N minutes) | Users wonder whether to wait for a link or a code, and check the wrong place | Subtitle: "We'll email you a 6-digit code." Add a "Didn't get it? Resend" plan with a visible cooldown |
| 6 | Sign-in | No Terms or Privacy link on the sign-in screen | Expected on account creation, especially for a privacy-led brand | Small "By continuing you agree to…" line |
| 7 | Android | No Android app; Android visitors get only "Continue in browser" | India's market is mostly Android, so this is the biggest distribution gap for an India-based tester to flag | Ship a PWA manifest + "Add to Home Screen" prompt as a cheap bridge, then track the Android app ask |
| 8 | Footer | Download column lists iOS but no hint for Android | Android users assume they were forgotten | "Android: use the web app →" |
| 9 | Home page | Most sections render only after scroll-reveal animations (blank in full-page captures) | Link-preview bots, screenshot tools and some reduced-motion users can see empty sections | Respect `prefers-reduced-motion` and render content visible by default, animating only as an enhancement |

## 5. Test-plan corrections (found by checking the cases against Bearly's docs)
| # | Case(s) | Problem | Fix |
|---|---|---|---|
| 1 | AUTH-01, AUTH-04, AUTH-06 | Written for password login. Bearly is **passwordless**: email code or Google | Replace with: code arrives < 2 min, wrong code, expired code (docs use a 10-min window for account deletion, so confirm the login window), code reuse, resend throttling, code requested for an unknown email doesn't reveal account existence |
| 2 | AUTH-03 | Assumes Sign in with Apple; only Google + email appear on web | Verify on iOS; drop if not offered |
| 3 | FILE-10, VOICE-05, BILL-03, env matrix "Android" | Docs: **"There is no Android app. On Android, use the web app."** | Re-target Android cases to Android Chrome mobile web; keep native for iOS (iOS 17+) |
| 4 | FILE-05 | Limit unspecified | Docs: **300 MB per attachment**. Test 299 MB and 301 MB |
| 5 | MEET-08 | Limit unspecified | Docs: **400 MB** max for Transcribe, error text "File is too large to transcribe. Max size: 400 MB" |
| 6 | FILE-07 | Expected result vague | Docs give exact strings: "Password required.", "That password didn't work.", "Unable to unlock this PDF." Assert them |
| 7 | CHAT-03, BILL | Gating messages unspecified | Docs: Council → "Council of Experts requires Pro or higher."; Free voice → "Choosing a voice model needs a paid plan." Assert exact copy |
| 8 | New | Missing case: send is blocked while a file shows "Uploading" or "Analyzing" (documented behaviour) | Add FILE-16: try to send mid-upload; expect a disabled Send, not a silent no-op |

## 6. Automation run
Not run in this session. The logged-in tests need `backend.bearly.ai`, and the public
checks were done with Playwright scripts instead. `mvn test` (see README) still applies.

## 7. To finish the remaining 101 cases
1. Allow `bearly.ai`, `backend.bearly.ai`, `exec.bearly.ai` (plus `accounts.google.com`
   for AUTH-02) in the test environment's network policy, or test from a normal machine.
2. Use one Free account and one Pro account (gating cases need both).
3. Devices: iPhone (iOS 17+) and an Android phone (mobile web), Mac or Windows for
   meetings and desktop features, Chrome with Bearly Companion for EXT-*.
4. Re-test BUG-006 on submit, and confirm BUG-005 on a real iPhone.
