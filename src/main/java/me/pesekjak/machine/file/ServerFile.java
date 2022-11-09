package me.pesekjak.machine.file;

import java.io.InputStream;

/**
 * File of the server
 */
public interface ServerFile {

    /**
     * @return name of the file
     */
    String getName();

    /**
     * @return stream of the file from the resources
     */
    InputStream getOriginal();

}
