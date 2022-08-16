package org.cbioportal.annotation;


import org.cbioportal.annotation.cli.AnnotationFailedException;
import org.cbioportal.annotation.cli.MergeFailedException;
import org.cbioportal.annotation.cli.NoSubcommandFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class AnnotationPipelineTest {

    /**
     * test with 0 arguments, should produce NoSubcommandFoundException
     */
    @Test
    void cli_test_0() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {};
            AnnotationPipeline.main(args);
        } catch (NoSubcommandFoundException e) {
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a NoSubcommandFoundException");
    }

    /**
     * merge with 0 arguments, should produce MergeFailedException
     */
    @Test
    void merge_test_1() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"merge"};
            AnnotationPipeline.main(args);
        } catch (MergeFailedException e) {
            assertEquals("required option: output-maf", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a MergeFailedException");
    }

    /**
     * merge with input-mafs-list and input-mafs-directory, should produce MergeFailedException
     */
    @Test
    void merge_test_2() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"merge", "--input-mafs-list", "a,b,c", "--input-mafs-directory", "d", "--output-maf", "e"};
            AnnotationPipeline.main(args);
        } catch (MergeFailedException e) {
            assertEquals("Please choose only one of the following options when running script: --input-mafs-list | --input-mafs-directory", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a MergeFailedException");
    }

    /**
     * merge with an invalid input-mafs-directory, should produce MergeFailedException
     */
    @Test
    void merge_test_3() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"merge", "--input-mafs-directory", "d", "--output-maf", "e"};
            AnnotationPipeline.main(args);
        } catch (MergeFailedException e) {
            assertEquals("Supplied input mafs directory is not a directory or it does not exist!", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a MergeFailedException");
    }

    /**
     * merge with nothing to merge using input-mafs-list with 1 element, should produce MergeFailedException
     */
    @Test
    void merge_test_4() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"merge", "--input-mafs-list", "d", "--output-maf", "e"};
            AnnotationPipeline.main(args);
        } catch (MergeFailedException e) {
            assertEquals("There is nothing to merge! Count of input files: 1", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a MergeFailedException");
    }

    /**
     * merge with nothing to merge using empty directory, should produce MergeFailedException
     */
    @Test
    void merge_test_5() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"merge", "--input-mafs-directory", Files.createTempDirectory("emptyDirectory").toAbsolutePath().toString(), "--output-maf", "e"};
            AnnotationPipeline.main(args);
        } catch (MergeFailedException e) {
            assertEquals("There is nothing to merge! Count of input files: 0", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a MergeFailedException");
    }

    /**
     * merge with nothing to merge using 1 item directory, should produce MergeFailedException
     */
    @Test
    void merge_test_6() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"merge", "--input-mafs-directory", "src/test/resources/directoryWith1Item", "--output-maf", "e"};
            AnnotationPipeline.main(args);
        } catch (MergeFailedException e) {
            assertEquals("There is nothing to merge! Count of input files: 1", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a MergeFailedException");
    }

    /**
     * merge with invalid input using input-mafs-list with 2 element, should produce MergeFailedException
     */
    @Test
    void merge_test_7() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"merge", "--input-mafs-list", "a,b", "--output-maf", "e"};
            AnnotationPipeline.main(args);
        } catch (MergeFailedException e) {
            assertEquals("java.nio.file.NoSuchFileException: a", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a MergeFailedException");
    }

    /**
     * annotate with no input, should produce AnnotationFailedException
     */
    @Test
    void annotate_test_1() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"annotate"};
            AnnotationPipeline.main(args);
        } catch (AnnotationFailedException e) {
            assertEquals("required option: filename", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a AnnotationFailedException");
    }

    /**
     * annotate with no output-filename option, should produce AnnotationFailedException
     */
    @Test
    void annotate_test_2() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"annotate", "--filename", "a"};
            AnnotationPipeline.main(args);
        } catch (AnnotationFailedException e) {
            assertEquals("required option: output-filename", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a AnnotationFailedException");
    }

    /**
     * annotate with invalid output-format option, should produce AnnotationFailedException
     */
    @Test
    void annotate_test_3() {
        try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
            final Logger logger = mock(Logger.class, RETURNS_DEEP_STUBS);
            loggerFactory.when(() -> LoggerFactory.getLogger(AnnotationPipeline.class)).thenReturn(logger);
            String[] args = {"annotate", "--filename", "a", "--output-filename", "b" , "--output-format", "c"};
            AnnotationPipeline.main(args);
        } catch (AnnotationFailedException e) {
            assertEquals("Error while reading output-format file: " + "c", e.getMessage());
            return;
        } catch (Exception ignored) {
        }
        fail("Test didn't produced a AnnotationFailedException");
    }
}