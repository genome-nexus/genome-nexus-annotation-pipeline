package org.cbioportal.annotation;

import org.cbioportal.annotation.pipeline.BatchConfiguration;
import org.cbioportal.annotator.internal.GenomeNexusImpl;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.AssertFile;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;

/**
 * Unit test version of .circleci/config.yml
 * ReflectionTestUtils.setField used in following tests change other tests' outcomes
 * Tests: check_if_nucleotide_context_provides_Ref_Tri_and_Var_Tri_columns, and
 *        check_if_my_variant_info_provides_gnomad_annotations
 * So all the other tests, use this method to set its default value as a dirty fix
 * I couldn't fix it with DirtiesContext or with other approaches, maybe you can fix it
 *
 * @author Mete Ozguz
 */
@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {BatchConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Sparing Batch Integration Tests")
public class SpringBatchIntegrationTest {
    private static final String IN = "src/test/resources/input/";
    private static final String EXPECTED = "src/test/resources/expected/";
    private static final String ACTUAL = "src/test/resources/actual/";
    @Autowired
    GenomeNexusImpl annotator;
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @AfterEach
    public void afterEach() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    @DisplayName("Check if maf file still the same when annotating with uniprot transcripts")
    public void check_if_maf_file_still_the_same_when_annotating_with_uniprot_transcripts() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "data_mutations_extended_100.txt";
        String expectedFile = EXPECTED + "data_mutations_extended_100.uniprot.txt";
        String actualFile = ACTUAL + "data_mutations_extended_100.uniprot.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(false))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Check if maf file still the same when annotating with mskcc transcripts")
    public void check_if_maf_file_still_the_same_when_annotating_with_mskcc_transcripts() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "data_mutations_extended_100.txt";
        String expectedFile = EXPECTED + "data_mutations_extended_100.mskcc.txt";
        String actualFile = ACTUAL + "data_mutations_extended_100.mskcc.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(false))
                .addString("isoformOverride", "mskcc")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Check if minimal example maf file still the same when annotating with uniprot transcripts")
    public void check_if_minimal_example_maf_file_still_the_same_when_annotating_with_uniprot_transcripts() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "minimal_example.txt";
        String expectedFile = EXPECTED + "minimal_example.uniprot.txt";
        String actualFile = ACTUAL + "minimal_example.uniprot.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Check if corner cases example maf file still the same when annotating with uniprot transcripts using two_tumor_seq_allele")
    public void check_if_corner_cases_example_maf_file_still_the_same_when_annotating_with_uniprot_transcripts_two_tumor_seq_allele() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "corner_cases.two_tumor_seq_allele.txt";
        String expectedFile = EXPECTED + "corner_cases.two_tumor_seq_allele.uniprot.txt";
        String actualFile = ACTUAL + "corner_cases.two_tumor_seq_allele.uniprot.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Check if corner cases example maf file still the same when annotating with mskcc transcripts using two_tumor_seq_allele")
    public void check_if_corner_cases_example_maf_file_still_the_same_when_annotating_with_mskcc_transcripts_two_tumor_seq_allele() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "corner_cases.two_tumor_seq_allele.txt";
        String expectedFile = EXPECTED + "corner_cases.two_tumor_seq_allele.mskcc.txt";
        String actualFile = ACTUAL + "corner_cases.two_tumor_seq_allele.mskcc.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "mskcc")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Check if corner cases example maf file still the same when annotating with uniprot transcripts")
    public void check_if_corner_cases_example_maf_file_still_the_same_when_annotating_with_uniprot_transcripts() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "corner_cases.txt";
        String expectedFile = EXPECTED + "corner_cases.uniprot.txt";
        String actualFile = ACTUAL + "corner_cases.uniprot.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Check if corner cases example maf file still the same when annotating with mskcc transcripts")
    public void check_if_corner_cases_example_maf_file_still_the_same_when_annotating_with_mskcc_transcripts() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "corner_cases.txt";
        String expectedFile = EXPECTED + "corner_cases.mskcc.txt";
        String actualFile = ACTUAL + "corner_cases.mskcc.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "mskcc")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    /**
     * This test does not directly reflect vcf2maf_tests.sh because the script alters the application output.
     *
     * @throws Exception
     */
    @Test
    @DisplayName("Run vcf2maf test case: mskcc")
    public void run_vcf2maf_test_case_mskcc() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "vcf2maf_tests.maf";
        String expectedFile = EXPECTED + "vcf2maf_tests.mskcc.txt";
        String actualFile = ACTUAL + "vcf2maf_tests.mskcc.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(false))
                .addString("isoformOverride", "mskcc")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    /**
     * This test does not directly reflect vcf2maf_tests.sh because the script alters the application output.
     *
     * @throws Exception
     */
    @Test
    @DisplayName("Run vcf2maf test case: uniprot")
    public void run_vcf2maf_test_case_uniprot() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "vcf2maf_tests.maf";
        String expectedFile = EXPECTED + "vcf2maf_tests.uniprot.txt";
        String actualFile = ACTUAL + "vcf2maf_tests.uniprot.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(false))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Check if my_variant_info provides gnomad annotations")
    public void check_if_my_variant_info_provides_gnomad_annotations() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary,my_variant_info");
        String inputFile = IN + "my_variant_info_corner_cases.txt";
        String expectedFile = EXPECTED + "my_variant_info_corner_cases.uniprot.txt";
        String actualFile = ACTUAL + "my_variant_info_corner_cases.uniprot.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Check if nucleotide_context provides Ref_Tri and Var_Tri columns")
    public void check_if_nucleotide_context_provides_Ref_Tri_and_Var_Tri_columns() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary,nucleotide_context");
        String inputFile = IN + "data_mutations_extended_100.txt";
        String expectedFile = EXPECTED + "data_mutations_extended_100.uniprot.nucleotide_context.txt";
        String actualFile = ACTUAL + "data_mutations_extended_100.uniprot.nucleotide_context.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("replace", String.valueOf(false))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Test output-format with extended")
    public void test_output_format_extended() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "extended.minimal_example.in.txt";
        String expectedFile = EXPECTED + "test_output_format_extended.expected.txt";
        String actualFile = ACTUAL + "test_output_format_extended.actual.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("outputFormat", "extended")
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .addString("postIntervalSize", String.valueOf(-1))
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Test output-format with minimal")
    public void test_output_format_minimal() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "minimal_example.txt";
        String expectedFile = EXPECTED + "test_output_format_minimal.expected.txt";
        String actualFile = ACTUAL + "test_output_format_minimal.actual.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("outputFormat", "minimal")
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .addString("postIntervalSize", String.valueOf(-1))
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    @Test
    @DisplayName("Test output-format with a format file")
    public void test_output_format_with_formatFile() throws Exception {
        ReflectionTestUtils.setField(annotator, "enrichmentFields", "annotation_summary");
        String inputFile = IN + "data_mutations_extended_100.txt";
        String expectedFile = EXPECTED + "test_output_format_with_formatFile.expected.txt";
        String actualFile = ACTUAL + "test_output_format_with_formatFile.actual.txt";
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filename", inputFile)
                .addString("outputFilename", actualFile)
                .addString("outputFormat", "Hugo_Symbol,Entrez_Gene_Id,Center,NCBI_Build,Chromosome,Annotation_Status")
                .addString("replace", String.valueOf(true))
                .addString("isoformOverride", "uniprot")
                .addString("errorReportLocation", null)
                .addString("postIntervalSize", String.valueOf(-1))
                .toJobParameters();
        testWith(jobParameters, expectedFile, actualFile);
    }

    private void testWith(JobParameters jobParameters, String expectedPath, String actualPath) throws Exception {
        FileSystemResource expectedResult = new FileSystemResource(expectedPath);
        FileSystemResource actualResult = new FileSystemResource(actualPath);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }
}