/*
 * Copyright (c) 2019 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.models;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.cbioportal.models.AnnotatedRecord;
import org.cbioportal.models.MutationRecord;
import org.junit.*;
import org.junit.runner.RunWith;
import org.cbioportal.annotator.MockGenomeNexusImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.util.ReflectionTestUtils;

@ContextConfiguration(classes=MockGenomeNexusImpl.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MutationRecordTest {

    private static final Class[] stringClassArray = { String.class };
    private List<String> noAdditionalProperties = new ArrayList<String>();
    private List<String> noAdditionalPropertiesExpected = new ArrayList<String>();
    private List<String> noAdditionalValuesExpected = new ArrayList<String>();
    // distinct sorted properties come out in the same order that they appear
    private List<String> distinctAdditionalProperties = Arrays.asList("distinctTestProperty2", "distinctTestProperty1"); // input order does not matter because these will construct a testing map
    private List<String> distinctAdditionalPropertiesExpected = Arrays.asList("distinctTestProperty1", "distinctTestProperty2"); // output order does matter .. the list should come sorted from the model functions
    private List<String> distinctAdditionalValuesExpected = Arrays.asList("distinctTestProperty1.value", "distinctTestProperty2.value");
    private List<String> redundantAdditionalProperties = Arrays.asList("Chromosome", "distinctTestProperty1", "distinctTestProperty2", "End_Position");
    private List<String> redundantAdditionalPropertiesExpected = Arrays.asList("distinctTestProperty1", "distinctTestProperty2");
    private List<String> redundantAdditionalValuesExpected = Arrays.asList("distinctTestProperty1.value", "distinctTestProperty2.value");
    private MutationRecord plainMutationRecord = createMutationRecord(noAdditionalProperties);
    private MutationRecord expandedMutationRecord = createMutationRecord(distinctAdditionalProperties);
    private MutationRecord expandedMutationRecordWithRedundancy = createMutationRecord(redundantAdditionalProperties);

    // test that a mutation record with no additional properties has exactly the predefined headers
    @Test
    public void testPlainMutationHeaders() {
        testMutationHeaders(plainMutationRecord, noAdditionalPropertiesExpected);
    }

    // test that a mutation record with a few additional distinct properties come out appended in sorted order
    @Test
    public void testDistinctAdditionalMutationHeaders() {
        testMutationHeaders(expandedMutationRecord, distinctAdditionalPropertiesExpected);
    }

    // test that a mutation record with a few additional distinct properties and some redundant ones come out appended in sorted order and no duplicates
    @Test
    public void testDistinctAdditionalMutationHeadersWithRedundancy() {
        testMutationHeaders(expandedMutationRecordWithRedundancy, redundantAdditionalPropertiesExpected);
    }

    // test that a mutation record with no additional properties has none
    @Test
    public void testPlainAdditionalHeaders() {
        testSortedAdditionalHeaders(plainMutationRecord, noAdditionalPropertiesExpected);
    }

    // test that a mutation record with a few additional distinct properties come out in sorted order
    @Test
    public void testDistinctAdditionalAdditionalHeaders() {
        testSortedAdditionalHeaders(expandedMutationRecord, distinctAdditionalPropertiesExpected);
    }

    // test that a mutation record with a few additional distinct properties and some redundant ones come out in sorted order and without any that match predefined headers
    @Test
    public void testDistinctAdditionalAdditionalHeadersWithRedundancy() {
        testSortedAdditionalHeaders(expandedMutationRecordWithRedundancy, redundantAdditionalPropertiesExpected);
    }

    // test that a mutation record with no additional properties has none
    @Test
    public void testPlainAdditionalValues() {
        testSortedAdditionalValues(plainMutationRecord, noAdditionalValuesExpected);
    }

    // test that a mutation record with a few additional distinct properties come out in sorted order
    @Test
    public void testDistinctAdditionalAdditionalValues() {
        testSortedAdditionalValues(expandedMutationRecord, distinctAdditionalValuesExpected);
    }

    // test that a mutation record with a few additional distinct properties and some redundant ones come out in sorted order and without any that match predefined headers
    @Test
    public void testDistinctAdditionalAdditionalValuesWithRedundancy() {
        testSortedAdditionalValues(expandedMutationRecordWithRedundancy, redundantAdditionalValuesExpected);
    }

    private void testMutationHeaders(MutationRecord testMutationRecord, List<String> expectedAdditionalHeaders) {
        List<String> expectedMutationHeaders = findExpectedHeaders(expectedAdditionalHeaders);
        List<String> actualMutationHeaders = testMutationRecord.getHeaderWithAdditionalFields();
        Assert.assertEquals("size of expected headers does not match size of actual headers", expectedMutationHeaders.size(), actualMutationHeaders.size());
        for (int pos = 0; pos < expectedMutationHeaders.size(); pos++) {
            String expectedHeader = expectedMutationHeaders.get(pos);
            String actualHeader = actualMutationHeaders.get(pos);
            Assert.assertEquals("expected header does not match actual header", expectedHeader, actualHeader);
        }
    }
    
    private void testSortedAdditionalHeaders(MutationRecord testMutationRecord, List<String> expectedAdditionalHeaders) {
        List<String> actualAdditionalHeaders = testMutationRecord.getSortedAdditionalPropertiesKeys();
        Assert.assertEquals("size of expected additional headers does not match size of actual additional headers", expectedAdditionalHeaders.size(), actualAdditionalHeaders.size());
        for (int pos = 0; pos < expectedAdditionalHeaders.size(); pos++) {
            String expectedHeader = expectedAdditionalHeaders.get(pos);
            String actualHeader = actualAdditionalHeaders.get(pos);
            Assert.assertEquals("expected header does not match actual header", expectedHeader, actualHeader);
        }
    }

    private void testSortedAdditionalValues(MutationRecord testMutationRecord, List<String> expectedAdditionalValues) {
        List<String> actualAdditionalValues = testMutationRecord.getSortedAdditionalPropertiesValues();
        Assert.assertEquals("size of expected additional values does not match size of actual additional values", expectedAdditionalValues.size(), actualAdditionalValues.size());
        for (int pos = 0; pos < expectedAdditionalValues.size(); pos++) {
            String expectedValue = expectedAdditionalValues.get(pos);
            String actualValue = actualAdditionalValues.get(pos);
            Assert.assertEquals("expected value does not match actual value", expectedValue, actualValue);
        }
    }
    
    private List<String> findExpectedHeaders(List<String> additionalExpectedHeaders) {
        List<String> expectedHeaders = new ArrayList<String>(plainMutationRecord.getHeader());
        expectedHeaders.addAll(additionalExpectedHeaders);
        return expectedHeaders;
    }

    //TODO: try using a "reverse my keyset iterartor order " Map subclass too
    private MutationRecord createMutationRecord(List<String> additionalProperties) {
        MutationRecord mutationRecord = new MutationRecord();
        for (String fieldName : mutationRecord.getHeader()) {
            String valueForField = fieldName + ".value";
            setMutationRecordField(mutationRecord, fieldName, valueForField);
        }
        Map<String, String> additionalPropertiesMap = new HashMap<String, String>();
        if (additionalProperties != null) {
            for (String additionalFieldName : additionalProperties) {
                String valueForField = additionalFieldName + ".value";
                additionalPropertiesMap.put(additionalFieldName, valueForField);
            }
                    
        }
        mutationRecord.setAdditionalProperties(additionalPropertiesMap);
        return mutationRecord;
    }

    private String getMutationRecordField(MutationRecord mutationRecord, String fieldName) {
        try {
            Method get = mutationRecord.getClass().getMethod("get" + fieldName.toUpperCase(), stringClassArray);
            return get.invoke(mutationRecord).toString();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMutationRecordField(MutationRecord mutationRecord, String fieldName, String value) {
        try {
            Method set = mutationRecord.getClass().getMethod("set" + fieldName.toUpperCase(), stringClassArray);
            set.invoke(mutationRecord, value);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
