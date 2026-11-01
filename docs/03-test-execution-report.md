# Bearly.ai — Test Execution & Bug Report

| Field | Value |
|---|---|
| Build under test | Web app build `38519b9` (built Oct 30, 2026), public site live on 2026-10-31. Desktop installers served: v5.2.11 |
| Tester | Pranav |
| Date | 2026-10-31 |
| Environment | Chromium 153 (Playwright), Linux, 1440×900 desktop and iPhone 13 emulation (390×844). A second browser was used as a second "device". Fake microphone fed from recorded speech for voice tests |
| Account / plan | Real **Pro** account (email-code sign-in). Total credit used for all testing: **$0.27** of $20 |
| Test plan | `02-bearly-test-cases.md` v2 (170 cases; rewritten during this run) + module 19 "Built-in tools" (16 cases) added 1 Nov |

## How to read this report
- **Pass / Fail / Partial** = observed in the live product. Every Fail links to a bug below.
- **Blocked** = couldn't run here (no phone, desktop app, Chrome Web Store, Google/Microsoft account).
- **Not run** = deliberately skipped because it would spend money, change your account in a lasting way, or publish something (purchases, account deletion, encryption setup, connectors, publishing a public Page, creating a scheduled routine).
- Results I couldn't confirm are marked Partial with the reason. Two findings that turned out to be test-tool artefacts were **withdrawn**, not reported (§7).

## 1. Summary
| Module | Cases | Pass | Fail | Partial | Blocked | Not run |
|---|---|---|---|---|---|---|
| Auth | 12 | 3 | 2 | 2 | 4 | 1 |
| Chat | 27 | 14 | 4 | 7 | 0 | 2 |
| Files | 21 | 9 | 6 | 4 | 1 | 1 |
| Meetings | 14 | 4 | 1 | 1 | 1 | 7 |
| Search | 8 | 1 | 4 | 1 | 1 | 1 |
| Voice & Dictation | 9 | 1 | 2 | 1 | 1 | 4 |
| Extension | 10 | 0 | 0 | 0 | 10 | 0 |
| Sync | 6 | 1 | 2 | 0 | 0 | 3 |
| Billing & Credits | 9 | 1 | 3 | 1 | 1 | 3 |
| Settings & Account | 9 | 2 | 2 | 1 | 0 | 4 |
| Prompt Templates | 5 | 2 | 2 | 0 | 0 | 1 |
| Routines | 6 | 1 | 0 | 0 | 0 | 5 |
| Projects | 7 | 1 | 0 | 1 | 0 | 5 |
| Sharing (Pages) | 4 | 0 | 1 | 0 | 0 | 3 |
| Text-to-speech | 3 | 0 | 1 | 1 | 0 | 1 |
| Privacy / ZDR / E2E | 5 | 2 | 1 | 0 | 0 | 2 |
| Connectors & Desktop | 7 | 0 | 0 | 0 | 4 | 3 |
| Non-functional | 8 | 1 | 3 | 2 | 1 | 1 |
| Built-in tools *(1 Nov)* | 16 | 11 | 1 | 4 | 0 | 0 |
| **Total** | **186** | **54** | **35** | **26** | **24** | **47** |

**Bugs logged: 35** (8 S2-major, 17 S3-minor, 10 S4-trivial) (details and screen recordings in `04-bug-reports.md`; BUG-31…35 were found on 1 Nov while recording and in the second exploration round, see §1b), plus 3 security observations, **20 improvement suggestions** (`05-improvement-suggestions.md`), all test data in `test-data/`, and test-plan changes in `02-bearly-test-cases.md` ("What changed from v1").

### 1b. Second exploration round (1 Nov 2026)
After the first report, every screen was swept again on the same account. Changes were made only where they could be undone, and each was restored.
- **UI inventory:** every clickable element on New Chat, a chat, Prompt Templates, Routines and Meetings was listed and clicked. Destructive, paid and sending actions were skipped. All 16 Settings tabs and 18 pop-up menus were opened and recorded. The full list is in the **UI Inventory** tab of `Bearly-QA-Review.xlsx`.
- **16 built-in tools** from the "/" menu were tested (module 19, above): 11 Pass, 1 Fail, 4 Partial.
- **Reversible account actions:**
  - pin / unpin, mark unread, rename and revert, archive and unarchive, select mode;
  - create and delete a project;
  - ZDR-only on and off;
  - Dark and Dracula themes, then back to System;
  - template edit and revert.
