package main.data.table_data;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
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
import java.util.function.Function;

public abstract class AbstractData {

    /**
     * Либо первичный ключ объекта,
     * либо первичный ключ родителя объекта, если таковой имеется
     */
    private Integer idColumn;

    protected AbstractData(Integer idColumn) {
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
     *
     * @return столбцы, соответвующие представлению данных
     */
    public abstract List<TableColumn> getColumns();

    /**
     * Проверка полноты содержащихся данных
     *
     * @return true - все данные заполнены
     */
    public abstract boolean isFullCompletion();

    /**
     * Получить действия вставки значений в PreparedStatement, относящиеся к INSERT запросу
     *
     * @return вставка значений в PreparedStatement
     */
    public abstract Consumer<PreparedStatement> insertStatementAction();

    /**
     * Получить действия вставки значений в PreparedStatement, относящиеся к UPDATE запросу
     *
     * @return вставка значений в PreparedStatement
     */
    public abstract Consumer<PreparedStatement> updateStatementAction();

    /**
     * Получить действия вставки значений в PreparedStatement, относящиеся к DELETE запросу
     *
     * @return вставка значений в PreparedStatement
     */
    public abstract Consumer<PreparedStatement> deleteStatementAction();

    /**
     * Получить объект MainViewController.TableName, представлением которого является объект
     *
     * @return объект MainViewController.TableName, представлением которого является объект
     */
    public abstract MainViewController.TableName getTableName();

    /**
     * Является ли объект состовным представлением данных
     *
     * @return true если объект состовным представлением данных
     */
    public abstract boolean isCompoundData();

    /**
     * Получить созданное значение внешнего ключа родителя для вставки новой строки в таблицу БД
     *
     * @return созданное значение внешнего ключа родителя
     */
    public abstract int getIdForeignKeyForInsert();

    //static

    /**
     * Получить представление данных таблицы, соответвующее переданной таблице
     *
     * @param table - таблица
     * @param rs    - объект, содержащий данные для создания представления данных таблицы
     * @return представление данных таблицы
     * @throws SQLException
     */
    public static AbstractData of(MainViewController.TableName table, ResultSet rs) throws SQLException {
        switch (table) {
            case CONTROL:
                return new ControlData(rs.getInt("idRedEngine"),
                        rs.getInt("idRedType"),
                        rs.getInt("idControl"),
                        rs.getInt("idControlType"),
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
     *
     * @param table - таблица
     * @return пустое представление данных таблицы
     */
    public static AbstractData ofNull(MainViewController.TableName table) {
        switch (table) {
            case CONTROL:
                return new ControlData(0, 0, 0, 0, 0d, "");
            default:
                return new ExperimentData(0, "", 0, 0);
        }
    }
    //exemplar

    /**
     * Получить нередактируемый стобец типа Integer
     *
     * @param columnName     - название столбца
     * @param columnDataName - параметр представления данных, с которым связывается столбец
     * @return нередактируемый стобец типа Integer
     */
    protected TableColumn<? extends AbstractData, Integer> getUneditableIntegerColumn(String columnName, String columnDataName) {
        TableColumn<? extends AbstractData, Integer> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        return column;
    }

    /**
     * Получить редактируемый стобец типа Integer
     *
     * @param columnName     - название столбца
     * @param columnDataName - параметр представления данных, с которым связывается столбец
     * @param actionOnEdit   - действие при редактировании ячеек столбца
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

    protected TableColumn<? extends AbstractData, String> getUneditableStringColumn(String columnName, String columnDataName) {
        TableColumn<? extends AbstractData, String> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        return column;
    }

    /**
     * Получить редактируемый стобец типа String
     *
     * @param columnName     - название столбца
     * @param columnDataName - параметр представления данных, с которым связывается столбец
     * @param actionOnEdit   - действие при редактировании ячеек столбца
     * @return редактируемый стобец типа String
     */
    protected TableColumn<? extends AbstractData, String> getStringColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> actionOnEdit) {
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
     *
     * @param columnName     - название столбца
     * @param columnDataName - параметр представления данных, с которым связывается столбец
     * @param actionOnEdit   - действие при редактировании ячеек столбца
     * @return редактируемый стобец типа Double
     */
    protected TableColumn<? extends AbstractData, Double> getDoubleColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> actionOnEdit) {
        TableColumn<? extends AbstractData, Double> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        column.setOnEditCommit(event -> {
            actionOnEdit.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        return column;
    }

    /**
     * Получить редактируемый столбец с выпадающим списком, в котором хранятся значения типа ComboboxData
     *
     * @param columnName   - наименование столбца
     * @param actionOnEdit - дейсвтия при изменении значения столбца
     * @param dataList     - значения в выпадающем списке
     * @param dataValue    - получение нового выбранного значения из выпадающего списка
     * @return столбец с выпадающим списком, в котором хранятся значения типа ComboboxData
     */
    protected TableColumn<? extends AbstractData, ComboboxData> getComboBoxColumn(String columnName,
                                                                                  Consumer<TableColumn.CellEditEvent> actionOnEdit,
                                                                                  ObservableList<ComboboxData> dataList,
                                                                                  Function<TableColumn.CellDataFeatures<AbstractData, ComboboxData>, SimpleObjectProperty<ComboboxData>> dataValue) {
        TableColumn<AbstractData, ComboboxData> column = new TableColumn<>(columnName);
        column.setCellValueFactory(dataValue::apply);
        column.setCellFactory(ComboBoxTableCell.forTableColumn(dataList));
        column.setOnEditCommit(event -> {
            actionOnEdit.accept(event);
            EditSession.addNewRow(event.getRowValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
        });
        column.setMinWidth(120d);
        return column;
    }

    protected TableColumn<? extends AbstractData, Boolean> getCheckBoxColumn(String columnName,
                                                                             Function<TableColumn.CellDataFeatures<AbstractData, Boolean>, SimpleBooleanProperty> dataValue,
                                                                             Consumer<Boolean> assignNewValue) {
        TableColumn<AbstractData, Boolean> column = new TableColumn<>(columnName);

        column.setCellValueFactory(cellDataFeatures -> {
            /*
            Note: singleCol.setOnEditCommit(): Not work for CheckBoxTableCell.
            */
            SimpleBooleanProperty property = dataValue.apply(cellDataFeatures);
            property.addListener((observableValue, oldValue, newValue) -> {
                assignNewValue.accept(newValue);
                EditSession.addNewRow(cellDataFeatures.getValue(), MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
            });
            return property;
        });
        column.setCellFactory(p -> {
            CheckBoxTableCell<AbstractData, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        return column;
    }

    //overrides
    @Override
    public String toString() {
        return idColumn.toString();
    }

}
