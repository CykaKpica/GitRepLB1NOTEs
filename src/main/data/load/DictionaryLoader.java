package main.data.load;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.data.table_data.ComboboxData;
import main.data.table_data.ControlData;
import main.data.table_data.RedEngineData;

import java.sql.*;
import java.util.function.BiConsumer;

/**
 * Класс для загрузки словарей (списков наименований типов объектов)
 */
public class DictionaryLoader {
    private static final String queryRedTypeNames = "SELECT idRedType, nameRedType FROM RedType";
    private static final String queryControlTypeNames = "SELECT idControlType, nameControlType FROM ControlType";
    /**
     * Загрузка всех словарей
     * Запустить в самом начале
     */
    public static void loadDictionary(){
        RedEngineData.putRedTypeNames(loadDictionary(queryRedTypeNames, (list, rs)->{
            try {
                list.add(new ComboboxData(rs.getString("nameRedType"), rs.getInt("idRedType")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        ControlData.putControlTypeNames(loadDictionary(queryControlTypeNames, (list, rs)->{
            try {
                list.add(new ComboboxData(rs.getString("nameControlType"), rs.getInt("idControlType")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    /**
     * Универсальный загрузчик словаря
     * @param query - запрос на получение данных
     * @param data - обработка данных
     * @return - словарь
     */
    private static ObservableList<ComboboxData> loadDictionary(String query, BiConsumer<ObservableList<ComboboxData>, ResultSet> data) {

        ObservableList<ComboboxData> dictionary = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(TableLoader.URL_TO_DB);) {
            Statement s = conn.createStatement();
            try (ResultSet rs = s.executeQuery(query)) {
                while (rs.next()) {
                    data.accept(dictionary, rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dictionary;
    }
























}
