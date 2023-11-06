package main.data.load;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.data.EditSession;
import main.data.SqlQueries;
import main.data.table_data.AbstractData;
import main.view.MainViewController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Класс-загрузчик
 * Выгрузка и загрузка данных из БД
 */
public class TableLoader {
    /**
     * Путь к БД
     */
    static final Path PATH_TO_DB = Paths.get(System.getProperty("user.dir") + "/src/main/data/ArtDB.accdb");
    /**
     * URL к БД
     */
    static final String URL_TO_DB = "jdbc:ucanaccess://" + PATH_TO_DB;

    /**
     * Получить данные, содержащиеся в таблице
     * @return данные, содержащиеся в таблице
     */
    public static ObservableList<AbstractData> getTableData(){
        switch (MainViewController.getTableNow()){
            case RED:
                return TableLoader.getDataRows(SqlQueries.getSelectQueryFromTable(MainViewController.TableName.RED), TableLoader.getDataObject(MainViewController.TableName.RED));
            case CONTROL:
                return TableLoader.getDataRows(SqlQueries.getSelectQueryFromTable(MainViewController.TableName.CONTROL), TableLoader.getDataObject(MainViewController.TableName.CONTROL));
            default:
                return TableLoader.getDataRows(SqlQueries.getSelectQueryFromTable(MainViewController.TableName.EXP), TableLoader.getDataObject(MainViewController.TableName.EXP));
        }
    }

    /**
     * Получить действия заполнения объекта данными из ResultSet
     * @param tableName - имя таблицы
     * @return действия заполнения объекта данными из ResultSet
     */
    private static BiConsumer<ObservableList<AbstractData>, ResultSet> getDataObject(MainViewController.TableName tableName){
        return (list, rs)->{
            try {
                list.add(AbstractData.of(tableName, rs));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Получить список объектов (строк)
     * @param query - запрос получения данных из БД
     * @param data - действия, заполняющие объект данными из БД
     * @return - список объектов (строк)
     */
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

    /**
     * Отправить пакетный запрос в одну таблицу
     * @param query - пакет запросов одного типа (INSERT/UPDATE/DELETE)
     * @param statementAction - действия, создающие пакеты
     */
    public static void sendPackageQueryForTable(String query, Consumer<PreparedStatement> statementAction){
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

    /**
     * Отправить пакет с одним запросом и получить уникальный номер сгенерированного ключа (в случае с INSERT)
     * @param query - пакет с одним запросом типа (INSERT/UPDATE/DELETE)
     * @param statementAction - действия, создающие пакет
     * @return сгенерированный ключ (в случае с INSERT) или 0
     */
    public static int sendSingleQuery(String query, Consumer<PreparedStatement> statementAction){
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
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Записать в БД запросы типа INSERT
     * @param saveInfo - записываемые строки
     */
    public static void writeInsertPackage(Set<EditSession.SaveInfo> saveInfo){
        if(! saveInfo.isEmpty()){
            AbstractData anyData = saveInfo.stream().findAny().get().DATA;
            if(anyData.isCompoundData()){
                saveInfo.forEach(si->{
                    TableLoader.sendSingleQuery(SqlQueries.getInsertQuery(anyData.getTableName()), statement ->si.DATA.insertStatementAction().accept(statement));
                });
            }else {
                String insertQuery = SqlQueries.getInsertQuery(anyData.getTableName());
                System.out.println(insertQuery);
                TableLoader.sendPackageQueryForTable(insertQuery, statement -> saveInfo.forEach(si->si.DATA.insertStatementAction().accept(statement)));
            }
        }
    }

    /**
     * Записать в БД запросы типа UPDATE
     * @param saveInfo - записываемые строки
     */
    public static void writeUpdatePackage(Set<EditSession.SaveInfo> saveInfo){
        if(! saveInfo.isEmpty()){
            String updateQuery = SqlQueries.getUpdateQuery(saveInfo.stream().findAny().get().TABLE);
            System.out.println(updateQuery);
            TableLoader.sendPackageQueryForTable(updateQuery, statement -> saveInfo.forEach(si-> si.DATA.updateStatementAction().accept(statement)));
        }
    }

    /**
     * Записать в БД запросы типа DELETE
     * @param saveInfo - удаляемые строки
     */
    public static void writeDeletePackage(Set<EditSession.SaveInfo> saveInfo){
        if(! saveInfo.isEmpty()){
            String deleteQuery = SqlQueries.getDeleteQuery(saveInfo.stream().findAny().get().TABLE);
            System.out.println(deleteQuery);
            TableLoader.sendPackageQueryForTable(deleteQuery, statement -> saveInfo.forEach(si->si.DATA.deleteStatementAction().accept(statement)));
        }
    }
}
