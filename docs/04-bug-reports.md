# Bearly.ai — Bug Reports (step-by-step)

Found on 2026-10-31 in web build `38519b9` (Settings → Account → Version), Chrome 153, **Pro** plan.
Each report is written so that someone opening Bearly for the first time can reproduce it.
Test files and exact prompts are in [`test-data/`](test-data/README.md) (referenced as `file.pdf` and **P-xx**).
Screenshots are in [`evidence/`](evidence/), and screen recordings are in [`evidence/video/`](evidence/video/).

## Before you start (common setup for every bug)
1. Download the [`test-data/files`](test-data/files) folder to your computer.
2. Open **https://bearly.ai/dashboard** in Chrome.
3. **Sign in:** type your email in **Email** → click **Continue** → open the email from Bearly → copy the three-word code (looks like `dart-gone-thee`) → paste it into the first box → click **Sign in**.
4. You land in the workspace. Names used below:
   - **Sidebar**: the left column (Chat, Prompt Templates, Routines, Meetings and Recordings, orange **New Chat** button, chat list).
   - **Composer**: the text box at the bottom/centre ("How can I help? Type / or @ for tools").
   - **+ button**: bottom-left of the composer (Attach files).
   - **Model picker**: the button inside the composer showing the model name (e.g. "Bearly Pro").
   - **Account menu**: your email at the very bottom of the sidebar → contains **App Settings**, Billing & Pro, Logout.
   - **Settings**: Account menu → **App Settings** (a dialog with tabs on the left).
5. Unless a step says otherwise, start each bug from a **New Chat** (click the orange **New Chat** button).

Severity: S1 blocker · S2 major · S3 minor · S4 trivial. Priority: P0–P3.

---

## BUG-01 — [Web][Privacy] Marketing says "ZDR models only", but 2 models aren't ZDR and "ZDR only" is off by default
**Severity / Priority / Frequency:** S2 / P1 / Always · **Plan:** any · **Video:** [`BUG-01.webm`](evidence/video/BUG-01.webm)

**Steps to reproduce**
1. Open **https://bearly.ai** (the public homepage, not the app). In the feature list find **MODELS**: it says *"Claude, GPT, Gemini, Grok, DeepSeek, GLM, MiniMax, **ZDR models only**"*. Scroll down to "What happens when you hit send": *"Every model on Bearly runs with zero data retention."*
2. Open **https://bearly.ai/dashboard** and sign in.
3. Click the **model picker** in the composer (shows "Bearly Pro").
4. At the bottom of the list click **Show all models**.
5. Scroll to **Anthropic → Fable 5.1** and to **Muse Spark 1.3**. Read the small badge next to each name (hover it to see the full text).
6. Open **Account menu → App Settings → Privacy & Security**.
7. Look at the switch **"Zero Data Retention only"**.

**Expected:** Every model has the green **ZDR** badge, **or** the homepage doesn't say "ZDR models only". If a non-ZDR model exists, "Zero Data Retention only" should be **on** by default.

**Actual:**
- Fable 5.1 and Muse Spark 1.3 show **"Standard"**: *"This model's primary route has no verified zero-data-retention guarantee."* All other models show ZDR.
- "Zero Data Retention only" is **off** by default. Its description says it also governs "Automatic fallbacks", so non-ZDR fallbacks are possible on a default account.

**Evidence:** `evidence/models_all.png`, `evidence/set_privacy_security.png`
**Impact:** The product's core promise (privacy) is contradicted inside the product. This is a trust and legal-copy risk.
**Suggested fix:** Default "ZDR only" to on (Standard models opt-in), or qualify the homepage copy.

---

## BUG-02 — [Web][Billing] Pro user can set an Analyst-only model as the default; chat then shows "Billing limit resolved"
**Severity / Priority / Frequency:** S2 / P1 / Always · **Plan:** Pro (any plan below Analyst) · **Video:** [`BUG-02.webm`](evidence/video/BUG-02.webm)

