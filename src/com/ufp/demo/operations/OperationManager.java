package com.ufp.demo.operations;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.io.InputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;

import javax.management.Notification;

import com.ufp.demo.services.CacheService;

@ManagedResource(objectName="com.ufp.demo:name=OperationManager", description="Operation Manager", currencyTimeLimit=15)
public class OperationManager implements NotificationPublisherAware {
    private static Logger logger = Logger.getLogger(OperationManager.class);
    private NotificationPublisher notificationPublisher;
    private CacheService cacheService;
    private String host = null;

    public String getHost() {
	return host;
    }

    public void setHost(String host) {
	logger.debug("setting host to " + host);
	this.host = host;
    }

    public boolean isHostSet() {
	return (host != null);
    }

    private Map<String, Operation> operations = new HashMap<String, Operation>();
    private List<String> URLs = new ArrayList<String>();

    public void setOperations(Map<String, Operation> operations) {
	this.operations = operations;
    }

    public void setCacheService(CacheService cacheService) {
	this.cacheService = cacheService;
    }

    @ManagedOperation(description="Current operation names") 
    public List<String> operationNames() {
	Set<String> set = operations.keySet();
	List<String> names = new ArrayList<String>();
	Iterator<String> iterator = set.iterator();

	while (iterator.hasNext()) {
	    names.add(iterator.next());
	}
	return names;
    }

    public void addURL(String url) {
	URLs.add(url);
	cacheService.put(host + "-" + URLs.size(), null);
	notificationPublisher.sendNotification(new Notification("add", this, URLs.size(), url));
    }

    public InputStream process(String name, int index) {
	InputStream inStream = null;
	if (URLs.size() >= index) {
	    String urlString = URLs.get(index-1);
	    try {
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		logger.debug("processing " + name + " operation");
		if (operations.containsKey(name)) {
		    inStream = operations.get(name).process(connection.getInputStream());
		} else {
		    logger.error("no operation named " + name);
		}
	    } catch (MalformedURLException mue) {
		logger.error(mue.getMessage(), mue);
		URLs.remove(index-1);
		cacheService.remove(host + "-" + index);
	    } catch (IOException ioe) {
		logger.error(ioe.getMessage(), ioe);
	    }
	}
	return inStream;
    }

    public void setNotificationPublisher(NotificationPublisher notificationPublisher) {
        this.notificationPublisher = notificationPublisher;
    }
}