- **New bugs:** BUG-32 (template edits silently cut off), BUG-33 (dark-theme contrast), BUG-34 (Best Pick wrong "nothing found"), BUG-35 (delete-dialog copy). BUG-15 and BUG-23 got new findings.
- **Credits used in round 2:** about $0.40 (Usage, 1 Nov).

### Top 10 to fix first
| # | Bug | Why it matters |
|---|---|---|
| 1 | BUG-01 Marketing says "ZDR models only", but 2 models are "Standard" and ZDR-only is **off** by default | Bearly's core promise is privacy; this is a trust and legal-copy risk |
| 2 | BUG-02 Plan gating bypassed through Settings → New Chats → Model; chat replies "Billing limit resolved" | Broken paywall path + a message that says the opposite of what happened |
| 3 | BUG-03 Attachments removed before sending stay stored, listed and searchable | User thinks a sensitive file is gone; it isn't |
| 4 | BUG-04 File download links work with no login, for 36+ min, and **after logout** | Anyone who gets the link (history, logs, a forwarded message) gets the file |
| 5 | BUG-05 Recording of silence stuck on "Transcription in progress" for 45+ min | Endless spinner; no "no speech" outcome |
| 6 | BUG-06 Search returns every chat/file for a nonsense query | Search can't be trusted, and the "No results" state never shows |
| 7 | BUG-07 Sending while offline gives a generic error with no Retry, even though the app shows "Offline" | Lost messages, confusing recovery |
| 8 | BUG-08 Rename/delete don't reach other open sessions; a deleted chat stays readable there | Stale data across devices |
| 9 | BUG-09 Voice call without microphone access does nothing at all | Silent failure on a headline feature |
| 10 | BUG-10 Four different file-size limits across docs, plan cards and real behaviour | Billing/storage expectations are unclear |

## 2. Execution log (all 186 cases)
### Auth
| ID | Result | Bug | Notes |
|---|---|---|---|
| AUTH-01 ★ | Pass | | Code email arrived; 3-word code `word-word-word`; signed in. Pasting the whole code into the first box wasn't verified |
| AUTH-02 ★ | Blocked | | No Google test account |
| AUTH-03 | Blocked | Obs. | No Microsoft account. "Continue with Microsoft" appeared only **after** logout; the first visit showed only Google |
| AUTH-04 | **Fail** | BUG-18 | Wrong and reused codes rejected with "Codewords invalid…". But 7 wrong codes in a row → no cooldown/lockout shown, and Sign in is enabled with 1 of 3 words |
| AUTH-05 | Partial | BUG-25 | Continue enabled for empty/invalid emails; no inline validation (not submitted) |
| AUTH-06 | **Fail** | BUG-18 | No "Resend code" option on the code screen. Expiry not tested |
| AUTH-07 | Pass | | Session survived 3 browser restarts |
| AUTH-08 | Pass | | Confirm dialog; token cleared; old token → 401 on every endpoint |
| AUTH-09 | Blocked | | Existing account, no new-account run |
| AUTH-10 | Blocked | | No other surfaces |
| AUTH-11 | Partial | | Devices list correct (browser, OS, method, active now). Remote sign-out not run: it would sign out your Mac |
| AUTH-12 | Not run | | Would create an account for a fake email |

