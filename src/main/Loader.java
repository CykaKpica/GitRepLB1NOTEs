package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.data_entity.table_data.AbstractData;
import main.data_entity.EditSession;
import main.view.MainViewController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Loader {

    private static final Path PATH_TO_DB = Paths.get(System.getProperty("user.dir") + "/src/main/data_entity/ArtDB.accdb");
    public static final String URL_TO_DB = "jdbc:ucanaccess://" + PATH_TO_DB;

    public static ObservableList<AbstractData> getTableData(){
        switch (MainViewController.getTableNow()){
            case RED:
                return getRedTableData();
            case CONTROL:
                return getControlTableData();
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
    private static ObservableList<AbstractData> getControlTableData(){
        return Loader.getDataRows(SqlQueries.getSelectQueryFromTable(MainViewController.TableName.CONTROL), Loader.getDataObject(MainViewController.TableName.CONTROL));
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

    public static void packageQueryInTable(String query, Consumer<PreparedStatement> statementAction){
        try(Connection connection = DriverManager.getConnection(URL_TO_DB);
        PreparedStatement statement = connection.prepareStatement(query)){
            connection.setAutoCommit(false);
            statementAction.accept(statement);
            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static int singleInsertQuery(String query, Consumer<PreparedStatement> statementAction){
        try(Connection connection = DriverManager.getConnection(URL_TO_DB);
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);
            statementAction.accept(statement);
            statement.executeBatch();
            connection.commit();
            try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            throw new SQLException("Ошибка.");
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
/*    public static Consumer<PreparedStatement> getStatementAction(Consumer<PreparedStatement> statementAction,
                                                                 Set<AbstractData> queryObjects) throws IllegalArgumentException{
        return (statement) -> {
            for (AbstractData data : queryObjects){
                statementAction.accept(data);
                try {
                    statement.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }*/


}
