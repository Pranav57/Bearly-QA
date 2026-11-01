# Bearly.ai — Improvement Suggestions (with steps to see the current behaviour)

These aren't defects. They are places where the product works but could be clearer, faster or safer.
Each suggestion says **how to see it today** (same setup as [04-bug-reports.md](04-bug-reports.md#before-you-start-common-setup-for-every-bug)),
**what to change**, and **why**. Prompts **P-xx** and files are in [`test-data/`](test-data/README.md).

| # | Area | Priority |
|---|---|---|
| SUG-01 | Lock badges in the model picker | High |
| SUG-02 | Show ZDR / Standard on each answer | High |
| SUG-03 | Collapse long pasted prompts | Medium |
| SUG-04 | "Stopped" label + Continue | Medium |
| SUG-05 | Upload progress % and offline state | High |
| SUG-06 | Queue messages typed offline | High |
| SUG-07 | Auto-summary + upload on Meetings | Medium |
| SUG-08 | Live dictation preview | Medium |
| SUG-09 | Highlight search matches | Medium |
| SUG-10 | Source chips with domain + preview | Medium |
| SUG-11 | Code-block language label + copy | Low |
| SUG-12 | Resend code, expiry, paste-split | High |
| SUG-13 | Credits calculator, one source for plan copy | High |
| SUG-14 | Live sidebar sync | Medium |
| SUG-15 | Keep empty drafts out of history | Low |
| SUG-16 | Real delete on Remove + Undo | High |
| SUG-17 | Installable web app (PWA) for Android | High |
| SUG-18 | One permission-error component | Medium |
| SUG-19 | Filter locked models from Settings defaults | High |
| SUG-20 | Explain ZDR / Standard / Full access / credits on first use | Medium |
| SUG-21 | Archive: toast with Undo, and an "Archived" label inside the chat | Medium |
| SUG-22 | Use the user's locale (₹, units, date format) in generated apps and answers | Medium |
| SUG-23 | People Search: answer directly for unambiguous public figures | Low |
| SUG-24 | Keep chart categories in the order of the data | Low |
| SUG-25 | Make "@" different from "/" (mentions of files, chats, models) or explain the difference | Low |
| SUG-26 | Separate error red from brand orange; add pressed states on mobile | Medium |
| SUG-27 | Explain in the Prompt Template editor how a template is used (/name, @ → Skills) | Medium |
| SUG-28 | "Request a model" and "Send diagnostic logs" in Give Feedback (esp. mobile) | Medium |
| SUG-29 | Usage: daily history and a one-line-per-day statement / CSV export | Medium |
| SUG-30 | Controls that look disabled must be disabled; explain why (uploading, empty code) | High |
| SUG-31 | Style the "1x" speed control like the Download button; align the time label | Low |

---

### SUG-01 — Mark locked models in the model picker
**See it today:** New Chat → click the model picker ("Bearly Pro") → **Show all models** → on a Pro account, find **Fable 5.1**. It looks like every other model; clicking it opens the Billing page.
**Change:** Add a 🔒 icon + "Analyst" badge to locked models; on hover show "Needs Analyst — $60/mo".
**Why:** Avoids a surprise jump to Billing; sets expectations before the click (related BUG-11).

### SUG-02 — Show a ZDR / Standard badge on each answer
**See it today:** Send **P-01**. Under the answer you see the model name ("Bearly Pro") but not whether this answer went through a zero-data-retention route. That's only visible in the model list (Show all models).
**Change:** A small shield "ZDR" (or "Standard") next to the model name under each answer, and in **Query info**.
**Why:** Privacy is Bearly's main promise; users should be able to see what happened to *this* message (related BUG-01).

### SUG-03 — Collapse long pasted prompts and turn huge pastes into attachments
**See it today:** New Chat → paste **P-12** (≈50,000 characters) → Enter. The sent message bubble is about 15,000 px tall; you scroll through it to reach the answer.
**Change:** Show the first ~10 lines with "Show more". Pastes over ~20k characters become an attachment chip ("Pasted text, 50 KB").
**Why:** Keeps long chats readable.

### SUG-04 — Label stopped answers and offer Continue
**See it today:** Send **P-06** → after ~5 s click **Stop** (the square button). The partial list stays, but nothing says it was stopped and there's no way to continue.
**Change:** Grey "Stopped" tag + **Continue** button.
**Why:** Users can't otherwise tell a stopped answer from a complete one.

### SUG-05 — Upload progress percentage and a "waiting for connection" state
**See it today:** New Chat → + → Attach files → a large file (e.g. `big_210MiB.txt` from `scripts/make_test_files.py --large`). The chip shows only a spinner. Go offline (DevTools → Network → Offline) for 15 s: still just a spinner. It finished ~3 min after reconnecting.
**Change:** "37% · 1 min left" on the chip; when offline, "Waiting for connection…"; when back, "Resuming…".
**Why:** Big uploads look frozen today.

### SUG-06 — Queue messages typed while offline
**See it today:** BUG-07 steps (go offline → send **P-11**). You get a generic error even though the app already shows "Offline".
**Change:** Disable Send with a tooltip "You're offline", or accept the message as "Queued — sends when you're back online".
**Why:** The app already knows it's offline (banner); use that.

### SUG-07 — Auto-summarize meetings, and allow uploading recordings on the Meetings page
**See it today:** Sidebar → **Meetings and Recordings** → **Start recording** → speak (or play `priya.wav`) 30 s → **Stop** → open the recording. The transcript appears, but the summary only after you click **Create summary**. The page has no **Upload** button for an existing recording.
**Change:** Generate the summary automatically (with a setting to turn it off); add "Upload recording".
**Why:** Matches the "Notes from your meetings" promise on the homepage; uploading old recordings is a common need.

### SUG-08 — Show live text while dictating
**See it today:** Account menu → App Settings → **Dictation** → turn on **Enable dictation** → New Chat → click the **microphone** in the composer → speak for 10 s. The composer stays empty until you click Stop.
**Change:** Grey interim text while speaking.
**Why:** Without it users don't know if dictation is working. (Turn dictation off again afterwards if you don't use it.)

### SUG-09 — Highlight why a search result matched
**See it today:** Sidebar magnifier → type `ocean` (after sending **P-10**). The result shows a snippet from a different message (the fruit list).
**Change:** Show the matching message with the search words highlighted; clicking scrolls to it.
**Why:** Users trust search more when they can see the match (related BUG-06).

### SUG-10 — Source chips with domain, favicon and preview
**See it today:** Send **P-13**. Sources appear as underlined words ("ISRO update"), with no domain, no URL on hover and no new-tab option.
**Change:** Chips like `[🌐 isro.gov.in]`, hover preview (title + first lines), real links.
**Why:** Users judge credibility by domain (related BUG-20).

### SUG-11 — Language label and always-visible copy button on code blocks
**See it today:** Send **P-04**. The Python block is highlighted, but there's no "python" label and no copy button on the block itself (only the whole-message Copy).
**Change:** Header bar on each code block: language + Copy.
**Why:** Faster for developers (a core audience).

### SUG-12 — Resend code, show expiry, split pasted codes
**See it today:** Signed out → enter email → Continue. The "Check your email" screen has no Resend, no expiry time and no "check spam" hint.
**Change:** "Didn't get it? Resend (30 s)", "Code expires in 10 min", "Check spam/promotions", and auto-split a pasted `word-word-word` across the 3 boxes.
**Why:** Stuck sign-ins are the most expensive support tickets (related BUG-18).

### SUG-13 — Credits calculator; generate all plan copy from one source
**See it today:** Compare https://bearly.ai/pricing, https://bearly.ai/docs/manual/plans-and-credits/ and Account menu → App Settings → Billing. Features and limits differ (BUG-10, BUG-21), and "$20 in credits" never says what that buys.
**Change:** A calculator ("$20 ≈ N Opus messages, M hours of meeting transcription"). Pricing page, docs and in-app cards all read from one plans config.
**Why:** Credits are abstract; inconsistent copy causes refund requests.

### SUG-14 — Push sidebar changes live
**See it today:** BUG-08 steps: rename/delete in one window, the other doesn't update.
**Change:** Send rename/delete/move events on the same live channel that already syncs messages (~5 s).
**Why:** Prevents reading or replying in deleted chats.

### SUG-15 — Keep empty drafts out of history
**See it today:** BUG-14 steps: an empty "New Chat 1" appears.
**Change:** Only create the history entry when the first message is sent; keep drafts per device.

### SUG-16 — "Remove" should really delete, with Undo
**See it today:** BUG-03 steps: removed attachments stay in Browse files and search.
**Change:** Remove = delete the upload, with a toast "Removed leave_policy.docx · Undo" (5 s).
**Why:** Forgiving and private.

### SUG-17 — Installable web app for Android
**See it today:** On an Android phone open https://bearly.ai → the only option is **Continue in browser**; the docs say *"There is no Android app."* In Chrome's menu there's no "Install app" prompt.
**Change:** Add a web-app manifest + service worker so Android users get **Install app**, a home-screen icon and full-screen mode, until a native app exists.
**Why:** Most of the Indian market is on Android; this is a cheap bridge.

### SUG-18 — One permission-error message for every mic feature
**See it today:** Block the microphone (BUG-09 step 1). Meetings → Start recording shows a clear, lasting message; Voice call shows "Voice unavailable — No microphone is available." for only ~3 s with no fix-it steps; dictation behaves differently again.
**Change:** One shared component: "Bearly can't use your microphone" + OS/browser-specific steps + "Try again".

### SUG-19 — Don't offer locked models as defaults
**See it today:** BUG-02 steps 3–4.
**Change:** In Settings → New Chats → Model (and in Routines and Projects), hide or lock models above the plan.

### SUG-20 — Explain new concepts the first time they appear
**See it today:** New Chat → click **Permissions** under the composer ("Standard" / "Full access"); model list badges "ZDR" / "Standard"; the usage bar "0%" at the bottom of the sidebar. None are explained in place.
**Change:** One-time tooltips ("ZDR = the AI provider keeps nothing", "Full access = no approval prompts for tools", "Credits = …").
**Why:** These concepts are new to most users and drive trust and billing decisions.

### SUG-21 — Archive: confirm with Undo, and label archived chats
**See it today:** Sidebar → hover a chat → **⋯ → Archive**. The chat disappears with no toast and no Undo. It's now under a small **Archives (1)** entry at the very bottom of the sidebar. Opening the archived chat shows no "Archived" label, and you can keep chatting in it.
**Change:** Toast "Chat archived · Undo". An "Archived — Unarchive" banner in the chat.
**Why:** Users who archive by accident think the chat is lost. (Unarchive is in ⋯ inside Archives; Delete says "unarchive first".)

### SUG-22 — Use the user's locale in generated content
**See it today:** Send **P-41** (EMI calculator) from India. The generated app defaults to **$ (USD)**, and the reply even says "Currency defaults to USD".
**Change:** Default to the account's country and currency (₹ and lakh/crore grouping for India), metric units and local date format.
**Why:** Bearly targets Indian users (₹ pricing elsewhere in the product). Wrong-currency defaults look careless.

### SUG-23 — People Search: don't ask for disambiguation when the person is obvious
**See it today:** Send **P-37** ("brief on Sundar Pichai"). The tool replies "Which Sundar Pichai do you mean—Google's CEO? If so, confirm…" instead of answering.
**Change:** Answer for the dominant public figure, with "Did you mean someone else?" underneath.
**Why:** An extra round-trip on every obvious query makes the tool feel slow.

### SUG-24 — Keep chart categories in data order
**See it today:** Send **P-40**. The CSV order is North, South, East, West. The chart shows North, **East, South**, West.
**Change:** Keep the input order by default; offer "Sort by value".
**Why:** Users often order categories on purpose (regions, months, funnel steps).

### SUG-25 — Give "@" its own purpose
**See it today:** In the composer type `/`, then clear it and type `@`. Both open the **same** menu (Skills, Bearly AI Models, Visualize, Research…).
**Change:** Use "@" for mentions (a file, a past chat, a model, a project), or show one menu with a hint "Tip: / and @ both open tools".
**Why:** The placeholder "Type / or @ for tools" suggests they do different things.

### SUG-26 — Don't use orange for both brand and errors; give buttons a pressed state
**See it today:** Sign-in page → type `abc@` → **Continue**. The red error "Please enter a valid email." sits under an orange-bordered field and an orange button. On a phone, tapping **Continue** gives no visual feedback (BUG-39).
**Change:** Keep orange for primary actions only; use a distinct error red with an icon next to the field. Add `:active` styles (darker fill or slight scale) and a loading state on every primary button.
**Why:** Users should tell "brand" from "problem" at a glance, and know their tap registered.

### SUG-27 — Explain how a Prompt Template is used
**See it today:** Sidebar → **Prompt Templates** → open a template. The Live Preview shows a form, plus one line: "Type /Simple Hindi Transla… in your chats to use this template". Nothing says the form appears inside the chat, that the result is sent as a message, or that the template is also listed under **@ → Skills**.
**Change:** A short "How it works" box: "In any chat type `/Template name` (or pick it under @ → Skills). Fill the fields, press Submit (Shift+Enter), and the prompt is sent with your values."
**Why:** Users aren't sure whether to copy-paste the template; this cuts confusion and support tickets.

### SUG-28 — Let users request a model and send diagnostic logs
**See it today:** There's no "Request a model" option anywhere. Advertised families like DeepSeek and MiniMax aren't selectable (BUG-41). Account menu → **Give Feedback** has a text box and **Attach File**, but no "include diagnostic logs" option, which is hard to work around on mobile.
**Change:** In Give Feedback, add a "Request a model" category and an **Include diagnostics** toggle that attaches app version, device, recent errors and request IDs, with no chat content.
**Why:** Many issues are device-specific. Logs make mobile bugs reproducible, and model requests show what users want.

### SUG-29 — Daily usage history and a simple statement
**See it today:** Account menu → App Settings → **Usage**. You get month totals, a single "Today" figure (which is wrong, see BUG-23) and a per-service table. There's no per-day list and no export.
**Change:** A daily list ("1 Nov · $0.94 · 120 calls") and **Download CSV / statement**, one line per day.
**Why:** Credit-based plans need simple, private spend tracking, especially for people who expense it.

### SUG-30 — Controls that look disabled must behave disabled
**See it today:**
- On the code screen, **Sign in** looks greyed out with empty boxes but still sends a request (BUG-18).
- While a file is uploading, Send looks active but Enter does nothing (BUG-26).
- **Contact Support** sends an empty message (BUG-40).

**Change:** One rule everywhere: a control is either enabled or truly `disabled`. When it's disabled, a tooltip says why ("Enter all 3 words", "Waiting for falcon_report.pdf…").
**Why:** Mismatched states make users think the app is broken.

### SUG-31 — Make the audio player controls consistent
**See it today:** Meetings and Recordings → open a recording. **1x** (playback speed) is small plain text, and **Download** is a bordered button. They sit on the same line (both centred at the same height), but look mismatched. The time label "0:48" sits lower than the progress bar.
**Change:** Give "1x" the same button style and size as Download, and vertically centre the time label with the bar.
**Why:** Visual polish; it also gives "1x" a bigger tap target.
