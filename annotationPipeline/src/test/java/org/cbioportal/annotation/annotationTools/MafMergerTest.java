package org.cbioportal.annotation.annotationTools;

import org.cbioportal.annotation.cli.MergeFailedException;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.AssertFile;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Mete Ozguz
 */
class MafMergerTest {

    private static final String IN = "src/test/resources/input/";

    private static final String EXPECTED = "src/test/resources/expected/";

    @Test
    void mergeInputMafs_2InvalidData_skipInvalidInput_false() throws Exception {
        List<String> inputMafs = new ArrayList<>();
        inputMafs.add(IN + "mafMergerTest_invalidData1.txt");
        inputMafs.add(IN + "mafMergerTest_invalidData2.txt");
        String outputMafFilename = "mafMergerTest_data12.txt";
        Path outPath = MafMerger.mergeInputMafs(inputMafs, outputMafFilename, false);

        FileSystemResource expectedResult = new FileSystemResource(EXPECTED + outputMafFilename);
        FileSystemResource actualResult = new FileSystemResource(outPath.toAbsolutePath());
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

    @Test
    void mergeInputMafs_2InvalidData_skipInvalidInput_true() {
        List<String> inputMafs = new ArrayList<>();
        inputMafs.add(IN + "mafMergerTest_invalidData1.txt");
        inputMafs.add(IN + "mafMergerTest_invalidData2.txt");
        String outputMafFilename = "mafMergerTest_data12_skipInvalidInput.txt";
        try {
            MafMerger.mergeInputMafs(inputMafs, outputMafFilename, true);
        } catch (IOException e) {
        } catch (MergeFailedException e) {
            assertEquals("There is nothing to merge!", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    void mergeInputMafs_1Invalid1ValidData_skipInvalidInput_true() throws Exception {
        List<String> inputMafs = new ArrayList<>();
        inputMafs.add(IN + "mafMergerTest_invalidData1.txt");
        inputMafs.add(IN + "mafMergerTest_minimal_example.in.txt");
        String outputMafFilename = "mergeInputMafs_1InvalidData_skipInvalidInput_true.out.txt";
        try {
            MafMerger.mergeInputMafs(inputMafs, outputMafFilename, true);
        } catch (IOException e) {
        } catch (MergeFailedException e) {
            assertEquals("There is nothing to merge!", e.getMessage());
            return;
        }
        fail();
    }

    @Test
    void mergeInputMafs_1Invalid2ValidData_skipInvalidInput_true() throws Exception {
        List<String> inputMafs = new ArrayList<>();
        inputMafs.add(IN + "mafMergerTest_invalidData1.txt");
        inputMafs.add(IN + "mafMergerTest_minimal_example.in.txt");
        inputMafs.add(IN + "mafMergerTest_minimal_example_altered.in.txt");
        String outputMafFilename = "mergeInputMafs_1Invalid2ValidData_skipInvalidInput_true.out.txt";
        Path outPath = MafMerger.mergeInputMafs(inputMafs, outputMafFilename, true);

        FileSystemResource expectedResult = new FileSystemResource(EXPECTED + "mergeInputMafs_1Invalid2ValidData_skipInvalidInput_true.out.txt");
        FileSystemResource actualResult = new FileSystemResource(outPath.toAbsolutePath());
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

    /**
     * Expected output data generated using annotation-tools/merge_mafs.py
     *
     * @throws Exception
     */
    @Test
    void mergeInputMafs_scriptData() throws Exception {
        List<String> inputMafs = new ArrayList<>();
        inputMafs.add(IN + "mafMergerTest_corner_cases.two_tumor_seq_allele.in.txt");
        inputMafs.add(IN + "mafMergerTest_minimal_example.in.txt");
        String outputMafFilename = "mafMergerTest_out.txt";
        Path outPath = MafMerger.mergeInputMafs(inputMafs, outputMafFilename, false);

        FileSystemResource expectedResult = new FileSystemResource(EXPECTED + outputMafFilename);
        FileSystemResource actualResult = new FileSystemResource(outPath.toAbsolutePath());
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

    @Test
    void mergeHeaders_skipInvalidInput_false() throws IOException {
        List<String> inputMafs = new ArrayList<>();
        inputMafs.add(IN + "mafMergerTest_invalidHeader1.txt");
        inputMafs.add(IN + "mafMergerTest_invalidHeader2.txt");
        Set<String> expectedHeaders = new LinkedHashSet<>();
        expectedHeaders.add("Hugo_Symbol");
        expectedHeaders.add("Entrez_Gene_Id");
        expectedHeaders.add("Center");
        expectedHeaders.add("NCBI_Build");
        expectedHeaders.add("Chromosome");
        expectedHeaders.add("Chromosome1");
        expectedHeaders.add("Start_Position");
        expectedHeaders.add("End_Position");
        expectedHeaders.add("Strand");
        Set<String> actualHeaders = MafMerger.mergeHeaders(inputMafs, false, new ArrayList<>());
        assertEquals(expectedHeaders, actualHeaders);
    }

    @Test
    void mergeHeaders_skipInvalidInput_true() throws IOException {
        List<String> inputMafs = new ArrayList<>();
        inputMafs.add(IN + "mafMergerTest_invalidHeader1.txt");
        inputMafs.add(IN + "mafMergerTest_invalidHeader2.txt");
        List<String> validInputMafs = new ArrayList<>();
        Set<String> expectedHeaders = new LinkedHashSet<>();
        expectedHeaders.add("Hugo_Symbol");
        expectedHeaders.add("Entrez_Gene_Id");
        Set<String> actualHeaders = MafMerger.mergeHeaders(inputMafs, true, validInputMafs);
        assertEquals(expectedHeaders, actualHeaders);
        assertEquals(0, validInputMafs.size());
    }
}