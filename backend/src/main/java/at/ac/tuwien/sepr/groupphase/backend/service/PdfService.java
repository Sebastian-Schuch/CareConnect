package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * Service for creating PDFs.
 */
public interface PdfService {
    /**
     * Creates a PDF document with the account data of the user.
     *
     * @param userLogin the user to create the account data sheet for
     * @return the created PDF document
     */
    PDDocument getAccountDataSheet(UserLoginDto userLogin);
}
