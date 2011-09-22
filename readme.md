The code builds with Ant, and was tested on FreeBSD, Tomcat 6.0.13 and 6.0.24 with Terracotta 3.1.0. You don't need Terracotta to run it, it functions just fine without it. The code publishes the image id's to the Terracotta cache so that you can cluster the servers but that's not necessary for one server. The servlet is JMX enabled and uses the Tomcat MBean server which is injected by Spring.

You can download the required .jars from [here][dl]

[dl]: http://www.ufp.com/image-operations-lib.tar.gz

and expand them into web/WEB-INF/lib

You build the .war file with

    $ANT_HOME/bin/ant clean dist

You install the .war file with

    cp dist/demo-<TIMESTAMP>.war $CATALINA_HOME/webapps/demo.war

You POST images to the server like this:

    curl -v -X POST --data "http://www.springsource.com/files/spring09_logo.png" -H"Content-type: text/plain" http://<server_address>:<server_port>/demo/demoservice/add

and then retrieve them with operations applied like this:

    http://<server_address>:<server_port>/demo/demoservice/1/raw
    http://<server_address>:<server_port>/demo/demoservice/1/blur
    http://<server_address>:<server_port>/demo/demoservice/1/grey

The servlet is a RESTful servlet and is implemented with Jersey (a JSR-311 implementation). The code is very simple (Jersey does A LOT of the work for you) and entirely encapsulated in DemoResource. Jersey is "enabled" with a SpringServlet configured in web.xml, set to scan packages for Resources and the package to scan is set as a property of the servlet. Spring is initialized the standard way with a listener.

An OperationManager is injected via Spring. The OperationManager has its Operations set via Spring config as a map of Operation beans (Grey, Blur and Raw). The OperationManager is NotificationPublisherAware so that Spring will inject the NotificationPublisher in the container. The OperationManager sends a notification each time a URL is added. There is also a CacheService injected, which is just a thin layer of Map operations on a Terracotta DistributedCache. The OperationManager uses the CacheService to publish the host and id's of images it has. The actual Operations use the Java 2D api to perform image operations. The BlurOperation is slow but I found the following site that goes into great detail about blur operations <http://www.jhlabs.com/ip/blurring.html>.
