#!/usr/bin/env bash
# Test vcf2maf test data for maf
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
set -e

exit_code=0

cd $DIR/../data/vcf2maf/ && make clean && make all
cd $DIR

for isoform in mskcc uniprot
do
    java -jar $DIR/../../annotationPipeline/target/annotationPipeline-0.1.0.jar --filename $DIR/../data/vcf2maf/tests.maf  --output-filename $DIR/../data/vcf2maf/tests.maf.gn_output.${isoform}.maf --isoform-override ${isoform} && \
        cd $DIR/../data/vcf2maf/ && \
        make tests.maf.gn_output.${isoform}.cut_columns.txt && \
        diff tests.maf.gn_output.${isoform}.cut_columns.txt test_output.${isoform}.cut_columns.txt || exit_code=1;
done

exit $exit_code