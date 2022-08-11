package org.cbioportal.annotation.pipeline;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mete Ozguz
 */
class DefaultLineCallbackHandlerTest {

    @Test
    void handleLine_success_Allele1() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(tokenizer, new ArrayList<>());
        handler.handleLine(testLine);
        assertTrue(tokenizer.hasNames());
    }

    @Test
    void handleLine_success_Allele1_with_null_tokenizer() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(null, new ArrayList<>());
        handler.handleLine(testLine);
    }

    @Test
    void checkHeader_success_Allele1() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler.checkHeader(testLine, tokenizer);
        assertEquals(true, tokenizer.hasNames());
    }

    @Test
    void checkHeader_success_Allele1_with_null_tokenizer() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        DefaultLineCallbackHandler.checkHeader(testLine, null);
    }

    @Test
    void handleLine_success_Allele2() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele2";
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(tokenizer, new ArrayList<>());
        handler.handleLine(testLine);
        assertTrue(tokenizer.hasNames());
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
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(tokenizer, new ArrayList<>());
        try {
            handler.handleLine(line);
        } catch (RuntimeException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertFalse(tokenizer.hasNames());
            return;
        }
        fail();
    }
}