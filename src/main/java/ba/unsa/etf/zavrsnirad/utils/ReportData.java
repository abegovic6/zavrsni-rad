package ba.unsa.etf.zavrsnirad.utils;

import ba.unsa.etf.zavrsnirad.dto.JRXMLColumn;

import java.util.List;

public class ReportData {
    private static String reportTitle;
    private static String reportSubtitle;
    private static List<JRXMLColumn> reportColumns;
    private static String reportQuery;
    private static String databaseConnectionString;

    private ReportData() {
    }

    public static String getReportTitle() {
        return reportTitle;
    }

    public static void setReportTitle(String reportTitle) {
        ReportData.reportTitle = reportTitle;
    }

    public static String getReportTitleStyleName() {
        return "title_" + reportTitle.replace(' ', '_');
    }

    public static String getReportSubtitle() {
        return reportSubtitle;
    }

    public static void setReportSubtitle(String reportSubtitle) {
        ReportData.reportSubtitle = reportSubtitle;
    }

    public static String getReportSubTitleStyleName() { return  "subtitle_" + reportSubtitle.replace(' ', '_'); }

    public static List<JRXMLColumn> getReportColumns() {
        return reportColumns;
    }

    public static void setReportColumns(List<JRXMLColumn> reportColumns) {
        ReportData.reportColumns = reportColumns;
    }

    public static String getReportQuery() {
        return reportQuery;
    }

    public static void setReportQuery(String reportQuery) {
        ReportData.reportQuery = reportQuery;
    }

    public static String getDatabaseConnectionString() {
        return databaseConnectionString;
    }

    public static void setDatabaseConnectionString(String databaseConnectionString) {
        ReportData.databaseConnectionString = databaseConnectionString;
    }
}
