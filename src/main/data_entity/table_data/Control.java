package main.data_entity.table_data;

import javafx.scene.control.TableColumn;
import main.Loader;
import main.SqlQueries;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Control extends RedEngine{

    private Integer idControl;
    private String controlName;

    Control(Integer idRedEngine, Integer idRedType, Integer idControl, Double chanceOpening, String controlName) {
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
            TableColumn.CellEditEvent<Control, String> expEvent = (TableColumn.CellEditEvent<Control, String >) event;
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
        final int idForeignKey = getIdForeignKey();
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
        return (statement) -> {
            try {

                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Consumer<PreparedStatement> deleteStatementAction() {
        return (statement) -> {
            try {
                statement.setInt(1, getIdColumn());
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
    @Override
    public int getIdForeignKey() {
        return Loader.singleInsertQuery(superQuery(), super.insertStatementAction());
    }
    public String superQuery(){
        return SqlQueries.getInsertQuery(super.getTableName());
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
