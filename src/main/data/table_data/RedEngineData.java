package main.data.table_data;

import javafx.scene.control.TableColumn;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public abstract class RedEngineData extends AbstractData {
    private Integer idRedType;
    private Double chanceOpening;
    RedEngineData(Integer idRedEngine, Integer idRedType, Double chanceOpening) {
        super(idRedEngine);
        this.idRedType = idRedType;
        this.chanceOpening = chanceOpening;
    }
    public Integer getIdRedType() {
        return this.idRedType;
    }

    public void setIdRedType(Integer idType) {
        this.idRedType = idType;
    }

    public Double getChanceOpening() {
        return chanceOpening;
    }

    public void setChanceOpening(Double chanceOpening) {
        this.chanceOpening = chanceOpening;
    }


    @Override
    public List<TableColumn> getColumns() {
        return List.of(getUneditableIntegerColumn("idRedEngine", "idColumn"),
                getIntegerColumn("idType", "idRedType", event -> {
                    //event.getRowValue().setIdRedType(event.getNewValue());
                    TableColumn.CellEditEvent<RedEngineData, Integer> expEvent = (TableColumn.CellEditEvent<RedEngineData, Integer>) event;
                    expEvent.getRowValue().setIdRedType(expEvent.getNewValue());
                }),
                getDoubleColumn("chanceOpening", "chanceOpening", event -> {
                    //event.getRowValue().setViewingRange(event.getNewValue());
                    TableColumn.CellEditEvent<RedEngineData, Double> expEvent = (TableColumn.CellEditEvent<RedEngineData, Double>) event;
                    expEvent.getRowValue().setChanceOpening(expEvent.getNewValue());
                }));
    }

    @Override
    public boolean isFullCompletion() {
        return ! idRedType.equals(0) && ! chanceOpening.equals(0d);
    }
    @Override
    public Consumer<PreparedStatement> insertStatementAction() {
        return (statement) -> {
            try {
                statement.setInt(1, idRedType);
                statement.setDouble(2, chanceOpening);
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
                statement.setInt(1, idRedType);
                statement.setDouble(2, chanceOpening);
                statement.setInt(3, getIdColumn());
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
    public boolean isCompoundData() {
        return false;
    }

    @Override
    public MainViewController.TableName getTableName() {
        return MainViewController.TableName.RED;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + idRedType + ", " + chanceOpening;
    }

}
