package main.data.table_data;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import static java.lang.Integer.parseInt;

public class CustomTableCell<T> extends TableCell<T, String> {
    private TextField textField;

    @Override
    public void startEdit() {
        super.startEdit();
        if (textField == null) {
            createTextField();
        }
        textField.setText(getString());
        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getString());
        setGraphic(null);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(String s, boolean b) {
        super.updateItem(s, b);

        if (b || s == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getString());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
            setGraphic(null);
        }
    }

    private String getString() {
        return String.valueOf(getItem());
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ENTER || t.getCode() == KeyCode.TAB) {
                commitEdit((textField.getText()));
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }

    public class IntegerCell<T> extends TableCell<T, Integer> {
        private TextField textField;
        @Override
        public void cancelEdit() {
            super.cancelEdit();

        }

        @Override
        protected void updateItem(Integer item, boolean b) {
            super.updateItem(item, b);
            if(item == null || b){
                setId("");
            }
            else if (false) {
                setId("errorRow");
            } else{
                setId("");
            }
        }


 /*       private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyPressed(t -> {
                if (t.getCode() == KeyCode.ENTER || t.getCode() == KeyCode.TAB) {
                    commitEdit((textField.getText()));
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }*/
    }
}
