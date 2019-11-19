SHELL=/bin/bash
VCF2MAF_VERSION=1ce59edc767e5761681a9367ee76c861b7160883
VCF2MAF_URL=https://raw.githubusercontent.com/mskcc/vcf2maf/$(VCF2MAF_VERSION)
ALL=tests.maf test_output.mskcc.maf test_output.uniprot.maf test_output.mskcc.cut_columns.txt test_output.uniprot.cut_columns.txt

COMPARE_COLUMNS=Hugo_Symbol Chromosome Start_Position End_Position Strand Variant_Classification Variant_Type Reference_Allele Tumor_Seq_Allele2 Tumor_Sample_Barcode Matched_Norm_Sample_Barcode Match_Norm_Seq_Allele1 Match_Norm_Seq_Allele2 Tumor_Validation_Allele1 Tumor_Validation_Allele2 Match_Norm_Validation_Allele1 Match_Norm_Validation_Allele2 Verification_Status Validation_Status Mutation_Status Sequencing_Phase Sequence_Source Validation_Method Score BAM_File Sequencer t_ref_count t_alt_count n_ref_count n_alt_count n_depth t_depth
IGNORE_RECORDS_FOR_TESTING="5	112174758	112174761	+	Frame_Shift_Del	DEL	AAGA" # 4 records with mutli allele records

tests.maf:
	curl $(VCF2MAF_URL)/tests/test.maf | grep -v $(IGNORE_RECORDS_FOR_TESTING) > $@

test_output.mskcc.maf:
	curl $(VCF2MAF_URL)/tests/test_output.custom_isoforms.maf | grep -v $(IGNORE_RECORDS_FOR_TESTING) > $@

test_output.uniprot.maf:
	curl $(VCF2MAF_URL)/tests/test_output.vep_isoforms.maf | grep -v $(IGNORE_RECORDS_FOR_TESTING) > $@

%.cut_columns.txt: %.maf
	if [[ "$(uname)" = "Darwin" ]]; then \
		AWK=gawk; \
	else \
		AWK=awk; \
	fi; \
	(echo $(COMPARE_COLUMNS) | sed 's/\ /	/g'; grep -v "^#" $< | $$AWK -vOFS='\t' -vFS='\t' -f ./../../scripts/select_by_column_name.awk $(COMPARE_COLUMNS)) > $@

all: $(ALL)

clean:
	rm -rf $(ALL)
