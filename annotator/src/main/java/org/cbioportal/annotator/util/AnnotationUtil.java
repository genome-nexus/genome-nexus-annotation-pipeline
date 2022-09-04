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

import com.google.common.base.Strings;
import org.cbioportal.models.MutationRecord;
import org.genome_nexus.client.AlleleFrequency;
import org.genome_nexus.client.ColocatedVariant;
import org.genome_nexus.client.TranscriptConsequenceSummary;
import org.genome_nexus.client.VariantAnnotation;
import org.mskcc.cbio.maf.MafUtil;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.cbioportal.models.Header.*;

/**
 * Utility class for resolving values from the Genome Nexus annotation JSON.
 *
 * @author Mete Ozguz
 * @author ochoaa
 */
public class AnnotationUtil {
    private static final Pattern PROTEIN_POSITTION_REGEX = Pattern.compile("p.[A-Za-z]([0-9]*).*$");
    private static final Pattern DBSNP_RSID_REGIX = Pattern.compile("^(rs\\d*)$");

    private AnnotationUtil() {
    }

    public static String resolveReferenceAllele(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getReferenceAllele() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getReferenceAllele();
        }
        return mRecord.get(Reference_Allele);
    }

    public static String resolveStart(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getStart() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getStart().toString();
        } else {
            return mRecord.get(Start_Position);
        }
    }

    public static String resolveChromosome(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getChromosome() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getChromosome();
        } else {
            return mRecord.get(Chromosome);
        }
    }

    public static String resolveProteinPosEnd(TranscriptConsequenceSummary canonicalTranscript) {
        Integer proteinEnd = null;
        if (canonicalTranscript != null && canonicalTranscript.getProteinPosition() != null) {
            proteinEnd = canonicalTranscript.getProteinPosition().getEnd();
        }
        return parseIntegerAsString(proteinEnd);
    }

    public static String resolveExon(TranscriptConsequenceSummary canonicalTranscript) {
        String exon = "";
        if (canonicalTranscript != null && canonicalTranscript.getExon() != null) {
            exon = canonicalTranscript.getExon();
        }
        return exon;
    }

    public static String resolveEntrezGeneId(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord, boolean replace) {
        if (!replace || canonicalTranscript == null || canonicalTranscript.getEntrezGeneId() == null) {
            return mRecord.get(Entrez_Gene_Id);
        } else {
            return canonicalTranscript.getEntrezGeneId();
        }
    }

    public static String resolveVariantType(VariantAnnotation gnResponse) {
        String variantType = "";
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getVariantType() != null) {
            variantType = gnResponse.getAnnotationSummary().getVariantType();
        }
        return variantType;
    }

    public static String resolveStrandSign(VariantAnnotation gnResponse, MutationRecord mRecord) {
        String strand;
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getStrandSign() != null) {
            strand = gnResponse.getAnnotationSummary().getStrandSign();
        } else {
            strand = mRecord.get(Strand);
        }
        return strand;
    }

    public static String resolveDbSnpRs(VariantAnnotation gnResponse, MutationRecord mRecord) {
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
        return dbSnpRs != null ? dbSnpRs : mRecord.get(dbSNP_RS);
    }

    public static String resolveGnomadAlleleFrequencyASJ(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfAsj();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveGnomadAlleleFrequencyNFE(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfNfe();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveGnomadAlleleFrequencyAMR(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfAmr();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveRefSeq(TranscriptConsequenceSummary canonicalTranscript) {
        String refSeq = "";
        if (canonicalTranscript != null) {
            refSeq = canonicalTranscript.getRefSeq();
        }
        return refSeq != null ? refSeq : "";
    }

    public static String resolveHgvsc(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsc = "";
        if (canonicalTranscript != null && canonicalTranscript.getHgvsc() != null) {
            hgvsc = canonicalTranscript.getHgvsc();
        }
        return hgvsc;
    }

    public static String resolveHgvsp(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsp = "";
        if (canonicalTranscript != null && canonicalTranscript.getHgvsp() != null) {
            hgvsp = canonicalTranscript.getHgvsp();
        }
        return hgvsp;
    }

    public static String resolveHugoSymbol(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord, boolean replace) {
        if (replace && canonicalTranscript != null && canonicalTranscript.getHugoGeneSymbol() != null) {
            return canonicalTranscript.getHugoGeneSymbol();
        } else {
            return mRecord.get(Hugo_Symbol);
        }
    }

    public static String resolveAssemblyName(VariantAnnotation gnResponse, MutationRecord mRecord) {
        return (gnResponse.getAssemblyName() == null) ? mRecord.get(NCBI_Build) : gnResponse.getAssemblyName();
    }

    public static String resolveProteinPosition(TranscriptConsequenceSummary canonicalTranscript, MutationRecord record) {
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
        return !Strings.isNullOrEmpty(proteinPosition) ? proteinPosition : record.get(Protein_position);
    }

    public static String resolveGnomadAlleleFrequencyOTH(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfOth();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveGnomadAlleleFrequencySAS(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfSas();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveGnomadAlleleFrequency(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAf();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveGnomadAlleleFrequencyEAS(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfEas();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveProteinPosStart(TranscriptConsequenceSummary canonicalTranscript) {
        Integer proteinStart = null;
        if (canonicalTranscript != null && canonicalTranscript.getProteinPosition() != null) {
            proteinStart = canonicalTranscript.getProteinPosition().getStart();
        }
        return parseIntegerAsString(proteinStart);
    }

    public static String resolveGnomadAlleleFrequencyAFR(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfAfr();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveTranscriptId(TranscriptConsequenceSummary canonicalTranscript) {
        String transcriptId = "";
        if (canonicalTranscript != null && canonicalTranscript.getTranscriptId() != null) {
            transcriptId = canonicalTranscript.getTranscriptId();
        }
        return transcriptId;
    }

    public static String resolveCodonChange(TranscriptConsequenceSummary canonicalTranscript) {
        String codonChange = "";
        if (canonicalTranscript != null && canonicalTranscript.getCodonChange() != null) {
            codonChange = canonicalTranscript.getCodonChange();
        }
        return codonChange;
    }

    public static String resolveTumorSeqAllele(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getVariantAllele() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getVariantAllele();
        }
        return MafUtil.resolveTumorSeqAllele(mRecord.get(Reference_Allele), mRecord.get(Tumor_Seq_Allele1), mRecord.get(Tumor_Seq_Allele2));
    }

    public static String resolveHgvspShort(TranscriptConsequenceSummary canonicalTranscript) {
        String hgvsp = "";
        if (canonicalTranscript != null && canonicalTranscript.getHgvspShort() != null) {
            hgvsp = canonicalTranscript.getHgvspShort();
        }
        return hgvsp;
    }

    public static String resolveConsequence(TranscriptConsequenceSummary canonicalTranscript) {
        if (canonicalTranscript == null || canonicalTranscript.getConsequenceTerms() == null) {
            return "";
        } else {
            return canonicalTranscript.getConsequenceTerms();
        }
    }

    public static String resolveGnomadAlleleFrequencyFIN(AlleleFrequency alleleFrequency) {
        Double toReturn = null;
        if (alleleFrequency != null) {
            toReturn = alleleFrequency.getAfFin();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveEnd(VariantAnnotation gnResponse, MutationRecord mRecord) {
        if (gnResponse.getAnnotationSummary() != null && gnResponse.getAnnotationSummary().getGenomicLocation().getEnd() != null) {
            return gnResponse.getAnnotationSummary().getGenomicLocation().getEnd().toString();
        } else {
            return mRecord.get(End_Position);
        }
    }

    public static String resolveVariantClassification(TranscriptConsequenceSummary canonicalTranscript, MutationRecord mRecord) {
        String variantClassification = null;
        if (canonicalTranscript != null) {
            variantClassification = canonicalTranscript.getVariantClassification();
        }
        return variantClassification != null ? variantClassification : mRecord.get(Variant_Classification);
    }

    public static String resolveSiftPrediction(TranscriptConsequenceSummary canonicalTranscript) {
        String siftPrediction = "";
        if (canonicalTranscript != null && canonicalTranscript.getSiftPrediction() != null) {
            siftPrediction = canonicalTranscript.getSiftPrediction();
        }
        return siftPrediction;
    }

    public static String resolveSiftScore(TranscriptConsequenceSummary canonicalTranscript) {
        Double toReturn = null;
        if (canonicalTranscript != null && canonicalTranscript.getSiftScore() != null) {
            toReturn = canonicalTranscript.getSiftScore();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolvePolyphenPrediction(TranscriptConsequenceSummary canonicalTranscript) {
        String polyphenPrediction = "";
        if (canonicalTranscript != null && canonicalTranscript.getPolyphenPrediction() != null) {
            polyphenPrediction = canonicalTranscript.getPolyphenPrediction();
        }
        return polyphenPrediction;
    }

    public static String resolvePolyphenScore(TranscriptConsequenceSummary canonicalTranscript) {
        Double toReturn = null;
        if (canonicalTranscript != null && canonicalTranscript.getPolyphenScore() != null) {
            toReturn = canonicalTranscript.getPolyphenScore();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveMaFunctionalImpact(VariantAnnotation gnResponse) {
        String maFunctionalImpact = "";
        if (gnResponse.getMutationAssessor() != null && gnResponse.getMutationAssessor().getAnnotation() != null) {
            maFunctionalImpact = gnResponse.getMutationAssessor().getAnnotation().getFunctionalImpact();
        }
        return maFunctionalImpact != null ? maFunctionalImpact : "";
    }

    public static String resolveMaFunctionalImpactScore(VariantAnnotation gnResponse) {
        Double toReturn = null;
        if (gnResponse.getMutationAssessor() != null && gnResponse.getMutationAssessor().getAnnotation() != null) {
            toReturn = gnResponse.getMutationAssessor().getAnnotation().getFunctionalImpactScore();
        }
        return parseDoubleAsString(toReturn);
    }

    public static String resolveMaLinkMSA(VariantAnnotation gnResponse) {
        String maLinkMSA = "";
        if (gnResponse.getMutationAssessor() != null && gnResponse.getMutationAssessor().getAnnotation() != null) {
            maLinkMSA = gnResponse.getMutationAssessor().getAnnotation().getMsaLink();
        }
        return maLinkMSA != null ? maLinkMSA : "";
    }

    public static String resolveMaLinkPDB(VariantAnnotation gnResponse) {
        String maLinkPDB = "";
        if (gnResponse.getMutationAssessor() != null && gnResponse.getMutationAssessor().getAnnotation() != null) {
            maLinkPDB = gnResponse.getMutationAssessor().getAnnotation().getPdbLink();
        }
        return maLinkPDB != null ? maLinkPDB : "";
    }

    public static String resolveRefTri(VariantAnnotation gnResponse) {
        String refTri = "";
        if (gnResponse.getNucleotideContext() != null && gnResponse.getNucleotideContext().getAnnotation() != null) {
            refTri = gnResponse.getNucleotideContext().getAnnotation().getSeq();
        }
        return refTri != null ? refTri : "";
    }

    public static String resolveVarTri(VariantAnnotation gnResponse) {
        String refTri = "";
        String varTri = "";
        if (gnResponse.getNucleotideContext() != null && gnResponse.getNucleotideContext().getAnnotation() != null) {
            refTri = gnResponse.getNucleotideContext().getAnnotation().getSeq();
            String alleleString = gnResponse.getAlleleString();

            if (refTri != null && alleleString != null) {
                Integer indexChange = alleleString.indexOf('/');

                if (indexChange > -1) {
                    // nucleotide context is only supported for SNV on the server side, so we only need to handle SNV case
                    varTri = "" + refTri.charAt(0) + alleleString.charAt(indexChange + 1) + refTri.charAt(2);
                }
            }

        }
        return varTri != null ? varTri : "";
    }

    private static String parseDoubleAsString(Double value) {
        return value != null ? String.valueOf(value) : "";
    }

    private static String parseIntegerAsString(Integer value) {
        return value != null ? String.valueOf(value) : "";
    }
}
