/*
 * Copyright (c) 2017 Memorial Sloan-Kettering Cancer Center.
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

import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import java.sql.*;
import java.util.List;
import org.apache.log4j.Logger;
import org.cbioportal.database.annotator.model.*;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author heinsz
 */

public class AnnotateRecordsWriter  implements ItemStreamWriter<MutationEvent>{
    @Autowired
    SQLQueryFactory databaseAnnotatorQueryFactory;

    private Connection con;
    private final Logger log = Logger.getLogger(AnnotateRecordsWriter.class);

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        con = databaseAnnotatorQueryFactory.getConnection();
    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {
        try {
            con.close();
        }
        catch (SQLException e) {
            log.error("Failed to close connection!!");
        }
    }

    @Override
    public void write(List<? extends MutationEvent> list) throws Exception {
        for (MutationEvent event : list) {
            updateMutationEventInDb(event);
        }
    }

    private void updateMutationEventInDb(MutationEvent annotatedEvent) throws Exception {
        if (!annotatedEvent.getPROTEIN_CHANGE().equals("MUTATED")) {
            PreparedStatement pstmt = con.prepareStatement("UPDATE mutation_event set chr = ?, start_position = ?, end_position = ?, reference_allele = ?, tumor_seq_allele = ?, protein_change = ?, mutation_type = ?, oncotator_codon_change = ?, oncotator_protein_pos_start = ?, oncotator_protein_pos_end = ? where mutation_event_id = ?");
            pstmt.setString(1, annotatedEvent.getCHR());
            pstmt.setInt(2, annotatedEvent.getSTART_POSITION());
            pstmt.setInt(3, annotatedEvent.getEND_POSITION());
            pstmt.setString(4, annotatedEvent.getREFERENCE_ALLELE());
            pstmt.setString(5, annotatedEvent.getTUMOR_SEQ_ALLELE());
            pstmt.setString(6, annotatedEvent.getPROTEIN_CHANGE());
            pstmt.setString(7, annotatedEvent.getMUTATION_TYPE());
            pstmt.setString(8, annotatedEvent.getONCOTATOR_CODON_CHANGE());
            pstmt.setInt(9, annotatedEvent.getONCOTATOR_PROTEIN_POS_START());
            pstmt.setInt(10, annotatedEvent.getONCOTATOR_PROTEIN_POS_END());
            pstmt.setInt(11, annotatedEvent.getMUTATION_EVENT_ID());
            try {
                Integer rs = pstmt.executeUpdate();
                log.info("Updated mutation event. Mutation event id: " + annotatedEvent.getMUTATION_EVENT_ID());
            }
            catch (SQLIntegrityConstraintViolationException e) {
                List<MutationEvent> mutationEvents = getDuplicatedMutationEvents(annotatedEvent);
                for (MutationEvent event : mutationEvents) {
                    if (event.getPROTEIN_CHANGE().equals(annotatedEvent.getPROTEIN_CHANGE())) {
                        updateMutationsInDb(annotatedEvent, event);
                        deleteMutationEvent(annotatedEvent);
                    }
                }
            }
        }
        else {
            log.error("Event " + annotatedEvent.getMUTATION_EVENT_ID() + " unable to be fixed.");
        }
    }

    private List<MutationEvent> getDuplicatedMutationEvents(MutationEvent annotatedEvent) {
        MutationEvent qMutationEvent = alias(MutationEvent.class, BatchConfiguration.MUTATION_EVENT_TABLE);
        List<MutationEvent> mutationEvents = databaseAnnotatorQueryFactory.select(
            Projections.constructor(MutationEvent.class, $(qMutationEvent.getMUTATION_EVENT_ID()),
                $(qMutationEvent.getCHR()),
                $(qMutationEvent.getSTART_POSITION()),
                $(qMutationEvent.getEND_POSITION()),
                $(qMutationEvent.getREFERENCE_ALLELE()),
                $(qMutationEvent.getTUMOR_SEQ_ALLELE()),
                $(qMutationEvent.getPROTEIN_CHANGE()),
                $(qMutationEvent.getMUTATION_TYPE()),
                $(qMutationEvent.getONCOTATOR_CODON_CHANGE()),
                $(qMutationEvent.getONCOTATOR_PROTEIN_POS_START()),
                $(qMutationEvent.getONCOTATOR_PROTEIN_POS_END())))
            .from($(qMutationEvent))
            .where($(qMutationEvent.getCHR())
                .eq(annotatedEvent.getCHR())
            .and($(qMutationEvent.getSTART_POSITION())
                .eq(annotatedEvent.getSTART_POSITION()))
            .and($(qMutationEvent.getEND_POSITION())
                .eq(annotatedEvent.getEND_POSITION()))
            .and($(qMutationEvent.getTUMOR_SEQ_ALLELE())
                .eq(annotatedEvent.getTUMOR_SEQ_ALLELE()))
            .and($(qMutationEvent.getMUTATION_TYPE()).eq(annotatedEvent.getMUTATION_TYPE())))
            .fetch();
        return mutationEvents;
    }

    private void updateMutationsInDb(MutationEvent eventToBeDeleted, MutationEvent properlyAnnotatedEvent) throws Exception{
        PreparedStatement pstmt = con.prepareStatement("UPDATE mutation set mutation_event_id = ? where mutation_event_id = ?");
        pstmt.setInt(1, properlyAnnotatedEvent.getMUTATION_EVENT_ID());
        pstmt.setInt(2, eventToBeDeleted.getMUTATION_EVENT_ID());
        pstmt.executeUpdate();
        log.info("Updating mutations - mutations linked to mutation event " + eventToBeDeleted.getMUTATION_EVENT_ID() + " now linked to " + properlyAnnotatedEvent.getMUTATION_EVENT_ID());
    }

    private void deleteMutationEvent(MutationEvent event) throws Exception{
        PreparedStatement pstmt = con.prepareStatement("delete from mutation_event where mutation_event_id = ?");
        pstmt.setInt(1, event.getMUTATION_EVENT_ID());
        log.info("Deleting mutation event. Properly annotated event already exists in database: " + event.getMUTATION_EVENT_ID());
    }
}
