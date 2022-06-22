//package ba.unsa.etf.zavrsnirad.controller;
//
//import ba.unsa.etf.zavrsnirad.dto.JRXMLColumn;
//import ba.unsa.etf.zavrsnirad.utils.*;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.Region;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import org.apache.commons.lang3.ObjectUtils;
//
//import javax.xml.stream.XMLStreamException;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class StartController {
//
//    @FXML
//    public VBox vbox;
//    @FXML
//    public TextField databaseStringTextField;
//    @FXML
//    public TextArea sqlQueryTextArea;
//
//    private String title;
//    private String subtitle;
//
//    public void next() {
//
//        String query = sqlQueryTextArea.getText();
//        Pattern p = Pattern.compile("\\s*\\w+,");
//        Pattern p1 = Pattern.compile("\\s+\\w+\\s+from");
//
//        Matcher m = p.matcher(query);
//        Matcher m1=p1.matcher(query);
//
//        String colsOnly="";
//        while(m.find()){colsOnly+=(m.group().trim());}
//        while(m1.find()){colsOnly+=(m1.group().substring(0,m1.group().length()-4).trim());}
//        List<String> columns = List.of(colsOnly.split(","));
//        System.out.println(columns);
//
//        var backup = new ArrayList<>(vbox.getChildren());
//        vbox.getChildren().clear();
//        var children = new ArrayList<Node>();
//        Label titleLabel = new Label(MyResourceBundle.getResourceBundle().getString("titleTextField"));
//        children.add(titleLabel);
//        TextField titleTextField = new TextField();
//        children.add(titleTextField);
//
//        Label subtitleLabel = new Label(MyResourceBundle.getResourceBundle().getString("subtitleTextField"));
//        children.add(subtitleLabel);
//        TextField subtitleTextField = new TextField();
//        children.add(subtitleTextField);
//
//
//        Button back = new Button(MyResourceBundle.getResourceBundle().getString("Back"));
//        back.getStyleClass().add("buttonstylesecondary");
//        back.setOnAction((EventHandler<ActionEvent>) event -> {
//            vbox.getChildren().clear();
//            vbox.getChildren().addAll(backup);
//        });
//        back.setMaxWidth(Double.MAX_VALUE);
//        back.setPrefWidth(Region.USE_COMPUTED_SIZE);
//        Button next = new Button(MyResourceBundle.getResourceBundle().getString("Next"));
//        next.getStyleClass().add("buttonstyle");
//        next.setOnAction((EventHandler<javafx.event.ActionEvent>) event -> {
//            if(Objects.equals(titleTextField.getText(), ""))
//                ReportData.setReportTitle(null);
//            else
//                ReportData.setReportTitle(titleTextField.getText());
//
//            if (Objects.equals(subtitleTextField.getText(), ""))
//                ReportData.setReportSubtitle(null);
//            else
//                ReportData.setReportSubtitle(subtitleTextField.getText());
//
//            ReportData.setReportQuery(sqlQueryTextArea.getText());
//            createReport();
//            newWindow();
//        });
//        next.setMaxWidth(Double.MAX_VALUE);
//        next.setPrefWidth(Region.USE_COMPUTED_SIZE);
//        next.setDefaultButton(true);
//        children.add(back);
//        children.add(next);
//        vbox.getChildren().addAll(children);
//    }
//
//    private void createReport() {
//        try {
//            Connection connection = DatabaseConnection.getInstance().getConnection();
//            PreparedStatement preparedStatement = connection.prepareStatement(ReportData.getReportQuery());
//            ResultSet resultSet = preparedStatement.executeQuery();
//
//            String filePath = getClass().getResource("/jrxml/template.jrxml").getFile();
//            JRXMLCreatorFromSQL JRXMLCreatorFromSQL = new JRXMLCreatorFromSQL(filePath, FilePath.JRXML_DESC_FILE_NAME.getFullPath());
//            //JRXMLCreatorFromSQL JRXMLCreatorFromSQL = new JRXMLCreatorFromSQL(filePath, "C:/Users/pp/Desktop/destination.jrxml");
//            List<JRXMLColumn> jrxmlColumnList = new ArrayList<>();
//            ResultSetMetaData rsMetaData = resultSet.getMetaData();
//            int count = rsMetaData.getColumnCount();
//            for(int i = 1; i<=count; i++) {
//                System.out.println("kolona " + rsMetaData.getColumnName(i) );
//                jrxmlColumnList
//                        .add(new JRXMLColumn(rsMetaData.getColumnName(i), getClassName(rsMetaData.getColumnType(i))));
//
//            }
//
//            ReportData.setReportColumns(jrxmlColumnList);
//            JRXMLCreatorFromSQL.createJRXML();
//
//        } catch (SQLException throwables) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle(MyResourceBundle.getResourceBundle().getString("errorTitle"));
//            alert.setHeaderText(MyResourceBundle.getResourceBundle().getString("pleaseTryAgain"));
//            alert.setContentText(MyResourceBundle.getResourceBundle().getString("sqlException"));
//
//            alert.showAndWait();
//        } catch (XMLStreamException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void newWindow() {
//        try {
//            Node source = vbox;
//
//            Stage oldStage  = (Stage) source.getScene().getWindow();
//            Stage newStage = new Stage();
//
//            HtmlController htmlController = new HtmlController();
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/htmlinput.fxml"),
//                    MyResourceBundle.getResourceBundle());
//            loader.setController(htmlController);
//            newStage.setScene(new Scene(loader.load(), 800, 500));
//            newStage.setMinHeight(500);
//            newStage.setMinWidth(800);
//
//            oldStage.close();
//            newStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void setConnection() {
//
//    }
//
//    private String getClassName(int classType) {
//        return switch (classType) {
//            case 2003 -> Array.class.getName();
//            case 2004 -> Blob.class.getName();
//            case 15 -> Boolean.class.getName();
//            case 1 -> Character.class.getName();
//            case 2005 -> Clob.class.getName();
//            case 91 -> Date.class.getName();
//            case 8, 3, 2 -> Double.class.getName();
//            case 6 -> Float.class.getName();
//            case -7, 4, 5, -5 -> Integer.class.getName();
//            case 0 -> ObjectUtils.Null.class.getName();
//            case 93, 2014 -> Timestamp.class.getName();
//            case 92 -> Time.class.getName();
//            default -> String.class.getName();
//        };
//    }
//}
