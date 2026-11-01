# Bearly.ai — Detailed Test Cases (v2)

**Scope:** Web app (`bearly.ai/dashboard`), mobile web (incl. Android), iOS app, Mac / Windows / Linux desktop apps, Chrome extension (Bearly Companion)
**Modules:** Auth · Chat · Files · Meetings & Transcription · Search · Voice & Dictation · Extension · Sync · Billing & Plans · Settings & Account · Prompt Templates · Routines · Projects · Built-in tools · Sharing (Pages) · Text-to-speech · Privacy & Encryption · Connectors & Permissions · Cross-cutting
**Priority:** P0 = release blocker, P1 = major, P2 = minor, P3 = polish
**Type:** F = functional, N = negative, E = edge, U = UX, P = performance, S = security, X = cross-platform

> How to use this: run every ★ case (Daily Smoke, about 60 min) on Web Chrome and one
> other surface each release. Run P1 on any module that changed. Log results in
> `03-test-execution-report.md`. Expected results quote Bearly's own UI strings or
> docs (bearly.ai/docs) where they exist, so pass/fail is objective.

> **Test data:** every prompt (**P-01…P-47**) and file (`falcon_report.pdf`, `priya.wav`, …) these cases use is in
> [`test-data/`](test-data/README.md); its table maps each prompt and file to the case IDs below.
> Step-by-step bug reproductions: [`04-bug-reports.md`](04-bug-reports.md).

## What changed from v1 (after testing the live product on 2026-10-31)
| Change | Cases | Why |
|---|---|---|
| **Rewritten** for passwordless login | AUTH-01, 04, 05, 06 | Bearly has no passwords. Sign-in is an emailed **3-word code** (`word-word-word`), Google, or Microsoft |
| **Rewritten** | AUTH-03 | No Apple sign-in on web; "Continue with Microsoft" exists. Apple stays an iOS-only check |
| **Re-targeted** | FILE-10, VOICE-05, BILL-03, SYNC-01, env matrix | Docs: *"There is no Android app. On Android, use the web app."* Android = mobile web |
| **Rewritten** | FILE-04 | Bearly accepts any file type (.exe, .zip): the agent inspects it in a sandbox. "Clear unsupported message" was the wrong expectation |
| **Rewritten** | FILE-05 | Limits differ between docs (300 MB), plan cards (Free 10 MB / Pro 200 MB / Analyst 600 MB / Research 2 GB) and real behaviour (Pro accepted 310 MiB). Test per plan |
| **Rewritten** | MEET-02 | The web Meetings page records from the mic only; there is no upload button. Transcribe files via chat (400 MB limit) |
| **Rewritten** | MEET-01 | The summary is **not** automatic; it is generated with "Create summary" |
| **Rewritten** | CHAT-06, 07 | Real features: "Rerun with same model", "Edit Query", version pager `n / n` |
| **Removed** | CHAT-20 (old "share link") | Replaced by SHARE-01…04: sharing builds a **Page**, reviewed before publishing |
| **Removed** | SYNC-04, SYNC-05 moved to DESK-* | Desktop-only items grouped in a Desktop module |
| **Added** | 69 new cases | Prompt Templates, Routines, Projects, Sharing, TTS, Privacy/ZDR/E2E, Connectors & Permissions, Desktop, command menu, queueing, message versions, gating-via-settings, removed-attachment cleanup, file-link lifetime, live sidebar sync, RTL, offline send |

Total: **205 cases** (170 + 16 in module 19 + 19 more added on 1 Nov): v1's 104, minus 3 removed or moved, plus 69 new. 16 cases are marked *(rewritten)* or *(re-targeted)*, and several more have sharper expected results.

## 0. Test environment matrix
| Surface | Configs to cover |
|---|---|
| Web | Chrome latest, Firefox latest, Safari latest (macOS), Edge latest; 1366×768, 1920×1080, 2560×1440; zoom 125% |
| Mobile web | Android Chrome (Pixel + Samsung), iPhone Safari (no app installed); 360–430 px wide |
| iOS app | iOS 17+ (docs minimum) and latest; iPhone SE + Pro Max; iPad |
| Desktop | macOS (Apple Silicon + Intel), Windows 11 + 10 at 100%/150% DPI, Linux AppImage |
| Extension | Chrome latest (Bearly Companion); Edge/Brave if supported |
| Network | Normal, throttled "Slow 3G", offline, network switch mid-request |
| Accounts | Free (personal email), Pro, Analyst, Research, Organization seat (Max), new account, account with 500+ chats |
| Privacy toggles | "Zero Data Retention only" on and off; E2E encryption set up and not set up |

---

