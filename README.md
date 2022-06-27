Making components: 

When updating Hotelbeds XSD (in source), need to regenerate the java code.  Run :
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-10.0.2.jdk/Contents/Home
ant build.xml


When generating hotelbeds activities:
curl "https://bitbucket.org/vmavromatis/apitude-openapi/raw/06f5dc2ef14a4f2a40f1a3a248f95d611238ea00/OpenAPI-Activity-ContentAPI-3.0.yaml" -o OpenAPI-Activity-ContentAPI-3.0.yaml
./src/main/scripts/go.sh generate --api-package com.hotelbeds.activities.api -g java --model-package com.hotelbeds.activities.model -o hbactivities -v -i OpenAPI-Activity-ContentAPI-3.0.yaml --artifact-id hotelbeds-activities --group-id com.hotelbeds.activities.openapi --artifact-version 1.0.0
cd hbactivities 
mvn clean package
mvn install:install-file -Dfile=target/hotelbeds-activities-1.0.0.jar  -DpomFile=pom.xml
mvn deploy:deploy-file -DpomFile=pom.xml -Dfile=target/hotelbeds-activities-1.0.0.jar -DrepositoryId=com.hotelbeds.activities -Durl=https://mymavenrepo.com/repo/BD04hfwp7O4dTroU9gDP/
