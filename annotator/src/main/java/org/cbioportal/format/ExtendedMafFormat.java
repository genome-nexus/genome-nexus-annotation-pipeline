package org.cbioportal.format;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created using https://docs.cbioportal.org/file-formats/#extended-maf-format
 * <pre>
 * The extended MAF format recognized by the portal has:
 *    32 columns from the TCGA MAF format.
 *    1 column with the amino acid change.
 *    4 columns with information on reference and variant allele counts in tumor and normal samples.
 * </pre>
 *
 * @author Mete Ozguz
 */
public class ExtendedMafFormat {
    public static final Set<String> headers = new LinkedHashSet<>();

    static {
        //
        // 32 columns from the TCGA MAF format.
        //
        headers.add("Hugo_Symbol"); // (Required): A HUGO gene symbol.
        headers.add("Entrez_Gene_Id"); // (Optional, but recommended): A Entrez Gene identifier.
        headers.add("Center"); // (Optional): The sequencing center.
        headers.add(
                "NCBI_Build"); // (Required): The Genome Reference Consortium Build is used by a variant calling software. It must be "GRCh37" or "GRCh38" for a human, and "GRCm38" for a mouse.
        headers.add("Chromosome"); // (Required): A chromosome number, e.g., "7".
        headers.add("Start_Position"); // (Required): Start position of event.
        headers.add("End_Position"); // (Required): End position of event.
        headers.add("Strand"); // (Optional): We assume that the mutation is reported for the + strand.
        headers.add("Variant_Classification"); // (Required): Translational effect of variant allele, e.g. Missense_Mutation, Silent, etc.
        headers.add("Variant_Type"); // (Optional): Variant Type, e.g. SNP, DNP, etc.
        headers.add("Reference_Allele"); // (Required): The plus strand reference allele at this position.
        headers.add("Tumor_Seq_Allele1"); // (Optional): Primary data genotype.
        headers.add("Tumor_Seq_Allele2"); // (Required): Primary data genotype.
        headers.add("dbSNP_RS"); // (Optional): Latest dbSNP rs ID.
        headers.add("dbSNP_Val_Status"); // (Optional): dbSNP validation status.
        headers.add(
                "Tumor_Sample_Barcode"); // (Required): This is the sample ID. Either a TCGA barcode (patient identifier will be extracted), or for non-TCGA data, a literal SAMPLE_ID as listed in the clinical data file.
        headers.add("Matched_Norm_Sample_Barcode"); // (Optional): The sample ID for the matched normal sample.
        headers.add("Match_Norm_Seq_Allele1"); // (Optional): Primary data.
        headers.add("Match_Norm_Seq_Allele2"); // (Optional): Primary data.
        headers.add("Tumor_Validation_Allele1"); // (Optional): Secondary data from orthogonal technology.
        headers.add("Tumor_Validation_Allele2"); // (Optional): Secondary data from orthogonal technology.
        headers.add("Match_Norm_Validation_Allele1"); // (Optional): Secondary data from orthogonal technology.
        headers.add("Match_Norm_Validation_Allele2"); // (Optional): Secondary data from orthogonal technology.
        headers.add("Verification_Status"); // (Optional): Second pass results from independent attempt using same methods as primary data source. "Verified", "Unknown" or "NA".
        headers.add(
                "Validation_Status"); // (Optional): Second pass results from orthogonal technology. "Valid", "Invalid", "Untested", "Inconclusive", "Redacted", "Unknown" or "NA".
        headers.add(
                "Mutation_Status"); // (Optional): "Somatic" or "Germline" are supported by the UI in Mutations tab. "None", "LOH" and "Wildtype" will not be loaded. Other values will be displayed as text.
        headers.add("Sequencing_Phase"); // (Optional): Indicates current sequencing phase.
        headers.add("Sequence_Source"); // (Optional): Molecular assay type used to produce the analytes used for sequencing.
        headers.add("Validation_Method"); // (Optional): The assay platforms used for the validation call.
        headers.add("Score"); // (Optional): Not used.
        headers.add("BAM_File"); // (Optional): Not used.
        headers.add("Sequencer"); // (Optional): Instrument used to produce primary data.
        //
        // 1 column with the amino acid change.
        //
        headers.add("HGVSp_Short"); // (Required): Amino Acid Change, e.g. p.V600E.
        //
        // 4 columns with information on reference and variant allele counts in tumor and normal samples.
        //
        headers.add("t_alt_count"); // (Optional): Variant allele count (tumor).
        headers.add("t_ref_count"); // (Optional): Reference allele count (tumor).
        headers.add("n_alt_count"); // (Optional): Variant allele count (normal).
        headers.add("n_ref_count"); // (Optional): Reference allele count (normal).
    }
}
