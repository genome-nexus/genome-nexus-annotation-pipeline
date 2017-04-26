Genome Nexus Annotation Pipeline
=========
These tools allow for annotation of genomic variants from a MAF for import into the cBioPortal using [Genome Nexus](http://genomenexus.org)

MAF Annotation
-----------
The `annotationPipeline` module is a command line tool to annotate a maf using genome nexus. 

To use it, build the project using maven and run it like so:
    
    mvn clean install
    $JAVA_HOME/bin/java -jar annotationPipeline/target/annotationPipeline-0.1.0.jar --filename <INPUT_MAF> --outputFilename <OUTPUT DESTINATION> --isoformOverride <mskcc or uniprot>
    
You can choose to replace the gene symbols in the new maf by the gene symbols found by Genome Nexus by supplying the `-r` optional parameter. To output error reporting to a file, supply the `-e` option and supply a location. By running the jar without any arguments or by providing the optional parameter `-h` you can view the full usage statement. If you have your own installation of Genome Nexus, you can point to it by modifying the `application.properties` file located in `annotationPipeline/src/main/resources`.

Direct Database Annotation
-----------
If you have data already loaded into a cBioPortal database but did not properly annotate it or discovered issues later, you can use the `databaseAnnotator` utility to fix it.

You will need to specify some database connection parameters inside the `application.properties` file located in `databaseAnnotator/src/main/resources`. Once this is done, build the project using maven and run like so:

    $JAVA_HOME/bin/java -jar databaseAnnotator/target/databaseAnnotator-0.1.0.jar --isoform <mskcc or uniprot>

As with the above tool, running the jar without any arguments or by providing the optional parameter `-h` will bring up the full usage statement. You can also specify a single study or set of studies to annotate by using the `--studies` parameter.

Annotator
-----------
The `annotator` module is the client code that makes calls to the Genome Nexus server and interprets the response. The other two modules use this as a dependency.
