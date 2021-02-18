/*
 * Copyright (c) 2017 * 2019 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.database.annotator;

import com.querydsl.sql.SQLQueryFactory;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.cbioportal.annotator.Annotator;
import org.cbioportal.annotator.GenomeNexusAnnotationFailureException;
import org.cbioportal.database.annotator.model.*;
import org.cbioportal.models.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.*;

/**
 *
 * @author heinsz
 */

public class AnnotateRecordsProcessor implements ItemProcessor<MutationEvent, MutationEvent>{
    @Value("#{jobParameters[isoform]}")
    private String isoform;

    @Autowired
    Annotator annotator;

    @Autowired
    SQLQueryFactory databaseAnnotatorQueryFactory;

    public final static int NA_INT = -1;

    private final Logger LOG = Logger.getLogger(AnnotateRecordsProcessor.class);

    @Override
    public MutationEvent process(MutationEvent i) throws Exception {
        // first make a MutationRecord that we can send to Genome Nexus to annotate
        Map<String, String> mafLine = new HashMap<>();
        String chromosome;
        if (i.getCHR().equals("23")) {
            chromosome = "X";
        }
        else if (i.getCHR().equals("24")) {
            chromosome = "Y";
        }
        else {
            chromosome = i.getCHR();
        }
        mafLine.put("Chromosome", chromosome);
        mafLine.put("Start_Position", String.valueOf(i.getSTART_POSITION()));
        mafLine.put("End_Position", String.valueOf(i.getEND_POSITION()));
        mafLine.put("Reference_Allele", i.getREFERENCE_ALLELE());
        mafLine.put("Tumor_Seq_Allele2", i.getTUMOR_SEQ_ALLELE());
        MutationRecord record = annotator.createRecord(mafLine);
        AnnotatedRecord annotatedRecord;
        try {
            annotatedRecord = annotator.annotateRecord(record, true, isoform, true);
        }
        catch (GenomeNexusAnnotationFailureException ex1) {
            LOG.warn("Failed to annotate record: " + ex1.getMessage());
            return i;
        }
        catch (HttpServerErrorException | ResourceAccessException e) {
           LOG.error("Failed to annotate record - errors accessing Genome Nexus");
           return i;
        }

        // If the mutation is a 5' or 3' flank mutation, or any other UTR, it shouldn't be in the database as these are supposed to be filtered.. Remove it.
        if (annotatedRecord.getVARIANT_CLASSIFICATION().equals("5'Flank") && annotatedRecord.getHUGO_SYMBOL().equals("TERT")) {
            i.setPROTEIN_CHANGE("Promoter");
        }
        else if (annotatedRecord.getVARIANT_CLASSIFICATION().equals("5'Flank") ||
            annotatedRecord.getVARIANT_CLASSIFICATION().equals("3'Flank") ||
            annotatedRecord.getVARIANT_CLASSIFICATION().equals("IGR") ||
            annotatedRecord.getVARIANT_CLASSIFICATION().equals("Intron") ||
            annotatedRecord.getVARIANT_CLASSIFICATION().equals("RNA") ||
            annotatedRecord.getVARIANT_CLASSIFICATION().equals("5'UTR") ||
            annotatedRecord.getVARIANT_CLASSIFICATION().equals("3'UTR")) {
            LOG.info("Mutation event " + String.valueOf(i.getMUTATION_EVENT_ID()) + " classification is in non translated region of genome. Removing from the database.");
            deleteMutation(i);
            return null;
        }

        // lets do some sanity checking to make sure good values came back from genome nexus
        String chr = !(annotatedRecord.getCHROMOSOME() == null || annotatedRecord.getCHROMOSOME().isEmpty()) ? annotatedRecord.getCHROMOSOME() : i.getCHR();
        Integer start = i.getSTART_POSITION();
        Integer end = i.getEND_POSITION();
        try {
            start = Integer.parseInt(annotatedRecord.getSTART_POSITION());
            end = Integer.parseInt(annotatedRecord.getEND_POSITION());
        }
        catch (NumberFormatException e) {
            LOG.warn("Record with non parseable start or end positions encountered.\n\tMUTATION_EVENT_ID: " + i.getMUTATION_EVENT_ID());
        }
        String ref = !(annotatedRecord.getREFERENCE_ALLELE() == null || annotatedRecord.getREFERENCE_ALLELE().isEmpty()) ? annotatedRecord.getREFERENCE_ALLELE() : i.getREFERENCE_ALLELE();
        String alt = !(annotatedRecord.getTUMOR_SEQ_ALLELE2() == null || annotatedRecord.getTUMOR_SEQ_ALLELE2().isEmpty()) ? annotatedRecord.getTUMOR_SEQ_ALLELE2() : i.getTUMOR_SEQ_ALLELE();
        String type = !(annotatedRecord.getVARIANT_CLASSIFICATION() == null || annotatedRecord.getVARIANT_CLASSIFICATION().isEmpty()) ? annotatedRecord.getVARIANT_CLASSIFICATION() : i.getMUTATION_TYPE();
        String codon = !(annotatedRecord.getCODONS() == null || annotatedRecord.getCODONS().isEmpty()) ? annotatedRecord.getCODONS() : i.getONCOTATOR_CODON_CHANGE();
        Integer proteinStart = !(annotatedRecord.getPROTEIN_POSITION() == null || annotatedRecord.getPROTEIN_POSITION().isEmpty()) ? getProteinPosStart(annotatedRecord.getPROTEIN_POSITION(), annotatedRecord.getHGVSP_SHORT()) : i.getONCOTATOR_PROTEIN_POS_START();
        Integer proteinEnd = !(annotatedRecord.getPROTEIN_POSITION() == null || annotatedRecord.getPROTEIN_POSITION().isEmpty()) ? getProteinPosEnd(annotatedRecord.getPROTEIN_POSITION(), annotatedRecord.getHGVSP_SHORT()) : i.getONCOTATOR_PROTEIN_POS_END();
        String change = !(annotatedRecord.getHGVSP_SHORT() == null || annotatedRecord.getHGVSP_SHORT().isEmpty()) ? annotatedRecord.getHGVSP_SHORT() : i.getPROTEIN_CHANGE();
        String pDot = "p.";
        if (change.startsWith(pDot)) {
            change = change.substring(pDot.length());
        }
        return new MutationEvent(i.getMUTATION_EVENT_ID(), chr, start, end, ref, alt, change, type, codon, proteinStart, proteinEnd);
    }

