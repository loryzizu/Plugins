# E-RelationalFromSql #
----------

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

###General###

|                              |                                                                             |
|------------------------------|------------------------------------------------------------------------------|
|**Name:**                     |E-RelationalFromSql                                                           |
|**Description:**              |Extracts data from external relational database tables into internal database |
|                              |                                                                              |
|**DPU class name:**           |RelationalFromSql                                                             | 
|**Configuration class name:** |RelationalFromSqlConfig_V2                                                    |
|**Dialogue class name:**      |RelationalFromSqlVaadinDialog                                                 |
|**WARNING:**                  |This DPU is a part of UV optional functionality (relational data) and in      |
|                              |current implementation it does not fully follow the UV philosophy as user     |
|                              |has control of physical database tables. See details in DPU class             |

***

###Configuration parameters###

|Parameter                           |Description                                                              |
|------------------------------------|-------------------------------------------------------------------------|
|**Database type:**                  |Database type: PostgreSQL, Oracle, MySQL, MS SQL                         |
|**Database host:**                  |Database host                                                            |
|**Database port:**                  |Database port                                                            |
|**Database name:**                  |Database name (for ORACLE SID name)                                      |
|**Instance name:**                  |Name of database instance (optional) - ONLY FOR MS SQL                   |
|**User name:**                      |Database user name                                                       |
|**User password:**                  |Database password                                                        |
|**Connect via SSL:**                |Use secured connection to the database                                   |
|**Truststore location:**            |Path to truststore file to be used to validate server's certificate      |
|                                    |(optional) If not filled, default Java truststore is used                |
|**Truststore password:**            |Truststore password (optional)                                           |
|**SQL query:**                      |SQL query to extract data from source database                           |
|**Target table name:**              |Table name used to internally store the extracted data                   |
|**Primary key columns:**            |Target table primary key (optional)                                      |
|**Indexed columns:**                |Target table indexed columns (optional)                                  |


***

### Inputs and outputs ###

|Name           |Type           |DataUnit           |Description                                  |
|---------------|---------------|-------------------|---------------------------------------------|
|outputTables   |o              |RelationalDataUnit |Extracted database tables                    |

***

### Version history ###

|Version          |Release notes               |
|-----------------|----------------------------|
|1.0.1-SNAPSHOT   |Update for helpers 2.0.0    |
|1.0.0-SNAPSHOT   |N/A                         |


***

### Developer's notes ###

|Author           |Notes                           |
|-----------------|--------------------------------|
|N/A              |N/A                             | 