**Steps to reproduce**
1. Sign in with a **Pro** account and click **New Chat**.
2. Click the **model picker** → **Show all models** → click **Fable 5.1**.
   - Result: the picker does **not** switch. The Settings dialog opens on **Billing** with *"Fable 5.1 needs the Analyst plan or higher. Upgrade to use this model…"* (this part is correct). Close the dialog with **Esc**.
3. Open **Account menu → App Settings → New Chats** (left tab under CHATS).
4. Click the **Model** dropdown (shows "Recommended") → choose **Fable 5.1**. A small "Saved" appears.
5. Close Settings (**Esc**) and click **New Chat**. The model picker now shows **Fable 5.1**.
6. Type prompt **P-23** `Which model are you? One short line.` and press **Enter**.

**Expected:** Step 4 shouldn't offer Fable (or should show the same upgrade prompt as step 2). If the chat starts on a locked model anyway, show the upgrade prompt.

**Actual:** The reply bubble says **"Billing limit resolved. Please rerun if necessary."** Nothing was resolved, no answer is generated, and there's no upgrade link. Rerunning gives the same result.

**Evidence:** `evidence/fable_gate.png` (step 2), `evidence/default_fable_chat.png` (step 6)
**Clean-up:** Settings → New Chats → Model → **Recommended**.

---

## BUG-03 — [Web][Files] Attachments removed before sending are still stored, listed and searchable
**Severity / Priority / Frequency:** S2 / P1 / Always · **Video:** [`BUG-03.webm`](evidence/video/BUG-03.webm)

**Test data:** `plain.txt`, `notes.md`, `sales.csv`, `leave_policy.docx`

**Steps to reproduce**
1. Click **New Chat**.
2. Click the **+ button** → **Attach files** → select `notes.md`, `sales.csv`, `leave_policy.docx` (Ctrl/⌘-click to pick several) → **Open**. Wait until the spinners on the file chips disappear (about 3 s).
3. Hover each of these 3 chips and click the small **×** (Remove file). All 3 chips disappear.
4. Click **+** → **Attach files** → select `plain.txt` → wait for it to finish.
5. Type `What does the attached file say?` and press **Enter**. Wait for the answer.
6. In the chat header (top right) click the **file icon with a number** ("Browse files").
7. Optional: click the **magnifier** at the top of the sidebar (Search conversations) → **Files** tab → type `leave_policy`.

**Expected:** Browse files shows **1** file (`plain.txt`). Search finds nothing for `leave_policy`.

**Actual:** Browse files shows **4** files. The three removed ones are listed as **"Uploaded"**, can be previewed and downloaded, and appear in file search. In the original run, a chat that sent 3 files listed **22**.

**Evidence:** `evidence/orphan_files.png`, `evidence/search_files.png`
**Impact:** A user who attaches the wrong (sensitive) document and removes it thinks it was never uploaded. It also uses storage quota.

---

## BUG-04 — [Web][Security] File download links work without login, for 36+ minutes, and after logout
**Severity / Priority / Frequency:** S2 / P1 / Always · **Video:** [`BUG-04.webm`](evidence/video/BUG-04.webm)

**Test data:** `falcon_report.pdf`

**Steps to reproduce**
1. In Chrome, open DevTools (**F12**) → **Network** tab → type `magic-files/open` in the filter box.
2. **New Chat** → **+** → **Attach files** → `falcon_report.pdf` → type **P-14** → **Enter**.
3. In the chat, hover the PDF preview card and click its **download** icon (arrow down, top right of the card).
4. In the Network tab, right-click the `open?fileId=…&key=v2…` request → **Copy → Copy URL**.
5. Open a **private/incognito window** (not signed in to Bearly) and paste the URL. The PDF downloads.
6. Back in the normal window: **Account menu → Logout → Logout**.
7. Paste the same URL again in the incognito window (or wait 30+ minutes first).

**Expected:** Step 5 needs a signed-in session, or the key expires within minutes. Step 7 fails after logout.