    private static int getProteinPosStart(String proteinPosition, String proteinChange) {
        // parts[0] is the protein start-end positions, parts[1] is the length
        String[] parts = proteinPosition.split("/");

        int position = getPartInt(0, parts[0].split("-"));

        // there is a case where the protein change is "-"
        if (position == NA_INT) {
            // try to extract it from protein change value
            Pattern p = Pattern.compile(".*[A-Z]([0-9]+)[^0-9]+");
            Matcher m = p.matcher(proteinChange);
            if (m.find()) {
                position = Integer.parseInt(m.group(1));
            }
        }
        return position;
    }

    private static int getProteinPosEnd(String proteinPosition, String proteinChange) {
        // parts[0] is the protein start-end positions, parts[1] is the length
        String[] parts = proteinPosition.split("/");
        int end = getPartInt(1, parts[0].split("-"));
        // if no end position is provided,
        // then use start position as end position
        if (end == -1) {
            end = getProteinPosStart(proteinPosition, proteinChange);
        }
        return end;
    }

    private static Integer getPartInt(int index, String[] parts) {
        try {
            String part = parts[index];
            return (int)(Float.parseFloat(part));
        } catch (ArrayIndexOutOfBoundsException e) {
            return NA_INT;
        } catch (NumberFormatException e) {
            return NA_INT;
        }
    }

    private void deleteMutation(MutationEvent event) throws Exception {
        // Need to delete the mutation event and all mutations associated with it.
        LOG.info("Deleting mutations associated with mutation event " + event.getMUTATION_EVENT_ID());
        Connection con = databaseAnnotatorQueryFactory.getConnection();
        PreparedStatement pstmt_delete_mutations = con.prepareStatement("delete from mutation where mutation_event_id = ?");
        pstmt_delete_mutations.setInt(1, event.getMUTATION_EVENT_ID());
        pstmt_delete_mutations.execute();

        LOG.info("Deleting mutation event " + event.getMUTATION_EVENT_ID());
        PreparedStatement pstmt_delete_mutation_event = con.prepareStatement("delete from mutation_event where mutation_event_id = ?");
        pstmt_delete_mutation_event.setInt(1, event.getMUTATION_EVENT_ID());
        pstmt_delete_mutation_event.execute();
        con.close();

    }
}
