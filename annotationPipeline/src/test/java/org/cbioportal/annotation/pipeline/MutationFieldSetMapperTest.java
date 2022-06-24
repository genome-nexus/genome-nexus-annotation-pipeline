package org.cbioportal.annotation.pipeline;

import org.apache.log4j.Logger;
import org.cbioportal.models.MutationRecord;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Mete Ozguz
 */
class MutationFieldSetMapperTest {

    @Test
    void mapFieldSet_testExpectedBehaviors() throws BindException {
        // it uses minimal_example.in.txt headers
        try (MockedStatic<Logger> loggerFactory = mockStatic(Logger.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> Logger.getLogger(MutationFieldSetMapper.class)).thenReturn(logger);
            String[] getNames = {"Chromosome", "Start_Position", "End_Position", "Reference_Allele", "Tumor_Seq_Allele1",
                    "NON_EXISTING_FIELD"};
            FieldSet fieldSet = Mockito.mock(FieldSet.class);
            when(fieldSet.getNames()).thenReturn(getNames);
            when(fieldSet.readRawString(anyString())).thenReturn("3", "178916927", "178916939", "TAGGCAACCGTGA",
                    "G", "NON_EXISTING_FIELD_VALUE");
            MutationFieldSetMapper mutationFieldSetMapper = new MutationFieldSetMapper();
            MutationRecord mutationRecord = mutationFieldSetMapper.mapFieldSet(fieldSet);
            assertEquals("3", mutationRecord.getCHROMOSOME());
            assertEquals("178916927", mutationRecord.getSTART_POSITION());
            assertEquals("178916939", mutationRecord.getEND_POSITION());
            assertEquals("TAGGCAACCGTGA", mutationRecord.getREFERENCE_ALLELE());
            assertEquals("G", mutationRecord.getTUMOR_SEQ_ALLELE1());
            Map<String, String> additionalProperties = mutationRecord.getAdditionalProperties();
            assertEquals("NON_EXISTING_FIELD_VALUE", additionalProperties.get("NON_EXISTING_FIELD"));
        }
    }
}