package ba.unsa.etf.zavrsnirad.controller;

import ba.unsa.etf.zavrsnirad.dto.JRXMLColumn;
import ba.unsa.etf.zavrsnirad.dump.PrintReport;
import ba.unsa.etf.zavrsnirad.utils.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainController {

    @FXML
    public VBox vbox;
    @FXML
    public VBox mainVBox;
    @FXML
    public TextField databaseStringTextField;
    @FXML
    public TextArea sqlQueryTextArea;
    @FXML
    public HTMLEditor htmlEditor;

    public void next() {
        var backup = new ArrayList<>(vbox.getChildren());
        vbox.getChildren().clear();
        var children = new ArrayList<Node>();
        Label titleLabel = new Label(MyResourceBundle.getResourceBundle().getString("titleTextField"));
        children.add(titleLabel);
        TextField titleTextField = new TextField();
        children.add(titleTextField);

        Label subtitleLabel = new Label(MyResourceBundle.getResourceBundle().getString("subtitleTextField"));
        children.add(subtitleLabel);
        TextField subtitleTextField = new TextField();
        children.add(subtitleTextField);


        Button back = new Button(MyResourceBundle.getResourceBundle().getString("Back"));
        back.getStyleClass().add("buttonstylesecondary");
        back.setOnAction((EventHandler<ActionEvent>) event -> {
            vbox.getChildren().clear();
            vbox.getChildren().addAll(backup);
        });
        back.setMaxWidth(Double.MAX_VALUE);
        back.setPrefWidth(Region.USE_COMPUTED_SIZE);
        Button next = new Button(MyResourceBundle.getResourceBundle().getString("Next"));
        next.getStyleClass().add("buttonstyle");
        next.setOnAction((EventHandler<javafx.event.ActionEvent>) event -> {
            if(Objects.equals(titleTextField.getText(), ""))
                ReportData.setReportTitle(null);
            else
                ReportData.setReportTitle(titleTextField.getText());

            if (Objects.equals(subtitleTextField.getText(), ""))
                ReportData.setReportSubtitle(null);
            else
                ReportData.setReportSubtitle(subtitleTextField.getText());

            ReportData.setReportQuery(sqlQueryTextArea.getText());
            if(createReport()) {
                nextWindow();
            };

        });
        next.setMaxWidth(Double.MAX_VALUE);
        next.setPrefWidth(Region.USE_COMPUTED_SIZE);
        next.setDefaultButton(true);
        children.add(back);
        children.add(next);
        vbox.getChildren().addAll(children);
    }

    private boolean createReport() {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(ReportData.getReportQuery())){

            ResultSet resultSet = preparedStatement.executeQuery();

            String filePath = Objects.requireNonNull(getClass().getResource("/jrxml/template.jrxml")).getFile();
            JRXMLCreatorFromSQL jrxmlCreatorFromSQL = new JRXMLCreatorFromSQL(filePath, FilePath.JRXML_DESC_FILE_NAME.getFullPath());
            //JRXMLCreatorFromSQL jrxmlCreatorFromSQL = new JRXMLCreatorFromSQL(filePath, "C:/Users/pp/Desktop/destination.jrxml");
            List<JRXMLColumn> jrxmlColumnList = new ArrayList<>();
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            int count = rsMetaData.getColumnCount();
            for(int i = 1; i<=count; i++) {
                jrxmlColumnList
                        .add(new JRXMLColumn(rsMetaData.getColumnName(i), Utils.getClassName(rsMetaData.getColumnType(i))));

            }
            ReportData.setReportColumns(jrxmlColumnList);
            jrxmlCreatorFromSQL.createJRXML();
            return true;
        } catch (SQLException throwables) {
            Utils.throwAlert("errorTitle", "pleaseTryAgain", "sqlException");
            return false;
        } catch (XMLStreamException | IOException e) {
            Utils.throwAlert("errorTitle", "pleaseTryAgain", "cantConvert");
            return false;
        }
    }

    private void nextWindow() {
        htmlEditor = new HTMLEditor();
        htmlEditor.setPrefHeight(mainVBox.getHeight() * 2 / 3);
        generateHtmlCodeFromJRXML();

        Button previewJRXML = new Button("Preview JRXML");
        previewJRXML.getStyleClass().add("buttonstyle");
        previewJRXML.setOnAction(event -> {
            if(produceCode()) {
                try {
                    PrintReport.showReport(DatabaseConnection.getInstance().getConnection());
                } catch (JRException e) {
                    e.printStackTrace();
                }
            }

        });
        previewJRXML.setMaxWidth(Double.MAX_VALUE);
        previewJRXML.setPrefWidth(Region.USE_COMPUTED_SIZE);

        Button downloadGeneratedJRXML = new Button("Download generated JRXML");
        downloadGeneratedJRXML.getStyleClass().add("buttonstyle");
        downloadGeneratedJRXML.setOnAction(arg0 -> {
            if(produceCode()) {
                downloadJRXML();
            }

        });
        downloadGeneratedJRXML.setMaxWidth(Double.MAX_VALUE);
        downloadGeneratedJRXML.setPrefWidth(Region.USE_COMPUTED_SIZE);

        VBox buttonVBox = new VBox();
        buttonVBox.getChildren().addAll(downloadGeneratedJRXML, previewJRXML);
        buttonVBox.setSpacing(10);
        buttonVBox.setAlignment(Pos.CENTER);
        buttonVBox.setPadding(new Insets(10, 10, 10, 10));

        mainVBox.getChildren().clear();
        mainVBox.getChildren().addAll(htmlEditor, buttonVBox);
    }

    private boolean produceCode() {
        try {
            new JRXMLCreatorFromHTML(htmlEditor.getHtmlText(),
                    FilePath.JRXML_DESC_FILE_NAME.getFullPath(), FilePath.FINAL_FILE_NAME.getFullPath());
            return true;
        } catch (XMLStreamException | IOException e) {
            Utils.throwAlert("errorTitle", "pleaseTryAgain", "cantConvert");
            return false;
        }
    }

    public void generateHtmlCodeFromJRXML() {
        try {
            // Paths
            String reportSrcJrxmlFile = FilePath.JRXML_DESC_FILE_NAME.getFullPath();
            String reportSrcJasperFile = FilePath.JAPSER_SRC_FILE_NAME.getFullPath();
            String reportHtmlDestFile = FilePath.HMTL_DEST_FILE_NAME.getFullPath();

            JasperCompileManager.compileReportToFile(reportSrcJrxmlFile, reportSrcJasperFile);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(reportSrcJasperFile);

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(jasperReport, new HashMap<>(), DatabaseConnection.getInstance().getConnection());

            JasperExportManager.exportReportToHtmlFile(jasperPrint, reportHtmlDestFile);
            StringBuilder contentBuilder = new StringBuilder();
            try {
                BufferedReader in = new BufferedReader(new FileReader(reportHtmlDestFile));
                String str;
                while ((str = in.readLine()) != null) {
                    contentBuilder.append(str);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String content = contentBuilder.toString();
            htmlEditor.setHtmlText(content);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    private void setHTMLContent() {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(FilePath.HMTL_DEST_FILE_NAME.getFullPath()))) {
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = contentBuilder.toString();
        htmlEditor.setHtmlText(content);
    }

    private void downloadJRXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(MyResourceBundle.getString ("ChooseFile"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(MyResourceBundle.getString ("TextFile"), "*.jrxml"));
        File output = fileChooser.showSaveDialog(mainVBox.getScene().getWindow());

        if(output == null) return;
        try {
            File input = new File(FilePath.FINAL_FILE_NAME.getFullPath());
            copyContent(input, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyContent(File a, File b) throws IOException {
        FileInputStream in = null;

        try (FileOutputStream out = new FileOutputStream(b)) {
            int n;
            in = new FileInputStream(a);
            while ((n = in.read()) != -1) {
                out.write(n);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }


}
