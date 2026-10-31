# Bearly.ai — Detailed Test Cases

**Scope:** Web, Mac, Windows, iOS, Android, Chrome extension
**Modules:** Auth · Chat · File Upload · Meetings & Transcription · Search · Voice · Extension · History/Sync · Billing/Plans · Settings · Cross-cutting (perf, offline, a11y, security)
**Priority:** P0 = release blocker, P1 = major, P2 = minor, P3 = polish
**Type:** F = functional, N = negative, E = edge, U = UX, P = performance, S = security, X = cross-platform

> How to use this: for each release, run every P0 case on every surface (the
> "Daily Smoke" set marked ★, about 45 min). Run P1 on the surface that changed.
> Log results in `03-test-execution-report.md`.

## 0. Test environment matrix
| Surface | Configs to cover |
|---|---|
| Web | Chrome latest, Firefox latest, Safari latest (macOS), Edge latest; 1366×768, 1920×1080, 2560×1440; zoom 125% |
| Mac | Latest macOS + one older version, Apple Silicon + Intel if available |
| Windows | Win 11 + Win 10, 100% / 150% DPI scaling |
| iOS | Latest iOS + one older version; iPhone small (SE) + large (Pro Max); iPad |
| Android | Android 14/15 + Android 11; Pixel + Samsung (One UI); small-screen device |
| Extension | Chrome latest; also Edge or Brave (Chromium) if supported |
| Network | Normal Wi-Fi, throttled "Slow 3G", offline, network switch mid-request |
| Accounts | Free, Pro, Analyst (or highest available), new account, account with 500+ chats |

---

## 1. Authentication & Onboarding
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| AUTH-01 ★ | P0 | F | Sign up with email | New email | 1. Open app 2. Sign up 3. Enter email and password 4. Verify email link | Account created, verification email arrives within 2 min, link logs you in and opens onboarding |
| AUTH-02 ★ | P0 | F | Log in with Google SSO | Google account | 1. Click "Continue with Google" 2. Pick account | Lands on the chat home and shows the correct name and avatar |
| AUTH-03 | P1 | F | Log in with Apple (iOS/Mac) | Apple ID | Sign in with Apple, including "Hide my email" | Login works, relay email is stored, no duplicate account |
| AUTH-04 | P1 | N | Wrong password | Existing account | Enter wrong password 3× | Clear error that doesn't reveal whether the email exists. Rate limiting or captcha after N attempts |
| AUTH-05 | P1 | N | Invalid email format | — | Enter `abc@`, `a b@x.com`, empty | Inline validation, submit disabled or blocked |
| AUTH-06 | P1 | F | Forgot password | Existing account | Request reset, open link, set new password | Reset email arrives, old password stops working, link works only once and expires |
| AUTH-07 | P1 | F | Session persistence | Logged in | Close browser or app, reopen after 1 h | Still logged in (or follows the documented timeout) |
| AUTH-08 | P1 | F | Logout everywhere | Logged in on web and mobile | Log out on web | Web returns to login. Mobile follows the documented behaviour (stays in or gets kicked out) |
| AUTH-09 | P2 | U | Onboarding clarity | New account | Go through first-run screens | Can be skipped, explains the key features, no dead ends, can't be shown again in a loop |
| AUTH-10 | P1 | X | Same account across surfaces | 1 account | Log in on web, desktop, mobile, extension | Same plan, same history, same settings everywhere |

