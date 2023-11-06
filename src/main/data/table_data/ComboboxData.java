package main.data.table_data;

public class ComboboxData {

    private final String TEXT;
    private final Integer CODE;
    public ComboboxData(String text, Integer code){
        this.TEXT = text;
        this.CODE = code;
    }

    public Integer getCODE() {
        return CODE;
    }

    public String getTEXT() {
        return TEXT;
    }

    @Override
    public String toString() {
        return this.TEXT;
    }
}
