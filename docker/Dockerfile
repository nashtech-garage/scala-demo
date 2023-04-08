FROM tomcat:8.5-jre8-alpine

RUN rm -rf /usr/local/tomcat/webapps/ROOT* /usr/local/tomcat/webapps/docs* /usr/local/tomcat/webapps/examples* /usr/local/tomcat/webapps/host-manager*

COPY data/tomcat/manager-context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml

#COPY data/tomcat/bin/jrebel.jar /usr/local/tomcat/bin/
#COPY data/tomcat/bin/libjrebel64.so /usr/local/tomcat/bin/
#COPY data/tomcat/bin/libjrebel64.dylib /usr/local/tomcat/bin/
#COPY data/tomcat/bin/setenv.sh /usr/local/tomcat/bin/

COPY data/tomcat/conf/context.xml /usr/local/tomcat/conf/
COPY data/tomcat/conf/tomcat-users.xml /usr/local/tomcat/conf/

COPY data/tomcat/lib/logback.xml /usr/local/tomcat/lib/

#COPY ./data/tomcat/lib/memcached.properties /usr/local/tomcat/lib/
#COPY ./data/tomcat/lib/tabula.properties /usr/local/tomcat/lib/
#COPY ./data/tomcat/lib/tabula-sso-config.xml /usr/local/tomcat/lib/

