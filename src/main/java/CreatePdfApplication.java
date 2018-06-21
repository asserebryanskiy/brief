import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class CreatePdfApplication {
    public static void main(String[] args) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            File file = new File("test.pdf");
            PdfDocument pdf = new PdfDocument(new PdfWriter(file));
            Document document = new Document(pdf);
            for (int i = 0; i < 2; i++) {
                document.add(new Paragraph().add((i + 1) + ". ").add("text " + i));
            }
            document.close();
            pdf.close();
//            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
//            return new byte[0];
        }
    }
}
