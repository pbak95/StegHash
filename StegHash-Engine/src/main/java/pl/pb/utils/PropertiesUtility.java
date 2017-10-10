package pl.pb.utils;



import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Patryk on 2017-10-08.
 */
public class PropertiesUtility {

    private final Properties properties = new Properties();

    private PropertiesUtility () {
        InputStream input = null;
        try {
            String[] path = this.getClass().getClassLoader().getResource("").getPath().split("target");
            input = new FileInputStream(path[0] + "config.properties");
            properties.load(input);
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

    public String getProperty (String key) {
        return properties.getProperty(key);
    }

    public int getIntegerProperty (String key) {
        String propStr = properties.getProperty(key);
        return tryParseInt(propStr) ? Integer.parseInt(propStr) : -1;
    }

    public  boolean tryParseInt (String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class PropertiesUtilityHolder {

        private static final PropertiesUtility INSTANCE = new PropertiesUtility();
    }

    public static PropertiesUtility getInstance() {
        return PropertiesUtilityHolder.INSTANCE;
    }
}
