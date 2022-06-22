package ba.unsa.etf.zavrsnirad.utils;

import javafx.scene.control.Alert;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.*;

public class Utils {

    public static String getClassName(int classType) {
        return switch (classType) {
            case 2003 -> Array.class.getName();
            case 2004 -> Blob.class.getName();
            case 15 -> Boolean.class.getName();
            case 1 -> Character.class.getName();
            case 2005 -> Clob.class.getName();
            case 91 -> Date.class.getName();
            case 8, 3, 2 -> Double.class.getName();
            case 6 -> Float.class.getName();
            case -7, 4, 5, -5 -> Integer.class.getName();
            case 0 -> ObjectUtils.Null.class.getName();
            case 93, 2014 -> Timestamp.class.getName();
            case 92 -> Time.class.getName();
            default -> String.class.getName();
        };
    }

    public static void throwAlert(String titleKey, String headerKey, String contentTextKey) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(MyResourceBundle.getResourceBundle().getString(titleKey));
        alert.setHeaderText(MyResourceBundle.getResourceBundle().getString(headerKey));
        alert.setContentText(MyResourceBundle.getResourceBundle().getString(contentTextKey));

        alert.showAndWait();
    }
}
