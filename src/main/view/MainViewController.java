package main.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import main.data.SqlQueries;
import main.data.*;
import main.data.table_data.AbstractData;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

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
    @FXML
    private Label saveInfoLabel;
    private static Label s_saveInfoLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        s_rootVbox = rootVbox;
        s_rootTable = rootTable;
        s_saveInfoLabel = saveInfoLabel;
        saveInfoLabel.setText("Все данные сохранены");
        rootTable.setEditable(true);
        rootTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        idSaveButton.setOnAction(event -> saveAction());
        idOtherTableButton.setOnAction(event -> Main.relayTable());
        idAddRowButton.setOnAction(actionEvent -> addEmptyItem());
        idDeleteRowButton.setOnAction(actionEvent -> deleteSelectionItems());
    }
    public enum TableName {
        EXP(SqlQueries.experimentTableName),
        RED(SqlQueries.redEngineTableName),
        CONTROL(SqlQueries.controlTableName);
        public final String TABLE_NAME;

        TableName(String tableName) {
            this.TABLE_NAME = tableName;
        }
    }

    private static TableName tableNow = TableName.EXP;


    public static TableName getTableNow() {
        return tableNow;
    }

    private static void setTableNow(TableName table) {
        MainViewController.tableNow = table;
    }

    public static void relayTableNow() {
        switch (tableNow) {
            case EXP:
                tableNow = TableName.CONTROL;
                break;
            default:
                tableNow = TableName.EXP;
        }
    }
    static void relaySavedStatus(String status){
        s_saveInfoLabel.setText(status);
    }
    public static void updateTable(ObservableList items, TableColumn... columns) {
        s_rootTable.getColumns().clear();
        s_rootTable.getItems().clear();
        addColumns(columns);
        addItems(items);
    }

    private static void addColumns(TableColumn... columns) {
        s_rootTable.getColumns().addAll(columns);
        s_rootTable.setRowFactory(tv ->
                new TableRow<AbstractData>() {
                    @Override
                    protected void updateItem(AbstractData item, boolean b) {
                        graphicProperty().unbind();
                        super.updateItem(item, b);
                        if(item == null || b){
                            setId("");
                        }
                        else if (!item.isFullCompletion()) {
                            setId("errorRow");
                        } else{
                            setId("");
                        }
                    }
                }
        );
    }

    private static void addItems(ObservableList list) {
        s_rootTable.setItems(list);
    }

    private static void addEmptyItem() {
        AbstractData newRow;
        switch (tableNow) {
            case RED:
                newRow = AbstractData.ofNull(TableName.RED);
                break;
            case CONTROL:
                newRow = AbstractData.ofNull(TableName.CONTROL);
                break;
            default:
                newRow = AbstractData.ofNull(TableName.EXP);
        }
        EditSession.addNewRow(newRow, MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        s_rootTable.getItems().add(newRow);
        s_rootTable.requestFocus();
        int lastRow = s_rootTable.getItems().size() - 1;
        s_rootTable.getSelectionModel().select(lastRow);
        s_rootTable.scrollTo(lastRow);
    }

    private static void deleteSelectionItems() {
        ObservableList<AbstractData> selectedItems = s_rootTable.getSelectionModel().getSelectedItems();
        EditSession.addRows(selectedItems, tableNow, EditSession.ModifyAction.DELETE);
        s_rootTable.getItems().removeAll(selectedItems);
    }


    private static void saveAction() {
        System.out.println(EditSession.getAllModifications());
        EditSession.passUnsavedRowsToLoader();
        Main.fillTableView();
    }


    private static void fdgf() {
        for (Object list : s_rootTable.getSelectionModel().getSelectedItems()) {

        }

        s_rootTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Триггер на актив строку");

            /*buttChangeEnStruc.setDisable(false);
            botChangeFrStruc.setDisable(false);*/
        });
    }

}
