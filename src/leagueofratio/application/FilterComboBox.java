package leagueofratio.application;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FilterComboBox extends ComboBox<String> {
    private final ObservableList<String> initialList;
    private ObservableList<String> bufferList = FXCollections.observableArrayList();
    private String previousValue = "";

    public FilterComboBox(ObservableList<String> items) {
        super(items);
        super.setEditable(true);
        this.initialList = items;

        this.configAutoFilterListener();
    }

    private void configAutoFilterListener() {
        final FilterComboBox currentInstance = this;
        this.getEditor().textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            previousValue = oldValue;
            final TextField editor1 = currentInstance.getEditor();
            final String selected = currentInstance.getSelectionModel().getSelectedItem();
            if (selected == null || !selected.equals(editor1.getText())) {
                filterItems(newValue, currentInstance);
                currentInstance.show();
                if (currentInstance.getItems().size() == 1) {
                    setUserInputToOnlyOption(currentInstance, editor1);
                }
            }
        });
    }

    private void filterItems(String filter, ComboBox<String> comboBox) {
        if (filter.startsWith(previousValue) && !previousValue.isEmpty()) {
            ObservableList<String> filteredList = this.readFromList(filter, bufferList);
            bufferList.clear();
            bufferList = filteredList;
        } else {
            bufferList = this.readFromList(filter, initialList);
        }
        comboBox.setItems(bufferList);
    }

    private ObservableList<String> readFromList(String filter, ObservableList<String> originalList) {
        ObservableList<String> filteredList = FXCollections.observableArrayList();
        originalList.stream().filter((item) -> (item.toLowerCase().startsWith(filter.toLowerCase()))).forEach((item) -> {
            filteredList.add(item);
        });

        return filteredList;
    }

    private void setUserInputToOnlyOption(ComboBox<String> currentInstance, final TextField editor) {
        final String onlyOption = currentInstance.getItems().get(0);
        final String currentText = editor.getText();
        if (onlyOption.length() > currentText.length()) {
            editor.setText(onlyOption);
            Platform.runLater(() -> {
                editor.selectRange(currentText.length(), onlyOption.length());
            });
        }
    }
}