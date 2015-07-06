# BDD-Videoannotator - Sample Project for Behat
This is a sample project which requires minimal prerequisites to show the use of bdd_videoannotator
with Behat (PHP). It searches for the project on github using Google. The test will fail as there is no appropriate link on the first result page. As you will see, the annotated screencast is more meaningful than the output on the command line to identify the problem in this case.

# Prerequisites
- Composer 1.0-dev (other versions may also work)
- Java JRE 1.8* (other versions may also work)
  * set also Java to PATH (check with `java -version`)
- PHP CLI 5.6.7 (tested version, others may also work)  
  PHP-Extensions that have to be enabled:
  * soap
  * curl 
  * mbstring
- Firefox Browser installed (Will be used by Selenium WebDriver; Tested Version 37.0.1)

#Running
-Install dependencies using composer
```
composer install
```
-Start selenium standalone-server
```sh
java -jar vendor/netwing/selenium-server-standalone/selenium-server-standalone-<version>.jar
```
-Execute the tests
```sh
bin\behat -f bdd_videoannotator\bddadapters\BehatReportingAdapter,pretty
```
You can also configure the plugin from the config file in `vendor/shell88/bdd_videoannotator/bdd_videoannotator/bddadapters/adapter_config.ini`.

#Output
You ill get an annotationFile (.eaf) and a screencast (.avi) in the project directory.
To view the annotationFile you have to install [ELAN](https://tla.mpi.nl/tools/tla-tools/elan/download/).
 


