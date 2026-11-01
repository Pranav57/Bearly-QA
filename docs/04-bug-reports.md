# Bearly.ai — Bug Reports (step-by-step)

Found on 2026-10-31 in web build `38519b9` (Settings → Account → Version), Chrome 153, **Pro** plan.
Each report is written so that someone opening Bearly for the first time can reproduce it.
Test files and exact prompts are in [`test-data/`](test-data/README.md) (referenced as `file.pdf` and **P-xx**).
Screenshots are in [`evidence/`](evidence/), and screen recordings are in [`evidence/video/`](evidence/video/).

**About the videos** (recorded 1 Nov 2026 on the same account, Chromium 1440×900):
- On GitHub, click a `.webm` file, then **View raw**. It plays in Chrome, Firefox or Edge. (Safari: use VLC.)
- A dark caption at the top shows the step number and the expected/actual result. The **red dot** is the mouse pointer. Red outlines and yellow highlighting mark the important text.
- The tester's email is replaced with `tester@example.com`, and the email field shows dots. Secret parts of links are shown as `[redacted]`.
- BUG-04 and BUG-08 show two browser windows side by side.
- Each video re-runs the steps below; where the 1 Nov result differed from 31 Oct, the report says so.

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
**Also (1 Nov):** https://bearly.ai/enterprise lists **"Zero data retention on every model"** under "Built for the security review".
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
5. Close Settings (**Esc**) and **reload the page (F5)**. A **New Chat** now opens with the model picker showing **Fable 5.1**.
   - Note: without the reload, the open tab keeps using the previous model (re-verified 1 Nov 2026), so the new default only applies after a refresh or in a new tab.
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
**Re-checked 1 Nov 2026 (video):** the same silent recording (made 31 Oct, 6:20 PM UTC) still showed "Transcription in progress" about **7 hours** later, including after a reload.

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

**Actual:** B keeps the old title and still lists, and fully displays, the deleted chat for 90+ s. Only the reload in step 6 updates it. After the reload B shows a generic **"Oops! Page not found"** instead of saying the chat was deleted.
**Video note:** the recording waits 45 s after each change (not 90 s) to keep it short.

---

## BUG-09 — [Web][Voice] "Voice unavailable" message disappears after ~3 s and gives no way to fix it
**Severity / Priority / Frequency:** S4 / P3 / Always · **Video:** [`BUG-09.webm`](evidence/video/BUG-09.webm)

**Steps to reproduce**
1. Use a computer with no microphone, or block the microphone for bearly.ai: click the **site-settings icon** left of the address bar → **Microphone → Block** → reload.
2. Click **New Chat**.
3. Click the **waveform icon** in the composer ("Start voice call"). Watch the message box.
4. For comparison: sidebar → **Meetings and Recordings** → **Start recording**.

**Expected:** The voice-call message stays until dismissed and says how to fix it, like Meetings' *"Recording could not start. Check your audio permissions and try again."*

**Actual:** The message box is replaced by **"Voice unavailable — No microphone is available."** for about **3 seconds** (0:15–0:17 in the video), then it disappears by itself. It gives no hint on how to allow the microphone. DevTools console: `realtimeVoice:error failed {errorName: UnknownError, errorCode: unavailable}`. An empty "New Chat N" also appears in the sidebar (BUG-14).

**Correction (1 Nov):** The earlier version of this report said *"nothing visible happens"*. That was wrong: the test script checked the screen 6 seconds after the click, by which time the 3-second message had already gone. (Thanks to manual review of the recording.) Severity lowered from S3/P2 to S4/P3. The test machine had no microphone at all, so "No microphone is available" was accurate there; a device with a microphone that's **blocked** hasn't been checked yet.

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
**Re-checked 1 Nov 2026 (video):** the 310 MiB file was again **accepted** with no limit message (upload ≈ 1 min, spinner only). Two new problems appeared:
- Pressing Enter while the chip was still processing did nothing (BUG-26).
- After sending P-20, the agent stayed on *"Reading attached file metadata…"* for 6+ minutes. The sent prompt was later gone from the chat (BUG-31).

The video ends with a summary card.

---

