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
        idSaveButton.setOnAction(event -> saveButtonAction());
        idOtherTableButton.setOnAction(event -> Main.relayTable());
        idAddRowButton.setOnAction(actionEvent -> addEmptyRow());
        idDeleteRowButton.setOnAction(actionEvent -> deleteSelectionRows());
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


    public static void relayTableNow() {
        switch (tableNow) {
            case EXP:
                tableNow = TableName.CONTROL;
                break;
            default:
                tableNow = TableName.EXP;
        }
    }
    static void setRelaySavedStatus(String status){
        s_saveInfoLabel.setText(status);
    }

    /**
     * Обвновить таблицу
     * Очищаются столбцы и данные, добавляются переданные столбцы и данные
     * @param items - новые данные
     * @param columns - новые столбцы
     */
    public static void updateTable(ObservableList items, TableColumn... columns) {
        addColumns(columns);
        addItems(items);
    }

    /**
     * Добавить столбцы в таблицу
     * Сначала удаляются прежние столбцы, затем добавляются новые
     * Также для строк столбцов добавляется RowFactory, где проверяется, полностью ли заполнена строка,
     * если строка заполнена неполностью, то текст в ней преобретает красный цвет
     * @param columns - новые стобцы
     */
    private static void addColumns(TableColumn... columns) {
        s_rootTable.getColumns().clear();
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

    /**
     * Добавить данные в таблицу
     * Сначала очищаются старые, потом добавляются новые
     * @param list - новые данные
     */
    private static void addItems(ObservableList list) {
        s_rootTable.getItems().clear();
        s_rootTable.setItems(list);
    }

    /**
     * Вставить пустую строку, а также опустить скролл к новой строке
     */
    private static void addEmptyRow() {
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

    /**
     * Удалить выбранные строки из таблицы и очистить выделение
     */
    private static void deleteSelectionRows() {
        ObservableList<AbstractData> selectedItems = s_rootTable.getSelectionModel().getSelectedItems();
        EditSession.addRows(selectedItems, tableNow, EditSession.ModifyAction.DELETE);
        s_rootTable.getItems().removeAll(selectedItems);
    }

    /**
     * Дейсвтия кнопки сохранить
     * Оправить несохраненные изменения таблицы в загрузчик
     * Обновить данные таблицы (загрузить таблицу заново)
     */
    private static void saveButtonAction() {
        EditSession.passUnsavedRowsToLoader();
        Main.fillTableView();
    }


/*    private static void fdgf() {
        for (Object list : s_rootTable.getSelectionModel().getSelectedItems()) {

        }

        s_rootTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Триггер на актив строку");

            *//*buttChangeEnStruc.setDisable(false);
            botChangeFrStruc.setDisable(false);*//*
        });
    }*/

}