**Actual:** Steps 5 and 7 both download the original, decrypted PDF (HTTP 200, 8,429 bytes): 36 minutes after creation and after logout. With the key removed or changed, the server returns 400, so the link can't be guessed; it can only leak (browser history, proxies, logs, forwarding).

**Command-line check:** `curl -o test.pdf "<copied URL>"` (no cookies) → `200`.
**Suggested fix:** Short TTL (≤ 5 min), tie the key to the session, revoke on logout and on file/chat deletion.

---

## BUG-05 — [Web][Meetings] Recording of silence stays "Transcription in progress" forever
**Severity / Priority / Frequency:** S2 / P2 / 1 of 1 (a speech recording in the same session finished in 15 s) · **Video:** [`BUG-05.webm`](evidence/video/BUG-05.webm)

**Test data:** `silence_45s.wav` (or simply a quiet room with your mic muted)

**Steps to reproduce**
1. In the sidebar click **Meetings and Recordings**.
2. Under **In person or voice note** click **Start recording** and allow the microphone if Chrome asks.
3. Keep the room silent (or feed `silence_45s.wav`, see test-data README) for about **45 seconds**.
4. Click **Stop** (the red "Stop recording · 0:45" button).
5. The recording appears in **Recordings** as "Recording — <date/time>". Click it.
6. Wait. Reload the page after 5, 15 and 45 minutes and open the recording again.

**Expected:** Within about a minute: an empty transcript or **"No speech detected"**.

**Actual:** **"Transcription in progress — You can leave this page. The final transcript will appear here when it's ready."** was still shown 45+ minutes later. No error or retry.

**Evidence:** `evidence/meet_silence_final.png`. Compare `evidence/meet1_result.png` (speech recording, done in 15 s).

---

## BUG-06 — [Web][Search] Chat and file search return everything for queries that match nothing
**Severity / Priority / Frequency:** S2 / P2 / Always · **Precondition:** at least 3 chats in history · **Video:** [`BUG-06.webm`](evidence/video/BUG-06.webm)

**Steps to reproduce**
1. Create 3 short chats, one per prompt: **P-01**, **P-02**, **P-10** (click **New Chat** before each).
2. Click the **magnifier** at the top of the sidebar ("Search conversations", or press Ctrl/⌘+F inside the app).
3. Type `zzqxv12345`. Wait 3 seconds.
4. Clear it and type `Ravi`. Then `"C++" OR <tag>`.
5. Click the **Files** tab and type `zzqxv12345`.

**Expected:** Step 3: "No results". Step 4 (`Ravi`): only the chat from P-02. Step 5: no files.

**Actual:** Every query returns **all** chats (with the same "N matches" counts) and every file. For `ocean`, the right chat is found but the snippet shows unrelated text (the fruit list) instead of the ocean lines.

**Evidence:** `evidence/search_none2.png`, `evidence/search_files.png`
**Notes:** Looks like semantic search with no relevance cut-off; add a threshold and keyword highlighting.

---

## BUG-07 — [Web][Chat] Sending while offline: generic error, no Retry, failed message stays
**Severity / Priority / Frequency:** S3 / P1 / Always · **Video:** [`BUG-07.webm`](evidence/video/BUG-07.webm)

**Steps to reproduce**
1. Open any chat.
2. Go offline: turn off Wi-Fi, **or** DevTools (F12) → **Network** tab → throttling dropdown → **Offline**.
3. Wait 2 s: an orange **"Offline"** banner appears at the top right (correct).
4. Type **P-11** `Offline test message: reply with just OK` → **Enter**.
5. Turn the network back on and wait 30 s. Then reload the page.

**Expected:** The send is blocked or queued while offline ("You're offline, will send when connected"), or a specific error with a **Retry** button. After reconnecting, the message is sent.

**Actual:** The message is accepted and the reply is **"An error occurred. Please wait and try again."** with only **Get help**. No Retry, nothing is resent after reconnecting, and the failed turn is still there after reload.

**Evidence:** `evidence/offline.png`, `evidence/offline_send.png`

---

