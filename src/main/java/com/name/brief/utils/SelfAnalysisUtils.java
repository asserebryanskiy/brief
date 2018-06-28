package com.name.brief.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.name.brief.model.games.conference.SelfAnalysis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class SelfAnalysisUtils {
    public static byte[] createPdf(SelfAnalysis selfAnalysis) {
        if (selfAnalysis == null) throw new IllegalArgumentException();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfDocument pdf = new PdfDocument(new PdfWriter(out));
            Document document = new Document(pdf);
            List<String> answers = selfAnalysis.getAnswers();
            for (int i = 0; i < answers.size(); i++) {
                String text = answers.get(i);
                document.add(new Paragraph().add((i + 1) + ". ").add(text));
            }
            document.close();
            pdf.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