## BUG-11 — [Web][Chat] Locked models aren't marked in the model picker
**Severity / Priority / Frequency:** S3 / P3 / Always · **Plan:** Pro · **Video:** [`BUG-11.webm`](evidence/video/BUG-11.webm)
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
**Severity / Priority / Frequency:** S3 / P3 / 4 of 4 (voice ×3, template ×1) · **Needs:** microphone (or `priya.wav` as fake mic) · **Video:** [`BUG-13.webm`](evidence/video/BUG-13.webm)
1. **New Chat** → click the **waveform icon** ("Start voice call").
2. Speak (or play `priya.wav`) for ~40 s, then click **End**.
3. Look at the chat title at the top and in the sidebar.

**Expected:** A title describing the conversation (e.g. "Launch deadline update").
**Actual:** **"Chat Title Request"**, which looks like the title generator's own internal request name. Text chats get good titles (e.g. "TCP vs UDP Explained").
**Re-checked 1 Nov (video):** reproduced again (2 of 2). The same title also appears on a chat started from a **Prompt Template** (`/Simple Hindi Translator`, BUG-28 recording), so it isn't limited to voice calls.

---

## BUG-14 — [Web][History] Empty "New Chat 1" chats are left in history
**Severity / Priority / Frequency:** S3 / P3 / 2 of 2 · **Test data:** `plain.txt` · **Video:** [`BUG-14.webm`](evidence/video/BUG-14.webm)
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
- *Added 1 Nov (full UI walk-through):* the **Share conversation** button in the chat header is an unnamed `span role=button`. The **Personalization** on/off switch has no name. The plus menu, chat-settings (sliders) and "jump to latest" buttons have no name. The sidebar search icon is 14×14 px and New project is 21×21 px.

---

