COMPASS, Release 2.3.0 beta 1
-----------------------------
http://www.compass-project.org


1. INTRODUCTION

Compass is a powerful, transactional Object to Search Engine Mapping (OSEM) Java framework. 
Compass allows you to declaratively map your Object domain model to the underlying Search Engine, 
synchronizing data changes between Index and different datasources. Compass provides a high level 
abstraction on top of the Lucene low level API. Compass also implements fast index operations and 
optimization and introduces transaction capabilities to the Search Engine.

2. RELEASE INFO

This branch has removed the ant build support  and use Maven instead.

Execute the install-lib.bat at lib/ to install the necessary dependence which is required when building project but not available in the public repository
Have upgraded some dependence's version
execute maven clean install to build the project.
Run mvn tomcat7:run cat compass-demo

Good luck.

   