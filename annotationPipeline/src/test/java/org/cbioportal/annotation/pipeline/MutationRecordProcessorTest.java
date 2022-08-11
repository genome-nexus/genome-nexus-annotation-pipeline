package org.cbioportal.annotation.pipeline;

import org.cbioportal.models.AnnotatedRecord;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mete Ozguz
 */
class MutationRecordProcessorTest {

    /**
     * Tests the output when such a statement is missing:
     *     record.getAdditionalProperties().put("NON_EXISTENT_FIELD_2", "NON_EXISTENT_FIELD_2_VALUE");
     * @throws Exception
     */
    @Test
    void process_withMissingProperty() throws Exception {
        String[] names = {"Hugo_Symbol", "Entrez_Gene_Id", "Annotation_Status", "NON_EXISTENT_FIELD_1", "NON_EXISTENT_FIELD_2"};
        List<String> header = new ArrayList<>(Arrays.asList(names));
        String expected = "Hugo_Symbol_VALUE\tEntrez_Gene_Id_VALUE\tSUCCESS\tNON_EXISTENT_FIELD_1_VALUE\t";
        process(header, expected);
    }

    /**
     * Tests the output when such a statement is used:
     *     record.getAdditionalProperties().put("NON_EXISTENT_FIELD_1", "NON_EXISTENT_FIELD_1_VALUE");
     * @throws Exception
     */
    @Test
    void process_withExistentProperty() throws Exception {
        String[] tmp = {"Hugo_Symbol", "Entrez_Gene_Id", "Annotation_Status", "NON_EXISTENT_FIELD_1"};
        List<String> header = new ArrayList<>(Arrays.asList(tmp));
        String expected = "Hugo_Symbol_VALUE\tEntrez_Gene_Id_VALUE\tSUCCESS\tNON_EXISTENT_FIELD_1_VALUE";
        process(header, expected);
    }

    private void process(List<String> header, String expected) throws Exception {
        MutationRecordProcessor processor = new MutationRecordProcessor(header);
        AnnotatedRecord record = new AnnotatedRecord();
        record.setHUGO_SYMBOL("Hugo_Symbol_VALUE");
        record.setENTREZ_GENE_ID("Entrez_Gene_Id_VALUE");
        record.setANNOTATION_STATUS("SUCCESS");
        record.getAdditionalProperties().put("NON_EXISTENT_FIELD_1", "NON_EXISTENT_FIELD_1_VALUE");

        String actual = processor.process(record);

        assertEquals(expected, actual);
    }

}