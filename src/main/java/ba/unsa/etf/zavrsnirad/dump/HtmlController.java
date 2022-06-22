//package ba.unsa.etf.zavrsnirad.controller;
//
//import ba.unsa.etf.zavrsnirad.dump.CreateJRXMLReport;
//import ba.unsa.etf.zavrsnirad.utils.DatabaseConnection;
//import ba.unsa.etf.zavrsnirad.utils.FilePath;
//import ba.unsa.etf.zavrsnirad.dump.PrintReport;
//import ba.unsa.etf.zavrsnirad.utils.JRXMLCreatorFromHTML;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.fxml.FXML;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.control.Button;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.TextArea;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.web.HTMLEditor;
//import net.sf.jasperreports.engine.*;
//import net.sf.jasperreports.engine.util.JRLoader;
//
//import javax.xml.stream.XMLStreamException;
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.HashMap;
//
//public class HtmlController {
//    public VBox rightVBox;
//    public VBox leftVBox;
//    public HTMLEditor htmlEditor;
//
//    public HtmlController() {
//
//    }
//
//    @FXML
//    public void initialize () {
//        createLeftSide();
//        createRightSide();
//    }
//
//    private void createLeftSide() {
//        leftVBox.getStyleClass().add("colormain");
//    }
//
//    private void createRightSide() {
//
//        htmlEditor = new HTMLEditor();
//        htmlEditor.setPrefHeight(245);
//        generateHtmlCodeFromJRXML();
//
//        final TextArea htmlCode = new TextArea();
//        htmlCode.setWrapText(true);
//
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        scrollPane.setContent(htmlCode);
//        scrollPane.setFitToWidth(true);
//        scrollPane.setPrefHeight(180);
//        Button showHTMLButton = new Button("Produce HTML Code");
//        showHTMLButton.getStyleClass().add("buttonstyle");
//        showHTMLButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent arg0) {
//                htmlCode.setText(htmlEditor.getHtmlText());
//            }
//        });
//
//        Button generateJRXMLCode = new Button("Produce JRXML Code");
//        generateJRXMLCode.getStyleClass().add("buttonstyle");
//        generateJRXMLCode.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent arg0) {
//
//                try {
//                    new JRXMLCreatorFromHTML(htmlEditor.getHtmlText(),
//                                FilePath.JRXML_DESC_FILE_NAME.getFullPath(), FilePath.FINAL_FILE_NAME.getFullPath());
//                    StringBuilder contentBuilder = new StringBuilder();
//                    try {
//                        BufferedReader in = new BufferedReader(new FileReader(FilePath.FINAL_FILE_NAME.getFullPath()));
//                        String str;
//                        while ((str = in.readLine()) != null) {
//                            contentBuilder.append(str);
//                        }
//                        in.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    String content = contentBuilder.toString();
//                    htmlCode.setText(content);
//                } catch (XMLStreamException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        Button previewJRXML = new Button("Preview JRXML");
//        previewJRXML.getStyleClass().add("buttonstyle");
//        previewJRXML.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent arg0) {
//                try {
//                    PrintReport.showReport(DatabaseConnection.getInstance().getConnection());
//                } catch (JRException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
////        Button produceXMLCode = new Button("Get styles");
////        produceXMLCode.getStyleClass().add("buttonstyle");
////        produceXMLCode.setOnAction(new EventHandler<ActionEvent>() {
////            @Override public void handle(ActionEvent arg0) {
////                try {
////                    new JRXMLCreatorFromHTML(htmlEditor.getHtmlText(),
////                            FilePath.JRXML_DESC_FILE_NAME.getFullPath(), FilePath.FINAL_FILE_NAME.getFullPath());
////                } catch (XMLStreamException e) {
////                    e.printStackTrace();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
////        });
//        HBox hBox = new HBox();
//        hBox.getChildren().addAll(showHTMLButton, previewJRXML, generateJRXMLCode);
//        hBox.setSpacing(10);
//        hBox.setAlignment(Pos.CENTER);
//        hBox.setPadding(new Insets(10, 10, 10, 10));
//        rightVBox.getChildren().addAll(htmlEditor, hBox, scrollPane);
//    }
//
//    public void generateHtmlCodeFromJRXML() {
//        try {
//            // Paths
//            String reportSrcJrxmlFile = FilePath.JRXML_DESC_FILE_NAME.getFullPath();
//            String reportSrcJasperFile = FilePath.JAPSER_SRC_FILE_NAME.getFullPath();
//            String reportHtmlDestFile = FilePath.HMTL_DEST_FILE_NAME.getFullPath();
//
//
//            JasperCompileManager.compileReportToFile(reportSrcJrxmlFile, reportSrcJasperFile);
//            JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(reportSrcJasperFile);
//
//            JasperPrint jasperPrint
//                    = JasperFillManager.fillReport(jasperReport, new HashMap<>(), DatabaseConnection.getInstance().getConnection());
//
//            JasperExportManager.exportReportToHtmlFile(jasperPrint, reportHtmlDestFile);
//            StringBuilder contentBuilder = new StringBuilder();
//            try {
//                BufferedReader in = new BufferedReader(new FileReader(reportHtmlDestFile));
//                String str;
//                while ((str = in.readLine()) != null) {
//                    contentBuilder.append(str);
//                }
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String content = contentBuilder.toString();
//            htmlEditor.setHtmlText(content);
//        } catch (JRException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
