package model.utils;

import java.io.*;

import static java.lang.Thread.currentThread;

/**
 * Created by stas on 10/16/17.
 */
public class DbUtils {

    public static InputStream getResourceAsInputStream(String resource) throws IOException {
        ClassLoader classloader = currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(resource);
    }
}



