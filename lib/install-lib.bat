call mvn install:install-file -Dfile=gs-all-6.2.2.jar -DgroupId=org.compass-project -DartifactId=gs-all -Dversion=6.6.2 -Dpackaging=jar
call mvn install:install-file -Dfile=gae-all-1.0.jar -DgroupId=org.compass-project -DartifactId=gae-all -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=fscontext.jar -DgroupId=com.sun.jndi -DartifactId=fscontext -Dversion=1.2-beta-3 -Dpackaging=jar
call mvn install:install-file -Dfile=providerutil-1.2.1.jar -DgroupId=com.sun.jndi -DartifactId=providerutil -Dversion=1.2 -Dpackaging=jar
call mvn install:install-file -Dfile=tim-concurrent-collections-1.0.2.jar -DgroupId=tim-concurrent -DartifactId=collections -Dversion=1.0.2 -Dpackaging=jar