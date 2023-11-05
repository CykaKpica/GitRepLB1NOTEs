package main.data_entity;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.hsqldb.lib.StringConverter;

import java.util.List;
import java.util.function.Consumer;

public final class Experiment extends AbstractData {
    private String expName;
    private Integer leadTime;
    private Integer period;
    public Experiment(String idExperiment, String name, String leadTime, String period) {
        super(4);
 /*       this.DATA[0] = idExperiment;
        this.DATA[1] = name;
        this.DATA[2] = leadTime;
        this.DATA[3] = period;*/

    }
public Experiment(Integer idExperiment, String expName, Integer leadTime, Integer period){
        super(4, idExperiment);
        this.expName = expName;
        this.leadTime = leadTime;
        this.period = period;
}
   /* public String getName() {
        return this.DATA[1];
    }

    public void setName(String name) {
        this.DATA[1] = name;
    }

    public String getLeadTime() {
        return this.DATA[2];
    }

    public void setLeadTime(String lead) {
        this.DATA[2] = lead;
    }

    public String getPeriod() {
        return this.DATA[3];
    }

    public void setPeriod(String period) {
        this.DATA[3] = period;
    }*/
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
                    //event.getRowValue().setName(event.getNewValue());
                    TableColumn.CellEditEvent<Experiment, String> expEvent = (TableColumn.CellEditEvent<Experiment, String>) event;
                    expEvent.getRowValue().setName(expEvent.getNewValue());
                }),
                getIntegerColumn("lead", "leadTime", event -> {
                    //event.getRowValue().setLeadTime(event.getNewValue());
                    TableColumn.CellEditEvent<Experiment, Integer> expEvent = (TableColumn.CellEditEvent<Experiment, Integer>) event;
                    expEvent.getRowValue().setLeadTime(expEvent.getNewValue());
                }),
                getIntegerColumn("period", "period", event -> {
                    //event.getRowValue().setPeriod(event.getNewValue());
                    TableColumn.CellEditEvent<Experiment, Integer> expEvent = (TableColumn.CellEditEvent<Experiment, Integer>) event;
                    expEvent.getRowValue().setPeriod(expEvent.getNewValue());
                }));
    }
    @Override
    protected TableColumn<Experiment, Integer> getUneditableIntegerColumn(String columnName, String columnDataName) {
        TableColumn<Experiment, Integer> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        return column;
    }
    protected TableColumn<? extends AbstractData, Integer> getIntegerColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action) {
       /* TableColumn<Experiment, Integer> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        column.setOnEditCommit(action::accept);
        return column;*/
        return super.getIntegerColumn(columnName, columnDataName, action);
    }

    @Override
    protected TableColumn<? extends AbstractData, String> getStringColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action) {
        TableColumn<Experiment, String > column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(action::accept);
        return column;
    }

    @Override
    protected TableColumn<? extends AbstractData, Double> getDoubleColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action) {
        TableColumn<Experiment, Double> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        column.setOnEditCommit(action::accept);
        return column;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + expName + ", " + leadTime + ", " + period;
    }
}
