package main;

import main.view.MainViewController;

public final class SqlQueries {
    private SqlQueries(){}
    public static final String redEngineTableName = "RedEngine";
    public static final String experimentTableName = "ExperimentBuild";
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

    public static String getSelectQueryFromTable(MainViewController.TableName table){
        switch (table){
            case RED:
                return getSelectQueryFromTable(redEngineTableName, redEngineColumnNames);
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


}