## BUG-16 — [Mobile web] Tap targets smaller than 44 px
**Severity / Priority / Frequency:** S4 / P3 / Always · **Video:** [`BUG-16.webm`](evidence/video/BUG-16.webm)
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
**Actual:** No resend; Sign in active with 1 word (it's also enabled with **0** words; it only looks pale); 7 wrong codes all give *"Codewords invalid. Make sure you typed them in correctly."* (HTTP 401) with no cooldown, lockout or CAPTCHA. (I stopped at 7.)
**Evidence:** `evidence/login_code.png`, `evidence/auth_wrong.png` · **2nd video:** [`BUG-18b.webm`](evidence/video/BUG-18b.webm)
**Re-checked 1 Nov 2026 (2nd video):**
- With **all three boxes empty**, Sign in looks greyed out but is not disabled (`disabled=false`, pointer cursor). Clicking it shows a spinner and sends a real `loginPerform` request with an empty code.
- 15 wrong codes in a row: every request returned **401**, each with *"Codewords invalid…"*; still no cooldown.
- A report from manual testing said that after many attempts `loginPerform` returned **429 Too Many Requests** while the screen still showed *"Codewords invalid"*, and that an **expired** code also shows *"Codewords invalid"*. Neither was reproduced in 15 attempts; the expired-code case needs a code left to expire and is still open.

---

## BUG-19 — [Web][Chat] Arabic (RTL) replies are displayed left-to-right
**Severity / Priority / Frequency:** S3 / P3 / Always · **Video:** [`BUG-19.webm`](evidence/video/BUG-19.webm)
1. **New Chat** → type **P-08** → Enter.
2. Look at the Arabic line of the reply.

**Expected:** The Arabic sentence is right-aligned, reads right-to-left, full stop on the left end.
**Actual:** Left-aligned with the full stop on the right (`direction: ltr`; no `dir="auto"` on message paragraphs).
**Evidence:** `evidence/i18n.png`

---

## BUG-20 — [Web][Search] Web-search source links are buttons, not links
**Severity / Priority / Frequency:** S3 / P3 / Always · **Video:** [`BUG-20.webm`](evidence/video/BUG-20.webm)
1. **New Chat** → type **P-13** → Enter. Wait for the underlined source names (e.g. "ISRO mission update · The Hindu").
2. Hover one: no URL in the browser's status bar. Right-click: no "Copy link address" / "Open in new tab". Middle-click: nothing.
3. Inspect it (right-click → Inspect): `<button data-markdown-link="https://…">`.

**Expected:** A real `<a href>` link, with a visible domain or preview.
**Actual:** A `<button>`; screen readers announce it as a button, and you can't preview or copy the URL.
**Evidence:** `evidence/websearch.png`

---

## BUG-21 — [Web][Pricing] Pricing page contradicts the docs and the in-app plans
**Severity / Priority / Frequency:** S3 / P2 / Always · **Video:** [`BUG-21.webm`](evidence/video/BUG-21.webm)
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
**Severity / Priority / Frequency:** S4 / P3 / Always · **Video:** [`BUG-22.webm`](evidence/video/BUG-22.webm)
1. **Account menu → App Settings → Get Desktop App**: *"Currently v5.2.9."*
2. Click **Download for macOS/Windows/Linux** (or open https://bearly.ai/download/mac): the file is `Bearly-5.2.11-…`.

**Expected:** The same version. **Actual:** 5.2.9 vs 5.2.11. **Evidence:** `evidence/set_Get_Desktop_App.png`

---

## BUG-23 — [Web][Usage] "Credits reset in 0 days", and service totals don't add up
**Severity / Priority / Frequency:** S4 / P3 / Always (on the last day of a billing period) · **Video:** [`BUG-23.webm`](evidence/video/BUG-23.webm)
1. On the day before your reset date, open **Account menu → App Settings → Usage**.

**Expected:** "Credits reset tomorrow" (or "in 1 day"); "Used this month" equals the "Usage by service" total.
**Actual:** **"Credits reset in 0 days (Nov 1)"** on Oct 31. "Used this month **$0.27**" vs service table total **$0.26**.
**Evidence:** `evidence/usage_final.png`
**Re-checked 1 Nov 2026, 03:38 UTC:** the same screen contradicts itself in three more ways (`evidence/usage_today_gt_month.png`):
- **"Today $0.67"** is larger than **"Used this month $0.40"**. Today is part of this month, so it can never be larger. Likely cause: "Today" uses the account's local day (India, which started 18:30 UTC on 31 Oct), while "this month" uses UTC.
- "Usage by service" lists **"Web search"** twice and **"Best Pick · store listings"** twice as separate rows.
- Usage says **"Credits reset in 29 days (Dec 1)"**, but Billing says the plan **"Cancels on November 3, 2026"**.

---

## BUG-24 — [Web][Shortcuts] Mac "⌘" labels shown on Windows/Linux
**Severity / Priority / Frequency:** S4 / P3 / Always · **OS:** Windows or Linux · **Video:** [`BUG-24.webm`](evidence/video/BUG-24.webm)
1. Open the app on Windows/Linux. Look at the top right of a chat: **"⌘ K"**.
2. Hover the sidebar magnifier: tooltip *"Search conversations (⌘F)"*.
3. Press **Ctrl+K**: the command menu opens and correctly shows "CTRL SHIFT N" for its own items.

**Expected:** "Ctrl K" / "Ctrl F" on Windows/Linux. **Actual:** ⌘ symbols, which Windows/Linux keyboards don't have.

---

## BUG-25 — [Web][Auth] Sign-in email field: no validation, no autocomplete, low contrast, 13 px on mobile
**Severity / Priority / Frequency:** S4 / P3 / Always · **Video:** [`BUG-25.webm`](evidence/video/BUG-25.webm)
1. Open **https://bearly.ai/dashboard** signed out.
2. Leave **Email** empty, or type `abc@` or `a b@x.com`: **Continue** stays clickable and nothing is flagged.
3. Inspect the input: no `required`, no `autocomplete="email"`.
4. On a phone (or DevTools → iPhone), tap the email field: the input is 13 px, so iOS Safari zooms the page.

**Expected:** Inline validation; Continue disabled until valid; `autocomplete="email"`; ≥ 16 px input; Continue text contrast ≥ 4.5:1.
**Actual:** No *inline* validation: Continue stays clickable for empty or malformed emails and nothing is flagged while typing. *Correction 1 Nov:* after clicking Continue with `abc@` the form does show **"Please enter a valid email."**, so validation exists on submit only. No `required` or `autocomplete="email"`; the input is 13 px on mobile, so iOS zooms; Continue text is ~2.4:1 and looks disabled.
**Evidence:** `evidence/login_desktop.png`, `evidence/login_mobile.png`

---

## BUG-26 — [Web][Files] Enter does nothing while a just-attached file is processing, with no feedback
**Severity / Priority / Frequency:** S4 / P3 / 1 of 2 · **Video:** [`BUG-26.webm`](evidence/video/BUG-26.webm)
1. **New Chat → + → Attach files → `falcon_report.pdf`**.
2. **Immediately** type **P-14** and press **Enter**.

**Expected:** Docs say sending waits until the file is ready, so show it ("Waiting for falcon_report.pdf…") or send automatically when ready.
**Actual:** Nothing happens, with no message. Pressing Enter again a few seconds later sends.
**Seen again 1 Nov 2026** with `big_310MiB.txt`: Enter pressed while the chip spinner was showing was silently ignored, and the prompt stayed in the composer (visible in [`BUG-10.webm`](evidence/video/BUG-10.webm), around 1:05).

---

## BUG-27 — [Web][Voice] Voice-call transcript has duplicated and cut-off lines
**Severity / Priority / Frequency:** S4 / P3 / 1 of 1 · **Test data:** `priya.wav` as mic input · **Video:** [`BUG-13.webm`](evidence/video/BUG-13.webm) (same call as BUG-13)
1. **New Chat** → **Start voice call** → play `priya.wav` near the mic (it repeats the same sentences) for ~40 s → **End**.
2. Click **Show transcript**.

**Expected:** Each spoken turn once.
**Actual:** Assistant lines repeat around single user turns ("Bearly: Okay." ×2, "Bearly: Mm-h" ×2, "mm." ×2) and some are cut off ("Mm-h").
**Evidence:** `evidence/voice_transcript.png` · **Video:** [`BUG-13.webm`](evidence/video/BUG-13.webm) (transcript at the end). In the 1 Nov call, user turns were split mid-sentence ("You: December. Arjen will own…", "You: . Hi team…"), but assistant lines were not duplicated this time.

---

## BUG-28 — [Web][Prompt Templates] Template form submits on Shift+Enter; the chat box uses Shift+Enter for a new line
**Severity / Priority / Frequency:** S4 / P3 / Always · **Video:** [`BUG-28.webm`](evidence/video/BUG-28.webm)
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
**Re-checked 1 Nov 2026: not reproduced.** The panel now shows **"Preparing share" / "Building your Page · Ns"** with a **Stop building** button until the preview is ready, and no early "Ready to share". Keep this as *fixed / not reproducible* unless it comes back; no video was made.

---

## BUG-30 — [Public website] Mobile header hides main pages; a dead link; an inaccessible toggle; an old page title
**Severity / Priority / Frequency:** S3 / P2 / Always · **Video:** [`BUG-30.webm`](evidence/video/BUG-30.webm)
1. On a phone (or DevTools → device toolbar → iPhone 12, 390 px) open **https://bearly.ai**. The header shows only **News** and **Get Bearly**, with no ☰ menu. Product, Privacy, Enterprise and Pricing are reachable only from the footer.
2. Open **https://bearly.ai/ai-assistant-comparison** → find the link to Claude's plan article (`support.claude.com/en/articles/11049762-choose-a-claude-plan`) → it returns **404**.
3. Open **https://bearly.ai/pricing** with a screen reader → the Monthly/Yearly switch is announced as a button with no on/off state (no `aria-pressed` / `role="switch"`).
4. Open **https://bearly.ai/dashboard** signed out → the browser tab title is the old tagline **"Bearly.ai | AI at your fingertips"**.

**Expected:** A ☰ menu on mobile with all main pages; no dead outbound links; the pricing switch exposes its state (`role="switch"` + `aria-checked`, or `aria-pressed`); the app title matches the current brand line.
**Actual:** As in steps 1–4: no mobile menu (main pages only in the footer), a 404 link, a switch with no announced state, and an outdated page title.
**Evidence:** `evidence/home_mobile_375.png`, `evidence/pricing_mobile_375.png`

---

## BUG-31 — [Web][Chat] Closing the tab while the agent is working deletes the prompt and its answer
**Severity / Priority / Frequency:** S2 / **P1** / 3 of 3 · **Trigger:** closing the tab while the agent is still working · **Video:** [`BUG-31.webm`](evidence/video/BUG-31.webm)
**Quick reproduction (found 1 Nov while recording, 2 more times):**
1. **New Chat** → send a task that takes a minute or more, e.g. `Best Pick: one pair of wireless earbuds under 3000 rupees available in India. One pick with price, in 2 lines.`
2. While it shows **Working…** (after ~20 s), **close the browser tab**.
3. Open https://bearly.ai/dashboard again and click the chat in the sidebar (it already has an auto-title).
4. The chat shows **"Ready when you are."**: the prompt and the agent's turn are gone.

The same happened with the BUG-34 Best Pick recording ("Budget Wireless Mouse Pick" chat, now empty).

**Original occurrence (310 MiB file):**
1. **New Chat → + → Attach files → `big_310MiB.txt`**. Wait until the chip's spinner stops (≈ 1 min).
2. Type **P-20** `What is the exact size in bytes of the attached file? Reply with the number only.` → **Enter**.
3. The prompt appears, and the agent shows **Working · Read Asset · "Reading attached file metadata…"**. Leave it for 6 minutes. It doesn't finish.
4. Close the tab. Open the chat again from the sidebar (it got the auto-title "Attached File Size").

**Expected:** The prompt and the agent's turn are still there, finished or with an error and **Retry**.
**Actual:** The chat has a title and **Browse files (1)**, but the body is empty (*"Ready when you are."*). The prompt and the agent's turn are gone, with no error. The chat looks as if nothing was ever asked.
**Evidence:** `evidence/bug31_lost_turn.png`
**Impact:** Users lose their question and don't know whether it ran (or used credits): Usage still bills the tool calls. Closing a laptop lid or a tab mid-answer is very common. Long-running agent turns need to be saved on the server and resumable.

---

## BUG-32 — [Web][Prompt Templates] Editing a template saves only the first few characters you type
**Severity / Priority / Frequency:** S2 / P1 / 3 of 3 (1 Nov 2026) · **Test data:** the template "Simple Hindi Translator" (created with P-26) · **Video:** [`BUG-32.webm`](evidence/video/BUG-32.webm)
1. Sidebar → **Prompt Templates** → open any template you own. The editor opens with **Template name**, **Description** and **Your template**. There is no Save button; the editor saves automatically.
2. Click at the end of the **Description** field and type ` QA-12345` at normal speed.
3. Wait 5 seconds. Then reload the page (**F5**), or click **Chat** in the sidebar and come back.
4. Read the Description.

**Expected:** The description ends with ` QA-12345`. If saving is still in progress, show "Saving…" / "Saved".
**Actual:** Only the first ~5 characters typed are kept. ` QA-12345` became **` QA-1`**, and in another run ` (QA edit)` became **` (QA `**. There is no saving indicator, no error and no warning when leaving the page. Pasting a whole value at once (one change event) does save fully, which points at the autosave debounce: it saves after the first keystrokes and never again.
**Impact:** Silent data loss in user-written prompts. The user believes their edit is saved and later runs a broken prompt.
**Clean-up:** Set the description back to its original text (tested and restored).

---

## BUG-33 — [Web][Appearance] Dark themes fail colour contrast across the sidebar
**Severity / Priority / Frequency:** S3 / P2 / Always · **Evidence:** `evidence/theme_dark_contrast.png` · **Video:** [`BUG-33.webm`](evidence/video/BUG-33.webm)
1. Account menu → **App Settings → Appearance** → Theme **Dark**. Close Settings.
2. Look at the sidebar: Prompt Templates, Routines, Meetings, the PROJECTS/TODAY headings and every chat title.
3. Optional: run the axe DevTools scan (see BUG-15).

**Expected:** Text contrast at least 4.5:1 (WCAG 2.1 AA), as in the default theme.
**Actual:** All sidebar text is **#696969 on #161616 = 3.29:1**. axe reports **21** contrast failures in Dark and **22** in Dracula, against 2 in System/Light. The **New Chat** button in Dark is 4.14:1.
**Note:** This replaces the 31 Oct result for SET-02 ("Dark theme fine"), which was judged by eye only.
**Clean-up:** Theme set back to **System**.

---

## BUG-34 — [Web][Best Pick] "Nothing under ₹1,500" for wireless mice, then suggests ₹10,000 mice
**Severity / Priority / Frequency:** S3 / P2 / 3 of 3 · **Evidence:** `evidence/bestpick_mouse.png` · **Video:** [`BUG-34.webm`](evidence/video/BUG-34.webm)
1. **New Chat** → type `Best Pick: one wireless mouse under 1500 rupees available in India. Give one pick with price and why, in 3 lines.` → Enter. Takes ~90 s.

**Expected:** A budget pick, e.g. Logitech M185/M221/B175, which sell in India for about ₹650–₹900 (Variety Infotech ₹649, GeM ₹880, Smartprix ₹894, checked 1 Nov 2026).
**Actual:** "Read 7 expert reviews, checked 218 listings at 16 stores". The card says **"Nothing up to ₹1,500 is worth recommending"**, and the text says **"No qualifying wireless mouse was found under ₹1,500"**. Those are two different claims. The "closest matches" are **Logitech MX Master 3S, MX Master 4 and M720**, which cost 3–7× the budget. It offers "Raise to ₹1,950".
**2nd run (1 Nov, video):** "read 8 expert reviews and 239 listings at 17 stores", again *"Nothing up to ₹1,500 is worth recommending"*, and *"I couldn't find a verified wireless mouse under ₹1,500 … would you consider raising the limit to ₹1,950?"* This time it named no expensive models.
**Impact:** Best Pick is a paid data tool (it shows on Usage as "Best Pick · …" rows). A wrong "nothing exists" answer pushes users to spend more.

---

## BUG-35 — [Web][Chat] Delete-chat dialog says "All chats … will be lost" when deleting one chat
**Severity / Priority / Frequency:** S4 / P3 / Always · **Evidence:** `evidence/delete_chat_dialog.png` · **Video:** [`BUG-35.webm`](evidence/video/BUG-35.webm)
1. Open any chat → press **Ctrl+D** (or Ctrl+K → Delete, or sidebar ⋯ → Delete).
2. Read the dialog. Click **Cancel**.

**Expected:** "Delete 'Model Identity'? This chat and its files will be permanently deleted."
**Actual:** *"Are you sure you want to delete chat 'Model Identity'? **All chats, files and memory will be lost.** This action cannot be undone."* It reads as if every chat will be deleted, and it's unclear whether account memory is affected.

---

## BUG-36 — [Web][Encryption] Opening and closing "Set up end-to-end encryption" removes the message-box controls
**Severity / Priority / Frequency:** S3 / P1 / 3 of 3 · **Evidence:** `evidence/e2e_controls_gone.png` · **Video:** [`BUG-36.webm`](evidence/video/BUG-36.webm)
1. Click **New Chat**. The message box shows **+**, chat settings, voice, the **model picker**, Work locally, Permissions and Connectors.
2. Click **Set up end-to-end encryption** (the small pill below the message box).
3. Don't set anything up. Close the pop-up with **×** (or Esc).

**Expected:** The message box is unchanged, because nothing was set up.
**Actual:** **+, chat settings, voice, the model picker, Work locally, Permissions and Connectors all disappear.** Only Send and "No project" remain. **New Chat** doesn't restore them; only a **page reload** does. The controls already vanish while the pop-up is open (it says *"Memory is off in these chats"*), so the new chat seems to switch into encrypted-chat mode before setup is confirmed.
**Impact:** Users who just look at the encryption offer lose the ability to attach files or choose a model, with no explanation.

---

## BUG-37 — [Public website] Top menu doesn't show the current page, and "Pricing" disappears on the Pricing page
**Severity / Priority / Frequency:** S4 / P3 / Always · **Video:** [`BUG-37.webm`](evidence/video/BUG-37.webm)
1. Open https://bearly.ai → click **Product**, then **Enterprise**, then **News** in the top menu.
2. On each page look at the menu item for that page.
3. Click **Pricing**. Look at the menu. Then click **News**.

**Expected:** The current page's item is highlighted (colour, underline or weight, plus `aria-current="page"`), and menu items never move.
**Actual:**
- All items look identical on every page (same colour rgb(92,92,102), same weight 500, no `aria-current`).
- On /pricing the **"Pricing" item is removed** from the menu (Product · Privacy · Enterprise · News), and the other items shift. It comes back when you leave the page.

---

## BUG-38 — [Public website] "Get Bearly" starts a download immediately, served from github.com
**Severity / Priority / Frequency:** S3 / P2 / Always (desktop) · **Video:** [`BUG-38.webm`](evidence/video/BUG-38.webm)
1. On a desktop browser open https://bearly.ai → click **Get Bearly** (top right).

**Expected:** A download page or dialog that says what will be downloaded (file, size, platform) and starts only when the user clicks **Download**.
**Actual:**
- The installer download starts **immediately**: `Bearly-5.2.11.AppImage` on Linux, `Bearly-Setup-5.2.11.exe` on Windows, `Bearly-5.2.11-universal.dmg` on Mac. A "Download started" dialog then offers *Download again* / *Continue in browser*.
- The file comes from `bearly.ai/download/<os>`, which redirects to **github.com/bearlyai/releases** and then `release-assets.githubusercontent.com`. If the browser blocks automatic downloads, the user lands on GitHub.
- Phones behave sensibly: iPhone goes to the App Store, Android to the web app.

**Impact:** For a privacy-first brand, an unrequested executable download, served from a third-party domain, undermines trust and can trigger browser or antivirus warnings.
**Note:** "Continue in browser opens GitHub" (reported in manual testing) was **not reproduced**. On desktop, Android and iPhone it opened `bearly.ai/dashboard/focus`. The GitHub page most likely came from the **Pricing plan buttons**, which download the installer from github.com (BUG-42).

---

## BUG-39 — [Web][Auth][Accessibility] Sign-in field and button outlines are almost invisible; no tap feedback on mobile
**Severity / Priority / Frequency:** S3 / P2 / Always · **Evidence:** `evidence/signin_outlines.png` · **Video:** [`BUG-39.webm`](evidence/video/BUG-39.webm)
1. Open https://bearly.ai/dashboard signed out. Look at the borders of **Continue with Microsoft**, **Continue with Google** and the **Email** box.
2. On a phone (or DevTools → iPhone 13), tap **Continue**.

**Expected:** Borders of input controls have at least **3:1** contrast with the background (WCAG 1.4.11). Buttons show a pressed state (colour change, ripple or scale) when tapped.
**Actual:**
- The Microsoft/Google button borders are `rgba(0,0,0,0.06)` on #F5F5F5, about **1.2:1** (dark mode: `rgba(255,255,255,0.04)` on #161616, about 1.1:1). The email box's orange border is **2.6:1** in light mode.
- Pressing **Continue** on mobile changes nothing (same background, transform and opacity before and during the press), so users can't tell their tap registered.
- Design note (manual testing): the orange brand colour sits next to the red error text ("Please enter a valid email."), and the two clash. Errors are hard to tell apart from normal accents.

---

## BUG-40 — [Web][Support] "Contact Support" accepts and sends an empty message
**Severity / Priority / Frequency:** S4 / P3 / 2 of 2 (desktop and mobile) · **Evidence:** `evidence/support_empty_sent.png`
1. Open any chat with an error reply (e.g. BUG-07) → click **Get help**. The *Contact Support* form opens.
2. Leave the text box **empty** and click **Contact Support**.

**Expected:** The button stays disabled, or a message says "Please describe the issue".
**Actual:** *"Thank you for contacting support! Your message has been sent."* Two empty tickets were sent this way during testing (no video, to avoid sending more).
**Note:** On desktop and mobile, Get help opens the same in-app form, plus a `mailto:support@bearly.ai` link (only that link opens the mail app).

---

## BUG-41 — [Web][Models] Advertised models (Gemini, DeepSeek, MiniMax) can't be chosen; Gemini only appears inside Council
**Severity / Priority / Frequency:** S3 / P2 / Always · **Video:** [`BUG-41.webm`](evidence/video/BUG-41.webm)
1. Homepage https://bearly.ai → MODELS line: *"Claude, GPT, **Gemini**, Grok, **DeepSeek**, GLM, **MiniMax**, ZDR models only"*. The Enterprise page says *"Claude, GPT, Gemini, and Grok"*.
2. In the app: model picker → **Show all models** (or Settings → This Chat → Model).
3. Type `/council` → send a question → open the **Council settings** gear.

**Expected:** Every advertised model family can be chosen, or the copy explains which are used only inside Bearly's own models.
**Actual:**
- The model list has Bearly Fast/Pro/Ultra, GPT 6 Astra/6.1 Sol/6 Luna, Sonnet/Opus/Fable/Haiku 5.5, Grok 4.7, Muse Spark 1.3 and GLM 5.3/Flash. There is **no Gemini, DeepSeek or MiniMax**.
- Usage shows Gemini running behind Bearly models (`vertex/gemini-3.8-flash`, 47 calls).
- **Council of Experts** offers *Gemini 3.8 Flash* and *Gemini 4 Argon*, which can't be picked as a chat model.

**Also:** There is no way to request a model except the generic **Give Feedback** form (see SUG-28).

---

## BUG-42 — [Public website][Pricing] Plan buttons ("Get Started", "Get Pro", "Get Analyst") download the desktop app from GitHub instead of signing up
**Severity / Priority / Frequency:** S2 / P1 / Always (desktop) · **Video:** [`BUG-42.webm`](evidence/video/BUG-42.webm)
1. On a desktop browser open https://bearly.ai/pricing.
2. Click **Get Pro** (or **Get Started** under Free, or **Get Analyst**).

**Expected:** Sign-up / checkout for that plan (or sign-in first, then the plan picker).
**Actual:**
- Every plan button starts the **desktop installer download** (`Bearly-5.2.11.AppImage` / `.exe` / `.dmg`) from `bearly.ai/download/<os>` → **github.com/bearlyai/releases** → `release-assets.githubusercontent.com`, and shows "Download started — Bearly is on its way." None of them select a plan or open checkout.
- If the browser blocks automatic downloads, the user lands on **GitHub**, which explains the manual-testing report "Get started opened GitHub".
- On iPhone the same buttons open the **App Store**; on Android they open the web app (`/dashboard/focus`).
- There is **no "Sign in" or "Open web app" link** on the public site (home, pricing, product, enterprise, privacy, news). On desktop the only way into the web app is **"Continue in browser"**, which appears only in the dialog shown *after* the installer download has already started. (On Android, "Get Bearly" goes straight to the web sign-in, which works well.)

**Impact:** The pricing page's buy buttons don't sell anything. A user who wants Pro gets an executable download from a third-party domain, which hurts conversion and trust.

---

## BUG-43 — [Web][Plans] Free plan: paid features are hidden, shown unlocked, or blocked only after sending, with three different behaviours
**Severity / Priority / Frequency:** S3 / P1 / Always (Free account) · **Video:** [`BUG-43.webm`](evidence/video/BUG-43.webm)
**Account:** a **Free** test account (sidebar badge "Free"), compared with the Pro account used elsewhere.
1. Sign in with a Free account. Look at the sidebar.
2. Open `https://bearly.ai/dashboard/focus/routines` directly.
3. In a New Chat type **/**, look at the tools, then send `Best Pick: one pair of wired earphones under 500 rupees in India, one line.` (also try **/Deep research** or **/council**).
4. Model picker → **Show all models** → click **Opus 5.5**.

**Expected:** One consistent pattern for paid features on Free: visible, marked with 🔒 and the plan name ("Pro"), with an explanation and upgrade button **before** use. Bearly's docs say *"If your plan does not include Council of Experts, selecting the command opens the upgrade information instead."*
**Actual:**
- **Routines is hidden entirely.** It's not in the Free sidebar, and its URL shows a generic *"Page not found — This page doesn't exist or has been moved"*. A Free user never learns it exists.
- **Paid tools are shown unlocked.** Deep research, Best Pick, People Search and others appear in the "/" menu with no lock. They can be selected and sent, and only then does the reply say *"This request tripped our free-plan checks. Go Pro to continue, or email support@bearly.ai if we've misread it."* (with **Go Pro**). That wording reads like an abuse filter, not "this is a Pro feature". Council (`/council` → Ask Experts) behaves the same, contrary to the docs.
- **Locked models look available.** In the model picker, Opus 5.5 (and others) look normal; clicking opens Billing with *"Opus 5.5 needs the Pro plan or higher"* (same pattern as BUG-11 on Pro). Bearly Pro and Ultra can be selected on Free with no prompt.
- Free accounts also have **no Usage tab** in Settings (only a "0%" meter in the sidebar), so Free users can't see what their daily allowance is or how much is left.
- *Correct on Free:* Settings → Voice clearly says *"Choosing a voice model needs a paid plan."*

**Impact:** Users can't tell what Pro adds, waste a request before learning it's paid, and see an alarming "tripped our checks" message. Hidden features (Routines) can't drive upgrades. See SUG-34 for the proposed single pattern.

---

## Security observations (not exploited)
**S-1. No Content-Security-Policy or anti-framing header on the app.**
Run `curl -sI https://bearly.ai/dashboard/focus/new` → the response has HSTS, `x-content-type-options`, `referrer-policy`, but **no** `content-security-policy` and no `x-frame-options` / `frame-ancestors`.

**S-2. Session token in localStorage.**
Signed in, DevTools → **Application → Local Storage → https://bearly.ai** → key `_bearly:token`. Any XSS could read it; a strict CSP (S-1) is the main defence. Chat rendering resisted the XSS payloads in **P-03**.

**S-3. "Continue with Microsoft" appears only after a logout.**
In a fresh private window, `/dashboard` shows only Google + email. After signing in and logging out on that device, the sign-in page also shows **Continue with Microsoft**. Check whether `loginCapabilities` is meant to depend on device history.
