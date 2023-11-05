package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.data_entity.AbstractData;
import main.data_entity.EditSession;
import main.view.MainViewController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Loader {

    private static final Path PATH_TO_DB = Paths.get(System.getProperty("user.dir") + "/src/main/data_entity/ArtDB.accdb");
    public static final String URL_TO_DB = "jdbc:ucanaccess://" + PATH_TO_DB;

    public static ObservableList<AbstractData> getTableData(){
        switch (MainViewController.getTableNow()){
            case RED:
                return getRedTableData();
            default:
                return getExpTableData();
        }
    }

    private static ObservableList<AbstractData> getRedTableData(){
        return Loader.getDataRows(SqlQueries.getSelectQueryFromTable(MainViewController.TableName.RED), Loader.getDataObject(MainViewController.TableName.RED));
    }
    private static ObservableList<AbstractData> getExpTableData(){
        return Loader.getDataRows(SqlQueries.getSelectQueryFromTable(MainViewController.TableName.EXP), Loader.getDataObject(MainViewController.TableName.EXP));
    }
    private static BiConsumer<ObservableList<AbstractData>, ResultSet> getDataObject(MainViewController.TableName tableName){
        return (list, rs)->{
            try {
                list.add(AbstractData.of(tableName, rs));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
    private static ObservableList<AbstractData> getDataRows(String query, BiConsumer<ObservableList<AbstractData>, ResultSet> data){
        ObservableList<AbstractData> properties = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(URL_TO_DB);) {
            Statement s = conn.createStatement();
            try (ResultSet rs = s.executeQuery(query)) {
                while (rs.next()){
                    data.accept(properties, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }



    private static void packageQueryInTable(String query, Consumer<PreparedStatement> statementAction){

        try(Connection connection = DriverManager.getConnection(URL_TO_DB);
        PreparedStatement statement = connection.prepareStatement(query)){
            connection.setAutoCommit(false);
            statementAction.accept(statement);
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить пакет запросов на одно из действий: Insert, Update, Delete
     * Все объекты массива queryObjects должны соответвовать одной операции
     * @param statementAction - вставка значений в statement
     * @param queryObjects - объекты вставки
     * @return - пакет с запросами
     * @exception IllegalArgumentException - если объекты массива queryObjects содержат разные операции
     */
    public static Consumer<PreparedStatement> getStatementAction(Consumer<EditSession.SaveInfo> statementAction, EditSession.SaveInfo[] queryObjects) throws IllegalArgumentException{
        EditSession.ModifyAction anyAction = queryObjects[0].ACTION;
        if (! Arrays.stream(queryObjects).allMatch(saveInfo -> saveInfo.ACTION.equals(anyAction))){
            throw new IllegalArgumentException("Ошибка сохранения");
        }
        return (statement) -> {
            for (EditSession.SaveInfo saveObject : queryObjects){
                statementAction.accept(saveObject);
                try {
                    statement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    private static String getInsertQuery(String tableName, String[] columnNames){
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        for(String columnName : columnNames){
            query.append(columnName + ", ");
        }
        query.deleteCharAt(query.length()-1);
        query.append(") VALUES (");
        for (int i = 0; i < columnNames.length; i++){
            query.append("?,");
        }
        query.deleteCharAt(query.length()-1);
        query.append(")");
        return query.toString();
    }
    private static String getUpdateQuery(String table, String[] columnNames, String idPrimaryKey){
        StringBuilder query = new StringBuilder("UPDATE " + table + " SET ");
        for(String columnName : columnNames){
            query.append(columnName + "= ?,");
        }
        query.deleteCharAt(query.length()-1);
        query.append(" WHERE " + idPrimaryKey + " = ?");
        return query.toString();
    }
}
