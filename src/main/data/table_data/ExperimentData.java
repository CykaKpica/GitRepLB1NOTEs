package main.data.table_data;

import javafx.scene.control.TableColumn;
import main.data.EditSession;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public final class ExperimentData extends AbstractData {
    private String expName;
    private Integer leadTime;
    private Integer period;

    ExperimentData(Integer idExperiment, String expName, Integer leadTime, Integer period) {
        super(idExperiment);
        this.expName = expName;
        this.leadTime = leadTime;
        this.period = period;
    }
    public String getName() {
        return this.expName;
    }

    public void setName(String name) {
        this.expName = name;
    }

    public Integer getLeadTime() {
        return this.leadTime;
    }

    public void setLeadTime(Integer lead) {
        this.leadTime = lead;
    }

    public Integer getPeriod() {
        return this.period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    @Override
    public List<TableColumn> getColumns() {
        return List.of(getUneditableIntegerColumn("idExperiment", "idColumn"),
                getStringColumn("name", "name", event -> {
                    TableColumn.CellEditEvent<ExperimentData, String> expEvent = (TableColumn.CellEditEvent<ExperimentData, String>) event;
                    expEvent.getRowValue().setName(expEvent.getNewValue());
                    EditSession.addNewRow(this, MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
                }),
                getIntegerColumn("lead", "leadTime", event -> {
                    TableColumn.CellEditEvent<ExperimentData, Integer> expEvent = (TableColumn.CellEditEvent<ExperimentData, Integer>) event;
                    expEvent.getRowValue().setLeadTime(expEvent.getNewValue());
                    EditSession.addNewRow(this, MainViewController.getTableNow(), EditSession.ModifyAction.UPDATE);
                }),
                getIntegerColumn("period", "period", event -> {
                    TableColumn.CellEditEvent<ExperimentData, Integer> expEvent = (TableColumn.CellEditEvent<ExperimentData, Integer>) event;
                    expEvent.getRowValue().setPeriod(expEvent.getNewValue());

                }));
    }

    @Override
    public boolean isFullCompletion() {
        return ! expName.equals("") && ! leadTime.equals(0) && ! period.equals(0);
    }

    @Override
    public Consumer<PreparedStatement> insertStatementAction() {
        return (statement) -> {
            try {
                statement.setString(1, expName);
                statement.setInt(2, leadTime);
                statement.setInt(3, period);
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }


    @Override
    public Consumer<PreparedStatement> updateStatementAction() {
        return statement -> {
            try {
                statement.setString(1, expName);
                statement.setInt(2, leadTime);
                statement.setInt(3, period);
                statement.setInt(4, getIdColumn());
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }


    @Override
    public Consumer<PreparedStatement> deleteStatementAction() {
        return statement -> {
            try {
                statement.setInt(1, getIdColumn());
                statement.addBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public MainViewController.TableName getTableName() {
        return MainViewController.TableName.EXP;
    }

    @Override
    public boolean isCompoundData() {
        return false;
    }

    @Override
    public void setGeneratedPrimaryKey(int newId) {
        this.setIdColumn(newId);
    }

    @Override
    public int getIdForeignKeyForInsert() {
        return 0;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + expName + ", " + leadTime + ", " + period;
    }
}
