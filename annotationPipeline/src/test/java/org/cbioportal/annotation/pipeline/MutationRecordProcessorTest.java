package org.cbioportal.annotation.pipeline;

import org.cbioportal.models.AnnotatedRecord;
import org.cbioportal.models.MutationRecord;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Mete Ozguz
 */
class MutationRecordProcessorTest {


    /**
     * Headers and their respective data should always be present
     * @throws Exception
     */
    @Test
    void process_withExistentProperty() throws Exception {
        String[] tmp = {"Hugo_Symbol", "Entrez_Gene_Id", "Annotation_Status", "NON_EXISTENT_FIELD_1"};
        List<String> header = new ArrayList<>(Arrays.asList(tmp));
        String expected = "Hugo_Symbol_VALUE\tEntrez_Gene_Id_VALUE\tSUCCESS\tNON_EXISTENT_FIELD_1_VALUE";

        MutationRecordProcessor processor = new MutationRecordProcessor(header);
        MutationRecord.init(header);
        AnnotatedRecord record = new AnnotatedRecord(new MutationRecord("Hugo_Symbol_VALUE\tEntrez_Gene_Id_VALUE\tSUCCESS\tNON_EXISTENT_FIELD_1_VALUE"));
        String actual = processor.process(record);
        assertEquals(expected, actual);
    }

}