## BUG-08 — [Web][Sync] Rename and delete don't reach other open sessions; a deleted chat stays readable
**Severity / Priority / Frequency:** S3 / P2 / 2 of 2 · **Video:** [`BUG-08.webm`](evidence/video/BUG-08.webm)

**Steps to reproduce**
1. Sign in to the same account in two windows: **Window A** (normal) and **Window B** (another browser or profile). Put them side by side.
2. In both, open the same chat (click it in the sidebar).
3. In **A**, type **P-29** `SYNC-PROBE-7731 reply with OK` → Enter. In **B** the message appears within ~5 s (correct).
4. In **A**, click the chat title at the top → select all → type `SYNC RENAMED` → **Enter**. Watch **B**'s sidebar for 90 s.
5. In **A**, press Ctrl/⌘+K → **Delete** → confirm **Delete**. Watch **B** for 90 s.
6. Reload **B**.

**Expected:** Steps 4–5: B's sidebar updates within seconds (like messages do). After step 5 B shows "This chat was deleted".

**Actual:** B keeps the old title and still lists, and fully displays, the deleted chat for 90+ s. Only the reload in step 6 updates it.

---

## BUG-09 — [Web][Voice] "Start voice call" does nothing when the microphone is blocked
**Severity / Priority / Frequency:** S3 / P2 / Always · **Video:** [`BUG-09.webm`](evidence/video/BUG-09.webm)

**Steps to reproduce**
1. Block the microphone for bearly.ai: click the **site-settings icon** left of the address bar → **Microphone → Block** → reload. (Or use a computer with no microphone.)
2. Click **New Chat**.
3. Click the **waveform icon** in the composer ("Start voice call").
4. For comparison: sidebar → **Meetings and Recordings** → **Start recording**.

**Expected:** A message like the one in step 4: *"Recording could not start. Check your audio permissions and try again."*, ideally with how to unblock.

**Actual:** Step 3: **nothing visible happens** (no message, no call UI). DevTools console: `realtimeVoice:error failed {errorName: UnknownError, errorCode: unavailable}`. An empty "New Chat 1" also appears in the sidebar (BUG-14).

**Evidence:** `evidence/mic_denied_voice.png` vs `evidence/mic_denied_meet.png`

---

## BUG-10 — [Web][Files/Billing] File size limit differs in docs, plan cards and real behaviour
**Severity / Priority / Frequency:** S3 / P2 / Always · **Test data:** `big_210MiB.txt`, `big_310MiB.txt` (generate with `scripts/make_test_files.py --large`) · **Video:** [`BUG-10.webm`](evidence/video/BUG-10.webm)

**Steps to reproduce**
1. Read the limits:
   - Docs https://bearly.ai/docs/chats/files/ : *"Each attachment must be 300 MB or smaller."*
   - **Account menu → App Settings → Billing**, plan cards: Free *"10MB file size limit"*, Pro *"200MB"*, Analyst *"600MB"*, Research *"2GB"*.
   - https://bearly.ai/pricing: no limit mentioned.
2. On a **Pro** account: **New Chat → + → Attach files → `big_310MiB.txt`**. Wait for the spinner to stop (≈1–3 min).
3. Type **P-20** `What is the exact size in bytes of the attached file? Reply with the number only.` → Enter.

**Expected:** One limit everywhere. A file over the Pro limit is rejected at selection with a message naming the limit.

**Actual:** The 310 MiB file (325,000,000 bytes) uploads and the agent answers **325000000**. So neither the 200 MB plan-card limit nor the 300 MB docs limit is enforced. Sizes are binary MiB but labelled "MB" (205,000,000 bytes is shown as "196 MB").

**Evidence:** `evidence/set_Billing.png`

---

## BUG-11 — [Web][Chat] Locked models aren't marked in the model picker
**Severity / Priority / Frequency:** S3 / P3 / Always · **Plan:** Pro
1. **New Chat** → click the **model picker** → **Show all models**.
2. Compare **Fable 5.1** with any other model.

