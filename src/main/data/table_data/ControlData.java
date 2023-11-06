package main.data.table_data;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import main.data.load.TableLoader;
import main.data.SqlQueries;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ControlData extends RedEngineData {

    /**
     * Первичный ключ строки
     */
    private Integer idControl;
    private Integer idControlType;
    private String controlName;
    /* example column with checkbox
    * without loading into DB (there is no such column there)
    * */
    private Boolean check = true;

    private static ObservableList<ComboboxData> controlTypeNames = FXCollections.observableArrayList();
    private static Map<Integer, ComboboxData> controlTypeNamesId = new HashMap<>();
    ControlData(Integer idRedEngine, Integer idRedType, Integer idControl, Integer idControlType, Double chanceOpening, String controlName) {
        super(idRedEngine, (idRedType.equals(0) ? 1 : idRedType), chanceOpening);
        this.idControl = idControl;
        this.idControlType = idControlType;
        this.controlName = controlName;
    }
    public static void putControlTypeNames(Collection<ComboboxData> typeNames){
        ControlData.controlTypeNames.clear();
        ControlData.controlTypeNamesId.clear();
        ControlData.controlTypeNames.addAll(typeNames);
        ControlData.controlTypeNamesId.putAll(typeNames.stream().collect(Collectors.toMap(ComboboxData::getCODE, cb->cb)));
        ControlData.controlTypeNamesId.put(0, new ComboboxData("unknown", 0));
        controlTypeNamesId.entrySet().forEach(es-> System.out.println(es.getKey() + " " + es.getValue()));
    }
    public static ObservableList<ComboboxData> getControlTypeNames(){
        return controlTypeNames;
    }

    public Integer getIdControl() {
        return idControl;
    }

    public void setIdControl(Integer idControl) {
        this.idControl = idControl;
    }

    public Integer getIdControlType() {
        return idControlType;
    }

    public void setIdControlType(Integer idType) {
        this.idControlType = idType;
    }

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> superList = new ArrayList<>();
        superList.add(getComboBoxColumn("nameControlType",
                event -> {
                    TableColumn.CellEditEvent<ControlData, ComboboxData> expEvent = (TableColumn.CellEditEvent<ControlData, ComboboxData>) event;
                    expEvent.getRowValue().setIdControlType(expEvent.getNewValue().getCODE());
                },
                getControlTypeNames(),
                (cellFeatures)->new SimpleObjectProperty<>( controlTypeNamesId.get(((ControlData)cellFeatures.getValue()).getIdControlType()) )));

        superList.add(getUneditableIntegerColumn("idControl", "idControl"));
        superList.add(getStringColumn("controlName", "controlName", event -> {
            TableColumn.CellEditEvent<ControlData, String> expEvent = (TableColumn.CellEditEvent<ControlData, String>) event;
            expEvent.getRowValue().setControlName(expEvent.getNewValue());
        }));
        superList.addAll(super.getColumns());
        /*
         * test column
         */
        superList.add(getCheckBoxColumn("test",
                (cellFeatures)->new SimpleBooleanProperty( ((ControlData)cellFeatures.getValue()).getCheck()),
                this::setCheck ));
        return superList;
    }

    @Override
    public boolean isFullCompletion() {
        return super.isFullCompletion() && !controlName.equals("") && !idControlType.equals(0);
    }

    @Override
    public Consumer<PreparedStatement> insertStatementAction() {
        final int idForeignKey = getIdForeignKeyForInsert();
        return (statement) -> {
            try {
                statement.setInt(1, idControlType);
                statement.setString(2, controlName);
                statement.setInt(3, idForeignKey);
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Consumer<PreparedStatement> updateStatementAction() {
        /* плюс запрос для состаной таблицы  */
        TableLoader.sendSingleQuery(SqlQueries.getUpdateQuery(super.getTableName()), statement -> super.updateStatementAction().accept(statement));
        return (statement) -> {
            try {
                statement.setInt(1, getIdControlType());
                statement.setString(2, getControlName());
                statement.setInt(3, getIdControl());
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Consumer<PreparedStatement> deleteStatementAction() {
        /* удалить сначала родителя */
        TableLoader.sendSingleQuery(SqlQueries.getDeleteQuery(super.getTableName()), statement -> super.deleteStatementAction().accept(statement));

        return (statement) -> {
            try {
                statement.setInt(1, getIdControl());
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public int getIdForeignKeyForInsert() {
        return TableLoader.sendSingleQuery(SqlQueries.getInsertQuery(super.getTableName()), super.insertStatementAction());
    }

    @Override
    public MainViewController.TableName getTableName() {
        return MainViewController.TableName.CONTROL;
    }

    @Override
    public boolean isCompoundData() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + ", idControl = " + idControl + ", controlName = " + controlName + ", idControlType = " + idControlType + ", check = " + check;
    }
}
