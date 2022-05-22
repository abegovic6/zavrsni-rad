package ba.unsa.etf.zavrsnirad;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartController {

    public VBox vbox;
    public TextField databaseStringTextField;
    public TextArea sqlQueryTextArea;

    public List<String> columns = new ArrayList<>();

    public void next() {
        String query = sqlQueryTextArea.getText();
        Pattern p = Pattern.compile("\\s*\\w+,");
        Pattern p1 = Pattern.compile("\\s+\\w+\\s+from");

        Matcher m = p.matcher(query);
        Matcher m1=p1.matcher(query);

        String colsOnly="";
        while(m.find()){colsOnly+=(m.group().trim());}
        while(m1.find()){colsOnly+=(m1.group().substring(0,m1.group().length()-4).trim());}
        columns = List.of(colsOnly.split(","));
        System.out.println(columns);

        var backup = new ArrayList<>(vbox.getChildren());
        vbox.getChildren().clear();
        var children = new ArrayList<Node>();
        Label selectColumns = new Label(MyResourceBundle.getResourceBundle().getString("SelectColumns"));
        children.add(selectColumns);

        ScrollPane scrollPane = new ScrollPane();
        VBox flowPane = new VBox();
        flowPane.setSpacing(10);
        flowPane.getStyleClass().add("padding");
        scrollPane.setMaxHeight(300);
        scrollPane.setPrefHeight(300);
        for(var col : columns) {
            CheckBox checkBox = new CheckBox();
            checkBox.setText(col);
            checkBox.setId(col);
            flowPane.getChildren().add(checkBox);
        }
        scrollPane.setContent(flowPane);
        children.add(scrollPane);
        Button back = new Button(MyResourceBundle.getResourceBundle().getString("Back"));
        back.getStyleClass().add("buttonstylesecondary");
        back.setOnAction((EventHandler) event -> {
            vbox.getChildren().clear();
            vbox.getChildren().addAll(backup);
        });
        back.setMaxWidth(Double.MAX_VALUE);
        back.setPrefWidth(Region.USE_COMPUTED_SIZE);
        Button next = new Button(MyResourceBundle.getResourceBundle().getString("Next"));
        next.getStyleClass().add("buttonstyle");
        next.setOnAction((EventHandler) event -> {
            System.out.println("implement");
        });
        next.setMaxWidth(Double.MAX_VALUE);
        next.setPrefWidth(Region.USE_COMPUTED_SIZE);
        next.setDefaultButton(true);
        children.add(back);
        children.add(next);
        vbox.getChildren().addAll(children);
    }
}