**Expected:** A lock icon or "Analyst" badge on Fable before clicking.
**Actual:** It looks identical to available models; clicking it opens the Billing upgrade page instead (see BUG-02 step 2).

---

## BUG-12 — [Web][Files] Two files with the same content: the 2nd chip shows the 1st file's name
**Severity / Priority / Frequency:** S3 / P3 / 3 of 3 · **Test data:** `dup_a.txt`, `dup_b.txt` (identical content) · **Video:** [`BUG-12.webm`](evidence/video/BUG-12.webm)
1. **New Chat → + → Attach files → `dup_a.txt`**. Wait for the chip.
2. **+ → Attach files → `dup_b.txt`**. Wait for the chip.
3. Hover each chip to read its full name.

**Expected:** Chips `dup_a.txt` and `dup_b.txt`.
**Actual:** Two chips **both named `dup_a.txt`**; `dup_b.txt` is never shown. The app de-duplicates by content and replaces the user's file name.
**Evidence:** `evidence/dedupe.png`

---

## BUG-13 — [Web][Chat] Voice-call chat auto-titled "Chat Title Request"
**Severity / Priority / Frequency:** S3 / P3 / 1 of 1 · **Needs:** microphone (or `priya.wav` as fake mic)
1. **New Chat** → click the **waveform icon** ("Start voice call").
2. Speak (or play `priya.wav`) for ~40 s, then click **End**.
3. Look at the chat title at the top and in the sidebar.

**Expected:** A title describing the conversation (e.g. "Launch deadline update").
**Actual:** **"Chat Title Request"**, which looks like the title generator's own internal request name. Text chats get good titles (e.g. "TCP vs UDP Explained").

---

## BUG-14 — [Web][History] Empty "New Chat 1" chats are left in history
**Severity / Priority / Frequency:** S3 / P3 / 2 of 2 · **Test data:** `plain.txt`
1. **New Chat → + → Attach files → `plain.txt`**. Wait for the chip. **Don't send.**
2. Click another chat in the sidebar.
3. Look at the sidebar: a **"New Chat 1"** entry now exists. Open it: 0 messages, but **Browse files (1)**.
4. Also happens after a failed voice call (BUG-09).

**Expected:** Unsent drafts don't create history entries (or are clearly marked "Draft").
**Actual:** A permanent, empty "New Chat 1" (it held 2 files and 0 messages in the original run).

---

## BUG-15 — [Web][Accessibility] Unlabelled chat box, unlabelled icon buttons, low contrast, nested controls
**Severity / Priority / Frequency:** S3 / P2 / Always · **Video:** [`BUG-15.webm`](evidence/video/BUG-15.webm)

**Steps to reproduce (no tools)**
1. Turn on a screen reader (NVDA on Windows, VoiceOver on Mac: ⌘+F5) and open **New Chat**.
2. Press **Tab** until focus reaches the **composer**: it's announced only as "edit text" with no name.
3. Keep tabbing to the **+** and the **sliders** icon next to it: announced as just "button".
4. Look at the orange **New Chat** button: pale orange text on a pale orange background.

**Steps to reproduce (axe DevTools, free Chrome extension)**
1. Install "axe DevTools" → open the app → F12 → **axe DevTools** tab → **Scan all of my page**.

**Expected:** WCAG 2.1 AA: every control has a name; text contrast ≥ 4.5:1; no interactive controls inside other interactive controls.

**Actual (axe, every main screen):**
- `aria-input-field-name` (serious): composer `[data-testid=chat-input]` has no accessible name.
- `button-name` (critical): `+` (plus-menu), chat-config (sliders), jump-to-bottom arrow.
- `color-contrast` (serious): **New Chat** button **2.05:1** (`#fc865…` on light orange). The sign-in **Continue** button is ~2.4:1.
- `nested-interactive` (serious): 19–33 per page. Sidebar chat rows are `role=button` and contain the "More actions" button.
- Settings dialog: 6 dropdowns with no accessible name.

---

