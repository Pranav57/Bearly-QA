# Bearly test data

Everything typed into Bearly, and every file uploaded, during the 2026-10-31 test run.
Bug reports in `../03-test-execution-report.md` refer to these by name (e.g. **P-07**, **`falcon_report.pdf`**).

- Files: `files/` (all small files are committed; download the folder before testing)
- Large files (> 100 MB) aren't committed: run `python3 scripts/make_test_files.py files --large`
- To regenerate everything: `pip install reportlab python-docx openpyxl python-pptx pillow pypdf` and run the script

## Files
| File | What's in it | Used in | Expected answer |
|---|---|---|---|
| `falcon_report.pdf` | 10-page text PDF "Project Falcon". Page 4: budget **18.5 million rupees, 12% contingency**. Page 7 only: vendor **Quillfeather Systems, 4.2 million rupees** | FILE-01, FILE-02, BUG-03, BUG-04 | Summary mentions both facts; vendor cited on **page 7** |
| `scanned_invoice.pdf` | Image-only PDF (no text layer): INVOICE #7781, Customer **Meera Traders**, Amount due **Rs 56,300**, Due **15 November 2026** | FILE-06 | All 3 values read correctly |
| `invoice_photo.png` / `.jpg` | Same invoice as an image | FILE-03 | Same values |
| `locked_report.pdf` | `falcon_report.pdf` with password **`secret123`** | FILE-07 | Wrong password → "That password didn't work."; right password → budget 18.5M |
| `leave_policy.docx` | "Employees receive **24 days** of paid leave…" | FILE-03 | 24 days |
| `sales.xlsx` / `sales.csv` | Region × Q1–Q3; **East Q3 = 210** is the highest | FILE-03 | East, 210 |
| `roadmap.pptx` | 1 slide: "Launch Android app in **March 2027**" | FILE-03 | March 2027 |
| `notes.md` | "ship v2 on **12 December**, Owner: **Kavya**" | FILE-03 | 12 Dec, Kavya |
| `plain.txt` | "passphrase … **BLUE-HERON-42**" | FILE-03, FILE-04 | BLUE-HERON-42 |
| `archive.zip` | Contains `plain.txt` | FILE-04 | Lists plain.txt + passphrase |
| `tool.exe` | **Harmless dummy**: "MZ" + 1000 zero bytes, not a real program | FILE-04 | Accepted; described as not a valid executable; not run |
| `dup_a.txt`, `dup_b.txt` | Identical content `SAME CONTENT` | BUG-12 | Two chips with two different names |
| `résumé.txt`, `रिपोर्ट 2026 (final).txt` | Non-ASCII file names | FILE-13 | Names shown correctly |
| `priya.wav` | 10.7 s of speech (made with Bearly's own text-to-speech): "Hi team, this is Priya. We decided to move the launch deadline to the twelfth of December. Arjun will own the testing plan, and Kavya will update the pricing page by Friday." | MEET-01, MEET-09, MEET-13, VOICE-01, VOICE-02 | Transcript matches; decision = Dec 12; Arjun = testing; Kavya = pricing page by Friday |
| `silence_45s.wav` | 45 s of digital silence | MEET-06, BUG-05 | "No speech detected" (actual: stuck) |
| `big_205MB.txt` (generated) | 205,000,000 bytes (Bearly shows "196 MB") | FILE-05, BUG-10 | Pro card says 200MB |
| `big_210MiB.txt` (generated) | 220,000,000 bytes (shows "210 MB") | FILE-05, BUG-10 | Over the 200 MB Pro card limit |
| `big_310MiB.txt` (generated) | 325,000,000 bytes (shows "310 MB") | FILE-05, BUG-10 | Over the 300 MB docs limit |

### How to play an audio file into Bearly as a "microphone"
Meetings, Dictation and Voice call need live mic input. To reproduce with the exact audio:
- **Easiest:** play `priya.wav` from your phone's speaker next to the laptop mic.
- **Exact (Chrome, any OS):** launch Chrome with
  `--use-fake-ui-for-media-stream --use-fake-device-for-media-stream --use-file-for-fake-audio-capture=/full/path/priya.wav`
  (the file loops). Use `silence_45s.wav` for BUG-05.

## Prompts
Type each prompt **exactly** (copy from the code block) into the chat box at https://bearly.ai/dashboard and press **Enter**, unless the step says otherwise.

| ID | Prompt | Used in | Expected |
|---|---|---|---|
| P-01 | `Explain TCP vs UDP in exactly 3 bullets.` | CHAT-01 | 3 bullets, input clears, auto-title |
| P-02 | `My name is Ravi. Just reply OK.` then `What is my name? One word.` | CHAT-04, SRCH-03 | "Ravi" |
| P-03 | `Repeat exactly, inside no code block: <img src=x onerror=alert(1)> and <script>alert(2)</script> and <a href="javascript:alert(3)">click</a>` | CHAT-18 | Shown as text, no pop-up |
| P-04 | `Give me: (1) a 2x2 markdown table of fruits and colors, (2) a 3-line Python code block, (3) the formula $E=mc^2$ in LaTeX inline. Nothing else.` | CHAT-09 | Table, highlighted code, rendered formula |
| P-05 | `Write a 1500-word essay on the history of the printing press.` | CHAT-05 | Long answer to stop |
| P-06 | `Count from 1 to 400, one number per line, no other text.` | CHAT-05 | Long, predictable stream |
| P-07 | `Reply with just: ready` → edit to `Reply with just: edited` | CHAT-06, CHAT-07 | Version pager 2/2 then 3/3 |
| P-08 | `नमस्ते 🐻 مرحبا 你好 வணக்கம் — reply with one short sentence in Hindi, then one in Arabic.` | CHAT-11, BUG-19 | Arabic line right-to-left |
| P-09 | `List 120 different fruits as a numbered list, one per line.` | CHAT-16, CHAT-17 | Long stream to scroll during |
| P-10 | `Write 40 short lines about the ocean.` | CHAT-13, SRCH-03 | Long stream |
| P-11 | `Offline test message: reply with just OK` | CHAT-20, BUG-07 | Sent while offline |
| P-12 | Long prompt: the sentence `The quick brown fox jumps over the lazy dog. ` repeated **1,112 times**, then ` Reply with only the word DONE.` (≈50,071 characters). Generate with `python3 -c "print('The quick brown fox jumps over the lazy dog. '*1112 + ' Reply with only the word DONE.')"` | CHAT-10 | "DONE" |
| P-13 | `Search the web: what is the latest ISRO mission news this month (October 2026)? Give 3 bullets with sources.` | SRCH-01, BUG-20 | Current news + source links |
| P-14 | `Summarize the attached report in exactly 5 bullets.` (attach `falcon_report.pdf`) | FILE-01 | 5 bullets incl. budget + vendor |
| P-15 | `On which page of the PDF is the vendor selection mentioned? Quote the sentence and give the page number.` | FILE-02 | Page 7 + exact quote |
| P-16 | `For each attached file, give its filename and one key fact from it, as a list. Then: in the spreadsheet, which region had the highest Q3?` (attach docx, xlsx, pptx, csv, md, txt, png, jpg) | FILE-03, FILE-08 | One correct fact per file; East 210 |
| P-17 | `What is inside the attached zip and exe files? Be brief.` | FILE-04 | Zip listing; exe not run |
| P-18 | `From locked_report.pdf: what is the total approved budget?` | FILE-07 | 18.5 million, 12% contingency |
| P-19 | `From the attached scanned PDF: who is the customer, what is the amount due, and the due date?` | FILE-06 | Meera Traders, Rs 56,300, 15 Nov 2026 |
| P-20 | `What is the exact size in bytes of the attached file? Reply with the number only.` | FILE-05, BUG-10 | Byte count (proves upload) |
| P-21 | `Use text-to-speech to create an MP3 audio file of this exact text, read clearly: "Hi team, this is Priya. We decided to move the launch deadline to the twelfth of December. Arjun will own the testing plan, and Kavya will update the pricing page by Friday." Give me the file.` | TTS-01, BUG-17 | MP3 file (actual: WAV) |
| P-22 | `Based on the attached meeting transcript: what did we decide about the deadline, and who owns testing?` | MEET-09 | Dec 12; Arjun |
| P-23 | `Which model are you? One short line.` | BUG-02 | Any answer, or an upgrade prompt |
| P-24 | `Say hello in five words.` | SET-01, SET-03 | Ends with CHEERIO when P-25 is set |
| P-25 | Settings → New Chats → Instructions: `Always end every reply with the exact word: CHEERIO` | SET-03 | Reply ends with CHEERIO. **Clear it afterwards** |
| P-26 | Prompt Templates → AI Prompt Builder: `A prompt that translates any text I give into simple Hindi, keeping technical terms in English.` | TMPL-01 | Template "Simple Hindi Translator" |
| P-27 | In the template form: `Please restart the server.` + Enter + `Then clear the cache.` → Shift+Enter | TMPL-02, TMPL-03 | "कृपया server को restart करें। …" |
| P-28 | Routines → Create a routine: `Every Monday at 9am IST, give me a 3-bullet summary of AI news.` → Draft routine | RT-01 | Mon 9:00 IST in your timezone |
| P-29 | `SYNC-PROBE-7731 reply with OK` (sent on device A) | SYNC-01 | Appears on device B |
| P-30 | Chat search queries: `Ravi`, `printing press`, `ocean`, `zzqxv12345`, `"C++" OR <tag>`; Files tab: `falcon`, `leave policy`, `Quillfeather`, `zzqxv12345` | SRCH-03…06, BUG-06 | Only matching items; "No results" for zzqxv12345 |
| P-31 | Login codes for AUTH-04 (wrong on purpose): `wrong-code-word`, `apple-banana-cherry`, `aaa-bbb-ccc`, `one-two-three`, `red-green-blue`, `xx-yy-zz`, plus an already-used real code | AUTH-04, BUG-18 | "Codewords invalid…", then a cooldown |