## 2. Chat (core)
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| CHAT-01 ★ | P0 | F | Send a basic prompt | Logged in | Type "Explain TCP vs UDP in 3 bullets" and press Enter | Response streams in, exactly 3 bullets, no UI freeze, input clears |
| CHAT-02 ★ | P0 | F | Switch model | Pro or higher | Pick model B, send prompt, then pick model C | Selected model is shown on the message; the reply really comes from that model (ask "which model are you") |
| CHAT-03 | P1 | F | Model gating on Free | Free | Pick a paid-only model | Upgrade prompt is shown, no silent fallback to a different model, no generic error |
| CHAT-04 ★ | P0 | F | Multi-turn context | — | Ask "My name is Ravi", then "What is my name?" | Answers "Ravi" |
| CHAT-05 | P1 | F | Stop generation | — | Ask for a long essay, click Stop mid-stream | Streaming stops right away, partial text stays, you can send again |
| CHAT-06 | P1 | F | Regenerate | Existing reply | Click Regenerate | New reply is produced. Previous versions can be browsed (if supported) |
| CHAT-07 | P1 | F | Edit previous message | — | Edit the 1st user message and resend | Thread branches or updates correctly, later messages are handled consistently |
| CHAT-08 | P1 | F | Copy response | — | Click Copy on a reply with markdown and code | Clipboard has clean text or markdown, and a "Copied" toast appears |
| CHAT-09 | P1 | F | Markdown & code rendering | — | Ask for a table, a Python block, LaTeX `$E=mc^2$` | Table renders, code has syntax highlighting and a copy button, math renders (or is shown as raw text consistently) |
| CHAT-10 | P2 | E | Very long prompt | — | Paste about 50k characters | Either accepted or a clear "too long" message with the limit. No crash or blank reply |
| CHAT-11 | P2 | E | Special characters / i18n | — | Send Hindi, Tamil, Arabic (RTL), emoji 🐻, Chinese | Shown correctly, RTL layout is right, reply is in the same language when asked |
| CHAT-12 | P1 | N | Empty / whitespace prompt | — | Press Enter with an empty box or only spaces | Nothing is sent, button disabled |
| CHAT-13 | P1 | N | Network drop mid-stream | — | Go offline while a reply is streaming | Clear error with Retry, partial text kept, no duplicate message when you retry |
| CHAT-14 | P1 | F | Shift+Enter newline | Web/desktop | Shift+Enter in input | Inserts a newline and doesn't send |
| CHAT-15 | P1 | F | New chat / rename / delete | — | Create, rename, delete a chat | Sidebar updates right away, delete asks for confirmation, deleted chat is gone on other devices |
| CHAT-16 | P2 | U | Typing while a reply streams | — | Type the next prompt while the reply is streaming | Input stays usable, nothing is lost, no jumping scroll |
| CHAT-17 | P2 | U | Auto-scroll behaviour | — | Scroll up during streaming | Doesn't force you back to the bottom. A "jump to latest" button appears |
| CHAT-18 | P1 | S | Prompt injection / HTML | — | Send `<img src=x onerror=alert(1)>` and `<script>alert(1)</script>` | Shown as text, no script runs (XSS) |
| CHAT-19 | P2 | P | Time to first token | — | Measure 5 prompts on each model | First token in under ~3 s on a normal network (record the baseline and track it across releases) |
| CHAT-20 | P2 | F | Share chat link (if feature exists) | — | Share, open in incognito | Read-only view works, revoking the link stops access |

