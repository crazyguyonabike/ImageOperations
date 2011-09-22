package com.ufp.demo.operations;

import java.io.InputStream;

public abstract class AbstractOperation implements Operation {
    private String name;

    public AbstractOperation() {
	this(null);
    }

    public AbstractOperation(String name) {
	this.name = name;
    }

    public String getName() {
	return this.name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public abstract InputStream process(InputStream inputStream);
}