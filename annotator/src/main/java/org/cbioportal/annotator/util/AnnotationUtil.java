/*
 * Copyright (c) 2020 Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.cbio.maf.MafUtil;
import org.cbioportal.models.MutationRecord;
import org.springframework.stereotype.Component;
import org.genome_nexus.client.*;

import com.google.common.base.Strings;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for resolving values from the Genome Nexus annotation JSON.
 *
 * @author ochoaa
 */
@Component
public class AnnotationUtil {
    private static final Pattern PROTEIN_POSITTION_REGEX = Pattern.compile("p.[A-Za-z]([0-9]*).*$");
    private static final Pattern DBSNP_RSID_REGIX = Pattern.compile("^(rs\\d*)$");

    public AnnotationUtil() {}

    public String resolveReferenceAllele(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getReferenceAllele() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getReferenceAllele();
        }
        return mRecord.getREFERENCE_ALLELE();
    }

    public String resolveStart(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getStart() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getStart().toString();
        } else {
            return mRecord.getSTART_POSITION();
        }
    }

    public String resolveChromosome(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getChromosome() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getChromosome();
        } else {
            return mRecord.getCHROMOSOME();
        }
    }

    public String resolveProteinPosEnd(TranscriptConsequenceSummary canonicalTranscript) {
        Integer proteinEnd = null;
        if (canonicalTranscript != null && canonicalTranscript.getProteinPosition() != null) {
            proteinEnd = canonicalTranscript.getProteinPosition().getEnd();
        }
        return parseIntegerAsString(proteinEnd);
    }

    public String resolveExon(TranscriptConsequenceSummary canonicalTranscript) {
        String exon = "";
        if (canonicalTranscript != null && canonicalTranscript.getExon() != null) {
            exon = canonicalTranscript.getExon();
        }
        return exon;
    }

    public String resolveEntrezGeneId(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord, boolean replace) {
        if (!replace || canonicalTranscript == null || canonicalTranscript.getEntrezGeneId() == null) {
            return mRecord.getENTREZ_GENE_ID();
        } else {
            return canonicalTranscript.getEntrezGeneId();
        }
    }

    public String resolveVariantType(VariantAnnotation gnResponse) {
        String variantType = "";
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getVariantType() != null) {
            variantType = gnResponse.getAnnotationSummary().getVariantType();
        }
        return variantType;
    }

    public String resolveStrandSign(VariantAnnotation gnResponse, MutationRecord mRecord) {
        String strand;
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getStrandSign() != null) {
            strand = gnResponse.getAnnotationSummary().getStrandSign();
        } else {
            strand = mRecord.getSTRAND();
        }
        return strand;
    }

    public String resolveDbSnpRs(VariantAnnotation gnResponse, MutationRecord mRecord) {
        String dbSnpRs = null;
        if (gnResponse.getColocatedVariants() != null && !gnResponse.getColocatedVariants().isEmpty()) {
            for (ColocatedVariant cv : gnResponse.getColocatedVariants()) {
                Matcher dbSnpRsIdMatcher = DBSNP_RSID_REGIX.matcher(cv.getDbSnpId());
                if (dbSnpRsIdMatcher.find()) {
                    dbSnpRs = dbSnpRsIdMatcher.group(0);
                    break;
                }
            }
        }
        return dbSnpRs != null ? dbSnpRs : mRecord.getDBSNP_RS();
    }

    public String resolveGnomadAlleleFrequencyASJ(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfAsj();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveGnomadAlleleFrequencyNFE(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfNfe();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveGnomadAlleleFrequencyAMR(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfAmr();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveRefSeq(TranscriptConsequenceSummary canonicalTranscript) {
        String refSeq = "";
        if (canonicalTranscript != null) {
            refSeq = canonicalTranscript.getRefSeq();
        }
        return refSeq != null ? refSeq : "";
    }

    public String resolveHgvsc(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsc = "";
        if (canonicalTranscript != null && canonicalTranscript.getHgvsc() != null) {
            hgvsc = canonicalTranscript.getHgvsc();
        }
        return hgvsc;
    }

    public String resolveHgvsp(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsp = "";
        if (canonicalTranscript != null && canonicalTranscript.getHgvsp() != null) {
            hgvsp = canonicalTranscript.getHgvsp();
        }
        return hgvsp;
    }

    public String resolveHugoSymbol(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord, boolean replace) {
        if (replace && canonicalTranscript != null && canonicalTranscript.getHugoGeneSymbol() != null) {
            return canonicalTranscript.getHugoGeneSymbol();
        } else {
            return mRecord.getHUGO_SYMBOL();
        }
    }

    public String resolveAssemblyName(VariantAnnotation gnResponse, MutationRecord mRecord) {
        return (gnResponse.getAssemblyName() == null) ? mRecord.getNCBI_BUILD() : gnResponse.getAssemblyName();
    }

    public String resolveProteinPosition(TranscriptConsequenceSummary canonicalTranscript, MutationRecord record) {
        String proteinPosition = null;
        if (canonicalTranscript != null) {
            String proteinPosStart = resolveProteinPosStart(canonicalTranscript);
            String hgvspShort = resolveHgvspShort(canonicalTranscript);
            if (!Strings.isNullOrEmpty(proteinPosStart)) {
                proteinPosition = proteinPosStart;
            } else if (!Strings.isNullOrEmpty(hgvspShort)) {
                // try extracting from hgvspShort if proteinPosStart null/empty
                Matcher matcher = PROTEIN_POSITTION_REGEX.matcher(hgvspShort);
                if (matcher.find()) {
                    proteinPosition = matcher.group(1);
                }
            }
        }
        return !Strings.isNullOrEmpty(proteinPosition) ? proteinPosition : record.getAdditionalProperties().getOrDefault("Protein_position", "");
    }

    public String resolveGnomadAlleleFrequencyOTH(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfOth();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveGnomadAlleleFrequencySAS(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfSas();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveGnomadAlleleFrequency(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAf();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveGnomadAlleleFrequencyEAS(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfEas();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveProteinPosStart(TranscriptConsequenceSummary canonicalTranscript) {
        Integer proteinStart = null;
        if (canonicalTranscript != null && canonicalTranscript.getProteinPosition() != null) {
            proteinStart = canonicalTranscript.getProteinPosition().getStart();
        }
        return parseIntegerAsString(proteinStart);
    }

    public String resolveGnomadAlleleFrequencyAFR(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfAfr();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveTranscriptId(TranscriptConsequenceSummary canonicalTranscript) {
        String transcriptId = "";
        if (canonicalTranscript != null && canonicalTranscript.getTranscriptId() != null) {
            transcriptId = canonicalTranscript.getTranscriptId();
        }
        return transcriptId;
    }

    public String resolveHotspot() {
        String hotspot = "0";
        // TODO this hotspot field is not valid anymore:
        // we need to redo this part if we want to include hotspot information
        //        if (canonicalTranscript != null) {
        //            if (canonicalTranscript.getIsHotspot() != null) {
        //                hotspot = canonicalTranscript.getIsHotspot().equals("true") ? "1" : "0";
        //            }
        //        }
        return hotspot;
    }

    public String resolveCodonChange(TranscriptConsequenceSummary canonicalTranscript) {
        String codonChange = "";
        if (canonicalTranscript != null && canonicalTranscript.getCodonChange() != null) {
            codonChange = canonicalTranscript.getCodonChange();
        }
        return codonChange;
    }

    public String resolveTumorSeqAllele(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getVariantAllele() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getVariantAllele();
        }
        return MafUtil.resolveTumorSeqAllele(mRecord.getREFERENCE_ALLELE(), mRecord.getTUMOR_SEQ_ALLELE1(), mRecord.getTUMOR_SEQ_ALLELE2());
    }

    public String resolveHgvspShort(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsp = "";
        if (canonicalTranscript != null && canonicalTranscript.getHgvspShort() != null) {
            hgvsp = canonicalTranscript.getHgvspShort();
        }
        return hgvsp;
    }

    public String resolveConsequence(TranscriptConsequenceSummary canonicalTranscript) {
        if (canonicalTranscript == null || canonicalTranscript.getConsequenceTerms() == null) {
            return "";
        } else {
            return canonicalTranscript.getConsequenceTerms();
        }
    }

    public String resolveGnomadAlleleFrequencyFIN(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfFin();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveEnd(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getEnd() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getEnd().toString();
        } else {
            return mRecord.getEND_POSITION();
        }
    }

    public String resolveVariantClassification(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord) {
        String variantClassification = null;
        if (canonicalTranscript != null) {
            variantClassification = canonicalTranscript.getVariantClassification();
        }
        return variantClassification != null ? variantClassification : mRecord.getVARIANT_CLASSIFICATION();
    }

    public String resolveSiftPrediction(TranscriptConsequenceSummary canonicalTranscript) {
        String siftPrediction = "";
        if (canonicalTranscript != null && canonicalTranscript.getSiftPrediction() != null) {
            siftPrediction = canonicalTranscript.getSiftPrediction();
        }
        return siftPrediction;
    }

    public String resolveSiftScore(TranscriptConsequenceSummary canonicalTranscript) {
        Double toReturn = null;
        if (canonicalTranscript != null && canonicalTranscript.getSiftScore() != null) {
            toReturn = canonicalTranscript.getSiftScore();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolvePolyphenPrediction(TranscriptConsequenceSummary canonicalTranscript) {
        String polyphenPrediction = "";
        if (canonicalTranscript != null && canonicalTranscript.getPolyphenPrediction() != null) {
            polyphenPrediction = canonicalTranscript.getPolyphenPrediction();
        }
        return polyphenPrediction;
    }

    public String resolvePolyphenScore(TranscriptConsequenceSummary canonicalTranscript) {
        Double toReturn = null;
        if (canonicalTranscript != null && canonicalTranscript.getPolyphenScore() != null) {
            toReturn = canonicalTranscript.getPolyphenScore();
        }
        return parseDoubleAsString(toReturn);
    }
    
    public String resolveMaFunctionalImpact(VariantAnnotation gnResponse) {
        String maFunctionalImpact = "";
        if (gnResponse.getMutationAssessor() != null && gnResponse.getMutationAssessor().getAnnotation() != null) {
            maFunctionalImpact = gnResponse.getMutationAssessor().getAnnotation().getFunctionalImpact();
        }
        return maFunctionalImpact != null ? maFunctionalImpact : "";
    }

    public String resolveMaFunctionalImpactScore(VariantAnnotation gnResponse) {
        Double toReturn = null;
        if (gnResponse.getMutationAssessor() != null && gnResponse.getMutationAssessor().getAnnotation() != null) {
            toReturn = gnResponse.getMutationAssessor().getAnnotation().getFunctionalImpactScore();
        }
        return parseDoubleAsString(toReturn);
    }

    public String resolveMaLinkMSA(VariantAnnotation gnResponse) {
        String maLinkMSA = "";
        if (gnResponse.getMutationAssessor() != null && gnResponse.getMutationAssessor().getAnnotation() != null) {
            maLinkMSA = gnResponse.getMutationAssessor().getAnnotation().getMsaLink();
        }
        return maLinkMSA != null ? maLinkMSA : "";
    }

    public String resolveMaLinkPDB(VariantAnnotation gnResponse) {
        String maLinkPDB = "";
        if (gnResponse.getMutationAssessor() != null && gnResponse.getMutationAssessor().getAnnotation() != null) {
            maLinkPDB = gnResponse.getMutationAssessor().getAnnotation().getPdbLink();
        }
        return maLinkPDB != null ? maLinkPDB : "";
    }

    private String parseDoubleAsString(Double value) {
        return value != null ? String.valueOf(value)  : "";
    }

    private String parseIntegerAsString(Integer value) {
        return value != null ? String.valueOf(value) : "";
    }
}