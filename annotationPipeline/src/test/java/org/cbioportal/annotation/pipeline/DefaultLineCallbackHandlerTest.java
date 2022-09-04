package org.cbioportal.annotation.pipeline;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Mete Ozguz
 */
class DefaultLineCallbackHandlerTest {

    @Test
    void handleLine_success_Allele1() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(new ArrayList<>());
        handler.handleLine(testLine);
    }

    @Test
    void checkHeader_success_Allele1() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        DefaultLineCallbackHandler.checkHeader(testLine);
    }

    @Test
    void handleLine_success_Allele2() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele2";
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(new ArrayList<>());
        handler.handleLine(testLine);
    }

    @Test
    void handleLine_missing_Chromosome() {
        String testLine = "Start_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        handleLine(testLine, "Input file does not contain all the necessary fields. Missing field: Chromosome");
    }

    @Test
    void handleLine_missing_Start_Position() {
        String testLine = "Chromosome\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        handleLine(testLine, "Input file does not contain all the necessary fields. Missing field: Start_Position");
    }

    @Test
    void handleLine_missing_End_Position() {
        String testLine = "Chromosome\tStart_Position\tReference_Allele\tTumor_Seq_Allele1";
        handleLine(testLine, "Input file does not contain all the necessary fields. Missing field: End_Position");
    }

    @Test
    void handleLine_missing_Reference_Allele() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tTumor_Seq_Allele1";
        handleLine(testLine, "Input file does not contain all the necessary fields. Missing field: Reference_Allele");
    }

    @Test
    void handleLine_missing_Tumor_Seq_Allele1_and_Tumor_Seq_Allele2() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele";
        handleLine(testLine, "Header line should either include \"Tumor_Seq_Allele1\" or \"Tumor_Seq_Allele2\"");
    }

    private void handleLine(String line, String expectedMessage) {
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(new ArrayList<>());
        try {
            handler.handleLine(line);
        } catch (RuntimeException e) {
            assertEquals(expectedMessage, e.getMessage());
            return;
        }
        fail();
    }
}