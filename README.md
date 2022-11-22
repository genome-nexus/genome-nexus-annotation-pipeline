# Genome Nexus Annotation Pipeline
These tools allow for annotation of genomic variants from a MAF for import into
the cBioPortal using [Genome Nexus](http://genomenexus.org).

## MAF Annotation
The `annotationPipeline` module is a command line tool to annotate a maf using genome nexus. 

**Pre-build steps**

Create your `application.properties`:

```
cp annotationPipeline/src/main/resources/application.properties.EXAMPLE annotationPipeline/src/main/resources/application.properties
```

If you have your own
installation of Genome Nexus, you can point to it by modifying the
`application.properties` file located in
`annotationPipeline/src/main/resources`.

Create your `log4j.properties`:

```
cp annotationPipeline/src/main/resources/log4j.properties.EXAMPLE annotationPipeline/src/main/resources/log4j.properties
```

Modify the property `log4j.appender.a.File` in your `log4j.properties` file to the desired log file path.

To use it, build the project using maven and run it like so:
    
    mvn clean install
    $JAVA_HOME/bin/java -jar annotationPipeline/target/annotationPipeline-*.jar \
        --filename <INPUT_MAF> \
        --output-filename <OUTPUT DESTINATION> \
        --isoform-override <mskcc or uniprot>
    
You can choose to replace the gene symbols in the new maf by the gene symbols
found by Genome Nexus by supplying the `-r` optional parameter. To output error
reporting to a file, supply the `-e` option a location for the file to be
saved. By running the jar without any arguments or by providing the optional
parameter `-h` you can view the full usage statement. 

### Optional parameters
| Short | Long | Description | 
| ------ | ------  | ------ |
| `-h` | `--help` | shows this help document and quits|
| `-f` | `--filename` |Mutation filename|
| `-o` | `--output-filename` | Output filename (including path)|
| `-t` | `--output-format`  | extended, minimal or a file path which includes output format (FORMAT EXAMPLE: Chromosome,Hugo_Symbol,Entrez_Gene_Id,Center,NCBI_Build)|
| `-i` | `--isoform-override` | Isoform Overrides. Options: mskcc or uniprot|
| `-e` | `--error-report-location` | Error report filename (including path)|
| `-r` | `--replace-symbol-entrez` | Replace gene symbols and entrez id with what is provided by annotator"|
| `-p` | `--post-interval-size` | Number of records to make POST requests to Genome Nexus with at a time |
| `-s` | `--strip-matching-bases` | Strip matching allele bases. Options: first, all, none. For example: AAC/AAT, strip-off first: AC/AT, strip-off all: C/T, strip-off none: AAC/AAT  |
| `-a` | `--add-original-genomic-location` | Add original genomic location data columns into the output, name columns with prefix 'IGNORE_Genome_Nexus_Original_'). This would be useful if saving a reference of original input is needed and won't be changed in any condition|
| `-d` | `--ignore-original-data` | Genome-nexus-annotation-pipeline reads original genomic location info as input by default, if not existing, reading from normal genomic location info columns. Adding `-d` ignores original genomic location info columns (columns with prefix 'IGNORE_Genome_Nexus_Original_') and only use whatever in normal genomic location info columns. This would be helpful if you'd like to stick with current genomic location info columns.|

### Minimal MAF Example

For an example minimal input file see
[test/data/minimal_example.in.txt](test/data/minimal_example.in.txt) and
corresponding output
[test/data/minimal_example.out.uniprot.txt](test/data/minimal_example.out.uniprot.txt).
The output file was generated with:

    $JAVA_HOME/bin/java -jar annotationPipeline/target/annotationPipeline-*.jar \
        -r \
        --filename test/data/minimal_example.in.txt  \
        --output-filename test/data/minimal_example.out.uniprot.txt \
        --isoform-override uniprot


## Direct Database Annotation
If you have data already loaded into a cBioPortal database but did not properly
annotate it or discovered issues later, you can use the `databaseAnnotator`
utility to fix it.

You will need to specify some database connection parameters inside the
`application.properties` file located in
`databaseAnnotator/src/main/resources`. Once this is done, build the project
using maven and run like so:

    $JAVA_HOME/bin/java -jar databaseAnnotator/target/databaseAnnotator-*.jar \
        --isoform <mskcc or uniprot>

As with the above tool, running the jar without any arguments or by providing
the optional parameter `-h` will bring up the full usage statement. You can
also specify a single study or set of studies to annotate by using the
`--studies` parameter.

## Annotator
The `annotator` module is the client code that makes calls to the Genome Nexus
server and interprets the response. The other two modules use this as a
dependency.

## Updating the Genome Nexus Annotation Pipeline
The annotation pipeline uses models brought in by the auto-generated **genome-nexus-java-api-client** [here](https://github.com/averyniceday/genome-nexus-annotation-pipeline/blob/2356cad06aa602ee423b08d5792c50e903200a1d/pom.xml#L77) and [here](https://github.com/averyniceday/genome-nexus-annotation-pipeline/blob/2356cad06aa602ee423b08d5792c50e903200a1d/pom.xml#L82) . 

In the event of an update to the Genome Nexus server (e.g adding to an existing model), the `genome-nexus-java-api-client` must be regenerated and propagated to this codebase. To generate the newest `genome-nexus-java-api-client`, refer to instructions [here](https://github.com/genome-nexus/genome-nexus-java-api-client/blob/master/README.md). 

Once updated, update the pom with the newest commit hash from the `genome-nexus-java-api-client` codebase.