### Chat
| ID | Result | Bug | Notes |
|---|---|---|---|
| CHAT-01 ★ | Pass | | 3 bullets, ~0.4 s to first change, auto-title "TCP vs UDP Explained" |
| CHAT-02 ★ | Pass | | Haiku 5.5 label on replies; Usage shows `azure-foundry/haiku-5.5` |
| CHAT-03 | Pass | BUG-02, SUG | Picker refuses Fable: "Fable 5.1 needs the Analyst plan or higher." No lock shown before clicking |
| CHAT-04 ★ | Pass | | "Ravi" |
| CHAT-05 | Pass | | Stops immediately; partial text kept after reload; can send again |
| CHAT-06 | Pass | | Rerun → version pager |
| CHAT-07 | Pass | | Edit Query → "3 / 3" versions |
| CHAT-08 | Partial | | Copy buttons on each message; clipboard content not verified |
| CHAT-09 | Pass | | Table, highlighted code, KaTeX |
| CHAT-10 | Pass | SUG-03 | 50,071 characters accepted in 5 s, but the sent bubble is ~15,000 px tall |
| CHAT-11 | **Fail** | BUG-19 | Hindi/Chinese/Tamil/emoji fine; Arabic rendered LTR, full stop on the wrong side |
| CHAT-12 | Pass | | Send disabled for empty/spaces |
| CHAT-13 | Partial | | "Offline" banner appears; the stream had finished, so mid-stream recovery couldn't be forced |
| CHAT-14 | Pass | | |
| CHAT-15 | Pass | | Rename via title; empty name reverts; delete confirm: "Are you sure you want to delete chat '…'? All chats, files and memory will be lost." |
| CHAT-16 | Pass | | Draft kept; button becomes "Queue message" |
| CHAT-17 | Partial | BUG-15 | Not forced down; jump-to-bottom button exists but has no accessible name |
| CHAT-18 | Pass | | No script, no dialog, no `javascript:` href |
| CHAT-19 | Partial | | Bearly Pro baseline ~0.4 s; other models not measured |
| CHAT-20 | **Fail** | BUG-07 | |
| CHAT-21 | Partial | | "Queue message" shown; queued send not exercised |
| CHAT-22 | Partial | | "Delete Query" present; not executed |
| CHAT-23 | Partial | | *1 Nov:* Edit in Canvas opens the reply in a Canvas panel (works). Read aloud gave no audible output in the headless test browser, so not verified |
| CHAT-24 | **Fail** | BUG-24 | Actions work; labels show ⌘ on Linux. *1 Nov:* Ctrl+D asks for confirmation (good); Download as Markdown opens a dialog with "Include tool activity", Copy, Download. "New" is labelled Ctrl+Shift+N, which Chrome reserves for incognito |
| CHAT-25 | **Fail** | BUG-13 | Voice-call chat auto-titled "Chat Title Request" |
| CHAT-26 | Not run | | Viewed only |
| CHAT-27 | Not run | | |

### Files
| ID | Result | Bug | Notes |
|---|---|---|---|
| FILE-01 ★ | Pass | | 5 accurate bullets; inline PDF viewer "1 / 10" |
| FILE-02 ★ | Pass | | "page 7" + exact quote |
| FILE-03 | Pass | | docx, xlsx, pptx, csv, md, txt, png, jpg all read correctly (OCR on images) |
| FILE-04 | Pass | | exe/zip accepted; zip listed; exe inspected "without running it" |
| FILE-05 | **Fail** | BUG-10 | Pro accepted 210 MiB and 310 MiB; agent read 325,000,000 bytes |
| FILE-06 | Pass | | Scanned invoice read correctly |
| FILE-07 | Pass | | "That password didn't work." → unlock works |
| FILE-08 | Pass | | 10 files, names kept straight, East Q3 = 210 |
| FILE-09 | Partial | | Drop uploads; drop-zone highlight not observed |
| FILE-10 | Blocked | | No device |
| FILE-11 | Partial | BUG-03 | Chip removed; but removed files stay stored (see BUG-03) |
| FILE-12 | Partial | SUG-05 | Upload finished ~3 min after reconnect; only a spinner while offline |
| FILE-13 | Pass | | `résumé_unique.txt`, `हिंदी_unique.txt` fine |
| FILE-14 | **Fail** | BUG-04 | |
| FILE-15 | Not run | | |
| FILE-16 | Partial | BUG-26 | First Enter with a just-attached PDF did nothing, no feedback (seen once) |
| FILE-17 | **Fail** | BUG-03 | |
| FILE-18 | **Fail** | BUG-12 | |
| FILE-19 | **Fail** | BUG-04 | |
| FILE-20 | Pass | | Browse files count + All/Uploaded filters |
| FILE-21 | **Fail** | BUG-14 | |

