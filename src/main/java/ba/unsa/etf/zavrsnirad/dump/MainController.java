package ba.unsa.etf.zavrsnirad.dump;

import ba.unsa.etf.zavrsnirad.utils.DatabaseConnection;
import ba.unsa.etf.zavrsnirad.dump.DynamicColumnDataSource;
import ba.unsa.etf.zavrsnirad.dump.DynamicReportBuilder;
import ba.unsa.etf.zavrsnirad.dump.PrintReport;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    public void generate() {
        try {
            var database = DatabaseConnection.getInstance();
            new PrintReport().showReport(database.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void runReport(List<String> columnHeaders, List<List<String>> rows) throws JRException {

        InputStream is = getClass().getResourceAsStream("../../../DynamicColumns.jrxml");
        JasperDesign jasperReportDesign = JRXmlLoader.load(is);
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder(jasperReportDesign, columnHeaders.size());
        reportBuilder.addDynamicColumns();
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperReportDesign);
        Map<String, Object> params = new HashMap<>();
        params.put("REPORT_TITLE", "Sample Dynamic Columns Report");
        DynamicColumnDataSource pdfDataSource = new DynamicColumnDataSource(columnHeaders, rows);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, pdfDataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, "/tmp/DynamicColumns.pdf");
    }

}
