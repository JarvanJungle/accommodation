export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-11.jdk/Contents/Home
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-10.0.2.jdk/Contents/Home
export ANT_OPTS="-Xmx1024m -XX:MaxPermSize=512m"
mvn -Dmaven.test.skip=true clean package