### Meetings
| ID | Result | Bug | Notes |
|---|---|---|---|
| MEET-01 ★ | Pass | | 48 s recording → accurate transcript in ~15 s; title "Launch Deadline and Action Items" |
| MEET-02 ★ | Not run | | Web Meetings page has no upload; chat transcription not exercised |
| MEET-03 | Not run | | |
| MEET-04 | Blocked | | Desktop only |
| MEET-05 | Pass | SUG | "Recording could not start. Check your audio permissions and try again." (no how-to link) |
| MEET-06 | **Fail** | BUG-05 | |
| MEET-07 | Not run | | |
| MEET-08 | Not run | | |
| MEET-09 | Pass | | "The launch deadline was moved to December 12. Arjun owns the testing plan." |
| MEET-10 | Partial | | Copy/Download present; transcript attached as `launch-deadline-and-action-items.md` |
| MEET-11 | Not run | | |
| MEET-12 | Not run | | Pro account |
| MEET-13 | Pass | | Overview / Key points / Decisions / Action items, nothing invented |
| MEET-14 | Not run | | |

### Search
| ID | Result | Bug | Notes |
|---|---|---|---|
| SRCH-01 ★ | Pass | | NVS-03 / GSLV-F18 news with ISRO + The Hindu sources, 22 s |
| SRCH-02 | Blocked | | External sites unreachable from the sandbox |
| SRCH-03 | **Fail** | BUG-06 | "Ravi" returns all 3 chats |
| SRCH-04 | Partial | BUG-06 | Content search ranks `falcon_report.pdf` first for "Quillfeather"; but lists everything |
| SRCH-05 | **Fail** | BUG-06 | |
| SRCH-06 | **Fail** | BUG-06 | |
| SRCH-07 | Not run | | |
| SRCH-08 | **Fail** | BUG-20 | |

### Voice & Dictation
| ID | Result | Bug | Notes |
|---|---|---|---|
| VOICE-01 ★ | Pass | | Accurate, not auto-sent. (Dictation was switched off again afterwards) |
| VOICE-02 | Partial | BUG-27 | Call works (GPT-Live 1); transcript has duplicated/truncated assistant lines |
| VOICE-03 | **Fail** | BUG-09 | |
| VOICE-04 | Not run | | |
| VOICE-05 | Blocked | | |
| VOICE-06/07/08 | Not run | | |
| VOICE-09 | **Fail** | SUG-08 | Text appears only after Stop |

### Extension: EXT-01…10 **Blocked**. Chrome Web Store isn't reachable from the test environment.

### Sync
| ID | Result | Bug | Notes |
|---|---|---|---|
| SYNC-01 ★ | Pass | | Message from device A visible on B in 5.3 s |
| SYNC-02 | **Fail** | BUG-08 | |
| SYNC-03 | Not run | | |
| SYNC-04 | **Fail** | BUG-08 | Rename not shown on B within 90 s (twice) |
| SYNC-05/06 | Not run | | |

### Billing & Credits
| ID | Result | Bug | Notes |
|---|---|---|---|
| BILL-01 ★, 02, 04 | Not run | | No purchases or plan changes on your real account (it is set to cancel on Nov 3) |
| BILL-03 | Blocked | | |
| BILL-05 | **Fail** | BUG-10, BUG-21 | |
| BILL-06 | Partial | | Boost tiles "$10 → Pay $12 / $20 → Pay $23 / $40 → Pay $46"; not purchased |
| BILL-07 | **Fail** | BUG-23 | |
| BILL-08 | **Fail** | BUG-02 | |
| BILL-09 | Pass | | $200 / $600 / $1000 per year |

### Settings & Account
| ID | Result | Bug | Notes |
|---|---|---|---|
| SET-01 | Pass | | Reverted to "Recommended" afterwards |
| SET-02 | **Fail** | BUG-33 | *1 Nov re-test with axe:* Dark/Dracula sidebar text 3.29:1 (21–22 contrast failures). 31 Oct had marked it Pass by eye. Reverted to System |
| SET-03 | Pass | | Reply ended "CHEERIO"; instructions cleared afterwards |
| SET-04, 06, 07, 08 | Not run | | Destructive or lasting changes |
| SET-05 | Partial | | Shows "Outside US & Canada: SMS isn't available in your region yet." |
| SET-09 | **Fail** | BUG-22 | |

