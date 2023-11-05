package main.data_entity;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.util.List;
import java.util.function.Consumer;

public final class Control extends AbstractData {
    private Integer idRedType;
    private Double chanceOpening;

    public Control(String idRedEngine, String idRedType, String viewingRange_m) {
        super(3);
        this.DATA[0] = idRedEngine;
        this.DATA[1] = idRedType;
        this.DATA[2] = viewingRange_m;
    }

    public Control(Integer idRedEngine, Integer idRedType, Double chanceOpening) {
        super(3, idRedEngine);
        this.idRedType = idRedType;
        this.chanceOpening = chanceOpening;
    }

    /*    public String getIdRedType(){
            return this.DATA[1];
        }
        public void setIdRedType(String idType){
            this.DATA[1] = idType;
        }
        public String getViewingRange(){
            return DATA[2];
        }
        public void setViewingRange(String range){
            this.DATA[2] = range;
        }*/
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
                    TableColumn.CellEditEvent<Control, Integer> expEvent = (TableColumn.CellEditEvent<Control, Integer>) event;
                    expEvent.getRowValue().setIdRedType(expEvent.getNewValue());
                }),
                getDoubleColumn("chanceOpening", "chanceOpening", event -> {
                    //event.getRowValue().setViewingRange(event.getNewValue());
                    TableColumn.CellEditEvent<Control, Double> expEvent = (TableColumn.CellEditEvent<Control, Double>) event;
                    expEvent.getRowValue().setChanceOpening(expEvent.getNewValue());
                }));
    }

    @Override
    protected TableColumn<Control, Integer> getUneditableIntegerColumn(String columnName, String columnDataName) {
        TableColumn<Control, Integer> column = new TableColumn<>(columnName);
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
        TableColumn<Control, String> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(action::accept);
        return column;
    }

    @Override
    protected TableColumn<? extends AbstractData, Double> getDoubleColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent> action) {
        TableColumn<Control, Double> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(columnDataName));
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        column.setOnEditCommit(action::accept);
        return column;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + idRedType + ", " + chanceOpening;
    }
    /*    protected TableColumn<? extends AbstractData, String> getColumn(String columnName, String columnDataName, Consumer<TableColumn.CellEditEvent<Control, String>> action) {
        TableColumn<Control, String> column = new TableColumn<>(columnName);
        column.setOnEditCommit(event -> {
            action.accept(event);
        });
        return super.getColumn(column, columnDataName);
    }*//*
    @Override
    protected TableColumn<Control, String> getColumn(TableColumn<? extends AbstractData, String> column, String columnDataName) {
        return (TableColumn<Control, String>) super.getColumn(column, columnDataName);
    }*/
}
