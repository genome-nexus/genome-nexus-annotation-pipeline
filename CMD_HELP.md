# -------- UNDER CONSTRUCTION ----------
# Genome Nexus Annotation Pipeline

This tool has two subcommands: annotate, merge  
In order to use it, you need to install Java version 8 or above.  
Help page can be displayed simply:

```
java -jar gnap.jar -h
```

## Subcommand - annotate

This subcommand allows the annotation of genomic variants from a MAF file using [Genome Nexus](http://genomenexus.org).  
Help page can be displayed simply:

```
java -jar gnap.jar annotate -h
```

### Options of the subcommand - annotate

* **-e, --error-report-location**: filename which will be used for error reporting

```
```

* **-f, --filename**: filename which will be annotated

```
```

* **-h, --help**

```
```

* **-i, --isoform-override**:
  * **mskcc** for Mouse Sphingosine Kinase (mSK) isoforms
  * **mskcc** for [Memorial Sloan Kettering Cancer Center](https://www.mskcc.org/) isoforms
  * **uniprot** for [UniProt](https://www.uniprot.org/) isoforms

```
```

* **-o, --output-filename**

```
```

* **-p, --post-interval-size**: number of maximum records in a single Genome Nexus POST request

```
```

* **-r, --replace-symbol-entrez**: it enables the replacement of gene symbols and entrez id with what is provided by the annotator

```
```


## Subcommand - merge

This subcommand merges given MAF files or given directory which contains MAF files, into a single MAF file.  
Help page can be displayed simply:

```
java -jar gnap.jar merge -h
```

### Options of the subcommand - merge