## 1. Authentication & Onboarding
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| AUTH-01 ★ | P0 | F | Sign in with emailed code *(rewritten)* | Existing or new email | 1. Open `/dashboard` 2. Enter email → Continue 3. Open the email 4. Paste the 3-word code → Sign in | Code email arrives in < 2 min. Pasting `word-word-word` into the first box fills all 3 boxes. Lands in the workspace; a new email gets an account + onboarding |
| AUTH-02 ★ | P0 | F | Continue with Google | Google account | Click "Continue with Google", pick account | Lands on chat home, correct name/email/avatar, plan badge correct |
| AUTH-03 | P1 | F | Continue with Microsoft *(rewritten)* | Microsoft account | Click "Continue with Microsoft" | Login works; same email via Google/Microsoft/code reaches the **same** account, no duplicate. Check the button is shown on a fresh device, not only after a previous login |
| AUTH-04 | P1 | N | Wrong / reused / partial code *(rewritten)* | Code requested | 1. Enter a wrong code 2. Enter an already-used code 3. Fill only 1 of 3 words | 1–2: "Codewords invalid. Make sure you typed them in correctly." 3: Sign in disabled. After N wrong attempts: cooldown or new-code requirement shown |
| AUTH-05 | P1 | N | Invalid email format | — | Enter `abc@`, `a b@x.com`, empty, spaces | Inline validation, Continue blocked; no code request sent |
| AUTH-06 | P1 | F | Code expiry and resend *(rewritten)* | Code requested | 1. Wait past the expiry window 2. Use the code 3. Look for resend | Expired code rejected with an "expired" message (not "invalid"). Resend available with a visible cooldown |
| AUTH-07 | P1 | F | Session persistence | Logged in | Close browser, reopen after 1 h | Still logged in |
| AUTH-08 | P1 | S | Logout revokes the session | Logged in | Account menu → Logout → confirm; replay the old token | Confirm dialog ("Bearly syncs your recent changes, then clears its data…"). Token removed. Old token gets 401 everywhere |
| AUTH-09 | P2 | U | Onboarding clarity | New account | Go through first run | Skippable, explains key features, no loops |
| AUTH-10 | P1 | X | Same account across surfaces | 1 account | Log in on web, desktop, iOS, extension | Same plan, history, settings everywhere |
| AUTH-11 | P1 | S | Devices list & remote sign-out *(new)* | Signed in on 2 devices | Settings → Devices → Sign out the other device | Other device returns to sign-in within seconds; list shows browser/OS, method (email code/Google), last active |
| AUTH-12 | P2 | S | Account enumeration *(new)* | Unknown email | Request a code for an unregistered email | Same screen and timing as a registered email. Nothing reveals whether an account exists |
| AUTH-13 | P1 | N | Sign in disabled state and rate-limit message *(new, 1 Nov)* | Code requested | 1. Click Sign in with all 3 boxes empty 2. Enter 15+ wrong codes quickly | 1. Sign in disabled until all words are filled, no request sent 2. After N attempts: "Too many attempts, try again in X min" (not "Codewords invalid") |
| AUTH-14 | P2 | N | Expired code message *(new, 1 Nov)* | Code requested and left unused past expiry | Enter the expired code | "This code has expired. Request a new one" + Resend |

