package org.iotp.server.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;


/**
 */
public class MiscUtils {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static String missingProperty(String propertyName) {
        return "The " + propertyName + " property need to be set!";
    }

    public static HashFunction forName(String name) {
        switch (name) {
            case "murmur3_32":
                return Hashing.murmur3_32();
            case "murmur3_128":
                return Hashing.murmur3_128();
            case "crc32":
                return Hashing.crc32();
            case "md5":
                return Hashing.md5();
            default:
                throw new IllegalArgumentException("Can't find hash function with name " + name);
        }
    }
}
