export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-11.jdk/Contents/Home
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-10.0.2.jdk/Contents/Home
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_72.jdk/Contents/Home
rm -rf debug*
java  -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog -Dorg.apache.commons.logging.simplelog.log.org.apache.http.wire=DEBUG -Dorg.apache.commons.logging.simplelog.log.org.apache.http=DEBUG -Dlogging.config=logback.xml  -jar target/eroamservice-0.0.1-SNAPSHOT.jar


