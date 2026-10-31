# Bearly.ai — Test Execution & Bug Report

| Field | Value |
|---|---|
| Build under test | Web app build `38519b9` (built Oct 30, 2026), public site live on 2026-10-31. Desktop installers served: v5.2.11 |
| Tester | Pranav |
| Date | 2026-10-31 |
| Environment | Chromium 153 (Playwright), Linux, 1440×900 desktop and iPhone 13 emulation (390×844). A second browser was used as a second "device". Fake microphone fed from recorded speech for voice tests |
| Account / plan | Real **Pro** account (email-code sign-in). Total credit used for all testing: **$0.27** of $20 |
| Test plan | `02-bearly-test-cases.md` v2 (170 cases; rewritten during this run) |

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
| Settings & Account | 9 | 3 | 1 | 1 | 0 | 4 |
| Prompt Templates | 5 | 2 | 1 | 0 | 0 | 2 |
| Routines | 6 | 1 | 0 | 0 | 0 | 5 |
| Projects | 7 | 0 | 0 | 1 | 0 | 6 |
| Sharing (Pages) | 4 | 0 | 1 | 0 | 0 | 3 |
| Text-to-speech | 3 | 0 | 1 | 1 | 0 | 1 |
| Privacy / ZDR / E2E | 5 | 1 | 1 | 0 | 0 | 3 |
| Connectors & Desktop | 7 | 0 | 0 | 0 | 4 | 3 |
| Non-functional | 8 | 1 | 3 | 2 | 1 | 1 |
| **Total** | **170** | **42** | **32** | **22** | **24** | **50** |

**Bugs logged: 30** (6 S2-major, 15 S3-minor, 9 S4-trivial) in §3, plus 3 security observations, **20 improvement suggestions** in §4, and test-plan changes in `02-bearly-test-cases.md` ("What changed from v1").

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

## 2. Execution log (all 170 cases)
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
| CHAT-23 | Partial | | Query info / Edit in Canvas / Read aloud present; Read aloud exercised via TTS |
| CHAT-24 | **Fail** | BUG-24 | Actions work; labels show ⌘ on Linux |
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
| SET-02 | Pass | | Dark theme fine; reverted to System |
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
| TMPL-04/05 | Not run | | Template left in your account; delete it if not wanted |
| RT-01 | Pass | | "Monday 9 AM IST" → "Mon Nov 2 · 3:30 AM UTC"; name, instructions, model filled |
| RT-02…06 | Not run | | Creating a scheduled routine was blocked by the test-session permission rules |
| PROJ-01…07 | Partial (1) / Not run (6) | | Viewed project "1": Docs & Data, chat defaults, encryption, skills. No changes made |
| SHARE-01 | **Fail** | BUG-29 | Page built in ~12 s, but "Ready to share" showed while still building |
| SHARE-02…04 | Not run | | Not published |
| TTS-01 | **Fail** | BUG-17 | |
| TTS-02 | Partial | | Fast/Studio/OpenAI; voices Zephyr, Puck, Charon, Kore… |
| TTS-03 | Not run | | |
| PRIV-01 | **Fail** | BUG-01 | |
| PRIV-02…04 | Not run | | Would change your account's privacy/encryption |
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

---

### BUG-01 — [Web][Privacy] "ZDR models only" is not true by default
- **Env:** Web, Chrome 153, Pro, build 38519b9
- **Severity / Priority / Frequency:** S2 / P1 / Always
- **Steps:**
  1. Read bearly.ai home: *"MODELS — … ZDR models only"* and *"Every model on Bearly runs with zero data retention."*
  2. In the app open the model picker → **Show all models**.
  3. Open Settings → **Privacy & Security**.
- **Expected:** Every model is ZDR, or the marketing copy is qualified.
- **Actual:** **Fable 5.1** and **Muse Spark 1.3** are labelled **"Standard. This model's primary route has no verified zero-data-retention guarantee."** The **"Zero Data Retention only"** switch is **off** by default (`aria-checked=false`). Its description says it also controls "automatic fallbacks", so non-ZDR fallbacks are possible by default.
- **Evidence:** `evidence/models_all.png`, `evidence/set_privacy_security.png`
- **Fix:** Turn ZDR-only on by default (and let users opt into Standard models), or change the marketing copy.

