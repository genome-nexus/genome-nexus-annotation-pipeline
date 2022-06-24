package org.cbioportal.annotation.pipeline;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Mete Ozguz
 */
class DefaultLineCallbackHandlerTest {

    private static final String expectedExString = "Input file does not contain all the necessary fields: [Chromosome, Start_Position, End_Position, Reference_Allele, Tumor_Seq_Allele1]";

    @Test
    void handleLine_success() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(tokenizer);
        handler.handleLine(testLine);
        assertEquals(true, tokenizer.hasNames());
    }

    @Test
    void handleLine_missing_Chromosome() {
        String testLine = "Start_Position\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        handleLine(testLine);
    }

    @Test
    void handleLine_missing_Start_Position() {
        String testLine = "Chromosome\tEnd_Position\tReference_Allele\tTumor_Seq_Allele1";
        handleLine(testLine);
    }

    @Test
    void handleLine_missing_End_Position() {
        String testLine = "Chromosome\tStart_Position\tReference_Allele\tTumor_Seq_Allele1";
        handleLine(testLine);
    }

    @Test
    void handleLine_missing_Reference_Allele() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tTumor_Seq_Allele1";
        handleLine(testLine);
    }

    @Test
    void handleLine_missing_Tumor_Seq_Allele1() {
        String testLine = "Chromosome\tStart_Position\tEnd_Position\tReference_Allele";
        handleLine(testLine);
    }

    private void handleLine(String line) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        DefaultLineCallbackHandler handler = new DefaultLineCallbackHandler(tokenizer);
        try {
            handler.handleLine(line);
        } catch (RuntimeException e) {
            assertEquals(expectedExString, e.getMessage());
            assertEquals(false, tokenizer.hasNames());
            return;
        }
        fail();
    }
}