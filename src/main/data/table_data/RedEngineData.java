package main.data.table_data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class RedEngineData extends AbstractData {
    private ComboboxData idRedType;
    private static ObservableList<ComboboxData> redTypeNames = FXCollections.observableArrayList();
    private static Map<Integer, ComboboxData> redTypeNamesId = new HashMap<>();
    private Double chanceOpening;
    RedEngineData(Integer idRedEngine, Integer idRedType, Double chanceOpening) {
        super(idRedEngine);
        this.idRedType = redTypeNamesId.get(idRedType);
        this.chanceOpening = chanceOpening;
    }
    public static void putRedTypeNames(Collection<ComboboxData> redTypeNames){
        RedEngineData.redTypeNames.clear();
        RedEngineData.redTypeNamesId.clear();
        RedEngineData.redTypeNames.addAll(redTypeNames);
        redTypeNamesId.putAll(redTypeNames.stream().collect(Collectors.toMap(ComboboxData::getCODE, cb->cb)));
        RedEngineData.redTypeNamesId.put(0, new ComboboxData("unknown", 0));
    }
    public static ObservableList<ComboboxData> getRedTypeNames(){
        return redTypeNames;
    }
    public String getIdRedType() {
        return this.idRedType.getTEXT();
    }

    public void setIdRedType(String idType) throws NoSuchElementException {
        this.idRedType = redTypeNames.stream().filter(cb->cb.getTEXT().equals(idType)).findAny().get();
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
                /*getUneditableStringColumn("nameRedType", "idRedType"),*/
                getDoubleColumn("chanceOpening", "chanceOpening", event -> {
                    TableColumn.CellEditEvent<RedEngineData, Double> expEvent = (TableColumn.CellEditEvent<RedEngineData, Double>) event;
                    expEvent.getRowValue().setChanceOpening(expEvent.getNewValue());
                }));
    }

    @Override
    public boolean isFullCompletion() {
        return ! idRedType.getCODE().equals(0) && ! chanceOpening.equals(0d);
    }
    @Override
    public Consumer<PreparedStatement> insertStatementAction() {
        return (statement) -> {
            try {
                statement.setInt(1, idRedType.getCODE());
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
                statement.setInt(1, idRedType.getCODE());
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
    public void setGeneratedPrimaryKey(int newId) {
        this.setIdColumn(newId);
    }

    @Override
    public MainViewController.TableName getTableName() {
        return MainViewController.TableName.RED;
    }

    @Override
    public String toString() {
        return "idRed = " + super.toString() + ", idRedType = " + idRedType + ", chanceOpening = " + chanceOpening;
    }

}
