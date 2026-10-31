#!/usr/bin/env python3
"""Regenerate every Bearly test file used in 03-test-execution-report.md.

Usage:  pip install reportlab python-docx openpyxl python-pptx pillow pypdf
        python3 make_test_files.py [output_dir]          # small files
        python3 make_test_files.py [output_dir] --large  # also the 205 MB / 210 MiB / 310 MiB files

The small files are already committed in ../files/. Large files are not
committed (GitHub limit). They are sparse files of zeros with a short header.
"""
import csv, os, sys, zipfile

out = sys.argv[1] if len(sys.argv) > 1 and not sys.argv[1].startswith("--") else os.path.join(os.path.dirname(__file__), "..", "files")
os.makedirs(out, exist_ok=True)
P = lambda name: os.path.join(out, name)


def small_files():
    from reportlab.lib.pagesizes import A4
    from reportlab.pdfgen import canvas
    import docx, openpyxl, pptx
    from PIL import Image, ImageDraw, ImageFont
    from pypdf import PdfReader, PdfWriter

    # falcon_report.pdf: 10 pages. Budget fact on page 4, vendor fact ONLY on page 7 (FILE-01/02)
    topics = ["Introduction to Project Falcon", "Market Overview", "Team Structure", "Budget Summary", "Risks",
              "Timeline", "Vendor Selection", "Testing Strategy", "Rollout Plan", "Conclusion"]
    c = canvas.Canvas(P("falcon_report.pdf"), pagesize=A4)
    for i, t in enumerate(topics, 1):
        c.setFont("Helvetica-Bold", 16); c.drawString(60, 780, f"Page {i}: {t}")
        c.setFont("Helvetica", 11); y = 750
        body = [f"This section of the Project Falcon report discusses {t.lower()} in detail.",
                "The project aims to modernise the customer onboarding flow for Acme Logistics.",
                "Stakeholders reviewed the plan in weekly meetings during the third quarter."]
        if i == 7: body.append("The selected vendor is Quillfeather Systems, chosen for a contract value of 4.2 million rupees.")
        if i == 4: body.append("The total approved budget is 18.5 million rupees, with 12 percent held as contingency.")
        for line in body * 3: c.drawString(60, y, line); y -= 18
        c.showPage()
    c.save()

    # scanned_invoice.pdf / invoice_photo.png / .jpg: image only, no text layer (FILE-03/06)
    img = Image.new("RGB", (1240, 1754), "white"); d = ImageDraw.Draw(img)
    try: f = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 40)
    except OSError: f = ImageFont.load_default()
    for y, line in [(200, "INVOICE #7781"), (300, "Customer: Meera Traders"), (400, "Amount due: Rs 56,300"), (500, "Due date: 15 November 2026")]:
        d.text((100, y), line, fill="black", font=f)
    img.save(P("scanned_invoice.pdf")); img.save(P("invoice_photo.png")); img.save(P("invoice_photo.jpg"), quality=80)

    # locked_report.pdf: password "secret123" (FILE-07)
    w = PdfWriter(); [w.add_page(p) for p in PdfReader(P("falcon_report.pdf")).pages]; w.encrypt("secret123"); w.write(P("locked_report.pdf"))

    dd = docx.Document(); dd.add_heading("Leave Policy 2026", 1)
    dd.add_paragraph("Employees receive 24 days of paid leave per year. Unused leave up to 8 days can be carried forward.")
    dd.save(P("leave_policy.docx"))
    rows = [["North", 120, 135, 150], ["South", 90, 110, 95], ["East", 200, 180, 210], ["West", 60, 75, 80]]
    wb = openpyxl.Workbook(); ws = wb.active; ws.append(["Region", "Q1", "Q2", "Q3"]); [ws.append(r) for r in rows]; wb.save(P("sales.xlsx"))
    with open(P("sales.csv"), "w", newline="") as fh:
        wr = csv.writer(fh); wr.writerow(["Region", "Q1", "Q2", "Q3"]); wr.writerows(rows)
    pr = pptx.Presentation(); s = pr.slides.add_slide(pr.slide_layouts[1])
    s.shapes.title.text = "Roadmap 2027"; s.placeholders[1].text = "Launch Android app in March 2027"; pr.save(P("roadmap.pptx"))
    open(P("notes.md"), "w").write("# Meeting notes\n\n- Decision: ship v2 on **12 December**\n- Owner: Kavya\n")
    open(P("plain.txt"), "w").write("The secret passphrase for this test is BLUE-HERON-42.\n")
    open(P("résumé.txt"), "w").write("codeword MANGO\n")
    open(P("रिपोर्ट 2026 (final).txt"), "w").write("यह एक परीक्षण फ़ाइल है। The codeword is MANGO.\n")
    open(P("dup_a.txt"), "w").write("SAME CONTENT\n"); open(P("dup_b.txt"), "w").write("SAME CONTENT\n")  # BUG-12
    open(P("tool.exe"), "wb").write(b"MZ" + bytes(1000))  # harmless dummy, not a real program (FILE-04)
    with zipfile.ZipFile(P("archive.zip"), "w") as z: z.write(P("plain.txt"), "plain.txt")
    print("small files written to", os.path.abspath(out))


def large_files():
    # FILE-05 / BUG-10. Bearly shows sizes in MiB but labels them "MB".
    for name, size in [("big_205MB.txt", 205_000_000), ("big_210MiB.txt", 220_000_000), ("big_310MiB.txt", 325_000_000)]:
        with open(P(name), "wb") as fh:
            fh.write(b"Bearly size-limit test file\n"); fh.truncate(size)
        print("large file", name, size, "bytes")


if __name__ == "__main__":
    small_files()
    if "--large" in sys.argv: large_files()
    # Audio files (priya.wav = speech, silence_45s.wav) are committed.
    # silence: ffmpeg -f lavfi -i anullsrc=r=16000:cl=mono -t 45 silence_45s.wav