### BUG-02 — [Web][Billing] Plan gating bypassed through the New Chats default model; misleading error
- **Severity / Priority / Frequency:** S2 / P1 / Always
- **Steps:**
  1. On **Pro**, pick Fable 5.1 in the chat model picker → correctly refused ("Fable 5.1 needs the Analyst plan or higher").
  2. Settings → **New Chats → Model → Fable 5.1** → accepted ("Saved").
  3. New Chat → picker shows **Fable 5.1** → send "Which model are you?"
- **Expected:** Locked models can't be set as defaults, or the chat shows the upgrade prompt.
- **Actual:** The reply is **"Billing limit resolved. Please rerun if necessary."** Nothing was resolved, no answer is produced, and there's no upgrade path.
- **Evidence:** `evidence/fable_gate.png`, `evidence/default_fable_chat.png`
- **Notes:** I reverted the setting to "Recommended" afterwards.

### BUG-03 — [Web][Files] Attachments removed before sending stay stored, listed and searchable
- **Severity / Priority / Frequency:** S2 / P1 / Always
- **Steps:**
  1. New chat → attach 10 small files → remove them with each chip's **Remove file** button (mouse click and keyboard both tried).
  2. Attach `tool.exe`, `archive.zip` → send. Later attach and send `locked_report.pdf`.
  3. Open **Browse files** in that chat; also search Files for a removed file's name.
- **Expected:** Only the 3 sent files exist.
- **Actual:** Browse files shows **22**. All 19 removed attachments are listed as "Uploaded" and returned by file search. Same for large files removed before sending (`big_210MiB.txt` is still listed).
- **Evidence:** `evidence/orphan_files.png`, `evidence/search_files.png`
- **Why it matters:** A user who attaches the wrong (sensitive) document and removes it believes it was never uploaded. It also counts against storage quotas.

### BUG-04 — [Web][Security] File download URLs are long-lived bearer links that survive logout
- **Severity / Priority / Frequency:** S2 / P1 / Always
- **Steps:**
  1. Open a chat with `falcon_report.pdf` → Download. The app fetches `https://bearly.ai/v1/magicbase/magic-files/open?fileId=…&key=v2.…`
  2. Request that URL with `curl` (no cookies, no token).
  3. Repeat after 35 min, and again after **logging out** (token revoked: all RPCs → 401).
- **Expected:** Downloads need the session, or the key expires in minutes and dies with the session.
- **Actual:** **HTTP 200, the original 8,429-byte decrypted PDF**, at 0 min, at 35 min, and after logout. Without the key, or with an altered key → 400, so it isn't guessable. The risk is leakage through browser history, proxies, logs or forwarding.
- **Fix:** Short TTL (≤5 min), bind to the session, revoke on logout and on file/chat deletion.

### BUG-05 — [Web][Meetings] Recording of silence never finishes transcription
- **Severity / Priority / Frequency:** S2 / P2 / 1 of 1 (speech recording in the same session finished in 15 s)
- **Steps:** Meetings → Start recording → 43 s of silence → Stop.
- **Expected:** "No speech detected" (or an empty transcript) within about a minute.
- **Actual:** **"Transcription in progress — You can leave this page."** still showing **45+ minutes** later.
- **Evidence:** `evidence/meet_silence_final.png`

### BUG-06 — [Web][Search] Chat & file search return everything for non-matching queries
- **Severity / Priority / Frequency:** S2 / P2 / Always
- **Steps:** Ctrl+F → type `zzqxv12345` (also `Ravi`, `"C++" OR <tag>`).
- **Expected:** Only matching chats; "No results" for nonsense.
- **Actual:** Always the same 3 chats with "10 matches / 4 matches / 1 match", even for `zzqxv12345`. The Files tab lists every file. For "ocean" the right chat is found, but the snippet shows the fruit list instead of the ocean lines.
- **Evidence:** `evidence/search_none2.png`, `evidence/search_files.png`
- **Notes:** It looks like semantic search with no relevance cut-off. Add a threshold plus keyword highlighting.

### BUG-07 — [Web][Chat] Offline send: generic error, no Retry, failure persists
- **Severity / Priority / Frequency:** S3 / P1 / Always
- **Steps:** Go offline (the app shows an orange **Offline** banner) → send a message → reconnect → reload.
- **Expected:** Send blocked or queued until online, or a specific offline error with **Retry**.
- **Actual:** The message is accepted; the reply is **"An error occurred. Please wait and try again."** with only "Get help". No retry after reconnecting, and the failed turn is still there after reload.
- **Evidence:** `evidence/offline.png`, `evidence/offline_send.png`

