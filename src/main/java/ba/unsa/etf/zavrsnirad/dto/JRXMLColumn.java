package ba.unsa.etf.zavrsnirad.dto;

public class JRXMLColumn {
    private String columnName;
    private String columnType;


    public JRXMLColumn(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
}
