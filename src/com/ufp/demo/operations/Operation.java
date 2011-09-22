package com.ufp.demo.operations;

import java.io.InputStream;

public interface Operation {
    public InputStream process(InputStream inputStream);
}