### BUG-08 — [Web][Sync] Rename/delete don't reach other open sessions
- **Severity / Priority / Frequency:** S3 / P2 / 2 of 2
- **Steps:** Same account in two browsers. On A rename a chat, then delete it (confirmed). Watch B for 90 s each.
- **Expected:** B updates live (messages already sync live in ~5 s).
- **Actual:** B's sidebar keeps the old name, and then the deleted chat, for 90+ s. Only a reload fixes it. B with the chat open could still read all its messages after deletion.

### BUG-09 — [Web][Voice] Voice call with no microphone access fails silently
- **Severity / Priority / Frequency:** S3 / P2 / Always
- **Steps:** Browser with no mic permission/device → "Start voice call".
- **Expected:** A message like the Meetings one ("Recording could not start. Check your audio permissions…").
- **Actual:** Nothing visible happens. Console: `realtimeVoice:error failed {errorName: UnknownError, errorCode: unavailable}`. An empty "New Chat 1" is also added to history.
- **Evidence:** `evidence/mic_denied_voice.png` vs `evidence/mic_denied_meet.png`

### BUG-10 — [Web][Files/Billing] File-size limits are inconsistent everywhere
- **Severity / Priority / Frequency:** S3 / P2 / Always
- **Sources:** docs *"Each attachment must be 300 MB or smaller"*; in-app Billing cards *Free 10MB, Pro 200MB, Analyst 600MB, Research 2GB*; /pricing says nothing.
- **Actual on Pro:** 205 MB (shown "196 MB"), 210 MiB and **310 MiB** all uploaded. The agent confirmed 325,000,000 bytes. Sizes are binary MiB labelled "MB".
- **Evidence:** `evidence/set_Billing.png`
- **Fix:** One limit, enforced at selection time with a clear message, and the same units everywhere.

### BUG-11 — [Web][Chat] Gated models aren't marked in the picker
S3/P3. Fable 5.1 looks identical to available models; clicking it opens Billing instead. Add a lock + "Analyst" badge.

### BUG-12 — [Web][Files] Same-content files show the wrong name
S3/P3, 3 of 3. Attach `dup_a.txt`, then `dup_b.txt` with identical bytes → two chips **both named `dup_a.txt`**. Content de-duplication is replacing the user's file name. Evidence `evidence/dedupe.png`.

### BUG-13 — [Web][Chat] Voice-call chat auto-titled "Chat Title Request"
S3/P3, 1 of 1. After a voice call the chat was named **"Chat Title Request"**, which looks like the title generator's own prompt leaking. Text chats are titled well ("TCP vs UDP Explained").

### BUG-14 — [Web][History] Empty "New Chat 1" chats left in history
S3/P3. Created when files are attached but never sent (it held 2 files, 0 messages), and when a voice call fails. They clutter history and hold uploaded files.

### BUG-15 — [Web][a11y] Accessibility violations on every main screen (axe-core, WCAG 2 A/AA)
S3/P2. App home, chat and Settings:
- **Composer** (`chat-input`, contenteditable textbox) has **no accessible name** (serious).
- **Icon buttons with no name** (critical): `+` (plus-menu), chat-config, jump-to-bottom.
- **"New Chat" button contrast 2.05:1** (needs 4.5:1). The sign-in "Continue" button is ~2.4:1.
- **Nested interactive controls** ×19–33 (sidebar rows are `role=button` containing buttons).
- **6 Settings dropdowns** have no accessible name.
- Billing toggle on /pricing exposes no on/off state.

### BUG-16 — [Mobile web] Small tap targets
S4/P3. At 390 px: Search 14×14, Open sidebar 24×24, message actions 28×28, dictation 28×28 (guideline 44×44). Otherwise the mobile app is solid: no overflow, 16 px composer, drawer sidebar, camera button.

### BUG-17 — [Web][TTS] Asked for MP3, got WAV
S3/P3. "Create an MP3…" → reply "The audio is ready in Voice Studio, where you can play and download the MP3." The served file is `bearly-voice-3Y0WCW.wav`, `audio/wav`.

### BUG-18 — [Web][Auth] Code screen: no resend, no visible throttling, partial code allowed
S3/P2. 7 consecutive wrong codes → 7× HTTP 401 and the same message each time, with no cooldown, lockout or CAPTCHA (I stopped at 7). There's no "Resend code" or "check spam" help. Sign in is enabled with only 1 of 3 words filled.

