R. Saini
The robotic Saini.

You can invite my bot instead of running it locally, in my opnion thats better.

However, if you insist on running it, all you really need to do is run:
****************
*  mvn clean   *
****************

and then:

***************
* mvn package *
***************
to obtain your JAR file.

Afterwards, you need to (obviously) be able to run it. The following allows you to do so: 

-------------------------------
java -jar <Your jar file name> 
-------------------------------

This command should be put in a bash (or batch file depending on OS). The config file requirements are in Config.java. Once that is set up,
everything should be running as expected.