## 2. Chat (core)
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| CHAT-01 ★ | P0 | F | Send a basic prompt | Logged in | "Explain TCP vs UDP in exactly 3 bullets" + Enter | Streams, exactly 3 bullets, input clears, chat auto-titled in sidebar |
| CHAT-02 ★ | P0 | F | Switch model | Pro+ | Pick model B, send; pick C, send | Model label under each reply matches the picker; usage pane shows that model |
| CHAT-03 | P1 | F | Model gating in picker | Plan below the model's tier | Pick a higher-tier model (e.g. Fable on Pro) | Doesn't switch; opens Billing with "<Model> needs the <Plan> plan or higher." The picker should mark locked models **before** the click |
| CHAT-04 ★ | P0 | F | Multi-turn context | — | "My name is Ravi" → "What is my name?" | "Ravi" |
| CHAT-05 | P1 | F | Stop generation | — | Long answer → Stop (send button becomes Stop) | Stops immediately, partial text kept after reload, can send again |
| CHAT-06 | P1 | F | Rerun with same model *(rewritten)* | Existing turn | Hover the user message → "Rerun with same model" | New answer; version pager shows `2 / 2` and both versions can be browsed |
| CHAT-07 | P1 | F | Edit Query *(rewritten)* | Existing turn | Hover → "Edit Query" → change text → Enter | New answer for the edited text; old version kept in the pager |
| CHAT-08 | P1 | F | Copy response | — | Copy on a reply with markdown and code | Clean markdown in clipboard; "Copied" feedback |
| CHAT-09 | P1 | F | Markdown, code, math | — | Ask for a table, Python block, `$E=mc^2$` | Table renders, code highlighted with a copy button, KaTeX renders |
| CHAT-10 | P2 | E | Very long prompt | — | Paste about 50k characters | Accepted (or a clear limit). The sent bubble should collapse; check page usability |
| CHAT-11 | P2 | E | i18n and RTL | — | Hindi, Tamil, Arabic, Chinese, emoji 🐻 | Correct glyphs. Arabic paragraphs render RTL (`dir="auto"`), punctuation on the correct side |
| CHAT-12 | P1 | N | Empty / whitespace prompt | — | Enter on empty or spaces only | Nothing sent, Send disabled |
| CHAT-13 | P1 | N | Network drop mid-stream | — | Go offline while streaming | "Offline" banner; stream resumes or a Retry is offered; no duplicates |
| CHAT-14 | P1 | F | Shift+Enter newline | Web/desktop | Shift+Enter | Newline, not sent |
| CHAT-15 | P1 | F | New chat / rename / delete | — | Rename via title (also empty name), delete via More actions or Ctrl+D | Rename reflected in sidebar; empty name reverts; delete confirm names the chat and warns files and memory are lost |
| CHAT-16 | P2 | U | Typing while a reply streams | — | Type during streaming | Draft kept; Send becomes "Queue message" |
| CHAT-17 | P2 | U | Auto-scroll | — | Scroll up during streaming | Not forced down; jump-to-latest button appears, with an accessible name |
| CHAT-18 | P1 | S | HTML/script injection | — | Send `<img src=x onerror=alert(1)>`, `<script>`, `javascript:` link | Rendered as text; no script, no dialog, no `javascript:` href |
| CHAT-19 | P2 | P | Time to first token | — | 5 prompts on each featured model | Baseline under ~3 s, tracked per release |
| CHAT-20 | P1 | N | Send while offline *(new)* | Offline banner shown | Send a message | Send blocked or queued until online, or a specific "You're offline" error with Retry. Not a generic error that stays in the chat |
| CHAT-21 | P1 | F | Queue message *(new)* | Reply streaming | Type → "Queue message" | Queued message sends automatically after the current answer |
| CHAT-22 | P2 | F | Delete Query *(new)* | Existing turn | Hover → Delete Query | "Delete this query?" … "This action cannot be undone." → turn removed |
| CHAT-23 | P2 | F | Query info / Read aloud / Edit in Canvas *(new)* | Existing reply | Use each hover action | Info shows model/cost/time; Read aloud plays audio; Canvas opens the reply for editing |
| CHAT-24 | P2 | F | Command menu *(new)* | — | Ctrl/⌘+K; run New, Rename, Generate New Name, Download as Markdown, Refresh | Each action works; shortcut labels match the OS (Ctrl on Windows/Linux) |
| CHAT-25 | P2 | F | Auto-title quality *(new)* | — | Start chats by text, voice call, file only | Titles describe the content. Never internal names like "Chat Title Request" |
| CHAT-26 | P2 | F | Per-chat settings *(new)* | — | Settings → This Chat: Response Style, Web Search off, Temperature, Instructions | Applied to this chat only; "Reset all to defaults" works |
| CHAT-27 | P2 | U | Long paste becomes attachment *(new)* | — | Paste 200k+ characters | Accepted as an attachment or a clear limit; UI stays responsive |
| CHAT-28 | P1 | F | Delete chat removes its files *(new, 1 Nov)* | Chat with an attached file | ⋯ / Ctrl+D → Delete; reopen the chat URL; search Files for the file | Chat gone ("Not found"); its files no longer listed or searchable |
| CHAT-29 | P1 | F | Advertised models are selectable *(new, 1 Nov)* | — | Compare homepage/Enterprise model list with Show all models and Council settings | Every advertised family selectable, or copy clarifies |
| CHAT-30 | P2 | U | Message timestamps *(new, 1 Nov)* | Existing chat | Hover a user and an assistant message; open Query info | Time visible on hover; full date and time available |
| CHAT-31 | P2 | U | Jump to latest message *(new, 1 Nov)* | Long chat | Scroll up; click the ⌄ button (desktop and 390 px) | Visible, labelled button returns to the latest message |