## BUG-16 — [Mobile web] Tap targets smaller than 44 px
**Severity / Priority / Frequency:** S4 / P3 / Always
1. On a phone (or Chrome DevTools → device toolbar → iPhone 13) open the app.
2. Measure (DevTools → inspect) the sidebar **search** icon, **Open sidebar**, a message's **copy/delete** icons.

**Expected:** ≥ 44×44 px (Apple HIG / WCAG 2.5.5).
**Actual:** Search 14×14, Open sidebar 24×24, message actions 28×28, dictation 28×28.
**Evidence:** `evidence/app_mobile.png`, `evidence/app_mobile_chat.png`

---

## BUG-17 — [Web][Text-to-speech] Asked for an MP3, told "MP3", file is WAV
**Severity / Priority / Frequency:** S3 / P3 / 1 of 1 · **Video:** [`BUG-17.webm`](evidence/video/BUG-17.webm)
1. **New Chat** → type **P-21** (asks for an **MP3**) → Enter.
2. Read the reply: *"The audio is ready in Voice Studio, where you can play and download the **MP3**."*
3. In the Voice Studio card, download the audio (or open the chat's **Browse files**).

**Expected:** An `.mp3` file (`audio/mpeg`), or the reply names the real format.
**Actual:** The file is `bearly-voice-XXXX.wav` (`audio/wav`).
**Evidence:** `evidence/tts.png`

---

## BUG-18 — [Web][Auth] Login code screen: no resend, no visible throttling, Sign in enabled with 1 of 3 words
**Severity / Priority / Frequency:** S3 / P2 / Always · **Video:** [`BUG-18.webm`](evidence/video/BUG-18.webm)
1. In a private window open **https://bearly.ai/dashboard** → enter your email → **Continue**.
2. On "Check your email" look for a **Resend** link. There is none, only **Back**.
3. Type only `abc` in the first of the three boxes: the **Sign in** button becomes active.
4. Enter wrong codes (prompt **P-31**) 7 times, clicking **Sign in** after each.

**Expected:** A "Resend code (in 30 s)" option; Sign in disabled until all 3 words are filled; after ~5 wrong attempts a cooldown or "request a new code".
**Actual:** No resend; Sign in active with 1 word; 7 wrong codes all give *"Codewords invalid. Make sure you typed them in correctly."* (HTTP 401) with no cooldown, lockout or CAPTCHA. (I stopped at 7.)
**Evidence:** `evidence/login_code.png`, `evidence/auth_wrong.png`

---

## BUG-19 — [Web][Chat] Arabic (RTL) replies are displayed left-to-right
**Severity / Priority / Frequency:** S3 / P3 / Always
1. **New Chat** → type **P-08** → Enter.
2. Look at the Arabic line of the reply.

**Expected:** The Arabic sentence is right-aligned, reads right-to-left, full stop on the left end.
**Actual:** Left-aligned with the full stop on the right (`direction: ltr`; no `dir="auto"` on message paragraphs).
**Evidence:** `evidence/i18n.png`

---

## BUG-20 — [Web][Search] Web-search source links are buttons, not links
**Severity / Priority / Frequency:** S3 / P3 / Always
1. **New Chat** → type **P-13** → Enter. Wait for the underlined source names (e.g. "ISRO mission update · The Hindu").
2. Hover one: no URL in the browser's status bar. Right-click: no "Copy link address" / "Open in new tab". Middle-click: nothing.
3. Inspect it (right-click → Inspect): `<button data-markdown-link="https://…">`.

**Expected:** A real `<a href>` link, with a visible domain or preview.
**Actual:** A `<button>`; screen readers announce it as a button, and you can't preview or copy the URL.
**Evidence:** `evidence/websearch.png`

---

## BUG-21 — [Web][Pricing] Pricing page contradicts the docs and the in-app plans
**Severity / Priority / Frequency:** S3 / P2 / Always
1. Open **https://bearly.ai/pricing**. Read the **Pro** card bullets and the FAQ "What's the difference between the paid tiers?".
2. Open **https://bearly.ai/docs/manual/plans-and-credits/** → "What each plan unlocks".
3. In the app: **Account menu → App Settings → Billing**, read the Free/Pro/Analyst/Research cards.

**Expected:** The same features per plan everywhere.
**Actual:**
- /pricing Pro card lists **"Cowork, deep research, code"**, but the docs say **Deep Research needs Analyst**.
- The FAQ says paid tiers differ only by credits + Fable; the docs add Deep Research and People Search (Analyst) and Ultra Deep Research (Research).
- The Free card says "Claude, GPT, Gemini, and Grok models"; the docs say "low-cost models".
- The in-app Research card says "Everything in **Pro**"; the docs say it includes Analyst.

**Evidence:** `evidence/pricing_desktop.png`, `evidence/set_Billing.png`

---

## BUG-22 — [Web][Settings] Desktop app version shown (v5.2.9) differs from the version downloaded (v5.2.11)
**Severity / Priority / Frequency:** S4 / P3 / Always
1. **Account menu → App Settings → Get Desktop App**: *"Currently v5.2.9."*
2. Click **Download for macOS/Windows/Linux** (or open https://bearly.ai/download/mac): the file is `Bearly-5.2.11-…`.

**Expected:** The same version. **Actual:** 5.2.9 vs 5.2.11. **Evidence:** `evidence/set_Get_Desktop_App.png`

---

## BUG-23 — [Web][Usage] "Credits reset in 0 days", and service totals don't add up
**Severity / Priority / Frequency:** S4 / P3 / Always (on the last day of a billing period)
1. On the day before your reset date, open **Account menu → App Settings → Usage**.

**Expected:** "Credits reset tomorrow" (or "in 1 day"); "Used this month" equals the "Usage by service" total.
**Actual:** **"Credits reset in 0 days (Nov 1)"** on Oct 31. "Used this month **$0.27**" vs service table total **$0.26**.
**Evidence:** `evidence/usage_final.png`

---

## BUG-24 — [Web][Shortcuts] Mac "⌘" labels shown on Windows/Linux
**Severity / Priority / Frequency:** S4 / P3 / Always · **OS:** Windows or Linux
1. Open the app on Windows/Linux. Look at the top right of a chat: **"⌘ K"**.
2. Hover the sidebar magnifier: tooltip *"Search conversations (⌘F)"*.
3. Press **Ctrl+K**: the command menu opens and correctly shows "CTRL SHIFT N" for its own items.

**Expected:** "Ctrl K" / "Ctrl F" on Windows/Linux. **Actual:** ⌘ symbols, which Windows/Linux keyboards don't have.

---

## BUG-25 — [Web][Auth] Sign-in email field: no validation, no autocomplete, low contrast, 13 px on mobile
**Severity / Priority / Frequency:** S4 / P3 / Always
1. Open **https://bearly.ai/dashboard** signed out.
2. Leave **Email** empty, or type `abc@` or `a b@x.com`: **Continue** stays clickable and nothing is flagged.
3. Inspect the input: no `required`, no `autocomplete="email"`.
4. On a phone (or DevTools → iPhone), tap the email field: the input is 13 px, so iOS Safari zooms the page.

**Expected:** Inline validation; Continue disabled until valid; `autocomplete="email"`; ≥ 16 px input; Continue text contrast ≥ 4.5:1 (it's ~2.4:1 and looks disabled).
**Evidence:** `evidence/login_desktop.png`, `evidence/login_mobile.png`

---

## BUG-26 — [Web][Files] Enter does nothing while a just-attached file is processing, with no feedback
**Severity / Priority / Frequency:** S4 / P3 / 1 of 2
1. **New Chat → + → Attach files → `falcon_report.pdf`**.
2. **Immediately** type **P-14** and press **Enter**.

**Expected:** Docs say sending waits until the file is ready, so show it ("Waiting for falcon_report.pdf…") or send automatically when ready.
**Actual:** Nothing happens, with no message. Pressing Enter again a few seconds later sends.

---

## BUG-27 — [Web][Voice] Voice-call transcript has duplicated and cut-off lines
**Severity / Priority / Frequency:** S4 / P3 / 1 of 1 · **Test data:** `priya.wav` as mic input
1. **New Chat** → **Start voice call** → play `priya.wav` near the mic (it repeats the same sentences) for ~40 s → **End**.
2. Click **Show transcript**.

**Expected:** Each spoken turn once.
**Actual:** Assistant lines repeat around single user turns ("Bearly: Okay." ×2, "Bearly: Mm-h" ×2, "mm." ×2) and some are cut off ("Mm-h").
**Evidence:** `evidence/voice_transcript.png`

---

## BUG-28 — [Web][Prompt Templates] Template form submits on Shift+Enter; the chat box uses Shift+Enter for a new line
**Severity / Priority / Frequency:** S4 / P3 / Always
1. Sidebar → **Prompt Templates** → **Create Prompt Template** → paste **P-26** → **Create Prompt**.
2. Go to a **New Chat**, type `/Simple` → press **Enter** to pick "Simple Hindi Translator".
3. In the form's **Text** box type `Please restart the server.`, press **Enter** (new line), then press **Shift+Enter**.

**Expected:** The same keys as the composer (Enter = send, Shift+Enter = new line), or a clear label.
**Actual:** Shift+Enter **submits** ("Submit (shift-enter)"), the opposite of the composer.
**Evidence:** `evidence/slash_tmpl2.png`

---

## BUG-29 — [Web][Share] "Ready to share" and Publish shown while the Page is still being built
**Severity / Priority / Frequency:** S4 / P3 / 1 of 1
1. Open a chat with a few messages (e.g. one made with **P-10**).
2. Click the **Share** icon (top right) → **Prepare share**.
3. Watch the right-hand panel for the first ~15 s.

**Expected:** "Ready to share" + Publish only after the preview has rendered.
**Actual:** The panel header shows **"Ready to share"** and an active **Publish** while the preview area is still a spinner and the composer still shows **Stop** (agent still working).
**Evidence:** `evidence/share2.png`

---

## BUG-30 — [Public website] Mobile header hides main pages; a dead link; an inaccessible toggle; an old page title
**Severity / Priority / Frequency:** S3 / P2 / Always
1. On a phone (or DevTools → device toolbar → iPhone 12, 390 px) open **https://bearly.ai**. The header shows only **News** and **Get Bearly**, with no ☰ menu. Product, Privacy, Enterprise and Pricing are reachable only from the footer.
2. Open **https://bearly.ai/ai-assistant-comparison** → find the link to Claude's plan article (`support.claude.com/en/articles/11049762-choose-a-claude-plan`) → it returns **404**.
3. Open **https://bearly.ai/pricing** with a screen reader → the Monthly/Yearly switch is announced as a button with no on/off state (no `aria-pressed` / `role="switch"`).
4. Open **https://bearly.ai/dashboard** signed out → the browser tab title is the old tagline **"Bearly.ai | AI at your fingertips"**.

**Evidence:** `evidence/home_mobile_375.png`, `evidence/pricing_mobile_375.png`

---

## Security observations (not exploited)
**S-1. No Content-Security-Policy or anti-framing header on the app.**
Run `curl -sI https://bearly.ai/dashboard/focus/new` → the response has HSTS, `x-content-type-options`, `referrer-policy`, but **no** `content-security-policy` and no `x-frame-options` / `frame-ancestors`.

**S-2. Session token in localStorage.**
Signed in, DevTools → **Application → Local Storage → https://bearly.ai** → key `_bearly:token`. Any XSS could read it; a strict CSP (S-1) is the main defence. Chat rendering resisted the XSS payloads in **P-03**.

**S-3. "Continue with Microsoft" appears only after a logout.**
In a fresh private window, `/dashboard` shows only Google + email. After signing in and logging out on that device, the sign-in page also shows **Continue with Microsoft**. Check whether `loginCapabilities` is meant to depend on device history.
