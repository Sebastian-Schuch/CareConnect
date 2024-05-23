package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.PdfCouldNotBeCreatedException;
import at.ac.tuwien.sepr.groupphase.backend.service.PdfService;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Service
public class PdfServiceImpl implements PdfService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final PDFont BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDFont HEADER = PDType1Font.HELVETICA;
    private static final PDFont DATA = PDType1Font.COURIER;
    private float yaxisPosition;

    @Override
    public PDDocument getAccountDataSheet(UserLoginDto userLogin) {
        LOG.trace("Creating account data sheet for userId {}", userLogin.getId());
        try {
            yaxisPosition = 700;
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            addWrappedTextCentered("Account Data Sheet", 22, BOLD, contentStream, page);

            updateY(-15);
            addHeaderAndWrappedText("Email: ", userLogin.getEmail(), contentStream, page);
            addHeaderAndWrappedText("Password: ", userLogin.getPassword(), contentStream, page);


            contentStream.close();
            return document;
        } catch (IOException e) {
            LOG.error(String.format("Could not create PDF document: %s", e.getMessage()));
            throw new PdfCouldNotBeCreatedException("Could not create PDF document: " + e.getMessage());
        }
    }

    /**
     * Adds wrapped text to the PDF document.
     *
     * @param text          the text to add
     * @param size          the size of the text
     * @param font          the font of the text
     * @param contentStream the content stream of the PDF document
     * @param page          the page of the PDF document
     * @throws IOException if the text could not be added
     */
    private void addWrappedTextCentered(String text, int size, PDFont font, PDPageContentStream contentStream, PDPage page) throws IOException {
        LOG.trace("Adding wrapped text to PDF document");
        String[] wrappedText = WordUtils.wrap(text, 75).split("\\r?\\n");
        String string;
        for (int i = 0; i < wrappedText.length; i++) {
            updateY(-15);
            contentStream.beginText();
            contentStream.setFont(font, size);
            string = wrappedText[i];
            float titleWidth = font.getStringWidth(string) / 1000 * size;
            contentStream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, yaxisPosition);
            contentStream.showText(string);
            contentStream.endText();
        }
    }

    /**
     * Adds a header and wrapped text to the PDF document.
     *
     * @param strHeader     the header to add
     * @param text          the text to add
     * @param contentStream the content stream of the PDF document
     * @param page          the page of the PDF document
     * @throws IOException if the text could not be added
     */
    private void addHeaderAndWrappedText(String strHeader, String text, PDPageContentStream contentStream, PDPage page)
        throws IOException {
        LOG.trace("Adding header and wrapped text to PDF document");
        int size = 12;
        updateY(-15);
        contentStream.beginText();
        float headerWidth = HEADER.getStringWidth(strHeader + text) / 1000 * size;
        contentStream.newLineAtOffset((page.getMediaBox().getWidth() - headerWidth) / 2, yaxisPosition);
        contentStream.setFont(BOLD, size);
        contentStream.showText(strHeader);
        contentStream.setFont(HEADER, size);
        String[] wrappedText = WordUtils.wrap(text, 75).split("\\r?\\n");
        String string;
        for (int i = 0; i < wrappedText.length; i++) {
            contentStream.setFont(DATA, size);
            string = wrappedText[i];
            if (i != 0) {
                updateY(-15);
                contentStream.beginText();
                float titleWidth = DATA.getStringWidth(string) / 1000 * size;
                contentStream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, yaxisPosition);
            }
            contentStream.showText(string);
            contentStream.endText();
        }
    }

    private void updateY(int i) {
        yaxisPosition += i;
    }
}