### New modules
| ID | Result | Bug | Notes |
|---|---|---|---|
| TMPL-01 | Pass | | "Simple Hindi Translator" generated; Create disabled when empty |
| TMPL-02 | Pass | | `/Simple` → form → "कृपया server को restart करें।" |
| TMPL-03 | **Fail** | BUG-28 | Form submits on Shift+Enter (the opposite of the composer) |
| TMPL-04 | **Fail** | BUG-32 | *1 Nov:* edits autosave only the first ~5 typed characters (3/3). Delete not run (template kept). Description restored |
| TMPL-05 | Not run | | |
| RT-01 | Pass | | "Monday 9 AM IST" → "Mon Nov 2 · 3:30 AM UTC"; name, instructions, model filled |
| RT-02…06 | Not run | | Creating a scheduled routine was blocked by the test-session permission rules |
| PROJ-01 | Pass | | *1 Nov:* created "QA Temp Project" (opens its Settings), menu Rename/Archive/Delete; delete dialog warns and offers "Also delete all chats in this project"; deleted. Rename not run |
| PROJ-06 | Partial | | *1 Nov:* chat ⋯ → Move to project lists projects; move not completed |
| PROJ-02…05, 07 | Not run | | |
| SHARE-01 | **Fail** | BUG-29 | Page built in ~12 s, but "Ready to share" showed while still building |
| SHARE-02…04 | Not run | | Not published |
| TTS-01 | **Fail** | BUG-17 | |
| TTS-02 | Partial | | Fast/Studio/OpenAI; voices Zephyr, Puck, Charon, Kore… |
| TTS-03 | Not run | | |
| PRIV-01 | **Fail** | BUG-01 | |
| PRIV-02 | Pass | | *1 Nov:* ZDR-only on → Fable 5.1 and Muse Spark hidden from the model list; switched back off |
| PRIV-03/04 | Not run | | Would create encryption keys / recovery phrase |
| PRIV-05 | Pass | | Disclosure text present |
| CONN-01…03 | Not run | | No OAuth to third parties |
| DESK-01…04 | Blocked | | No desktop app (web shows the correct "get desktop" prompt for Work locally) |

### Non-functional
| ID | Result | Bug | Notes |
|---|---|---|---|
| NFR-01 | Pass | | Warm load to usable composer 1.2 s; heap 54 MB; DCL 274 ms |
| NFR-02 | Partial | BUG-07 | Offline banner good; sending offline isn't |
| NFR-03 | **Fail** | BUG-15 | axe: 4 violation types on every main screen |
| NFR-04 | Blocked | | |
| NFR-05 | Partial | BUG-16 | App at 390 px: no overflow, 16 px composer, drawer sidebar. Tap targets 14–28 px |
| NFR-06 | **Fail** | BUG-04, S-1, S-2 | |
| NFR-07 | **Fail** | BUG-07, BUG-02 | Generic or misleading errors |
| NFR-08 | Not run | | |

### Built-in tools *(module 19, run 1 Nov)*
| ID | Result | Bug | Notes |
|---|---|---|---|
| TOOL-01 | Pass | | 28 s: "Bengaluru: 23.8°C right now — source: Google Weather." |
| TOOL-02 | Pass | | 27 s: "About 2 hours 24 minutes … Bengaluru–Mysuru Expressway (NH 275)" |
| TOOL-03 | Pass | | Correct paper, authors, NeurIPS 2017, real abstract quote |
| TOOL-04 | **Fail** | BUG-34 | 92 s: "No qualifying wireless mouse under ₹1,500"; suggests MX Master 3S/4, M720 |
| TOOL-05 | Pass | | "bearly-qa-test-7731.com is available to register." (not cross-checked with a registrar) |
| TOOL-06 | Partial | | Asked "Which Sundar Pichai do you mean—Google's CEO?" instead of answering (SUG-23) |
| TOOL-07 | Pass | | Correct summary; transcript .txt attached |
| TOOL-08 | Pass | | `qa-test-plan.docx` (36 KB): title, "Regression Testing" heading, 3 bullets; preview works |
| TOOL-09 | Partial | | Values right; bars re-ordered North, East, South, West (data order N, S, E, W) (SUG-24) |
| TOOL-10 | Partial | | Sliders work, maths correct (1,863.93/month); defaults to **USD** for an India user (SUG-22) |
| TOOL-11 | Pass | | Correct image (Gemini Flash Lite image model); Edit / Change area / Extend / Combine offered |
| TOOL-12 | Pass | | `priya transcript.srt` matches priya.wav |
| TOOL-13 | Partial | | Replied "unknown" although another chat says "My name is Ravi" and the chat menu shows Memories: "All Chats". May be by design; needs a spec |
| TOOL-14 | Pass | | Consensus answer ("start with a modular monolith"), within limit |
| TOOL-15 | Pass | | `software-testing-basics.pptx` + PDF copy, exactly 3 slides |
| TOOL-16 | Pass | | `q3_by_region.xlsx` with total 535 |

