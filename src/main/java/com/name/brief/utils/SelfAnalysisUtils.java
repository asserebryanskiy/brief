package com.name.brief.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.name.brief.model.games.conference.SelfAnalysis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SelfAnalysisUtils {
    public static byte[] createPdf(SelfAnalysis selfAnalysis) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfDocument pdf = new PdfDocument(new PdfWriter(out));
            Document document = new Document(pdf);
            for (int i = 0; i < 3; i++) {
                document.add(new Paragraph().add((i + 1) + ". ").add(selfAnalysis.getAnswers().get(i)));
            }
            document.close();
            pdf.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
