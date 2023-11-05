package main.data_entity;

import javafx.collections.ObservableList;
import main.Loader;
import main.SqlQueries;
import main.data_entity.table_data.AbstractData;
import main.view.MainViewController;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class EditSession {
    private EditSession(){}

    public enum ModifyAction{
        UPDATE, DELETE
    }

    private static Set<SaveInfo> MODIFY_INFO = new HashSet<>();
/*
    private static Map<Integer, AbstractData> MODIFY_ROWS = new HashMap<>();
*/
    public static void addRows(Collection<AbstractData> list, MainViewController.TableName table, ModifyAction action){
        list.forEach(elem-> addNewRow(elem, table, action));
    }
    public static void addNewRow(AbstractData data, MainViewController.TableName table, ModifyAction action){
        if(MODIFY_INFO.stream().map(mi->mi.DATA).anyMatch(d->d==data)){
            if(action.equals(ModifyAction.DELETE)){
                Set<SaveInfo> equalsInfo = MODIFY_INFO.stream()
                        .filter(mi->mi.DATA==data)
                        .collect(Collectors.toSet());
                equalsInfo.stream().filter(si->si.DATA.getIdColumn().equals(0))
                        .forEach(si->MODIFY_INFO.remove(si));
                equalsInfo.stream()
                        .filter(mi->MODIFY_INFO.contains(mi))
                        .forEach(mi->mi.setACTION(ModifyAction.DELETE));
            }
        }
        else{
            MODIFY_INFO.add(new SaveInfo(table, action, data));
        }
    }

    public static void groupByRows(){
        Set<SaveInfo> delete = MODIFY_INFO.stream().filter(mi->mi.ACTION.equals(ModifyAction.DELETE)).collect(Collectors.toSet());
        Set<SaveInfo> insert = MODIFY_INFO.stream()
                .filter(mi->mi.ACTION.equals(ModifyAction.UPDATE))
                .filter((mi->mi.DATA.getIdColumn().equals(0))).collect(Collectors.toSet());
        Set<SaveInfo> update = MODIFY_INFO.stream()
                .filter(mi->mi.ACTION.equals(ModifyAction.UPDATE))
                .filter(mi->! mi.DATA.getIdColumn().equals(0)).collect(Collectors.toSet());
        /* удалить из инсерта строки, которые есть в делите и удалить из делитов эти строки*/
        Set<SaveInfo> removeUncreatedRows = delete.stream().filter(insert::contains).collect(Collectors.toSet());
        delete.removeAll(removeUncreatedRows);
        insert.removeAll(removeUncreatedRows);
        /* удалить из апдейтов строки, которые есть в делите */
        update.removeAll(delete);
        if(! insert.isEmpty()){
            AbstractData anyData = insert.stream().findAny().get().DATA;
            if(anyData.isCompoundData()){
                insert.forEach(si->{
                    Loader.singleInsertQuery(SqlQueries.getInsertQuery(anyData.getTableName()), statement ->si.DATA.insertStatementAction().accept(statement));
                });
            }else {
                String insertQuery = SqlQueries.getInsertQuery(anyData.getTableName());
                System.out.println(insertQuery);
                Loader.packageQueryInTable(insertQuery, statement -> insert.forEach(si->si.DATA.insertStatementAction().accept(statement)));
            }
        }
        if(! update.isEmpty()){
            String updateQuery = SqlQueries.getUpdateQuery(update.stream().findAny().get().TABLE);
            System.out.println(updateQuery);
            Loader.packageQueryInTable(updateQuery, statement -> update.forEach(saveInfo -> saveInfo.DATA.updateStatementAction().accept(statement)));
        }
        if(! delete.isEmpty()){
            String deleteQuery = SqlQueries.getDeleteQuery(delete.stream().findAny().get().TABLE);
            System.out.println(deleteQuery);
            Loader.packageQueryInTable(deleteQuery, statement -> delete.forEach(si->si.DATA.deleteStatementAction().accept(statement)));
        }
        MODIFY_INFO.clear();
    }
    public static class SaveInfo{
        public final MainViewController.TableName TABLE;
        private ModifyAction ACTION;
        public final AbstractData DATA;
        public SaveInfo(MainViewController.TableName table, ModifyAction action, AbstractData row){
            this.TABLE = table;
            this.ACTION = action;
            this.DATA = row;
        }

        public ModifyAction getACTION() {
            return ACTION;
        }

        public void setACTION(ModifyAction ACTION) {
            this.ACTION = ACTION;
        }

        @Override
        public String toString() {
            return "Operation: " + ((DATA.getIdColumn().equals(0)) ? "INSERT" : ACTION)+ " from table: " + TABLE.TABLE_NAME + "\n" +
            "VALUES: " + DATA;
        }
    }

    public static Integer getNewId(Set<Integer> existingId){
        Integer newId = 1;
        while(existingId.contains(newId)){
            newId ++;
        }
        return newId;
    }
    public static String getAllModifications(){
        return MODIFY_INFO.stream()
                .map(SaveInfo::toString)
                .reduce((saveInfo1, saveInfo2) -> saveInfo1 + "\n" + saveInfo2).orElse("none") +
                "\n-----------------------------------------------------------------------------------------";
    }
}