## 3. Bugs
| ID | Title | Sev | Pri | Freq | Linked |
|---|---|---|---|---|---|
| BUG-01 | "ZDR models only" claim vs 2 Standard models and ZDR-only off by default | S2 | P1 | Always | PRIV-01 |
| BUG-02 | Default-model setting bypasses plan gating; misleading "Billing limit resolved" | S2 | P1 | Always | BILL-08 |
| BUG-03 | Attachments removed before sending remain stored/listed/searchable | S2 | P1 | Always | FILE-17 |
| BUG-04 | Keyed file URLs: no login needed, 36+ min lifetime, survive logout | S2 | P1 | Always | FILE-14/19 |
| BUG-05 | Silent recording stuck "Transcription in progress" (45+ min) | S2 | P2 | 1/1 | MEET-06 |
| BUG-06 | Chat & file search ignore relevance; nonsense matches everything | S2 | P2 | Always | SRCH-03/05/06 |
| BUG-07 | Offline send → generic error, no Retry, failed turn persists | S3 | P1 | Always | CHAT-20 |
| BUG-08 | Rename/delete not synced to open sessions; deleted chat still readable | S3 | P2 | 2/2 | SYNC-02/04 |
| BUG-09 | Voice call with no mic access: nothing happens | S3 | P2 | Always | VOICE-03 |
| BUG-10 | File-size limits differ: docs 300 MB / cards 10–2000 MB / Pro accepts 310 MiB | S3 | P2 | Always | FILE-05 |
| BUG-11 | Gated models not marked in the model picker | S3 | P3 | Always | CHAT-03 |
| BUG-12 | Same-content files: 2nd chip shows the 1st file's name | S3 | P3 | 3/3 | FILE-18 |
| BUG-13 | Voice-call chat auto-titled "Chat Title Request" | S3 | P3 | 1/1 | CHAT-25 |
| BUG-14 | Empty "New Chat 1" entries left in history | S3 | P3 | 2/2 | FILE-21 |
| BUG-15 | Accessibility: composer unnamed, icon buttons unnamed, contrast 2.05:1, nested controls | S3 | P2 | Always | NFR-03 |
| BUG-16 | Small mobile tap targets (14–28 px) | S4 | P3 | Always | NFR-05 |
| BUG-17 | TTS: asked for MP3, told "MP3", file is WAV | S3 | P3 | 1/1 | TTS-01 |
| BUG-18 | Login code: no resend, no visible throttling after 7 wrong codes, Sign in enabled with 1/3 words | S3 | P2 | Always | AUTH-04/06 |
| BUG-19 | Arabic replies rendered LTR | S3 | P3 | Always | CHAT-11 |
| BUG-20 | Source links are `<button>`s, not links (no URL, no copy link, no new tab) | S3 | P3 | Always | SRCH-08 |
| BUG-21 | Pricing page contradicts docs (Pro "deep research", tiers FAQ, Free models) | S3 | P2 | Always | BILL-05 |
| BUG-22 | Settings says desktop "v5.2.9"; download serves v5.2.11 | S4 | P3 | Always | SET-09 |
| BUG-23 | Usage: "Credits reset in 0 days (Nov 1)"; service total $0.26 vs used $0.27 | S4 | P3 | Always | BILL-07 |
| BUG-24 | ⌘ shortcut labels on Linux/Windows; Ctrl+F hijacked without a visible result | S4 | P3 | Always | CHAT-24 |
| BUG-25 | Sign-in email: no validation, no `autocomplete="email"`, low-contrast Continue | S4 | P3 | Always | AUTH-05 |
| BUG-26 | Enter while a file is processing does nothing, with no feedback | S4 | P3 | 1/2 | FILE-16 |
| BUG-27 | Voice transcript duplicates/truncates assistant lines ("Mm-h") | S4 | P3 | 1/1 | VOICE-02 |
| BUG-28 | Template form submits on Shift+Enter (composer: newline) | S4 | P3 | Always | TMPL-03 |
| BUG-29 | Share: "Ready to share" + Publish enabled while the Page is still building | S4 | P3 | 1/1 | SHARE-01 |
| BUG-30 | Public site: mobile header hides Product/Pricing/Privacy/Enterprise; dead link on /ai-assistant-comparison; pricing toggle has no state for screen readers | S3 | P2 | Always | — |
| BUG-31 | A sent prompt disappears from the chat when the agent gets stuck on a large attachment *(found 1 Nov while recording videos)* | S2 | P2 | 1/1 | FILE-05 |
| BUG-32 | Template edits autosave only the first ~5 typed characters *(1 Nov)* | S2 | P1 | 3/3 | TMPL-04 |
| BUG-33 | Dark / Dracula themes: sidebar text 3.29:1 contrast *(1 Nov)* | S3 | P2 | Always | SET-02, NFR-03 |
| BUG-34 | Best Pick says no wireless mouse under ₹1,500 exists; suggests ₹10k mice *(1 Nov)* | S3 | P2 | 1/1 | TOOL-04 |
| BUG-35 | Delete-chat dialog: "All chats, files and memory will be lost" *(1 Nov)* | S4 | P3 | Always | CHAT-15 |

