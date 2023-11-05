package main.data_entity;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import main.view.MainViewController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public sealed abstract class AbstractData permits Experiment, Control {
    protected final String[] DATA;

    private Integer idColumn;

    protected AbstractData(int size) {
        this.DATA = new String[size];
    }
    protected AbstractData(int size, Integer idColumn){
        this(size);
        this.idColumn = idColumn;
    }

    /*    public String getIdColumn() {
            return this.DATA[0];
        }

        public void setIdColumn(String id) {
            this.DATA[0] = id;
        }*/
    public Integer getIdColumn() {
        return this.idColumn;
    }

    public void setIdColumn(Integer id) {
        this.idColumn = id;
    }

    public abstract List<TableColumn> getColumns();

    protected TableColumn<? extends AbstractData, String> getStringColumn(String nameColumn, String columnDataName) {
        TableColumn<? extends AbstractData, String> column = new TableColumn<>(nameColumn);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        return column;
    }

    protected TableColumn<? extends AbstractData, String> getColumn(TableColumn<? extends AbstractData, String> column, String columnDataName) {
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        //column.setCellFactory(p -> new CustomTableCell<>());
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        return column;
    }

    protected TableColumn<? extends AbstractData, String> getColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action) {
        TableColumn<Experiment, String> column = new TableColumn<>(columnName);
        column.setOnEditCommit(action::accept);
        return getColumn(column, columnDataName);
    }

    protected void getCellEditEventAction(Consumer<TableColumn.CellEditEvent> action) {
    }

    public static AbstractData of(MainViewController.TableName table, ResultSet rs) throws SQLException {
        switch (table) {
            case RED:
                /*return new Control(Integer.toString(rs.getInt("idRedEngine")),
                        Integer.toString(rs.getInt("idRedType")),
                        Integer.toString(rs.getInt("viewingRange_m")));*/
                return new Control(rs.getInt("idRedEngine"),
                        rs.getInt("idRedType"),
                        rs.getDouble("chanceOpeningEnemy"));
            default:
                /*return new Experiment(Integer.toString(rs.getInt("idExperimentBuild")),
                        rs.getString("nameEB"),
                        Integer.toString(rs.getInt("leadTime_sec")),
                        Integer.toString(rs.getInt("periodBetweenReconnaissance_sec")));*/
                return new Experiment(rs.getInt("idExperimentBuild"),
                        rs.getString("nameEB"),
                        rs.getInt("leadTime_sec"),
                        rs.getInt("periodBetweenReconnaissance_sec"));
        }
    }

    @Override
    public String toString() {
        return idColumn.toString();
    }

    protected abstract TableColumn<? extends AbstractData, Integer> getUneditableIntegerColumn(String columnName, String columnDataName);
    protected TableColumn<? extends AbstractData, Integer> getIntegerColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action) {
        TableColumn<? extends AbstractData, Integer> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        column.setOnEditCommit(action::accept);
        return column;
    }
    protected abstract TableColumn<? extends AbstractData, String> getStringColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action);
    protected abstract TableColumn<? extends AbstractData, Double> getDoubleColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action);

}
