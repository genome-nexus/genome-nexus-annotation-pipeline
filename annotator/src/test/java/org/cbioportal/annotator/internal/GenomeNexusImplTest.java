/*
 * Copyright (c) 2018 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.annotator.internal;

import org.cbioportal.models.AnnotatedRecord;
import org.cbioportal.models.MutationRecord;
import org.cbioportal.annotator.GenomeNexusTestConfiguration;
import org.cbioportal.annotator.MockGenomeNexusImpl;

import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 *
 * @author ochoaa
 */
@ContextConfiguration(classes=MockGenomeNexusImpl.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GenomeNexusImplTest {

    @Autowired
    MockGenomeNexusImpl annotator;

    private AnnotationSummaryStatistics summaryStatistics;
    @Autowired
    private void setSummaryStatistics() {
        this.summaryStatistics = Mockito.mock(AnnotationSummaryStatistics.class);
    }

    @Autowired
    public void initAnnotator() {
        ReflectionTestUtils.setField(annotator, "genomeNexusBaseUrl", GenomeNexusTestConfiguration.GENOME_NEXUS_BASE_URL);
        ReflectionTestUtils.setField(annotator, "isoformQueryParameter", GenomeNexusTestConfiguration.ISOFORM_QUERY_PARAMETER);
        ReflectionTestUtils.setField(annotator, "enrichmentFields", GenomeNexusTestConfiguration.ENRICHMENT_FIELDS);
    }

    private List<AnnotatedRecord> mockAnnotatedRecords;
    @Autowired
    private void setMockAnnotatedRecords() {
        this.mockAnnotatedRecords = makeMockAnnotatedRecords();
    }

    private List<AnnotatedRecord> mockAnnotatedRecordsWithPost;
    @Autowired
    private void setMockAnnotatedRecordsWithPost() {
        this.mockAnnotatedRecordsWithPost = makeMockAnnotatedRecordsWithPost();
    }

    /**
     * Test resolved variant classifications (mutation types) returned by Genome Nexus.
     */
    @Test
    public void testResolveVariantClassification() {
        StringBuilder errorMessage = new StringBuilder("\nFailures:\n");
        int failCount = 0;

        Map<String, String> expectedMutationTypes = makeMockExpectedMutationType();
        for (AnnotatedRecord record : mockAnnotatedRecords) {
            if (!record.getVARIANT_CLASSIFICATION().equals(expectedMutationTypes.get(record.getTUMOR_SAMPLE_BARCODE()))) {
                errorMessage.append("testResolveVariantClassification(), annotated record variant classification '")
                        .append(record.getVARIANT_CLASSIFICATION())
                        .append("' does not match expected type '")
                        .append(expectedMutationTypes.get(record.getTUMOR_SAMPLE_BARCODE()))
                        .append(" for record '")
                        .append(record.getTUMOR_SAMPLE_BARCODE())
                        .append("'\n");
                failCount += 1;
            }
        }

        if (failCount > 0) {
            Assert.fail(errorMessage.toString());
        }
    }

    /**
     * Test resolved protein changes returned by Genome Nexus.
     */
    @Test
    public void testResolveProteinChange() {
        StringBuilder errorMessage = new StringBuilder("\nFailures:\n");
        int failCount = 0;

        Map<String, String> expectedProteinChanges = makeMockExpectedProteinChange();
        for (AnnotatedRecord record : mockAnnotatedRecords) {
            if (!record.getHGVSP_SHORT().equals(expectedProteinChanges.get(record.getTUMOR_SAMPLE_BARCODE()))) {
                errorMessage.append("testResolveProteinChange(), annotated record protein change '")
                        .append(record.getHGVSP_SHORT())
                        .append("' does not match expected protein change '")
                        .append(expectedProteinChanges.get(record.getTUMOR_SAMPLE_BARCODE()))
                        .append(" for record '")
                        .append(record.getTUMOR_SAMPLE_BARCODE())
                        .append("'\n");
                failCount += 1;
            }
        }

        if (failCount > 0) {
            Assert.fail(errorMessage.toString());
        }
    }

    @Test
    public void testExtractGenomicLocation() {
        StringBuilder errorMessage = new StringBuilder("\nFailures:\n");
        int failCount = 0;

        Map<String, String> expectedGenomicLocations = makeMockExpectedGenomicLocations();
        for (AnnotatedRecord record : mockAnnotatedRecords) {
            String genomicLocation = annotator.extractGenomicLocationAsString(record);
            if (!genomicLocation.equals(expectedGenomicLocations.get(record.getTUMOR_SAMPLE_BARCODE()))) {
                errorMessage.append("testConvertToHgvs(), record genomicLocation '")
                        .append(genomicLocation)
                        .append("' does not match expected genomicLocation '")
                        .append(expectedGenomicLocations.get(record.getTUMOR_SAMPLE_BARCODE()))
                        .append(" for record '")
                        .append(record.getTUMOR_SAMPLE_BARCODE())
                        .append("'\n");
                failCount += 1;
            }
        }

        if (failCount > 0) {
            Assert.fail(errorMessage.toString());
        }
    }

    @Test
    public void testPostVsGetAnnotationResults() throws Exception {
        StringBuilder errorMessage = new StringBuilder("\nFailures:\n");
        int failCount = 0;

        Map<String, String> expectedProteinChanges = makeMockExpectedProteinChange();
        for (AnnotatedRecord record : mockAnnotatedRecordsWithPost) {
            if (!record.getHGVSP_SHORT().equals(expectedProteinChanges.get(record.getTUMOR_SAMPLE_BARCODE()))) {
                errorMessage.append("testResolveProteinChange(), annotated record protein change '")
                        .append(record.getHGVSP_SHORT())
                        .append("' does not match expected protein change '")
                        .append(expectedProteinChanges.get(record.getTUMOR_SAMPLE_BARCODE()))
                        .append(" for record '")
                        .append(record.getTUMOR_SAMPLE_BARCODE())
                        .append("'\n");
                failCount += 1;
            }
        }

        if (failCount > 0) {
            Assert.fail(errorMessage.toString());
        }
    }

    private List<AnnotatedRecord> makeMockAnnotatedRecordsWithPost() {
        List<MutationRecord> mockMutationRecords = makeMockMutationRecords();
        List<AnnotatedRecord> mockAnnotatedRecordsWithPost = new ArrayList();
        for (MutationRecord record : mockMutationRecords) {
            mockAnnotatedRecordsWithPost.add(annotator.makeMockPOSTAnnotatedRecord(record));
        }
        return mockAnnotatedRecordsWithPost;
    }

    private List<AnnotatedRecord> makeMockAnnotatedRecords() {
        List<MutationRecord> mockMutationRecords = makeMockMutationRecords();

        List<AnnotatedRecord> mockAnnotatedRecords = new ArrayList();
        for (MutationRecord record : mockMutationRecords) {
            mockAnnotatedRecords.add(annotator.makeMockAnnotatedRecord(record));
        }
        return mockAnnotatedRecords;
    }

    private List<MutationRecord> makeMockMutationRecords() {
        List<MutationRecord> mockMutationRecords = new ArrayList();

        MutationRecord record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-1");
        record.setCHROMOSOME("4");
        record.setSTART_POSITION("9784947");
        record.setEND_POSITION("9784948");
        record.setREFERENCE_ALLELE("-");
        record.setTUMOR_SEQ_ALLELE2("AGA");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-2");
        record.setCHROMOSOME("3");
        record.setSTART_POSITION("14940279");
        record.setEND_POSITION("14940280");
        record.setREFERENCE_ALLELE("-");
        record.setTUMOR_SEQ_ALLELE2("CAT");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-3");
        record.setCHROMOSOME("16");
        record.setSTART_POSITION("9057113");
        record.setEND_POSITION("9057114");
        record.setREFERENCE_ALLELE("-");
        record.setTUMOR_SEQ_ALLELE2("CTG");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-4");
        record.setCHROMOSOME("13");
        record.setSTART_POSITION("28608258");
        record.setEND_POSITION("28608275");
        record.setREFERENCE_ALLELE("CATATTCATATTCTCTGA");
        record.setTUMOR_SEQ_ALLELE2("GGGGTGGGGGGG");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-5");
        record.setCHROMOSOME("22");
        record.setSTART_POSITION("36689419");
        record.setEND_POSITION("36689421");
        record.setREFERENCE_ALLELE("CCT");
        record.setTUMOR_SEQ_ALLELE2("-");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-6");
        record.setCHROMOSOME("3");
        record.setSTART_POSITION("14106026");
        record.setEND_POSITION("14106037");
        record.setREFERENCE_ALLELE("CCAGCAGTAGCT");
        record.setTUMOR_SEQ_ALLELE2("-");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-7");
        record.setCHROMOSOME("22");
        record.setSTART_POSITION("29091840");
        record.setEND_POSITION("29091841");
        record.setREFERENCE_ALLELE("TG");
        record.setTUMOR_SEQ_ALLELE2("CA");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-8");
        record.setCHROMOSOME("19");
        record.setSTART_POSITION("46141892");
        record.setEND_POSITION("46141893");
        record.setREFERENCE_ALLELE("TC");
        record.setTUMOR_SEQ_ALLELE2("AA");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-9");
        record.setCHROMOSOME("11");
        record.setSTART_POSITION("62393546");
        record.setEND_POSITION("62393547");
        record.setREFERENCE_ALLELE("GG");
        record.setTUMOR_SEQ_ALLELE2("AA");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-10");
        record.setCHROMOSOME("1");
        record.setSTART_POSITION("65325832");
        record.setEND_POSITION("65325833");
        record.setREFERENCE_ALLELE("-");
        record.setTUMOR_SEQ_ALLELE2("G");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-11");
        record.setCHROMOSOME("4");
        record.setSTART_POSITION("77675978");
        record.setEND_POSITION("77675979");
        record.setREFERENCE_ALLELE("-");
        record.setTUMOR_SEQ_ALLELE2("C");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-12");
        record.setCHROMOSOME("8");
        record.setSTART_POSITION("37696499");
        record.setEND_POSITION("37696500");
        record.setREFERENCE_ALLELE("-");
        record.setTUMOR_SEQ_ALLELE2("G");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-13");
        record.setCHROMOSOME("10");
        record.setSTART_POSITION("101953779");
        record.setEND_POSITION("101953779");
        record.setREFERENCE_ALLELE("T");
        record.setTUMOR_SEQ_ALLELE2("-");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-14");
        record.setCHROMOSOME("6");
        record.setSTART_POSITION("137519505");
        record.setEND_POSITION("137519506");
        record.setREFERENCE_ALLELE("CT");
        record.setTUMOR_SEQ_ALLELE2("-");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-15");
        record.setCHROMOSOME("3");
        record.setSTART_POSITION("114058003");
        record.setEND_POSITION("114058003");
        record.setREFERENCE_ALLELE("G");
        record.setTUMOR_SEQ_ALLELE2("-");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-16");
        record.setCHROMOSOME("9");
        record.setSTART_POSITION("135797242");
        record.setEND_POSITION("135797242");
        record.setREFERENCE_ALLELE("C");
        record.setTUMOR_SEQ_ALLELE2("AT");
        mockMutationRecords.add(record);

        record = new MutationRecord();
        record.setTUMOR_SAMPLE_BARCODE("SAMPLE-VARIANT-17");
        record.setCHROMOSOME("6");
        record.setSTART_POSITION("137519505");
        record.setEND_POSITION("137519506");
        record.setREFERENCE_ALLELE("CT");
        record.setTUMOR_SEQ_ALLELE2("A");
        mockMutationRecords.add(record);

        return mockMutationRecords;
    }

    private Map<String, String> makeMockExpectedMutationType() {
        Map<String, String> map = new HashMap<>();
        map.put("SAMPLE-VARIANT-1", "In_Frame_Ins");
        map.put("SAMPLE-VARIANT-2", "In_Frame_Ins");
        map.put("SAMPLE-VARIANT-3", "In_Frame_Ins");
        map.put("SAMPLE-VARIANT-4", "In_Frame_Del");
        map.put("SAMPLE-VARIANT-5", "In_Frame_Del");
        map.put("SAMPLE-VARIANT-6", "In_Frame_Del");
        map.put("SAMPLE-VARIANT-7", "Missense_Mutation");
        map.put("SAMPLE-VARIANT-8", "Splice_Site");
        map.put("SAMPLE-VARIANT-9", "Nonsense_Mutation");
        map.put("SAMPLE-VARIANT-10", "Frame_Shift_Ins");
        map.put("SAMPLE-VARIANT-11", "Frame_Shift_Ins");
        map.put("SAMPLE-VARIANT-12", "Frame_Shift_Ins");
        map.put("SAMPLE-VARIANT-13", "Frame_Shift_Del");
        map.put("SAMPLE-VARIANT-14", "Frame_Shift_Del");
        map.put("SAMPLE-VARIANT-15", "Frame_Shift_Del");
        map.put("SAMPLE-VARIANT-16", "Frame_Shift_Ins");
        map.put("SAMPLE-VARIANT-17", "Frame_Shift_Del");
        return map;
    }

    private Map<String, String> makeMockExpectedProteinChange() {
        Map<String, String> map = new HashMap<>();
        map.put("SAMPLE-VARIANT-1", "p.G432delinsES");
        map.put("SAMPLE-VARIANT-2", "p.H1034delinsPY");
        map.put("SAMPLE-VARIANT-3", "p.Q10delinsHR");
        map.put("SAMPLE-VARIANT-4", "p.F594_D600delinsSPPPH");
        map.put("SAMPLE-VARIANT-5", "p.E1350del");
        map.put("SAMPLE-VARIANT-6", "p.S124_S127del");
        map.put("SAMPLE-VARIANT-7", "p.K416E");
        map.put("SAMPLE-VARIANT-8", "p.X218_splice");
        map.put("SAMPLE-VARIANT-9", "p.Q928*");
        map.put("SAMPLE-VARIANT-10", "p.L431Vfs*22");
        map.put("SAMPLE-VARIANT-11", "p.D1450*");
        map.put("SAMPLE-VARIANT-12", "p.A765Rfs*98");
        map.put("SAMPLE-VARIANT-13", "p.R646Gfs*22");
        map.put("SAMPLE-VARIANT-14", "p.S378Ffs*6");
        map.put("SAMPLE-VARIANT-15", "p.P692Lfs*43");
        map.put("SAMPLE-VARIANT-16", "p.M209Ifs*2");
        map.put("SAMPLE-VARIANT-17", "p.S378Ffs*5");
        return map;
    }

    private Map<String, String> makeMockExpectedGenomicLocations() {
        Map<String, String> map = new HashMap<>();
        map.put("SAMPLE-VARIANT-1", "4,9784947,9784948,-,AGA");
        map.put("SAMPLE-VARIANT-2", "3,14940279,14940280,-,CAT");
        map.put("SAMPLE-VARIANT-3", "16,9057113,9057114,-,CTG");
        map.put("SAMPLE-VARIANT-4", "13,28608258,28608275,CATATTCATATTCTCTGA,GGGGTGGGGGGG");
        map.put("SAMPLE-VARIANT-5", "22,36689419,36689421,CCT,-");
        map.put("SAMPLE-VARIANT-6", "3,14106026,14106037,CCAGCAGTAGCT,-");
        map.put("SAMPLE-VARIANT-7", "22,29091840,29091841,TG,CA");
        map.put("SAMPLE-VARIANT-8", "19,46141892,46141893,TC,AA");
        map.put("SAMPLE-VARIANT-9", "11,62393546,62393547,GG,AA");
        map.put("SAMPLE-VARIANT-10", "1,65325832,65325833,-,G");
        map.put("SAMPLE-VARIANT-11", "4,77675978,77675979,-,C");
        map.put("SAMPLE-VARIANT-12", "8,37696499,37696500,-,G");
        map.put("SAMPLE-VARIANT-13", "10,101953779,101953779,T,-");
        map.put("SAMPLE-VARIANT-14", "6,137519505,137519506,CT,-");
        map.put("SAMPLE-VARIANT-15", "3,114058003,114058003,G,-");
        map.put("SAMPLE-VARIANT-16", "9,135797242,135797242,C,AT");
        map.put("SAMPLE-VARIANT-17", "6,137519505,137519506,CT,A");
        return map;
    }
}