**Full step-by-step reports** (first-time-user steps, test data, expected vs actual, screenshots and screen recordings) for every bug and security observation are in **[04-bug-reports.md](04-bug-reports.md)**.

## 4. Improvement suggestions
25 suggestions (SUG-21…25 added 1 Nov), each with steps to see the current behaviour, the proposed change and why: **[05-improvement-suggestions.md](05-improvement-suggestions.md)**.

## 4a. Test data
Every prompt (P-01…P-47) and every uploaded file used in this run: **[test-data/](test-data/README.md)**.

## 5. What worked well (worth saying in an interview)
- Core chat is fast (~0.4 s to first change) and solid: context, stop, rerun, edit with version history, and queueing.
- File understanding is excellent: 10 formats at once including OCR, page-accurate citations, a password-PDF flow with exact docs copy, and safe sandboxed inspection of .exe/.zip.
- Meeting transcription and summary were accurate, with no invented items.
- Logout properly revokes the session server-side (old token → 401).
- *1 Nov:* the built-in tools are strong. Weather, Maps, Scholar, Domain, YouTube, image generation, transcription, and real Word/Excel/PowerPoint files all worked first time in 25–60 s. The ZDR-only switch correctly hides non-ZDR models.
- The mobile web app is well built: 16 px input, drawer sidebar, camera upload, no overflow.
- Every public sitemap URL (206) returns 200, with 0 dead internal links.

## 6. Test artefacts left in the account
- Test chats (e.g. "TCP vs UDP Explained", "QA Renamed Chat ✅", "Report Summary Request", "Attached File Size", "Inspect ZIP and EXE Files", "Team Update Audio"…), 2 meeting recordings, prompt template **"Simple Hindi Translator"**, and about **1 GB of test uploads** (205 MB / 210 MiB / 310 MiB / 200 MB dummy files). Delete them from the sidebar if you don't want them.
- Every setting I changed was restored: dictation off, New Chats model "Recommended", instructions empty, theme System. Billing, encryption, connectors, phone and API keys were not touched.
- *Added 1 Nov:* about 16 tool-test chats ("Bengaluru Current Weather", "Budget Wireless Mouse Pick", "Regression Test Plan", "Loan EMI Calculator", "Bear Mascot Illustration", …) and their generated files; 3 "Model Identity" chats (BUG-02 videos); about 12 empty "New Chat 1/2" drafts (BUG-14); a Canvas copy of the TCP answer.
- *1 Nov restores:* ZDR-only back off, theme back to System, template description back to the original, "TCP vs UDP Explained" unpinned, renamed back and unarchived, temporary project deleted, New Chats model "Recommended".
- The test browser was logged out at the end.

## 7. Findings withdrawn after re-testing
- *"Non-ASCII file names are dropped."* My test tool mishandled non-ASCII **file paths**; the same names passed as in-memory files uploaded fine.
- *"Remove-file buttons are covered by neighbouring chips."* Hovering a chip reveals its button and keyboard removal works.
- *"Stopped replies lose their partial text."* A longer re-test kept the partial text after reload.

## 8. Automation
The Playwright scripts used for this run (sign-in, chat, files, meetings with a fake microphone, two-device sync, axe scan) were run ad hoc. `mvn test` (Selenium/TestNG) still covers public pages and basic chat; see README.