## 3. File Uploads
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| FILE-01 ★ | P0 | F | Upload PDF and summarize | 10-page text PDF | Upload, ask "Summarize in 5 bullets" | Upload progress shown, summary matches the document content |
| FILE-02 ★ | P0 | F | Q&A with citation | Known PDF | Ask a fact that's only on page 7 | Correct answer, cites or links page 7 (if citations are supported) |
| FILE-03 | P1 | F | Supported types | Sample set | Upload .pdf .docx .txt .md .csv .xlsx .pptx .png .jpg | Each one is accepted or clearly rejected. The supported list matches the docs |
| FILE-04 | P1 | N | Unsupported type | .exe, .zip | Upload | Clear "unsupported" message, no hang |
| FILE-05 | P1 | E | Size limit | File just under and just over the limit | Upload both | Under: works. Over: message states the limit, no partial upload |
| FILE-06 | P1 | E | Scanned PDF (image-only) | Scanned PDF | Ask about its content | Either OCR works or a clear message says the text couldn't be read. No hallucinated summary |
| FILE-07 | P2 | E | Password-protected PDF | Locked PDF | Upload | Asks for the password or rejects with a clear message |
| FILE-08 | P1 | F | Multiple files | 3 PDFs | Upload all, ask "Compare doc A and doc C" | Uses the correct files, keeps their names straight |
| FILE-09 | P1 | F | Drag and drop | Web/desktop | Drag a file onto the chat | Drop zone highlights, upload starts |
| FILE-10 | P1 | F | Mobile upload sources | iOS/Android | Upload from Files, Photos, Camera | All three sources work, permission prompts appear and show the right wording |
| FILE-11 | P1 | N | Cancel upload | Large file | Cancel mid-upload | Upload stops, no orphan file chip, you can upload again |
| FILE-12 | P1 | N | Network loss during upload | — | Go offline at 50% | Error with Retry, no stuck spinner |
| FILE-13 | P2 | E | Unicode file name | `रिपोर्ट 2026 (final).pdf` | Upload | Name shown correctly, no encoding garbage |
| FILE-14 | P1 | S | File privacy | User A uploads | Try to open the file URL as user B or logged out | Access denied (no public URL guessing) |
| FILE-15 | P2 | P | Large document time | 300-page PDF | Upload and ask a question | Processing time recorded, UI shows progress, no timeout below the stated limit |

## 4. Meetings & Transcription
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| MEET-01 ★ | P0 | F | Record live meeting (desktop) | Mic permission | Start recording, talk for 2 min, stop | Transcript is produced, accuracy is fine for clear English, a summary and action items appear |
| MEET-02 ★ | P0 | F | Upload recording | 10-min .mp3/.m4a/.mp4 | Upload | Transcript with timestamps, duration matches the file |
| MEET-03 | P1 | F | Speaker labels | 2-speaker audio | Transcribe | Speakers are told apart, labels can be renamed |
| MEET-04 | P1 | F | System audio capture (Zoom/Meet/Teams) | Desktop | Record a meeting where the other side talks | The other side's voice is captured, not only your mic |
| MEET-05 | P1 | N | Mic permission denied | Deny the OS mic prompt | Start recording | Clear message with steps to enable, no silent empty recording |
| MEET-06 | P1 | E | Silence / no speech | — | Record 1 min of silence | "No speech detected" (or similar). No hallucinated transcript |
| MEET-07 | P1 | E | Accents / Indian English / Hinglish | Sample audio | Transcribe | Accuracy recorded. Code-mixing handled reasonably |
| MEET-08 | P1 | E | Long meeting | 90-min file | Upload | Finishes, or hits the documented limit with a clear message. Transcription minutes are deducted correctly |
| MEET-09 | P1 | F | Ask questions about meeting | Transcribed meeting | "What did we decide about the deadline?" | Answer is based on the transcript |
| MEET-10 | P1 | F | Export / copy transcript | — | Export as TXT/DOCX/PDF, copy | File opens, content and timestamps are complete |
| MEET-11 | P1 | N | App closed / sleep during recording | Desktop/mobile | Lock screen or close the lid while recording | Recording either continues or is saved safely up to that point. No total loss |
| MEET-12 | P2 | F | Plan minutes limit | Free | Go over the free minutes | Limit message, the recording isn't silently cut off |

## 5. Search
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| SRCH-01 ★ | P0 | F | Web search with sources | — | "Latest news on ISRO missions this month" | Up-to-date answer, sources listed, links open the right pages |
| SRCH-02 | P1 | F | Citation accuracy | — | Open 3 cited sources | Each source actually backs the claim next to it |
| SRCH-03 | P1 | F | Search own chat history | 20+ chats | Search for a word from an old chat | Finds it, clicking opens the chat at the matching message |
| SRCH-04 | P1 | F | Search uploaded files | Uploaded files | Search by file name and by content | Correct files are returned |
| SRCH-05 | P2 | N | No results | — | Search `zzqxv12345` | "No results" empty state, no error |
| SRCH-06 | P2 | E | Special characters in search | — | Search `"C++" OR <tag>` | No crash, input is escaped |
| SRCH-07 | P2 | P | Search latency | 500+ chats | Search | Results within ~1–2 s |

