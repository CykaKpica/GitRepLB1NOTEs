package main.data_entity;

import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class EditSession {
    private EditSession(){}

    public enum ModifyAction{
        INSERT, UPDATE, DELETE
    }

    private static Set<SaveInfo> MODIFY_INFO = new HashSet<>();
    private static Map<Integer, AbstractData> MODIFY_ROWS = new HashMap<>();


    public static void addModifyRow(AbstractData data, String tableName, ModifyAction action){
        Integer newId = getNewId(MODIFY_ROWS.keySet());
        MODIFY_ROWS.put(newId, data);
        MODIFY_INFO.add(new SaveInfo(tableName, action, newId));
    }
    public static void addDeletedRows(ObservableList<AbstractData> list, String tableName){
        list.forEach(elem->addModifyRow(elem, tableName, ModifyAction.DELETE));
    }

    public static String getAllModifications(){
        return MODIFY_INFO.stream()
                .map(saveInfo -> saveInfo.toString())
                .reduce((saveInfo1, saveInfo2) -> saveInfo1 + "\n" + saveInfo2).orElse("none");
    }
    public static class SaveInfo{
        public final String TABLE_NAME;
        public final ModifyAction ACTION;
        public final Integer ID_MODIFY_ROW;
        public SaveInfo(String tableName, ModifyAction action, Integer idModifyRow){
            this.TABLE_NAME = tableName;
            this.ACTION = action;
            this.ID_MODIFY_ROW = idModifyRow;
        }

        @Override
        public String toString() {
            return "Operation: " + ACTION + " from table: " + TABLE_NAME + "\n" +
            "VALUES: " + EditSession.MODIFY_ROWS.get(ID_MODIFY_ROW);
        }
    }

    public static Integer getNewId(Set<Integer> existingId){
        Integer newId = 1;
        while(existingId.contains(newId)){
            newId ++;
        }
        return newId;
    }

}
