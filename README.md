# BDD-Videoannotator
BDD-Videoannotator enables you to log test executions as an annotated screencast. As the name implies its main focus is on 
BDD-Frameworks like [Cucumber-JVM](https://github.com/cucumber/cucumber-jvm). Especially when you execute acceptance-tests against
the real UI (see [SeleniumHQ]https://github.com/SeleniumHQ/selenium) for example), it can be useful to capture a video 
(e.g. in case of an unexpected error).
Besides recording the screencast, BDD-Videoannotator will also align the text of the currently executed Gherkin-Step to the video.

To support different BDD-Frameworks it consists of a server component and an adapter component. 
Currently following BDD-Frameworks are supported:
- Cucumber-JVM (see [bdd_videoannotator/java](https://github.com/shell88/bdd_videoannotator/tree/master/java))
- Behat (see [bdd_videoannotator/php](https://github.com/shell88/bdd_videoannotator/tree/master/php))

#Basic Usage
BDD-Videoannotator will export a video file and a metadata-file containing the annotations (File extension ".eaf").
To view the annotated video you have to open the ".eaf"-File with [ELAN](https://tla.mpi.nl/tools/tla-tools/elan/).

#Issues
To improve bdd_videoannotator please give your feedback by opening issues at the [Github Issue Tracker](https://github.com/shell88/bdd_videoannotator/issues).

