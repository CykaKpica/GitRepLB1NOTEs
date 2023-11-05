package main.view;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import main.Loader;
import main.SqlQueries;
import main.data_entity.AbstractData;
import main.data_entity.Control;

import java.sql.SQLException;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Main extends Application {
    public static void main(String[] args) {
        try{
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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

    static void relayTable(){
        MainViewController.relayTableNow();
        fillTableView();
    }
    private static void fillTableView(){
        ObservableList<AbstractData> data = Loader.getTableData();
        List<TableColumn> columns = data.get(0).getColumns();
        MainViewController.updateTable(data, columns.toArray(new TableColumn[columns.size()]));
    }
}
