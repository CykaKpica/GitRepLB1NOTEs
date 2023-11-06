package main.data.table_data;

import javafx.scene.control.TableColumn;
import main.data.Loader;
import main.data.SqlQueries;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ControlData extends RedEngineData {

    /**
     * Первичный ключ строки
     */
    private Integer idControl;

    private String controlName;

    ControlData(Integer idRedEngine, Integer idRedType, Integer idControl, Double chanceOpening, String controlName) {
        super(idRedEngine, idRedType, chanceOpening);
        this.idControl = idControl;
        this.controlName = controlName;
    }

    public Integer getIdControl() {
        return idControl;
    }

    public void setIdControl(Integer idControl) {
        this.idControl = idControl;
    }

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

    @Override
    public List<TableColumn> getColumns() {
        List<TableColumn> superList = new ArrayList<>(super.getColumns());
        superList.add(getUneditableIntegerColumn("idControl", "idControl"));
        superList.add(getStringColumn("controlName", "controlName", event->{
            TableColumn.CellEditEvent<ControlData, String> expEvent = (TableColumn.CellEditEvent<ControlData, String >) event;
            expEvent.getRowValue().setControlName(expEvent.getNewValue());
        }));
        return superList;
    }

    @Override
    public boolean isFullCompletion() {
        return super.isFullCompletion() && ! controlName.equals("");
    }
    @Override
    public Consumer<PreparedStatement> insertStatementAction() {
        final int idForeignKey = getIdForeignKeyForInsert();
        return (statement) -> {
            try {
                statement.setString(1, controlName);
                statement.setInt(2, idForeignKey);
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
    @Override
    public Consumer<PreparedStatement> updateStatementAction() {
        /* плюс запрос для состаной таблицы  */
        Loader.sendSingleQuery(SqlQueries.getUpdateQuery(super.getTableName()), statement -> super.updateStatementAction().accept(statement));
        return (statement) -> {
            try {
                statement.setString(1, getControlName());
                statement.setInt(2, getIdControl());
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
    @Override
    public Consumer<PreparedStatement> deleteStatementAction() {
        /* удалить сначала родителя */
        Loader.sendSingleQuery(SqlQueries.getDeleteQuery(super.getTableName()), statement -> super.deleteStatementAction().accept(statement));

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
        return Loader.sendSingleQuery(SqlQueries.getInsertQuery(super.getTableName()), super.insertStatementAction());
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
        return super.toString() + ", " + controlName;
    }
}
