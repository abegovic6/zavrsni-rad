package ba.unsa.etf.zavrsnirad.dump;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class PrintReport extends JFrame {
    public static void showReport(Connection conn) throws JRException {
        String reportSrcFile = "C:/Users/pp/Desktop/destination.jrxml";

        JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
        // Fields for resources path
        HashMap<String, Object> parameters = new HashMap<>();
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        list.add(parameters);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setVisible(true);
    }
}

