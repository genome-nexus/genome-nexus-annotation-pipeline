# Genome Nexus Annotation Pipeline

This tool has two subcommands: annotate, merge  
To use it, you need to install Java version 8 or above.

The help page can be displayed simply by:

```
java -jar gnap.jar -h
```

The output will be

```
usage: GenomeNexusAnnotationPipeline annotate
annotate is the default behavior when a subcommand is omitted.
annotate subcommand options:
 -e,--error-report-location <arg>   Error report filename (including path)
 -f,--filename <arg>                Mutation filename
 -h,--help                          shows this help document and quits.
 -i,--isoform-override <arg>        Isoform Overrides. Options: `mskcc` (preferred) or uniprot (legacy)
 -o,--output-filename <arg>         Output filename (including path)
 -p,--post-interval-size <arg>      Number of records to make POST requests to Genome Nexus with at
                                    a time
 -r,--replace-symbol-entrez         Replace gene symbols and entrez id with what is provided by
                                    annotator
 -t,--output-format <arg>           extended, minimal or a file path which includes output format
                                    (FORMAT EXAMPLE:
                                    Chromosome,Hugo_Symbol,Entrez_Gene_Id,Center,NCBI_Build)
Visit https://github.com/genome-nexus/genome-nexus-annotation-pipeline/blob/master/CMD_HELP.md for
more.

usage: GenomeNexusAnnotationPipeline merge
merge subcommand options:
 -d,--input-mafs-directory <arg>   directory containing all MAFs to merge
 -h,--help                         shows this help document and quits.
 -i,--input-mafs-list <arg>        comma-delimited list of MAFs to merge
 -o,--output-maf <arg>             output filename for merged MAF [REQUIRED]
 -s,--skip-invalid-input           skips invalid input file. Input file must include following
                                   headers:Chromosome, Start_Position, End_Position,
                                   Reference_Allele. Input file should either include
                                   Tumor_Seq_Allele1 or Tumor_Seq_Allele2
Visit https://github.com/genome-nexus/genome-nexus-annotation-pipeline/blob/master/CMD_HELP.md for
more.

```  

Let's go over all the subcommands and their options one by one!

## Subcommand - annotate

This subcommand allows the annotation of genomic variants from a MAF file using [Genome Nexus](http://genomenexus.org).  
The help page for the subcommand annotate can be displayed simply by:

```
java -jar gnap.jar annotate -h
```

Let's go over the rest of the options of the subcommand annotate.

### Options of the subcommand - annotate

* **-e, --error-report-location**: filename which will be used for error reporting

```
java -jar gnap.jar annotate --filename in.txt --output-filename out.txt --error-report-location error.log
```

After the above command is completed successfully, a file named error.log, which includes info about failed annotations will be created.

* **-f, --filename**: filename which will be annotated. File should be tab separated and [valid](#definition-of-valid-input-file).

```
java -jar gnap.jar annotate --filename in.txt --output-filename out.txt
```

After the above command is completed successfully, a file named out.txt, which includes all variants annotated successfully and unsuccessfully will be created.

* **-i, --isoform-override**:
  * **mskcc** for [Memorial Sloan Kettering Cancer Center](https://www.mskcc.org/) isoforms (preferred)
  * **uniprot** (legacy)

```
java -jar gnap.jar annotate --filename in.txt --output-filename out.txt --isoform-override mskcc
```

After the above command is completed successfully, a file named out.txt, which includes all variants annotated successfully and unsuccessfully overridden by mskcc isoform, will be created.

* **-o, --output-filename**: filename to which annotations will be written.

```
java -jar gnap.jar annotate --filename in.txt --output-filename out.txt
```

After the above command is completed successfully, a file named out.txt, which includes all variants annotated successfully and unsuccessfully will be created.

* **-p, --post-interval-size**: number of maximum records in a single Genome Nexus POST request
  * Application uses a post interval size of 100 by default.
  * You can set this option to 1 to use Genome Nexus GET request

```
java -jar gnap.jar annotate --filename in.txt --output-filename out.txt --post-interval-size 500
```

The command above will send a list of 500 Genomic Locations per POST request.

* **-r, --replace-symbol-entrez**: it enables the replacement of gene symbols and entrez id with what is provided by the annotator

```
java -jar gnap.jar annotate --filename in.txt --output-filename out.txt --replace-symbol-entrez
```

* **-t,--output-format**: it enables formatting of the output file based on a pre-defined format or based on a custom format.
  * **minimal** 
    * It will produce an output file with 3 header groups:
      1. the headers of input file 
      2. any additional headers (sorted alphabetically) 
      3. the header named Annotation_Status
  * **extended** 
    * It will produce an output file with the headers of the extended MAF format (a-c) plus additional headers (d-e): 
      1. 32 columns from the TCGA MAF format
      2. 1 column with the amino acid change
      3. 4 columns with information on reference and variant allele counts in tumor and normal samples
      4. any additional headers (sorted alphabetically)
      5. the header named Annotation_Status. 
  * **custom format file** 
    * A file containing a single line of headers separated with comma.
    * It will produce an output file with 2 header groups:
      1. the headers of format file
      2. the header named Annotation_Status

```
java -jar gnap.jar annotate --filename in.txt --output-filename out.txt --output-format format.txt
```

Let's assume the content of the format.txt file is: Chromosome,Hugo_Symbol,Entrez_Gene_Id,Center,NCBI_Build
After the above command is completed successfully, a file name out.txt will be produced with the above 5 headers.

## Subcommand - merge

This subcommand merges given MAF files or the files in a given directory, using their headers, into a single MAF file.

The help page for the subcommand merge can be displayed simply by::

```
java -jar gnap.jar merge -help
```

Let's go over the rest of the options of the subcommand merge.

### Options of the subcommand - merge

* **-d, --input-mafs-directory**: The directory name which homes MAF files to be merged. MAF files doesn't have to be [valid](#definition-of-valid-input-file).

```
java -jar gnap.jar merge --input-mafs-directory MAF_DIR --output-maf out.txt
```

* **-i, --input-mafs-list**: comma-delimited list of MAF files to be merged. Prefer to use the option input-mafs-directory in case of many files.

```
java -jar gnap.jar merge --input-mafs-list file1,file2,file3 --output-maf out.txt
```

* **-o, --output-maf**: The name of the output file

```
java -jar gnap.jar merge --input-mafs-list file1,file2 --output-maf out.txt
```

The command above will merge file1 and file2 based on their headers and will write to the file named out.txt

* **-s, --skip-invalid-input**: It enables to skip [invalid](#definition-of-valid-input-file) MAF files.

```
java -jar gnap.jar merge --input-mafs-directory MAF_DIR --output-maf out.txt --skip-invalid-input
```

The command above will select the valid files inside the folder named MAF_DIR first, and then it will merge all of them based on their headers. Finally, it will write to the file named out.txt

### Appendix

#### Definition of Valid Input File

A valıd input file should include the following headers: **Chromosome, Start_Position, End_Position, Reference_Allele** and should include one of these headers: **Tumor_Seq_Allele1, Tumor_Seq_Allele2**  
A valid input file with only the headers mentioned above is called **minimal** file.
