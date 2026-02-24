#!/usr/bin/env bash
# Download TCGA test data from cBioPortal datahub (Git LFS media URL)
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DATA_DIR="$SCRIPT_DIR/../data"

DATAHUB_BASE="https://media.githubusercontent.com/media/cBioPortal/datahub/master/crdc/gdc"

# CHOL TCGA GDC (~3764 variants, GRCh38)
echo "Downloading chol_tcga_gdc data_mutations.txt..."
curl -sL "$DATAHUB_BASE/chol_tcga_gdc/data_mutations.txt" \
    -o "$DATA_DIR/chol_tcga_gdc_data_mutations.txt"
echo "Downloaded: $(wc -l < "$DATA_DIR/chol_tcga_gdc_data_mutations.txt") lines"
