package main.data_entity.table_data;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import main.data_entity.EditSession;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractData {

    private Integer idColumn;

    protected AbstractData(Integer idColumn){
        this.idColumn = idColumn;
    }
    public Integer getIdColumn() {
        return this.idColumn;
    }

    public void setIdColumn(Integer id) {
        this.idColumn = id;
    }
    //abstract

    /**
     * Получить столбцы, соответвующие представлению данных
     * @return
     */
    public abstract List<TableColumn> getColumns();

    /**
     * Проверка полноты содержащихся данных
     * @return true - все данные заполнены
     */
    public abstract boolean isFullCompletion();

    public abstract Consumer<PreparedStatement> insertStatementAction();
    public abstract Consumer<PreparedStatement> updateStatementAction();
    public abstract Consumer<PreparedStatement> deleteStatementAction();
    public abstract MainViewController.TableName getTableName();
    public abstract boolean isCompoundData();
    public int getIdForeignKey(){
        return 0;
    }
    public String superQuery(){
        return "";
    }

    //static
    /**
     * Получить представление данных таблицы, соответвующее переданной таблице
     * @param table - таблица
     * @param rs - объект, содержащий данные для создания представления данных таблицы
     * @return - представление данных таблицы
     * @throws SQLException
     */
    public static AbstractData of(MainViewController.TableName table, ResultSet rs) throws SQLException {
        switch (table) {
            case RED:
                return new RedEngine(rs.getInt("idRedEngine"),
                        rs.getInt("idRedType"),
                        rs.getDouble("chanceOpeningEnemy"));
            case CONTROL:
                return new Control(rs.getInt("idRedEngine"),
                        rs.getInt("idRedType"),
                        rs.getInt("idControl"),
                        rs.getDouble("chanceOpeningEnemy"),
                        rs.getString("nameControl"));
            default:
                return new Experiment(rs.getInt("idExperimentBuild"),
                        rs.getString("nameEB"),
                        rs.getInt("leadTime_sec"),
                        rs.getInt("periodBetweenReconnaissance_sec"));
        }
    }
    public static AbstractData ofNull(MainViewController.TableName table){
        switch (table) {
            case RED:
                return new RedEngine(0,0, 0d);
            case CONTROL:
                return new Control(0, 0, 0, 0d, "");
            default:
                return new Experiment(0, "", 0, 0);
        }
    }
    //exemplar
    protected TableColumn<? extends AbstractData, Integer> getUneditableIntegerColumn(String columnName, String columnDataName){
        TableColumn<? extends AbstractData, Integer> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        return column;
    }
    protected TableColumn<? extends AbstractData, Integer> getIntegerColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action) {
        TableColumn<? extends AbstractData, Integer> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        column.setOnEditCommit(event -> {
            action.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        return column;
    }
    protected TableColumn<? extends AbstractData, String> getStringColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action){
        TableColumn<? extends AbstractData, String> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            action.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        return column;
    }
    protected TableColumn<? extends AbstractData, Double> getDoubleColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action){
        TableColumn<? extends AbstractData, Double> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        column.setOnEditCommit(event -> {
            action.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        return column;
    }

    //overrides
    @Override
    public String toString() {
        return idColumn.toString();
    }

}
