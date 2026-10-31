# Bearly.ai — Product Overview (for QA prep)

> Compiled 31 Oct 2026 from the job description and public third-party listings
> (G2, PricingSaaS, Chrome Web Store, review sites). bearly.ai itself could not be
> opened from the environment where this was written, so check every detail
> against the live product before relying on it.

## What it is
Bearly is an all-in-one AI assistant for **writers, engineers, researchers and
analysts**. It puts several large language models behind one interface and adds
tools around them: reading and summarizing documents, writing, web search, meeting
transcription and voice.

## Surfaces (each one needs testing)
| Surface | What to know |
|---|---|
| Web app | Main product. Chat, uploads, search, history, settings, billing |
| Mac desktop app | Global hotkey or launcher, native file drag-and-drop, meeting audio capture |
| Windows desktop app | Same as Mac. Watch for installer, auto-update, DPI/scaling and mic permissions |
| iOS app | Chat, voice, file and photo upload, history sync |
| Android app | Same as iOS. Many device sizes and OS versions, back-button behaviour |
| Chrome extension | Summarize or chat with the current page, select text and act on it |

## Core features (mapped to the job description)
1. **Chat**: multi-model chat (GPT-, Claude- and Gemini-family models, depending on plan), conversation history, regenerate, copy, rename/delete chats, markdown and code rendering.
2. **File uploads**: PDFs, docs, papers, books, images, possibly audio/video. Ask questions over the content, summarize, cite pages.
3. **Meetings & transcription**: record or upload meetings, get transcripts, summaries and action items, speaker labels.
4. **Search**: AI web search with sources and citations, plus search across your own chats and files.
5. **Voice**: speech-to-text input and voice conversation mode.
6. **Browser extension**: summarize the page or video, chat with the page, rewrite selected text.

## Plans (sources disagree, so verify on the pricing page)
Free · Pro (~$20/mo) · Analyst (~$50–60/mo; recently said to include a newer
Claude model) · Research (~$100/mo) · Max (~$200/mo) · Enterprise. Plan limits
(model access, upload size, transcription minutes) are a common source of bugs:
paywalls that show up in the wrong place, limits that don't match the copy,
upgrades that don't take effect until a reload.

## Public user feedback themes (useful for exploratory testing)
- G2: 4.4/5 from a small number of reviews.
- "Pro is expensive for small businesses", so pricing and value messaging matter.
- "No offline features", so test offline and flaky-network handling on every surface.

## What the team actually wants from the hire
- Run the main flows **daily** on web, desktop and mobile.
- **Test each release on the day it ships.**
- Write bug reports they can act on: **steps, expected, actual, screen recording**.
- Report UX friction (**confusing / slow / annoying**), not only crashes.
- Manual testing is fine. Playwright or Appium is a plus. This repo includes a Selenium smoke suite to show automation skills.

## Sources
- https://g2.com/products/bearly/reviews
- https://pricingsaas.com/companies/bearly
- https://pricingsaas.com/companies/bearly/diffs/2026W43
- https://chrome.google.com/webstore/detail/bearlyai/lkennebljmnnpimcgdjlocbncpamgipp/reviews
- https://www.krowdbase.com/software/bearly
- https://creatoreconomytools.com/tool/bearly-ai
