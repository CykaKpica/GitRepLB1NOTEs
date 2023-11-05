package main;

import main.view.MainViewController;

import java.util.Arrays;

public final class SqlQueries {
    private SqlQueries(){}
    public static final String redEngineTableName = "RedEngine";
    public static final String experimentTableName = "ExperimentBuild";
    public static final String controlTableName = "Control";
    /**
     * 0 - column with primary key
     */
    public static final String[] redEngineColumnNames = {
            "idRedEngine",
            "idRedType",
            "chanceOpeningEnemy"};
    /**
     * 0 - column with primary key
     */
    public static final String[] experimentColumnNames = {
            "idExperimentBuild",
            "nameEB",
            "leadTime_sec",
            "periodBetweenReconnaissance_sec"};
    private static final String[] controlColumnNames = {"idControl", "nameControl"};
    public static String[] getControlColumnNames(){
        String[] columns = new String[redEngineColumnNames.length + controlColumnNames.length];
        System.arraycopy(redEngineColumnNames, 0, columns, 0, redEngineColumnNames.length);
        System.arraycopy(controlColumnNames, 0, columns, redEngineColumnNames.length, controlColumnNames.length);
        return columns;
    }
    private static String[] getColumnNamesWithoutFirst(String[] columnNames){
        String[] columnsWithoutPrimaryKey = new String[columnNames.length-1];
        System.arraycopy(columnNames, 1, columnsWithoutPrimaryKey, 0, columnsWithoutPrimaryKey.length);
        return columnsWithoutPrimaryKey;
    }
    public static String getSelectQueryFromTable(MainViewController.TableName table){
        switch (table){
            case RED:
                return getSelectQueryFromTable(redEngineTableName, redEngineColumnNames);
            case CONTROL:
                return getSelectQueryFromTable(redEngineTableName, controlTableName, redEngineColumnNames, controlColumnNames);
            default:
                return getSelectQueryFromTable(experimentTableName, experimentColumnNames);
        }
    }
    private static String getSelectQueryFromTable(String tableName, String... columnNames){
        StringBuilder query = new StringBuilder("SELECT ");
        for (String columnName : columnNames){
            query.append(columnName + ",");
        }
        query.deleteCharAt(query.length()-1);
        query.append(" FROM " + tableName);
        return query.toString();
    }
    public static String getInsertQuery(MainViewController.TableName table){
        switch (table){
            case RED:
                return getInsertQuery(redEngineTableName, getColumnNamesWithoutFirst(redEngineColumnNames));
            case CONTROL:
                return getInsertQueryWithForeignKey(controlTableName, getColumnNamesWithoutFirst(controlColumnNames), redEngineColumnNames[0]);
            case EXP:
                return getInsertQuery(experimentTableName, getColumnNamesWithoutFirst(experimentColumnNames));
        }
        return "";
    }
    public static String getUpdateQuery(MainViewController.TableName table){
        switch (table){
            case RED:
                return getUpdateQuery(redEngineTableName, getColumnNamesWithoutFirst(redEngineColumnNames), redEngineColumnNames[0]);
            case CONTROL:
                return getUpdateQuery(controlTableName, getColumnNamesWithoutFirst(controlColumnNames), controlColumnNames[0]);
            case EXP:
                return getUpdateQuery(experimentTableName, getColumnNamesWithoutFirst(experimentColumnNames), experimentColumnNames[0]);
        }
        return "";
    }
    public static String getDeleteQuery(MainViewController.TableName table){
        switch (table){
            case RED:
                return getDeleteQuery(redEngineTableName, redEngineColumnNames[0]);
            case CONTROL:
                return getDeleteQuery(controlTableName, controlColumnNames[0]);
            case EXP:
                return getDeleteQuery(experimentTableName, experimentColumnNames[0]);
        }
        return "";
    }
    /**
     * Сгнерировать Select запрос для таблицы, содержащей один внешний ключ
     * @param rootTableName - имя внешней таблицы, с которой связывает внешний ключ
     * @param tableName - имя таблицы, в которой содержится внешний ключ
     * @param rootColumnNames - имена столбцов внешней таблицы, где на 0 позии находится имя ключа, идентичного в первой и второй таблицах
     * @param columnNames - имена столбцов таблицы tableName
     * @return
     */
    private static String getSelectQueryFromTable(String rootTableName, String tableName, String[] rootColumnNames, String[] columnNames){
        StringBuilder query = new StringBuilder("SELECT ");
        for (String rootColumnName : rootColumnNames){
            query.append(rootTableName+"."+rootColumnName + ",");
        }
        for (String columnName : columnNames){
            query.append(tableName+"."+columnName+",");
        }
        query.deleteCharAt(query.length()-1);
        query.append(" FROM " + tableName + " INNER JOIN "+ rootTableName + " ON " + tableName +"."+rootColumnNames[0]+"="+rootTableName+"."+rootColumnNames[0]);
        return query.toString();
    }

    private static String getInsertQuery(String tableName, String[] columnNames){
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        for(String columnName : columnNames){
            query.append(columnName + ",");
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
    private static String getInsertQueryWithForeignKey(String tableName, String[] columnNames, String foreignColumnName){
        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        for(String columnName : columnNames){
            query.append(columnName + ",");
        }
        query.append(foreignColumnName + ") VALUES (");
        for (int i = 0; i < columnNames.length; i++){
            query.append("?,");
        }
        query.append("?)");
        System.out.println(query);
        return query.toString();
    }
    private static String getUpdateQuery(String table, String[] columnNames, String primaryKeyColumnName){
        StringBuilder query = new StringBuilder("UPDATE " + table + " SET ");
        for(String columnName : columnNames){
            query.append(columnName + "= ?,");
        }
        query.deleteCharAt(query.length()-1);
        query.append(" WHERE " + primaryKeyColumnName + " = ?");
        return query.toString();
    }
    private static String getDeleteQuery(String table, String primaryKeyColumnName){
        return new StringBuilder("DELETE FROM " + table + " WHERE " + primaryKeyColumnName + " = ?").toString();
    }
}