## 6. Voice
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| VOICE-01 ★ | P0 | F | Dictation to text | Mic permission | Tap mic, say a sentence, stop | Accurate text appears in the input and isn't sent automatically (unless that's the documented behaviour) |
| VOICE-02 | P1 | F | Voice conversation mode | — | Hold a 3-turn spoken conversation | AI speaks its replies, you can interrupt it, it keeps context |
| VOICE-03 | P1 | N | Mic denied | — | Deny the permission | Clear guidance, no crash |
| VOICE-04 | P1 | E | Background noise | Fan or café noise | Dictate | Accuracy recorded. No junk text when nobody is speaking |
| VOICE-05 | P1 | E | Interruption (phone call / Bluetooth disconnect) | Mobile | Take a call during voice mode | Pauses gracefully and resumes or ends cleanly |
| VOICE-06 | P2 | F | Language support | — | Dictate in Hindi | Transcribed correctly, or a clear "unsupported" message |

## 7. Chrome Extension
| ID | Pri | Type | Title | Preconditions | Steps | Expected result |
|---|---|---|---|---|---|---|
| EXT-01 ★ | P0 | F | Install & log in | Chrome | Install from the Web Store, open it, log in | Logged in with the same account as web, plan shown correctly |
| EXT-02 ★ | P0 | F | Summarize current page | News article open | Click "Summarize" | Summary matches the article, not a different tab |
| EXT-03 | P1 | F | YouTube video summary | Video with captions | Summarize | Uses the transcript, timestamps if supported |
| EXT-04 | P1 | F | Selected text actions | Select a paragraph | Right-click or popup → Rewrite / Explain / Translate | Acts only on the selection, can be inserted or copied |
| EXT-05 | P1 | E | Restricted pages | `chrome://settings`, Web Store, PDF in browser, Google Docs | Open the extension | Clear message where it can't work, no blank panel |
| EXT-06 | P1 | E | Paywalled / login-only pages | — | Summarize | Uses only the visible content or explains why it can't |
| EXT-07 | P1 | F | Extension history sync | — | Chat in the extension | Shows up in web app history (if designed to) |
| EXT-08 | P2 | P | Page performance impact | Heavy site | Compare page load with the extension on and off | No noticeable slowdown, no console errors injected into the host page |
| EXT-09 | P2 | S | Permissions | — | Check the requested permissions | Only what's needed, and it's explained |
| EXT-10 | P2 | X | Logout sync | — | Log out on web | Extension follows the documented behaviour |

## 8. History, Sync & Cross-platform
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| SYNC-01 ★ | P0 | X | Chat sync | Start a chat on mobile, continue on web | Same messages, same order, within a few seconds |
| SYNC-02 | P1 | X | Delete sync | Delete on web | Gone on mobile/desktop after refresh |
| SYNC-03 | P1 | X | Simultaneous edits | Send from two devices at once | No lost or duplicated messages |
| SYNC-04 | P1 | F | Desktop auto-update | Install the previous version and launch | Update is offered or applied, settings and login kept |
| SYNC-05 | P1 | F | Desktop global shortcut | Press the hotkey from another app | Bearly opens and focuses the input |
| SYNC-06 | P2 | E | Large history | Account with 1000 chats | Scroll the sidebar | Smooth infinite scroll, no memory blow-up |

## 9. Billing & Plans
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| BILL-01 ★ | P0 | F | Upgrade Free → Pro (test card / sandbox) | Upgrade | Payment works, features unlock **without logging out**, receipt email arrives |
| BILL-02 | P1 | F | Downgrade / cancel | Cancel | Access continues until the period ends, clear confirmation |
| BILL-03 | P1 | F | Mobile in-app purchase | Subscribe on iOS/Android | Plan shows up on web too, restore purchase works |
| BILL-04 | P1 | N | Declined card | Use a decline test card | Clear error, no plan change |
| BILL-05 | P2 | U | Pricing page consistency | Compare the pricing page, in-app paywall and app store listings | Same prices, same limits, INR shown correctly if localized |

