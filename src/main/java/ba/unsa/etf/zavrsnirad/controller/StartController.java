package ba.unsa.etf.zavrsnirad.controller;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ba.unsa.etf.zavrsnirad.dto.JRXMLColumn;
import ba.unsa.etf.zavrsnirad.utils.DatabaseConnection;
import ba.unsa.etf.zavrsnirad.utils.FilePath;
import ba.unsa.etf.zavrsnirad.utils.MyResourceBundle;
import ba.unsa.etf.zavrsnirad.utils.XmlCreator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang3.ObjectUtils;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartController {

    @FXML
    public VBox vbox;
    @FXML
    public TextField databaseStringTextField;
    @FXML
    public TextArea sqlQueryTextArea;

    private String title;
    private String subtitle;

    public void next() {

        String query = sqlQueryTextArea.getText();
        Pattern p = Pattern.compile("\\s*\\w+,");
        Pattern p1 = Pattern.compile("\\s+\\w+\\s+from");

        Matcher m = p.matcher(query);
        Matcher m1=p1.matcher(query);

        String colsOnly="";
        while(m.find()){colsOnly+=(m.group().trim());}
        while(m1.find()){colsOnly+=(m1.group().substring(0,m1.group().length()-4).trim());}
        List<String> columns = List.of(colsOnly.split(","));
        System.out.println(columns);

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
                title = null;
            else
                title = titleTextField.getText();

            if (Objects.equals(subtitleTextField.getText(), ""))
                subtitle = null;
            else
                subtitle = subtitleTextField.getText();

            createReport();
            newWindow();
        });
        next.setMaxWidth(Double.MAX_VALUE);
        next.setPrefWidth(Region.USE_COMPUTED_SIZE);
        next.setDefaultButton(true);
        children.add(back);
        children.add(next);
        vbox.getChildren().addAll(children);
    }

//    private void readJRXML() {
//        try {
//            String filePath = getClass().getResource("/jrxml/template.jrxml").getFile();
//
//            XmlCreator xmlCreator = new XmlCreator(filePath, FilePath.JRXML_DESC_FILE_NAME.getFullPath());
//            JRXMLColumn jrxmlColumn1 = new JRXMLColumn("kolona 1", getClassName(12637));
//            JRXMLColumn jrxmlColumn2 = new JRXMLColumn("kolona 2", getClassName(1923));
//            xmlCreator
//                    .addQueryString("string")
//                    .addNewColumns(List.of(jrxmlColumn1, jrxmlColumn2));
//
//        } catch (FileNotFoundException | XMLStreamException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    private void createReport() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryTextArea.getText());
            ResultSet resultSet = preparedStatement.executeQuery();

            String filePath = getClass().getResource("/jrxml/template.jrxml").getFile();
            XmlCreator xmlCreator = new XmlCreator(filePath, FilePath.JRXML_DESC_FILE_NAME.getFullPath());
            //XmlCreator xmlCreator = new XmlCreator(filePath, "C:/Users/pp/Desktop/destination.jrxml");
//            FastReportBuilder drb = new FastReportBuilder();
            List<JRXMLColumn> jrxmlColumnList = new ArrayList<>();
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            int count = rsMetaData.getColumnCount();
            for(int i = 1; i<=count; i++) {
                System.out.println("kolona " + rsMetaData.getColumnName(i) );
                jrxmlColumnList
                        .add(new JRXMLColumn(rsMetaData.getColumnName(i), getClassName(rsMetaData.getColumnType(i))));

            }

            xmlCreator.createJRXML(sqlQueryTextArea.getText(), null, jrxmlColumnList);

            //xmlCreator.addQueryString(sqlQueryTextArea.getText()).addNewColumns(jrxmlColumnList);

//            if(title != null)
//                drb.setTitle(title);
//            if(subtitle != null)
//                drb.setSubtitle(subtitle);
//            DynamicReport dynamicReport = drb.setUseFullPageWidth(true).build();
//            JasperReport jasperReport =
//                    DynamicJasperHelper.generateJasperReport(dynamicReport, new ClassicLayoutManager(), new HashMap());
//            DynamicJasperHelper.generateJRXML(jasperReport, "utf-8", FilePath.JRXML_SRC_FILE_NAME.getFullPath());
//
//            return DynamicJasperHelper.nerateJasperPrint(dynamicReport, new ClassicLayoutManager(), resultSet);
//        }
//        catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle(MyResourceBundle.getResourceBundle().getString("errorTitle"));
//            alert.setHeaderText(MyResourceBundle.getResourceBundle().getString("pleaseTryAgain"));
//            alert.setContentText(MyResourceBundle.getResourceBundle().getString("unexpected"));
//
//            alert.showAndWait();
//        } catch (JRException e) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle(MyResourceBundle.getResourceBundle().getString("errorTitle"));
//            alert.setHeaderText(MyResourceBundle.getResourceBundle().getString("pleaseTryAgain"));
//            alert.setContentText(MyResourceBundle.getResourceBundle().getString("cantConvert"));
//
//            alert.showAndWait();
            //return null;
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(MyResourceBundle.getResourceBundle().getString("errorTitle"));
            alert.setHeaderText(MyResourceBundle.getResourceBundle().getString("pleaseTryAgain"));
            alert.setContentText(MyResourceBundle.getResourceBundle().getString("sqlException"));

            alert.showAndWait();
            //return null;
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return null;
    }

    private void newWindow() {
        try {
            Node source = vbox;

            Stage oldStage  = (Stage) source.getScene().getWindow();
            Stage newStage = new Stage();

            HtmlController htmlController = new HtmlController();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/htmlinput.fxml"),
                    MyResourceBundle.getResourceBundle());
            loader.setController(htmlController);
            newStage.setScene(new Scene(loader.load(), 800, 500));
            newStage.setMinHeight(500);
            newStage.setMinWidth(800);

            oldStage.close();
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setConnection() {

    }

    private String getClassName(int classType) {
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
}
