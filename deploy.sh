project=TCC_Projeto-1.0-SNAPSHOT
server=ws

/home/jouwee/apache-tomcat-8.0.27/bin/shutdown.sh
pkill java

rm -rf target

mvn package

webapps_dir=/home/jouwee/apache-tomcat-8.0.27/webapps
# Remove existing assets (if any)
rm -rf $webapps_dir/$server
# Copy WAR file into place
cp target/$project.war $webapps_dir/$server.war
# Restart tomcat

/home/jouwee/apache-tomcat-8.0.27/bin/startup.sh