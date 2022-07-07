package ba.unsa.etf.zavrsnirad.utils;

import javafx.scene.control.Alert;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.*;

public class Utils {

    public static String getClassName(int classType) {
        switch (classType) {
            case 2003: return Array.class.getName();
            case 2004: return Blob.class.getName();
            case 15: return Boolean.class.getName();
            case 1: return Character.class.getName();
            case 2005: return Clob.class.getName();
            case 91: return Date.class.getName();
            case 8: return  Double.class.getName();
            case 3: return  Double.class.getName();
            case 2: return  Double.class.getName();
            case 6: return Float.class.getName();
            case -7: return Integer.class.getName();
            case -5: return Integer.class.getName();
            case 4: return Integer.class.getName();
            case 5: return Integer.class.getName();
            case 0: return ObjectUtils.Null.class.getName();
            case 2014: return Timestamp.class.getName();
            case 93: return Timestamp.class.getName();
            case 92: return Time.class.getName();
            default: return String.class.getName();
        }
    }

    public static void throwAlert(String titleKey, String headerKey, String contentTextKey) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(MyResourceBundle.getResourceBundle().getString(titleKey));
        alert.setHeaderText(MyResourceBundle.getResourceBundle().getString(headerKey));
        alert.setContentText(MyResourceBundle.getResourceBundle().getString(contentTextKey));

        alert.showAndWait();
    }
}
