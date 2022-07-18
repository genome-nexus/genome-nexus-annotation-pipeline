package org.cbioportal.annotation.pipeline;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Mete Ozguz
 */
class DefaultLineCallbackHandlerTest {

    @Test
    void handleLine_success_Allele1() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(tokenizer);
        handler.handleLine(testLine);
        assertEquals(true, tokenizer.hasNames());
    }

    @Test
    void handleLine_success_Allele2() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele2";
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(tokenizer);
        handler.handleLine(testLine);
        assertEquals(true, tokenizer.hasNames());
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
        handleLine(testLine, "Input file needs contain at least one of these fields: Tumor_Seq_Allele1, Tumor_Seq_Allele2");
    }

    private void handleLine(String line, String expectedMessage) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(tokenizer);
        try {
            handler.handleLine(line);
        } catch (RuntimeException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(false, tokenizer.hasNames());
            return;
        }
        fail();
    }
}