## 10. Settings & Account
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| SET-01 | P1 | F | Change default model | Set default, open a new chat | New chat uses that model |
| SET-02 | P1 | F | Theme (light/dark/system) | Toggle | Applies everywhere, all text readable in both themes |
| SET-03 | P1 | F | Custom instructions / persona | Set "Always reply in British English" | Applied to new chats |
| SET-04 | P1 | S | Data export & account delete | Request export, then delete | Export is complete. Deletion removes data and you can't log in afterwards |
| SET-05 | P2 | F | Notification preferences | Toggle | Respected |

## 11. Cross-cutting (non-functional)
| ID | Pri | Type | Title | Steps | Expected result |
|---|---|---|---|---|---|
| NFR-01 | P1 | P | Cold start time | Launch desktop and mobile apps cold | Record the baseline (target: under 3 s to usable) |
| NFR-02 | P1 | N | Offline mode | Open the app with no network | Clear offline banner, cached history readable, no infinite spinner |
| NFR-03 | P1 | U | Accessibility (web) | Keyboard-only navigation, screen reader (VoiceOver/NVDA), axe scan | All controls reachable, labelled, focus visible, contrast at least 4.5:1 |
| NFR-04 | P2 | U | Mobile a11y | Dynamic Type / large font, TalkBack | No clipped text, buttons announced |
| NFR-05 | P1 | U | Responsive web | Resize to 375 px wide | Layout adapts, sidebar collapses, input stays visible above the keyboard |
| NFR-06 | P1 | S | Security basics | Check HTTPS, cookies (Secure/HttpOnly), tokens not in URLs or localStorage, rate limits | Nothing sensitive exposed |
| NFR-07 | P2 | U | Error message quality | Cause 5 different errors | Human-readable, actionable, no stack traces |
| NFR-08 | P2 | P | Memory leak | Use one chat tab for 1 h | Memory stays stable (Chrome Task Manager) |

---

## 12. Daily smoke checklist (★ cases, about 45 min per release)
AUTH-01/02 → CHAT-01/02/04 → FILE-01/02 → MEET-01/02 → SRCH-01 → VOICE-01 → EXT-01/02 → SYNC-01 → BILL-01 (staging only)
Run on: Web (Chrome) + one desktop + one phone. Rotate the other browsers and OSes each day.

## 13. Exploratory "confusing / slow / annoying" charters
1. **First 5 minutes as a new writer.** Can I find out how to upload a draft and ask for edits without help?
2. **Researcher with 3 papers.** Does it keep sources straight? Are citations trustworthy?
3. **Analyst with a CSV.** Can it do correct arithmetic over the data? Check the numbers by hand.
4. **Engineer pasting a stack trace.** Is code formatting kept intact? Does copy/paste give clean text?
5. **Meeting on bad Wi-Fi.** What happens to the recording if the network drops?
6. **Model-switch confusion.** Is it obvious which model answered and what each costs?
7. **Plan limits.** Can I tell how much quota is left before I hit the wall?

## 14. Bug report template (what Bearly asked for)
```
Title: [Surface][Module] Short, specific symptom
Env: Web Chrome 130 / macOS 15.1 / App v2.4.1 / Pro plan / Wi-Fi
Severity: S1 Blocker | S2 Major | S3 Minor | S4 Trivial     Priority: P0–P3
Frequency: Always | 3/5 | Once
Steps to reproduce:
  1. ...
  2. ...
Expected: ...
Actual: ...
Evidence: screen recording link (required), screenshot, console/network log (HAR)
Notes: workaround, first seen in version X, regression? (Y/N)
```
