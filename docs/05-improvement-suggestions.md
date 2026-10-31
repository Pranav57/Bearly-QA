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
**See it today:** Block the microphone (BUG-09 step 1). Meetings → Start recording shows a clear message; Voice call shows nothing; dictation behaves differently again.
**Change:** One shared component: "Bearly can't use your microphone" + OS/browser-specific steps + "Try again".

### SUG-19 — Don't offer locked models as defaults
**See it today:** BUG-02 steps 3–4.
**Change:** In Settings → New Chats → Model (and in Routines and Projects), hide or lock models above the plan.

### SUG-20 — Explain new concepts the first time they appear
**See it today:** New Chat → click **Permissions** under the composer ("Standard" / "Full access"); model list badges "ZDR" / "Standard"; the usage bar "0%" at the bottom of the sidebar. None are explained in place.
**Change:** One-time tooltips ("ZDR = the AI provider keeps nothing", "Full access = no approval prompts for tools", "Credits = …").
**Why:** These concepts are new to most users and drive trust and billing decisions.
