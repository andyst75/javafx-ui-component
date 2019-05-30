package ru.easyxbrl.javafx_ui;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class TableCellDataPicker <S,T> extends TableCell<S,LocalDate> {
	
	private DatePicker datePicker ;

    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,LocalDate>> forTableColumn(
            final String format) {
        return list -> new TableCellDataPicker<S,T>(format);
    }
	
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,LocalDate>> forTableColumn() {
        return list -> new TableCellDataPicker<S,T>();
    }
	
	public TableCellDataPicker() {
		this("dd.MM.yyyy");
	}
	
	public TableCellDataPicker(String format) {
		
		setFormatter(DateTimeFormatter.ofPattern(format));
		datePicker = new DatePicker();
		
		datePicker.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
            	// silent conversion error
                try {
                    datePicker.setValue(datePicker.getConverter().fromString(datePicker.getEditor().getText()));
                	Platform.runLater(() -> commitEdit(datePicker.getValue()));
                } finally {}
                
            }
            if (event.getCode() == KeyCode.ESCAPE) {
            	Platform.runLater(() -> cancelEdit());
            }
		});
		
		datePicker.setDayCellFactory(picker -> {
            final DateCell cell = new DateCell();
            cell.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                datePicker.setValue(cell.getItem());
                if (event.getClickCount() == 2) {
                    datePicker.hide();
                    Platform.runLater(() -> commitEdit(cell.getItem()));
                }
                event.consume();
            });
            cell.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                	Platform.runLater(() -> commitEdit(datePicker.getValue()));
                }
            });
            return cell ;
		});
		
		contentDisplayProperty().bind(Bindings.when(editingProperty())
                .then(ContentDisplay.GRAPHIC_ONLY)
                .otherwise(ContentDisplay.TEXT_ONLY));
	}
	
	@Override
    public void updateItem(LocalDate date, boolean empty) {
        super.updateItem(date, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(date.format(getFormatter()));
            setGraphic(datePicker);
        }
    }
    
    @Override
    public void startEdit() {
        super.startEdit();
        if (!isEmpty()) {
            datePicker.setValue(getItem()==null?LocalDate.now():getItem());
        }
    }

    private ObjectProperty<DateTimeFormatter> formatter =
            new SimpleObjectProperty<DateTimeFormatter>(this, "formatter");
	
    public final ObjectProperty<DateTimeFormatter> formatterProperty() {
        return formatter;
    }

    public final void setFormatter(DateTimeFormatter value) {
    	formatterProperty().set(value);
    }

    public final DateTimeFormatter getFormatter() {
        return formatterProperty().get();
    }
    

}
