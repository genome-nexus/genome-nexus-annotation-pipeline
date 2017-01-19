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

import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import java.util.*;
import org.cbioportal.database.annotator.model.*;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author heinsz
 */

public class AnnotateRecordsReader implements ItemStreamReader<MutationEvent>{
    @Value("#{jobParameters[studies]}")
    private String studies;

    @Autowired
    SQLQueryFactory databaseAnnotatorQueryFactory;

    private final  String MISSING_PROTEIN_CHANGE = "MUTATED";

    private List<MutationEvent> mutationEvents;

    @Override
    public void open(ExecutionContext ec) throws ItemStreamException {
        List<String> studyList = new ArrayList<>();
        if (studies != null && !studies.trim().equals("")) {
            studyList = Arrays.asList(studies.split(":"));
        }
        this.mutationEvents = getMutationEvents(studyList);

    }

    @Override
    public void update(ExecutionContext ec) throws ItemStreamException {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public MutationEvent read() throws Exception {
        if (!mutationEvents.isEmpty()) {
            return mutationEvents.remove(0);
        }
        return null;
    }

    @Transactional
    private List<MutationEvent> getMutationEvents(List<String> studyList) {
        MutationEvent qMutationEvent = alias(MutationEvent.class, BatchConfiguration.MUTATION_EVENT_TABLE);
        Mutation qMutation = alias(Mutation.class, BatchConfiguration.MUTATION_TABLE);
        Sample qSample = alias(Sample.class, BatchConfiguration.SAMPLE_TABLE);
        Patient qPatient = alias(Patient.class, BatchConfiguration.PATIENT_TABLE);
        CancerStudy qCancerStudy = alias(CancerStudy.class, BatchConfiguration.CANCER_STUDY_TABLE);

        List<MutationEvent> mutationEvents = new ArrayList<>();
        if (studyList.size() > 0) {
            mutationEvents = databaseAnnotatorQueryFactory.select(
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
                .join($(qMutation))
                .on($(qMutationEvent.getMUTATION_EVENT_ID()).eq($(qMutation.getMUTATION_EVENT_ID())))
                .join($(qSample))
                .on($(qMutation.getSAMPLE_ID()).eq($(qSample.getINTERNAL_ID())))
                .join($(qPatient))
                .on($(qSample.getPATIENT_ID()).eq($(qPatient.getINTERNAL_ID())))
                .join($(qCancerStudy))
                .on($(qPatient.getCANCER_STUDY_ID()).eq($(qCancerStudy.getCANCER_STUDY_ID())))
                .where($(qMutationEvent.getPROTEIN_CHANGE()).eq(MISSING_PROTEIN_CHANGE)
                    .and($(qMutationEvent.getREFERENCE_ALLELE()).ne(""))
                    .and($(qMutationEvent.getREFERENCE_ALLELE()).ne($(qMutationEvent.getTUMOR_SEQ_ALLELE())))
                .and($(qCancerStudy.getCANCER_STUDY_IDENTIFIER()).in(studyList)))
                .fetch();
        }
        else {
            mutationEvents = databaseAnnotatorQueryFactory.select(
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
                .where($(qMutationEvent.getPROTEIN_CHANGE()).eq(MISSING_PROTEIN_CHANGE)
                    .and($(qMutationEvent.getREFERENCE_ALLELE()).ne(""))
                    .and($(qMutationEvent.getREFERENCE_ALLELE()).ne($(qMutationEvent.getTUMOR_SEQ_ALLELE()))))
                .fetch();
        }
        return mutationEvents;
    }
}
