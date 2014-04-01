package edu.yale.library.ladybird.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 */
public class Util {

    static Properties prop;

    static {
        prop = new Properties();
        InputStream input = null;

        try {
            input = Util.class.getResourceAsStream("/ladybird.properties");
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getProperty(String p) {
        return prop.getProperty(p);
    }
}
