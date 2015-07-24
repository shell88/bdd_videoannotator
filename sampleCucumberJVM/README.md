# BDD-Videoannotator - Sample Project for Cucumber-JVM
This is a sample project which requires minimal prerequisites to show the use of bdd_videoannotator
with Cucumber JVM (Java). It searches for the project on github using Google. The test will fail as there is no appropriate link on the first result page. As you will see, the annotated screencast is more meaningful than the output on the command line to identify the problem in this case.

# Prerequisites
- Java JRE 1.7* (other versions may also work)
  * set also Java to PATH (check with `java -version`)
- Maven 3.2.5 (other versions may also work)
  * set also Maven to PATH (check with `mvn --version`)
- Libraries from bdd_videoannotator:
  * Download [bdd-videoannotator-server 0.1-beta.2](https://github.com/shell88/bdd_videoannotator/releases/download/v0.1-beta.2/bdd-videoannotator-server-0.1.jar) 
  * Download [bdd-videoannotator-java 0.1-beta.2](https://github.com/shell88/bdd_videoannotator/releases/download/v0.1-beta.2/bdd-videoannotator-java-0.1.jar)
 * Install to local repository
```sh
 mvn install:install-file -Dfile=bdd-videoannotator-server-0.1.jar -DgroupId=com.github.shell88 -DartifactId=bdd-videoannotator-server -Dversion=0.1-beta.2 -Dpackaging=jar
```
```sh
mvn install:install-file -Dfile=bdd-videoannotator-java-0.1.jar -DgroupId=com.github.shell88 -DartifactId=bdd-videoannotator-java -Dversion=0.1-beta.2 -Dpackaging=jar
```
- Firefox Browser installed (Will be used by Selenium WebDriver; Tested Version 37.0.1)

# Running

```sh
mvn clean test
```
You can also configure the plugin from the config file in `src/test/resources/adapter_config.properties`.

#Output
In subfolder `output_bddvideoannotator` you will get an annotationFile (.eaf) and a screencast (.avi).
To view the annotationFile you have to install [ELAN](https://tla.mpi.nl/tools/tla-tools/elan/download/).
 