### BUG-19 — [Web][Chat] RTL text rendered LTR
S3/P3. Arabic reply `مرحبًا، سعيد بلقائك.` renders with `direction: ltr`, left-aligned, punctuation on the wrong side. Fix: `dir="auto"` on message paragraphs. Evidence `evidence/i18n.png`.

### BUG-20 — [Web][Search] Source links are buttons
S3/P3. Web-search citations render as `<button data-markdown-link="https://…">`. There's no hover URL, no "Copy link address", and no middle-click/new tab, and screen readers announce them as buttons.

### BUG-21 — [Web][Pricing] Pricing page contradicts docs and in-app plans
S3/P2. /pricing Pro card lists **"deep research"**, but docs say Deep Research needs **Analyst**. The FAQ says paid tiers differ only by credits + Fable, but docs list Deep Research, People Search and Ultra research. The Free card says "Claude, GPT, Gemini, and Grok models", but docs say "low-cost models". The in-app Research card says "Everything in Pro" instead of Analyst.

### BUG-22 — [Web][Settings] Desktop version mismatch
S4/P3. Settings → Get Desktop App: "Currently v5.2.9". `/download/*` redirects to **v5.2.11**.

### BUG-23 — [Web][Usage] Reset countdown and totals
S4/P3. On Oct 31: **"Credits reset in 0 days (Nov 1)"** (should be "tomorrow"/"in 1 day"). Usage by service total **$0.26** vs "Used this month **$0.27**".

### BUG-24 — [Web][Shortcuts] Mac labels on Linux/Windows
S4/P3. Header shows **"⌘ K"**; tooltips "Search conversations (⌘F)", "Open command menu (⌘K)". The command menu itself correctly shows "CTRL SHIFT N". Ctrl+K and Ctrl+L work.

### BUG-25 — [Web][Auth] Sign-in email field
S4/P3. Continue stays enabled for empty/`abc@`/`a b@x.com`; no `required` or `autocomplete="email"`. Button text contrast ~2.4:1 and it looks disabled. On mobile the email input is 13 px (iOS Safari zooms on focus).

### BUG-26 — [Web][Files] Enter ignored while a file is processing, without feedback
S4/P3, 1 of 2. Attach a PDF and press Enter straight away → nothing happens, no message. The docs say send waits for "Uploading/Analyzing"; the UI should show that.

### BUG-27 — [Web][Voice] Voice transcript duplicates/truncates lines
S4/P3. The saved call transcript shows "Bearly: Okay." twice, "Bearly: Mm-h" (cut off) twice, and "mm." twice around single user turns. Evidence `evidence/voice_transcript.png`.

### BUG-28 — [Web][Templates] Inconsistent submit key
S4/P3. The template form submits on **Shift+Enter** ("Submit (shift-enter)"). In the composer, Shift+Enter is a newline.

### BUG-29 — [Web][Share] "Ready to share" too early
S4/P3. After "Prepare share", the header showed **"Ready to share"** + Publish while the preview was still a spinner and the composer still showed Stop.

### BUG-30 — [Public site] Marketing site issues (from the logged-out audit)
S3/P2. At 375 px the header shows only News + Get Bearly, with no menu button, so Pricing, Product, Privacy and Enterprise are reachable only from the footer. `support.claude.com/en/articles/11049762-choose-a-claude-plan` on /ai-assistant-comparison returns 404. The sign-in page `<title>` is the old "Bearly.ai | AI at your fingertips". The Monthly/Yearly toggle has no `aria-pressed`/`role=switch`.

### Security observations (not exploited)
- **S-1:** The app shell `/dashboard/*` sends no `Content-Security-Policy` and no `frame-ancestors`/`X-Frame-Options`. HSTS preload, `nosniff` and referrer-policy are present.
- **S-2:** The session token is kept in `localStorage` (`_bearly:token`), so any XSS could read it. Combined with S-1, a strict CSP is the main defence. CHAT-18 found no XSS.
- **S-3:** "Continue with Microsoft" appears on the sign-in page only after a logout on the device. Check `loginCapabilities` behaviour.

