package main.data.table_data;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import main.data.EditSession;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractData {

    /**
     * Либо первичный ключ объекта,
     * либо первичный ключ родителя объекта, если таковой имеется
     *
     * */
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
     * @return столбцы, соответвующие представлению данных
     */
    public abstract List<TableColumn> getColumns();

    /**
     * Проверка полноты содержащихся данных
     * @return true - все данные заполнены
     */
    public abstract boolean isFullCompletion();

    /**
     * Получить действия вставки значений в PreparedStatement, относящиеся к INSERT запросу
     * @return вставка значений в PreparedStatement
     */
    public abstract Consumer<PreparedStatement> insertStatementAction();
    /**
     * Получить действия вставки значений в PreparedStatement, относящиеся к UPDATE запросу
     * @return вставка значений в PreparedStatement
     */
    public abstract Consumer<PreparedStatement> updateStatementAction();
    /**
     * Получить действия вставки значений в PreparedStatement, относящиеся к DELETE запросу
     * @return вставка значений в PreparedStatement
     */
    public abstract Consumer<PreparedStatement> deleteStatementAction();

    /**
     * Получить объект MainViewController.TableName, представлением которого является объект
     * @return объект MainViewController.TableName, представлением которого является объект
     */
    public abstract MainViewController.TableName getTableName();

    /**
     * Является ли объект состовным представлением данных
     * @return true если объект состовным представлением данных
     */
    public abstract boolean isCompoundData();

    /**
     * Получить созданное значение внешнего ключа родителя для вставки новой строки в таблицу БД
     * @return созданное значение внешнего ключа родителя
     */
    public abstract int getIdForeignKeyForInsert();

    //static
    /**
     * Получить представление данных таблицы, соответвующее переданной таблице
     * @param table - таблица
     * @param rs - объект, содержащий данные для создания представления данных таблицы
     * @return представление данных таблицы
     * @throws SQLException
     */
    public static AbstractData of(MainViewController.TableName table, ResultSet rs) throws SQLException {
        switch (table) {
            case CONTROL:
                return new ControlData(rs.getInt("idRedEngine"),
                        rs.getInt("idRedType"),
                        rs.getInt("idControl"),
                        rs.getDouble("chanceOpeningEnemy"),
                        rs.getString("nameControl"));
            default:
                return new ExperimentData(rs.getInt("idExperimentBuild"),
                        rs.getString("nameEB"),
                        rs.getInt("leadTime_sec"),
                        rs.getInt("periodBetweenReconnaissance_sec"));
        }
    }

    /**
     * Получить представление данных таблицы, не содержащее значений и соответвующее переданной таблице
     * @param table - таблица
     * @return пустое представление данных таблицы
     */
    public static AbstractData ofNull(MainViewController.TableName table){
        switch (table) {
            case CONTROL:
                return new ControlData(0, 0, 0, 0d, "");
            default:
                return new ExperimentData(0, "", 0, 0);
        }
    }
    //exemplar

    /**
     * Получить нередактируемый стобец типа Integer
     * @param columnName - название столбца
     * @param columnDataName - параметр представления данных, с которым связывается столбец
     * @return нередактируемый стобец типа Integer
     */
    protected TableColumn<? extends AbstractData, Integer> getUneditableIntegerColumn(String columnName, String columnDataName){
        TableColumn<? extends AbstractData, Integer> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        return column;
    }

    /**
     * Получить редактируемый стобец типа Integer
     * @param columnName - название столбца
     * @param columnDataName - параметр представления данных, с которым связывается столбец
     * @param actionOnEdit - действие при редактировании ячеек столбца
     * @return редактируемый стобец типа Integer
     */
    protected TableColumn<? extends AbstractData, Integer> getIntegerColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> actionOnEdit) {
        TableColumn<? extends AbstractData, Integer> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        column.setOnEditCommit(event -> {
            actionOnEdit.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        return column;
    }

    /**
     * Получить редактируемый стобец типа String
     * @param columnName - название столбца
     * @param columnDataName - параметр представления данных, с которым связывается столбец
     * @param actionOnEdit - действие при редактировании ячеек столбца
     * @return редактируемый стобец типа String
     */
    protected TableColumn<? extends AbstractData, String> getStringColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> actionOnEdit){
        TableColumn<? extends AbstractData, String> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            actionOnEdit.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        return column;
    }

    /**
     * Получить редактируемый стобец типа Double
     * @param columnName - название столбца
     * @param columnDataName - параметр представления данных, с которым связывается столбец
     * @param actionOnEdit - действие при редактировании ячеек столбца
     * @return редактируемый стобец типа Double
     */
    protected TableColumn<? extends AbstractData, Double> getDoubleColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> actionOnEdit){
        TableColumn<? extends AbstractData, Double> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        column.setOnEditCommit(event -> {
            actionOnEdit.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        return column;
    }

    protected TableColumn<? extends AbstractData, ComboboxData> getComboboxColumn(String columnName,
                                                                                  Consumer<TableColumn.CellEditEvent> actionOnEdit,
                                                                                  ObservableList<ComboboxData> dataList,
                                                                                  Supplier<ComboboxData> dataValue){
        TableColumn<AbstractData, ComboboxData> column = new TableColumn<>(columnName);
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(dataValue.get()));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(dataList));
        column.setOnEditCommit(event->{
            actionOnEdit.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        column.setMinWidth(120d);
        return column;
    }

    //overrides
    @Override
    public String toString() {
        return idColumn.toString();
    }

}
