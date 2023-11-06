package main.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import main.data.load.DictionaryLoader;
import main.data.load.TableLoader;
import main.data.EditSession;
import main.data.table_data.AbstractData;

import java.util.List;

import static java.lang.Integer.parseInt;

public class Main extends Application {
    public static void main(String[] args) {
        try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        DictionaryLoader.loadDictionary();
        launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("main_view.fxml"));
        Parent root = fxmlLoader.load();
        stage.setResizable(false);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        fillTableView();
    }


    private static void startFillData(){

    }
    /**
     * Поменять источник данных в таблице формы
     */
    static void relayTable(){
        MainViewController.relayTableNow();
        EditSession.removeModifyInfo();
        fillTableView();
    }

    /**
     * Заполнить источник данных таблицы в форме
     */
    static void fillTableView(){
        ObservableList<AbstractData> data = TableLoader.getTableData();
        List<TableColumn> columns = data.get(0).getColumns();
        MainViewController.updateTable(data, columns.toArray(new TableColumn[columns.size()]));
    }

    /**
     * Поменять статус сохранения
     */
    public static void relaySavedStatus(){
        Platform.runLater(() -> MainViewController.setRelaySavedStatus(EditSession.getSavedStatus().MSG));
    }
}
