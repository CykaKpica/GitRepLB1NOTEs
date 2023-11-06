package main.data;

import main.data.table_data.AbstractData;
import main.view.Main;
import main.view.MainViewController;

import java.util.*;
import java.util.stream.Collectors;

public final class EditSession {
    private EditSession(){}

    /**
     * Действие совершенное над строкой
     */
    public enum ModifyAction{
        UPDATE, DELETE
    }

    /**
     * Информация о состоянии сохранения
     */
    public enum ModifyInfo{
        SAVED("Все данные сохранены"), NOT_SAVED("Есть несохраненные данные");
        public final String MSG;
         ModifyInfo(String info){
            this.MSG = info;
        }
    }
    /**
     * Информация о текущем состоянии сохранения
     */
    private static ModifyInfo savedStatus = ModifyInfo.SAVED;

    public static ModifyInfo getSavedStatus() {
        return savedStatus;
    }

    /**
     * Массив несохраненных изменений таблицы
     */
    private static Set<SaveInfo> MODIFY_INFO = new HashSet<>();

    /**
     * Добавить несохранённые строки
     * @param list - массив несохранённых строк
     * @param table - таблица, к которой относятся строки
     * @param action - дейсвтие, совершенное над всеми строками (одинаковое для всех)
     */
    public static void addRows(Collection<AbstractData> list, MainViewController.TableName table, ModifyAction action){
        list.forEach(elem-> addNewRow(elem, table, action));
    }

    /**
     * Добавить новую несохранённую строку
     * Если строка уже есть в несохранённых, а переданным действием является удаление, то действие изменяется на удаление,
     * если строка при этом ещё не создана с БД, то она удаляется из несохранённых изменений
     * @param data - несохранённая строка
     * @param table - таблица, к которой относится несохранённая строка
     * @param action - действие, совершённое над строкой
     */
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
            savedStatus = ModifyInfo.NOT_SAVED;
            Main.relaySavedStatus();
        }
    }

    /**
     * Передать несохранённые строки в загрузчик.
     * Неохранённые строки сортируются на пакеты INSERT, UPDATE, DELETE и передаются тремя пакетами для загрузки в БД.
     */
    public static void passUnsavedRowsToLoader(){
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
        Loader.writeInsertPackage(insert);
        Loader.writeUpdatePackage(update);
        Loader.writeDeletePackage(delete);
        removeModifyInfo();
    }

    /**
     * Очистить несохранённые данные и обновить статус сохранения
     */
    public static void removeModifyInfo(){
        MODIFY_INFO.clear();
        savedStatus = ModifyInfo.SAVED;
        Main.relaySavedStatus();
    }
    /**
     * Класс для хранения информации об измененной строке
     */
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

    /**
     * Получить строчное представление всех несохраненных изменений
     * @return все несохранённые изменения
     */
    public static String getAllModifications(){
        return MODIFY_INFO.stream()
                .map(SaveInfo::toString)
                .reduce((saveInfo1, saveInfo2) -> saveInfo1 + "\n" + saveInfo2).orElse("none") +
                "\n-----------------------------------------------------------------------------------------";
    }
}
