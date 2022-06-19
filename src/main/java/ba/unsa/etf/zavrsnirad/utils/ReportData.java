package ba.unsa.etf.zavrsnirad.utils;

import ba.unsa.etf.zavrsnirad.dto.JRXMLColumn;

import java.util.List;

public class ReportData {
    private static String REPORT_TITLE;
    private static String REPORT_SUBTITLE;
    private static List<JRXMLColumn> REPORT_COLUMNS;
    private static String REPORT_QUERY;

    public static String getReportTitle() {
        return REPORT_TITLE;
    }

    public static void setReportTitle(String reportTitle) {
        REPORT_TITLE = reportTitle;
    }

    public static String getReportTitleStyleName() {
        return "title_" + REPORT_TITLE;
    }

    public static String getReportSubtitle() {
        return REPORT_SUBTITLE;
    }

    public static void setReportSubtitle(String reportSubtitle) {
        REPORT_SUBTITLE = reportSubtitle;
    }

    public static String getReportSubTitleStyleName() { return  "subtitle_" + REPORT_SUBTITLE; }

    public static List<JRXMLColumn> getReportColumns() {
        return REPORT_COLUMNS;
    }

    public static void setReportColumns(List<JRXMLColumn> reportColumns) {
        REPORT_COLUMNS = reportColumns;
    }

    public static String getReportQuery() {
        return REPORT_QUERY;
    }

    public static void setReportQuery(String reportQuery) {
        REPORT_QUERY = reportQuery;
    }
}