## 3. Files
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| FILE-01 ★ | P0 | F | Upload PDF and summarize | 10-page text PDF | + → Attach files; "Summarize in 5 bullets" | Uploading/Analyzing state visible; summary matches content |
| FILE-02 ★ | P0 | F | Q&A with page citation | Fact only on page 7 | Ask for it + page number | Correct fact, cites page 7 |
| FILE-03 | P1 | F | Supported types | Sample set | .pdf .docx .txt .md .csv .xlsx .pptx .png .jpg | Each read correctly (images via OCR) |
| FILE-04 | P1 | F | Arbitrary types (.exe, .zip) *(rewritten)* | .exe, .zip | Attach and ask what's inside | Accepted; zip contents listed; executable inspected **without running it**; no hang |
| FILE-05 | P1 | E | Size limit per plan *(rewritten)* | Files just under and over each plan's documented limit | Upload on Free, Pro, Analyst | Behaviour matches **one** published limit; the over-limit file is rejected up front with the limit in MB. Displayed units consistent (MB vs MiB) |
| FILE-06 | P1 | E | Scanned PDF (image-only) | Scanned PDF | Ask about its content | OCR text correct, or a clear "couldn't read" message; no invented summary |
| FILE-07 | P2 | E | Password-protected PDF | Locked PDF | Attach; wrong then right password | "Password required." → "That password didn't work." → "Unlock and add" works |
| FILE-08 | P1 | F | Multiple files | 3+ files | Ask to compare A and C | Correct files, names kept straight |
| FILE-09 | P1 | F | Drag and drop | Web/desktop | Drag a file onto the chat | Drop zone highlights; upload starts |
| FILE-10 | P1 | F | Mobile upload sources *(re-targeted)* | iOS app; Android Chrome | Files, Photos, "Take photo" (+ menu, camera button) | All sources work; permission prompts appear |
| FILE-11 | P1 | N | Cancel upload | Large file | Remove the chip mid-upload | Upload request aborted, nothing stored, can re-upload |
| FILE-12 | P1 | N | Network loss during upload | — | Offline at 50%, then back | "Waiting for connection" state; resumes or offers Retry; no endless spinner |
| FILE-13 | P2 | E | Unicode file name | `रिपोर्ट 2026 (final).pdf`, `résumé.txt` | Upload | Name shown correctly |
| FILE-14 | P1 | S | File privacy | User A's file | Open its URL as user B and logged out | Without the key: denied. Keyed URL: short lifetime, revoked on logout/deletion |
| FILE-15 | P2 | P | Large document time | 300-page PDF | Upload and ask | Time recorded; progress shown; no timeout |
| FILE-16 | P1 | U | Send while a file is processing *(new)* | File shows Uploading/Analyzing | Press Enter | Docs: send waits. Expect visible feedback ("Waiting for file…"), not a silent no-op |
| FILE-17 | P1 | S | Removed attachments are deleted *(new)* | — | Attach 3 files, remove 2, send | Browse files lists only the sent file; removed files don't appear in search or storage |
| FILE-18 | P2 | E | Same bytes, different names *(new)* | `a.txt` and `b.txt` with identical content | Attach both | Two chips named `a.txt` and `b.txt` (or one chip with a clear "duplicate" note). Never two chips with the same name |
| FILE-19 | P1 | S | Keyed file-link lifetime *(new)* | File download URL captured | Fetch it logged out at 0 min, 30 min, after logout, after chat delete | Expires quickly and stops working after logout and after deletion |
| FILE-20 | P2 | F | Browse files panel *(new)* | Chat with uploads + generated files | Header "Browse files (n)"; filters All / Project / Uploaded / Generated | Count and filters correct; preview opens; download returns the original bytes |
| FILE-21 | P2 | F | Orphan chats from uploads *(new)* | — | Attach a file on a new chat, then leave without sending | No empty "New Chat 1" left in history (or it's clearly a draft) |

## 4. Meetings & Transcription
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| MEET-01 ★ | P0 | F | Record in-person meeting (web) *(rewritten)* | Mic permission | Meetings → Start recording, talk 2 min, Stop | "Saved continuously on this device"; uploads after stop; accurate transcript; auto-title |
| MEET-02 ★ | P0 | F | Transcribe an audio file *(rewritten)* | 10-min .mp3/.m4a/.mp4 | Attach in chat, "Transcribe this" | Transcript with timestamps; files over 400 MB: "File is too large to transcribe. Max size: 400 MB" |
| MEET-03 | P1 | F | Speaker labels | 2-speaker audio | Transcribe | Speakers told apart; renamable |
| MEET-04 | P1 | F | Online meeting capture (desktop) | Desktop app | Record a Zoom/Meet/Teams call | Other side captured, not only the mic |
| MEET-05 | P1 | N | Mic permission denied | Block mic | Start recording | "Recording could not start. Check your audio permissions and try again." Should also link to how to enable it |
| MEET-06 | P1 | E | Silence / no speech | — | Record 40–60 s of silence | Finishes within ~1 min with "No speech detected". Never stuck in "Transcription in progress" |
| MEET-07 | P1 | E | Indian English / Hinglish | Sample audio | Transcribe | Accuracy recorded |
| MEET-08 | P1 | E | Long meeting | 74–76 min | Record past "Up to 1 hr 15 min" | Stops or warns cleanly at the limit; nothing lost |
| MEET-09 | P1 | F | Ask about a meeting | Transcribed meeting | Add to chat → "Attach the transcript to a new draft" → ask | Answer grounded in the transcript |
| MEET-10 | P1 | F | Export / copy | — | Copy, Download audio, transcript `.md` | Complete content and timestamps |
| MEET-11 | P1 | N | Tab closed / sleep during recording | — | Close the tab mid-recording, reopen | Recording up to that point recoverable ("saved continuously") |
| MEET-12 | P2 | F | Minutes / credits | Free | Exceed allowance | Clear limit message; recording not cut silently |
| MEET-13 | P1 | F | Create summary *(new)* | Transcribed meeting | "Create summary" | Overview, Key points, Decisions, Action items with owners, Open questions; nothing invented; Refresh works |
| MEET-14 | P2 | F | Pause / resume *(new)* | Recording | Pause 10 s → resume | Paused span excluded; timer correct |

## 5. Search
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| SRCH-01 ★ | P0 | F | Web search with sources | — | "Latest ISRO mission news this month" | Current answer; "Web Search" action shown; source links open the right pages |
| SRCH-02 | P1 | F | Citation accuracy | — | Open 3 cited sources | Each source supports its claim |
| SRCH-03 | P1 | F | Search own chats | 20+ chats | Ctrl/⌘+F, search a word from an old chat | Only matching chats; snippet shows the matching message; click opens it |
| SRCH-04 | P1 | F | Search files | Uploaded files | Files tab: search by name and by content | Correct files first |
| SRCH-05 | P2 | N | No results | — | `zzqxv12345` | "No results" empty state. No unrelated chats or files |
| SRCH-06 | P2 | E | Special characters | — | `"C++" OR <tag>` | No crash, escaped, relevant results only |
| SRCH-07 | P2 | P | Search latency | 500+ chats | Search | Results in ~1–2 s |
| SRCH-08 | P2 | U | Source links are real links *(new)* | Answer with sources | Hover, middle-click, right-click "Copy link" | Real `<a href>` with URL preview; opens in a new tab |

## 6. Voice & Dictation
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| VOICE-01 ★ | P0 | F | Dictation *(rewritten)* | Settings → Dictation → Enable | Mic button (or hold Right Shift), speak, stop | Accurate text in the composer, not auto-sent (default "Fill the composer") |
| VOICE-02 | P1 | F | Voice call | Mic | "Start voice call", 3 spoken turns, End | Listening/Thinking/Speaking states; replies spoken; transcript saved in chat without duplicate lines |
| VOICE-03 | P1 | N | Mic denied / no mic | — | Start voice call and dictation | Clear message with how to fix. No silent no-op |
| VOICE-04 | P1 | E | Background noise | Noise | Dictate | No junk text in silence |
| VOICE-05 | P1 | E | Interruption *(re-targeted)* | iOS app / Android Chrome | Phone call during voice mode | Pauses or ends cleanly |
| VOICE-06 | P2 | F | Language support | — | Dictate Hindi | Correct, or a clear "unsupported" |
| VOICE-07 | P2 | F | Dictation vocabulary & replacements *(new)* | Add "Bearly" + a replacement | Dictate | Custom words spelled right; replacements applied |
| VOICE-08 | P2 | F | Voice model gating *(new)* | Free | Settings → Voice → Model | "Choosing a voice model needs a paid plan." |
| VOICE-09 | P2 | U | Live dictation preview *(new)* | — | Dictate 10 s | Interim text visible while speaking |
| VOICE-10 | P2 | F | Voice call with any chat model *(new, 1 Nov)* | Fake mic | Pick GLM 5.3 / Grok 4.7 / Haiku 5.5 → Start voice call | Call works, or the voice button is hidden for unsupported models |

## 7. Chrome Extension (Bearly Companion)
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| EXT-01 ★ | P0 | F | Install & log in | Chrome | Install, open side panel, log in | Same account, plan correct; appears under Settings → Devices → "Browser extension and API keys" |
| EXT-02 ★ | P0 | F | Summarize current page | Article open | Summarize | Summary of the active tab |
| EXT-03 | P1 | F | YouTube summary | Captioned video | Summarize | Uses the transcript |
| EXT-04 | P1 | F | Selected text actions | Select text | Rewrite/Explain/Translate | Acts on the selection only |
| EXT-05 | P1 | E | Restricted pages | `chrome://`, Web Store, PDF viewer | Open panel | Clear "can't work here" |
| EXT-06 | P1 | E | Paywalled pages | — | Summarize | Uses visible content or explains |
| EXT-07 | P1 | F | History sync | — | Chat in the extension | Appears in web history |
| EXT-08 | P2 | P | Page performance | Heavy site | On vs off | No slowdown or console noise |
| EXT-09 | P2 | S | Permissions | — | Review manifest | Minimal, explained |
| EXT-10 | P2 | X | Desktop Chrome control link *(rewritten)* | Desktop app + extension | Desktop app drives Chrome | Works; removing the key in Devices disconnects it |

## 8. Sync & Cross-platform
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| SYNC-01 ★ | P0 | X | Message sync *(re-targeted)* | Send on iOS / phone browser, watch web with the chat open | Appears live within ~5 s |
| SYNC-02 | P1 | X | Delete sync | Delete on device A while B has the chat open | B's sidebar updates live; B shows "This chat was deleted" instead of a still-readable chat |
| SYNC-03 | P1 | X | Simultaneous sends | Send from 2 devices at once | No lost or duplicated messages |
| SYNC-04 | P1 | X | Rename sync *(new)* | Rename on A | B's sidebar updates live (not only after reload) |
| SYNC-05 | P2 | E | Large history | 1000 chats | Smooth sidebar scroll |
| SYNC-06 | P2 | X | Settings sync *(new)* | Change New Chats default on web | Desktop/iOS new chats use it |

## 9. Billing, Plans & Credits
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| BILL-01 ★ | P0 | F | Upgrade Free → Pro (sandbox) | Upgrade | Payment works; features unlock without logout; receipt email |
| BILL-02 | P1 | F | Cancel / reactivate | Cancel; then Reactivate | "Cancels on <date>" shown; access until then; Reactivate restores |
| BILL-03 | P1 | F | iOS in-app purchase *(re-targeted)* | Subscribe on iOS | Plan on web; Restore works; Credit Boost hidden in the iOS app (docs) |
| BILL-04 | P1 | N | Declined card | Decline test card | Clear error, no plan change |
| BILL-05 | P2 | U | Plan copy consistency | Compare /pricing, in-app Billing cards, docs plan table, App Store | Same prices, features, **file limits** and model access everywhere |
| BILL-06 | P1 | F | Credit Boost *(new)* | Billing → $10/$20/$40 | Shows "Pay $12/$23/$46"; credits added once; fees explained |
| BILL-07 | P1 | F | Usage meter accuracy *(new)* | Use known services | Settings → Usage per service = sum of activity; header % matches; "Credits reset in …" correct (no "0 days" on the day before) |
| BILL-08 | P1 | N | Gating can't be bypassed via settings *(new)* | Pro | Settings → New Chats → Model = Fable 5.1 (Analyst) | Locked models unavailable as defaults, or the chat shows the upgrade prompt. Never a misleading error |
| BILL-09 | P2 | F | Yearly toggle *(new)* | Toggle Monthly/Yearly | Yearly = 10× monthly; "17% off" |
| BILL-10 | P2 | U | Daily usage history / statement *(new, 1 Nov)* | Usage pane: look for per-day history and export | Per-day list and export |
| BILL-11 | P2 | F | Organization per-seat billing *(new, 1 Nov)* | Enterprise page and Organizations: per-person plans, spend limits, usage by member | Seats billed per person; limits per person/group; usage per member |
| BILL-12 | P1 | U | Free plan: paid features discoverable and explained *(new, 1 Nov)* | Sign in with a Free account; check sidebar, "/" menu, model picker; use one paid tool | Paid features visible with lock + plan name; upgrade explained before use |
| BILL-13 | P2 | U | Free plan: usage visibility *(new, 1 Nov)* | Free account → Settings | Free users can see their allowance and what is left |

## 10. Settings & Account
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| SET-01 | P1 | F | Default model for new chats | New Chats → Model | New chats use it |
| SET-02 | P1 | F | Theme | Appearance: System/Light/Dark + named themes; font size | Applied everywhere; readable |
| SET-03 | P1 | F | Default instructions | New Chats → Instructions | Applied to new chats |
| SET-04 | P1 | S | Delete account | Account → Delete → email verification code | Six-digit code within 10 min + typed email; data removed; can't sign in |
| SET-05 | P2 | F | Notifications / SMS | Phone & SMS (US/Canada) | Outside US/CA: "SMS isn't available in your region yet" |
| SET-06 | P1 | F | Personalization *(new)* | Name/Occupation/About | Used in replies; project "Disable Personalization" overrides |
| SET-07 | P1 | S | API keys *(new)* | Create key, call API, delete key | Key shown once; calls work; deleted key gets 401 |
| SET-08 | P2 | F | Lock all data *(new)* | Account → Lock | Password-protected notebooks need the password again |
| SET-09 | P2 | F | Desktop app download *(new)* | Get Desktop App | Version shown = version served by `/download/*` |
| SET-10 | P1 | S | Delete account flow *(new, 1 Nov)* | Account → Delete → read the dialog → Cancel | Lists what is deleted and kept, email verification, delay, cancellation |

## 11. Prompt Templates *(new module)*
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| TMPL-01 | P1 | F | Create with AI Prompt Builder | Describe a template → Create Prompt | Name, description, `{{ fields }}` generated; Create disabled when empty |
| TMPL-02 | P1 | F | Use via slash command | Type `/Name` → Enter → fill form → Submit | Prompt sent with field values; result correct |
| TMPL-03 | P2 | U | Form keyboard behaviour | In the field press Enter, then Shift+Enter | Consistent with the composer (Enter vs Shift+Enter) or clearly labelled |
| TMPL-04 | P2 | F | Edit / delete template | Edit fields, delete | Changes apply; deleted template gone from `/` |
| TMPL-05 | P2 | F | Field types | Short Answer / Paragraph / Allow File Uploads | Each renders and validates |

## 12. Routines *(new module)*
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| RT-01 | P1 | F | Draft from a description | "Every Monday 9am IST, 3-bullet AI news" → Draft routine | Name, instructions, model, schedule in the **user's** timezone; "Next:" run correct |
| RT-02 | P1 | F | Create, run now, results | Create routine → run | Result in its chat; notification filter respected |
| RT-03 | P1 | F | Edit / pause / delete | — | Paused routine doesn't run; deleted routine never runs again |
| RT-04 | P1 | S | Tool permissions | Standard vs Full access | Standard asks for approval; Full access only on the approved desktop |
| RT-05 | P2 | F | Free plan | Open Routines on Free | Upgrade prompt (docs) |
| RT-06 | P2 | F | Create from chat | "Remind me every Friday…" in chat | Routine drafted from the chat |

## 13. Projects *(new module)*
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| PROJ-01 | P1 | F | Create / rename / delete project | — | Sidebar updates; delete warns about contents |
| PROJ-02 | P1 | F | Project files (Docs & Data) | Upload to the project; ask in a project chat | Answers use project files; "Always Search Documents" honoured |
| PROJ-03 | P1 | F | Project chat defaults | Project Chat Settings: model, instructions | New project chats use them |
| PROJ-04 | P1 | S | Collaboration roles | Invite a member as reader | Reader can't add files: "You need writer access to add files to this Project." |
| PROJ-05 | P1 | S | Project encryption | Encryption → Protect | Protected; members without keys can't read |
| PROJ-06 | P2 | F | Move chat to project | Chat More actions → Move to project | Chat appears under the project |
| PROJ-07 | P2 | F | Project skills | Install from source | Skill available in project chats |

## 14. Sharing (Pages) *(new module, replaces old CHAT-20)*
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| SHARE-01 | P1 | F | Prepare a Page | Share → Prepare share | Page built ("Your full conversation isn't shared"); "Ready to share" only when the preview has finished building |
| SHARE-02 | P1 | F | Publish and open logged out | Publish; open link in incognito | Read-only Page with link preview |
| SHARE-03 | P1 | S | Unpublish | Revoke | Link stops working (incl. CDN cache) |
| SHARE-04 | P2 | S | Content scope | Chat with private files | Page contains only what's in the preview |

## 15. Text-to-speech / Voice Studio *(new module)*
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| TTS-01 | P1 | F | Generate speech | "Make an MP3 of: …" | Voice Studio card; audio plays; download in the **requested** format (or the format named correctly) |
| TTS-02 | P2 | F | Voices and engines | Fast / Studio / OpenAI; voices Zephyr, Puck, … | Preview plays; chosen voice used |
| TTS-03 | P2 | F | Read aloud | Message → Read aloud | Plays and stops cleanly |

## 16. Privacy, ZDR & Encryption *(new module)*
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| PRIV-01 | P0 | S | ZDR labelling vs claims | Model picker "Show all models" | Every model labelled ZDR or Standard; marketing claims ("ZDR models only", "Every model runs with zero data retention") match the product defaults |
| PRIV-02 | P1 | S | "Zero Data Retention only" toggle | Turn on; try a Standard model (Fable, Muse Spark) | Standard models blocked/hidden; fallbacks also ZDR |
| PRIV-03 | P1 | S | Set up E2E encryption | Set up this device + recovery phrase | Chats sealed; second device needs approval; recovery phrase restores |
| PRIV-04 | P1 | S | E2E recovery | Lose device, use recovery phrase | Access restored; wrong phrase rejected |
| PRIV-05 | P2 | S | Personalization disclosure | Personalization screen | "This is shared with AI models in your conversations." visible |
| PRIV-06 | P1 | F | ZDR-only keeps chat context *(new, 1 Nov)* | Turn ZDR-only on → tell a fact → ask it back → turn off | Context kept (ZDR affects provider retention, not Bearly history) |
| PRIV-07 | P1 | F | Open/close E2E setup without finishing *(new, 1 Nov)* | New Chat → Set up end-to-end encryption → close | Message box unchanged |

## 17. Connectors, Permissions & Desktop *(new module)*
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| CONN-01 | P1 | F | Add a built-in connector | Settings → Connectors → GitHub → Add → OAuth | Connected; tools usable in chat |
| CONN-02 | P1 | F | Custom MCP server | Add custom MCP server URL | Tools listed; errors readable for a bad URL |
| CONN-03 | P1 | S | Permission prompts | Standard mode chat uses a tool | Approval asked; "Full access" skips it (warned as not recommended) |
| DESK-01 | P1 | F | Work locally (desktop) | Pick a folder | Reads/edits only that folder; web shows the "Get Bearly for desktop" prompt |
| DESK-02 | P1 | F | Global shortcut | Ctrl/⌘+Shift+P from another app | Bearly comes to front |
| DESK-03 | P1 | F | Auto-update | Install the previous version | Update applied; login and settings kept |
| DESK-04 | P1 | S | Computer use / Chrome control | Ask to do a browser task | Approval prompts; visible control; stop works |

## 18. Cross-cutting (non-functional)
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| NFR-01 | P1 | P | Load time | Cold and warm load to usable composer | Baseline (warm ≈1.2 s measured) |
| NFR-02 | P1 | N | Offline mode | Open the app offline | "Offline" banner; cached history readable |
| NFR-03 | P1 | U | Accessibility (web) | Keyboard-only, NVDA/VoiceOver, axe | Composer has a name; icon buttons named; no nested interactive controls; contrast ≥ 4.5:1 |
| NFR-04 | P2 | U | Mobile a11y | Dynamic Type, TalkBack | No clipping; announced |
| NFR-05 | P1 | U | Responsive web | 360–430 px | Sidebar drawer; composer 16 px font (no iOS zoom); tap targets ≥ 44 px |
| NFR-06 | P1 | S | Security basics | Headers, token storage, rate limits | CSP + frame-ancestors on the app; token not in URLs; login attempts throttled |
| NFR-07 | P2 | U | Error message quality | Cause 5 errors | Specific and actionable (offline ≠ "An error occurred") |
| NFR-08 | P2 | P | Memory | One chat tab for 1 h | Heap stable |
| NFR-09 | P2 | U | Support from desktop and mobile *(new, 1 Nov)* | Get help → Contact Support on desktop and 390 px | Same in-app form; empty message blocked |
| NFR-10 | P2 | U | Public site navigation state *(new, 1 Nov)* | Click each top-menu page; open Pricing | Current page highlighted; items never removed |
| NFR-11 | P1 | S | Download flow from the website *(new, 1 Nov)* | Click Get Bearly on Windows/Mac/Linux/iPhone/Android | Download only after an explicit click; served from bearly.ai |
| NFR-12 | P1 | F | Pricing plan buttons *(new, 1 Nov)* | /pricing → Get Started / Get Pro / Get Analyst | Open sign-up / checkout for that plan |

---

## 19. Built-in tools (the "/" menu) *(new module, 1 Nov)*
Type `/` in the composer to see the tools. Prompts **P-32…P-47** are in `test-data/`. Each case: New Chat → send the prompt → check the reply and any files in **Browse files**.

| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| TOOL-01 | P2 | F | Weather | — | Send P-32 | Current temperature for the city, with source, < 60 s |
| TOOL-02 | P2 | F | Maps (routes) | — | Send P-33 | Plausible drive time and main road (Bengaluru–Mysuru ≈ 2–3 h via NH 275 expressway) |
| TOOL-03 | P2 | F | Scholar | — | Send P-34 | Correct paper ("Attention Is All You Need", Vaswani et al., NeurIPS 2017) with a real quote from the abstract |
| TOOL-04 | P1 | F | Best Pick | — | Send P-35 | A product that meets the budget, with price and store. If nothing fits, the "closest matches" are close to the budget |
| TOOL-05 | P2 | F | Domain research | — | Send P-36 | Availability answer for the exact domain |
| TOOL-06 | P2 | U | People Search | — | Send P-37 (a well-known public figure) | A cited brief. Asks to disambiguate only when the name is truly ambiguous |
| TOOL-07 | P2 | F | YouTube digest | — | Send P-38 | Summary matches the video; transcript file attached |
| TOOL-08 | P1 | F | Document editor: Word | — | Send P-39 | Real `.docx` with the title, heading and 3 bullets; preview and download work |
| TOOL-09 | P2 | F | Create chart | — | Send P-40 | Bar chart with the right values, categories in the order given; source CSV attached |
| TOOL-10 | P2 | F | Visualize (interactive app) | — | Send P-41 | Working sliders; correct maths (₹2,50,000 at 6.5 % for 20 y ≈ 1,864/month); currency suits the user's region |
| TOOL-11 | P1 | F | Image generation | — | Send P-42 | Image matches the prompt; Edit / Change area / Extend / Combine tools available |
| TOOL-12 | P1 | F | Transcribe a file | `priya.wav` | Attach + send P-43 | Word-for-word transcript (`.srt`) matching the audio |
| TOOL-13 | P2 | F | Memory across chats | A chat where you told it your name (P-02) | New chat → P-44 | Follows the Memories setting: recalls "Ravi" when memories are on for all chats, otherwise says it doesn't know |
| TOOL-14 | P2 | F | Council of Experts | — | Send P-45 | Answer shows multiple expert views and a consensus, within the word limit |
| TOOL-15 | P1 | F | Document editor: PowerPoint | — | Send P-46 | Real `.pptx` with exactly 3 slides; preview works |
| TOOL-16 | P1 | F | Document editor: Excel | — | Send P-47 | Real `.xlsx` with the rows and a working total (= 535) |
| TOOL-17 | P2 | F | Council via /council *(new, 1 Nov)* | — | `/council` → question → open tabs → Council settings | 2–5 model tabs, synthesis, model picker for future councils |

---

## 20. Daily smoke checklist (★, about 60 min)
AUTH-01/02 → CHAT-01/02/04 → FILE-01/02 → MEET-01/02 → SRCH-01 → VOICE-01 → EXT-01/02 → SYNC-01 → BILL-01 (staging) → PRIV-01
Run on Web Chrome + one other surface; rotate browsers/OS daily.

## 21. Exploratory charters
1. **New writer, first 5 minutes.** Can I upload a draft and get edits without help?
2. **Researcher with 3 papers.** Are sources kept straight and citations trustworthy?
3. **Analyst with a CSV.** Is the arithmetic right? Check by hand.
4. **Engineer pasting a stack trace.** Is formatting intact and copy/paste clean?
5. **Meeting on bad Wi-Fi.** What happens to the recording if the network drops?
6. **Plan limits.** Can I tell what each model costs and how much credit is left?
7. **Privacy-first user.** Does every model and fallback honour ZDR? Where does data go?
8. **Two devices.** Rename, delete and edit on one; is the other ever stale or misleading?

## 22. Bug report template
```
Title: [Surface][Module] Short, specific symptom
Env: Web Chrome 153 / Linux / build 38519b9 / Pro plan / Wi-Fi
Severity: S1 Blocker | S2 Major | S3 Minor | S4 Trivial     Priority: P0–P3
Frequency: Always | 3/5 | Once
Steps to reproduce:
  1. ...
Expected: ... (quote docs / UI copy where possible)
Actual: ...
Evidence: screen recording (required), screenshot, console/network log (HAR)
Notes: workaround, first seen in version X, regression? (Y/N)
```
