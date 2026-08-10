/*
 * Copyright (c) 2026 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal CMO-Pipelines.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.annotator.util;

import java.util.Arrays;

import org.genome_nexus.client.TranscriptConsequence;
import org.genome_nexus.client.TranscriptConsequenceSummary;
import org.genome_nexus.client.VariantAnnotation;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationUtilTest {

    private final AnnotationUtil annotationUtil = new AnnotationUtil();

    @Test
    public void resolveHgvsOffsetReturnsOffsetForCanonicalTranscript() {
        VariantAnnotation gnResponse = new VariantAnnotation();
        gnResponse.setTranscriptConsequences(Arrays.asList(
            new TranscriptConsequence().transcriptId("ENST00000000001").hgvsOffset(1),
            new TranscriptConsequence().transcriptId("ENST00000000002").hgvsOffset(2)
        ));
        TranscriptConsequenceSummary canonicalTranscript = new TranscriptConsequenceSummary()
            .transcriptId("ENST00000000002");

        Assert.assertEquals("2", annotationUtil.resolveHgvsOffset(gnResponse, canonicalTranscript));
    }

    @Test
    public void resolveHgvsOffsetReturnsEmptyWhenCanonicalTranscriptHasNoOffset() {
        VariantAnnotation gnResponse = new VariantAnnotation();
        gnResponse.setTranscriptConsequences(Arrays.asList(
            new TranscriptConsequence().transcriptId("ENST00000000001")
        ));
        TranscriptConsequenceSummary canonicalTranscript = new TranscriptConsequenceSummary()
            .transcriptId("ENST00000000001");

        Assert.assertEquals("", annotationUtil.resolveHgvsOffset(gnResponse, canonicalTranscript));
    }

    @Test
    public void resolveHgvsOffsetReturnsEmptyWhenNoMatchingTranscriptFound() {
        VariantAnnotation gnResponse = new VariantAnnotation();
        gnResponse.setTranscriptConsequences(Arrays.asList(
            new TranscriptConsequence().transcriptId("ENST00000000001").hgvsOffset(5)
        ));
        TranscriptConsequenceSummary canonicalTranscript = new TranscriptConsequenceSummary()
            .transcriptId("ENST00000099999");

        Assert.assertEquals("", annotationUtil.resolveHgvsOffset(gnResponse, canonicalTranscript));
    }

    @Test
    public void resolveHgvsOffsetReturnsEmptyWhenTranscriptConsequencesMissing() {
        VariantAnnotation gnResponse = new VariantAnnotation();
        TranscriptConsequenceSummary canonicalTranscript = new TranscriptConsequenceSummary()
            .transcriptId("ENST00000000001");

        Assert.assertEquals("", annotationUtil.resolveHgvsOffset(gnResponse, canonicalTranscript));
    }

    @Test
    public void resolveHgvsOffsetReturnsEmptyWhenCanonicalTranscriptIsNull() {
        VariantAnnotation gnResponse = new VariantAnnotation();
        gnResponse.setTranscriptConsequences(Arrays.asList(
            new TranscriptConsequence().transcriptId("ENST00000000001").hgvsOffset(5)
        ));

        Assert.assertEquals("", annotationUtil.resolveHgvsOffset(gnResponse, null));
    }
}
