###Build warning###
As Oracle license does not allow to distribute Oracle JDBC driver and it is not published in any public 
Maven repository (and it cannot be), one needs to manually install ojdbc7.jar into local Maven repository to be able to build this DPU.

Oracle JDBC driver JAR (ojdbc7.jar) can be downloaded via Oracle site http://www.oracle.com/technetwork/database/features/jdbc/default-2280470.html

This DPU uses 12.1.0.2.0 version. 

To be able to build this DPU, this JAR must be either deployed to private artifactory or installed into local
Maven repository via mvn install:install-file.
In order to make build work without changes in pom, JAR must be depoloyed / installed with these values:

|              |            |
|--------------|------------|
|group-id      |com.oracle  |
|artifact-id   |ojdbc7      |
|version       |12.1.0.2.0  |

To install JAR to your local repository, use following command:

	mvn install:install-file -Dfile=<dir>/ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.2.0 -Dpackaging=jar

***
