package main.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import main.SqlQueries;
import main.data_entity.*;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    public enum TableName {
        EXP(SqlQueries.experimentTableName), RED(SqlQueries.redEngineTableName);
        public final String TABLE_NAME;
        TableName(String tableName){
            this.TABLE_NAME = tableName;
        }
    }
    private static TableName tableNow = TableName.EXP;


    public static TableName getTableNow() {
        return tableNow;
    }
    private static void setTableNow(TableName table){
        MainViewController.tableNow = table;
    }
    public static void relayTableNow(){
        switch (tableNow){
            case EXP:
                tableNow = TableName.RED;
                break;
            case RED:
                tableNow = TableName.EXP;
                break;
        }
    }
    @FXML
    private VBox rootVbox;

    @FXML
    private TableView<?> rootTable;

    private static VBox s_rootVbox;
    private static TableView s_rootTable;

    @FXML
    private Button idSaveButton;
    @FXML
    private Button idOtherTableButton;
    @FXML
    private Button idAddRowButton;

    @FXML
    private Button idDeleteRowButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        s_rootVbox = rootVbox;
        s_rootTable = rootTable;
        rootTable.setEditable(true);
        rootTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        idSaveButton.setOnAction(event -> saveAction());
        idOtherTableButton.setOnAction(event -> Main.relayTable());
        idAddRowButton.setOnAction(actionEvent -> addEmptyItem());
        idDeleteRowButton.setOnAction(actionEvent -> deleteSelectionItems());
    }

    public static void updateTable(ObservableList items, TableColumn... columns){
        s_rootTable.getColumns().clear();
        s_rootTable.getItems().clear();
        addColumns(columns);
        addItems(items);
    }
    private static void addColumns(TableColumn... columns){
        s_rootTable.getColumns().addAll(columns);
        /*Arrays.stream(columns).forEach(c->{
            c.setCellFactory(p -> new CustomTableCell<>());
        });*/
    }
    private static void addItems(ObservableList list){
        s_rootTable.setItems(list);
    }

    private static void addEmptyItem(){
        AbstractData newRow;
        switch (tableNow){
            case RED:
                newRow = new Control("", "", "");
                break;
            default:
                newRow = new Experiment("", "", "", "");
        }
        s_rootTable.getItems().add(newRow);
        s_rootTable.requestFocus();
        int lastRow = s_rootTable.getItems().size()-1;
        s_rootTable.getSelectionModel().select(lastRow);
        s_rootTable.scrollTo(lastRow);
    }

    private static void deleteSelectionItems(){
        ObservableList<AbstractData> selectedItems = s_rootTable.getSelectionModel().getSelectedItems();
        EditSession.addDeletedRows(selectedItems, tableNow.TABLE_NAME);
        s_rootTable.getItems().removeAll(selectedItems);
    }


    private static void saveAction(){
        System.out.println(EditSession.getAllModifications());
    }

    private static void fdgf(){
        for( Object list : s_rootTable.getSelectionModel().getSelectedItems()){

        }

        s_rootTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Триггер на актив строку");

            /*buttChangeEnStruc.setDisable(false);
            botChangeFrStruc.setDisable(false);*/
        });
    }

}
