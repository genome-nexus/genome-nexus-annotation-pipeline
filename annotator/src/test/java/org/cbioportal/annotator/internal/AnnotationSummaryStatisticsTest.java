package org.cbioportal.annotator.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationSummaryStatisticsTest {

    @Test
    void averageResponseTime0() {
        AnnotationSummaryStatistics annotationSummaryStatistics = new AnnotationSummaryStatistics(null);
        assertEquals("0.000", annotationSummaryStatistics.averageResponseTime());
    }

    @Test
    void averageResponseTime1() {
        AnnotationSummaryStatistics annotationSummaryStatistics = new AnnotationSummaryStatistics(null);
        annotationSummaryStatistics.addDuration(1L);
        assertEquals("1.000", annotationSummaryStatistics.averageResponseTime());
    }

    @Test
    void averageResponseTime_2andAHalfMan() {
        AnnotationSummaryStatistics annotationSummaryStatistics = new AnnotationSummaryStatistics(null);
        annotationSummaryStatistics.addDuration(1L);
        annotationSummaryStatistics.addDuration(4L);
        assertEquals("2.500", annotationSummaryStatistics.averageResponseTime());
    }
}