## 4. Improvement suggestions (beyond the bugs)
| # | Area | Suggestion | Why |
|---|---|---|---|
| SUG-01 | Model picker | Lock icon + plan badge on gated models; a "ZDR / Standard" filter | Avoids surprise upgrade walls and supports privacy-minded choice |
| SUG-02 | Privacy | Show a small "ZDR" or "Standard" badge on each **answer**, not just in the picker | Users can see what happened to *this* message |
| SUG-03 | Chat | Collapse long user messages ("Show more") and turn 20k+ pastes into an attachment chip | A 50k paste made a 15,000 px bubble |
| SUG-04 | Chat | After Stop, label the reply "Stopped" with a **Continue** button | Clearer state, saves a re-prompt |
| SUG-05 | Uploads | Show % progress + ETA, and a "Waiting for connection…" state when offline | Large uploads showed only a spinner for 3 min |
| SUG-06 | Offline | Queue messages typed offline and send them on reconnect | Makes the existing Offline banner useful |
| SUG-07 | Meetings | Auto-create the summary after transcription; add "Upload recording" to the web Meetings page | Matches the "meeting notes" promise; the page has no upload today |
| SUG-08 | Dictation | Show interim text while speaking | Users can't tell it's working until they stop |
| SUG-09 | Search | Highlight the matched words in the snippet and jump to the matching message | Today's snippets don't show why a chat matched |
| SUG-10 | Sources | Favicon + domain chips with hover preview and real links | Easier to judge credibility (SRCH-02) |
| SUG-11 | Code | Language label + always-visible copy button on code blocks | Faster copy for developers |
| SUG-12 | Login | "Resend code (30 s)", "Check spam", the code expiry time, and auto-split on paste | Fewer stuck sign-ins |
| SUG-13 | Pricing | A credits calculator ("$20 ≈ N Opus messages / M meeting hours"), generated from the same source as in-app plan cards | Credits are abstract; and it fixes BUG-10/21 at the root |
| SUG-14 | Sync | Push sidebar events (rename/delete/move) over the same live channel messages use | Fixes BUG-08 |
| SUG-15 | History | Keep empty drafts out of history until the first message is sent | Fixes BUG-14 |
| SUG-16 | Files | "Remove" should delete the upload, with a "Removed" toast + Undo | Fixes BUG-03 with a forgiving UX |
| SUG-17 | Mobile | Add an installable web app (PWA) + "Add to Home Screen" prompt for Android | There's no Android app, and Android is most of the Indian market |
| SUG-18 | Voice | One permission-error component used by Meetings, Voice call and Dictation, with OS-specific "how to enable" steps | Consistent recovery |
| SUG-19 | Settings | Filter the New Chats model list by plan, or badge locked entries | Prevents BUG-02 by design |
| SUG-20 | Onboarding | Explain "ZDR", "Standard", "Full access" and credits inline the first time they appear | These are new concepts to most users |

## 5. What worked well (worth saying in an interview)
- Core chat is fast (~0.4 s to first change) and solid: context, stop, rerun, edit with version history, and queueing.
- File understanding is excellent: 10 formats at once including OCR, page-accurate citations, a password-PDF flow with exact docs copy, and safe sandboxed inspection of .exe/.zip.
- Meeting transcription and summary were accurate, with no invented items.
- Logout properly revokes the session server-side (old token → 401).
- The mobile web app is well built: 16 px input, drawer sidebar, camera upload, no overflow.
- Every public sitemap URL (206) returns 200, with 0 dead internal links.

## 6. Test artefacts left in the account
- Test chats (e.g. "TCP vs UDP Explained", "QA Renamed Chat ✅", "Report Summary Request", "Attached File Size", "Inspect ZIP and EXE Files", "Team Update Audio"…), 2 meeting recordings, prompt template **"Simple Hindi Translator"**, and about **1 GB of test uploads** (205 MB / 210 MiB / 310 MiB / 200 MB dummy files). Delete them from the sidebar if you don't want them.
- Every setting I changed was restored: dictation off, New Chats model "Recommended", instructions empty, theme System. Billing, encryption, connectors, phone and API keys were not touched.
- The test browser was logged out at the end.

## 7. Findings withdrawn after re-testing
- *"Non-ASCII file names are dropped."* My test tool mishandled non-ASCII **file paths**; the same names passed as in-memory files uploaded fine.
- *"Remove-file buttons are covered by neighbouring chips."* Hovering a chip reveals its button and keyboard removal works.
- *"Stopped replies lose their partial text."* A longer re-test kept the partial text after reload.

## 8. Automation
The Playwright scripts used for this run (sign-in, chat, files, meetings with a fake microphone, two-device sync, axe scan) were run ad hoc. `mvn test` (Selenium/TestNG) still covers public pages and basic chat; see README.
