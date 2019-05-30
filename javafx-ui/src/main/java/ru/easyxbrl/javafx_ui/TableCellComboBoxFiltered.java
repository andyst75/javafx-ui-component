package ru.easyxbrl.javafx_ui;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ru.easyxbrl.javafx_ui.ComboBoxFiltered.Matches;

/**
 * ComboBox autocomplete for TableView
 * 
 * need add to css : .table-cell .combo-box .text-field { -fx-padding: 0; }  
 * @author a.starikov
 *
 * @param <S>
 * @param <T>
 */
public class TableCellComboBoxFiltered <S,T> extends TableCell<S,T> {
	
    private ComboBoxFiltered<T> comboBox;
    private ObservableList<T> items;
    private TableCellComboBoxListViewSkin<?> skin; 

    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter,
            final ObservableList<T> items,
            final Matches<T> matches) {
        return list -> new TableCellComboBoxFiltered<S,T>(converter, items, matches);
    }
    
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter,
            final ObservableList<T> items) {
        return list -> new TableCellComboBoxFiltered<S,T>(converter, items);
    }

    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final ObservableList<T> items) {
        return list -> new TableCellComboBoxFiltered<S,T>(items);
    }
	
	
    public TableCellComboBoxFiltered() {
    	this(FXCollections.observableArrayList());
    }


    public TableCellComboBoxFiltered(ObservableList<T> items) {
    	this(null, items, null);
    }

	public TableCellComboBoxFiltered(StringConverter<T> converter, ObservableList<T> items) {
    	this(converter, items, null);
	}
    
	public TableCellComboBoxFiltered(StringConverter<T> converter, ObservableList<T> items, Matches<T> matches) {
        
		this.items = items;
		comboBox = new ComboBoxFiltered<>(converter, items, matches);
		skin = new TableCellComboBoxListViewSkin<>(comboBox);
        comboBox.setEditable(true);
        listWidthProperty().set(USE_PREF_SIZE);
        
        setComboBoxListItemOnly(false);

        matchesProperty().bindBidirectional(comboBox.matchesProperty());
        comboBoxEditableProperty().bindBidirectional(comboBox.editableProperty());
        converterProperty().bindBidirectional(comboBox.converterProperty());
        
    	comboBox.wrapWidthTextProperty().bindBidirectional(listWidthProperty());
        
        comboBox.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                comboBoxCommit(comboBox, this);
            } else if (e.getCode() == KeyCode.ESCAPE) {
                this.cancelEdit();
            }
        });

        comboBox.getEditor().focusedProperty().addListener(o -> {
            if (!comboBox.isFocused()) {
                comboBoxCommit(comboBox, this);
            }
        });

        boolean success = listenToComboBoxSkin();
        if (!success) {
            comboBox.skinProperty().addListener(new InvalidationListener() {
                @Override public void invalidated(Observable observable) {
                    boolean successInListener = listenToComboBoxSkin();
                    if (successInListener) {
                        comboBox.skinProperty().removeListener(this);
                    }
                }
            });
        }

        comboBox.getSelectionModel().select(getItem());
        
        super.startEdit();
        setText(null);
        setGraphic(comboBox);

	}

	/**
	 * Update TableCell value
	 * @param comboBox
	 * @param cell
	 */
	
    private void comboBoxCommit(ComboBoxFiltered<T> comboBox, Cell<T> cell) {
    	
    	if (comboBox.isEditable()) {
    		StringConverter<T> sc = getConverter();
    		String text = comboBox.getEditor().getText();
            T value = sc.fromString(text);
            
        	if (!isComboBoxListItemOnly()) {
        		cell.commitEdit(value);	
        	} else {
        		// workaround for "bad" stringconverter - compare text only
        		for(T item:comboBox.getItems()) {
        			if (comboBox.getEditor().getText().equals(sc.toString(item))) {
        				cell.commitEdit(value);
        				return;
        			}
        		}
        		cell.cancelEdit();
        	}
    	} else {
    		cell.commitEdit(comboBox.getValue());
    	}
    	
    }
    
    private boolean listenToComboBoxSkin() {
        Node popupContent = skin.getPopupContent();
        if (popupContent != null && popupContent instanceof ListView) {
            popupContent.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> commitEdit(comboBox.getValue()));
            return true;
        }
    	
        return false;
    }


    /**
     * Custom properties
     * 
     */
    
    private ObjectProperty<Matches<T>> matches =
            new SimpleObjectProperty<Matches<T>>(this, "matches");
	
    public final ObjectProperty<Matches<T>> matchesProperty() {
        return matches;
    }

    public final void setMatches(Matches<T> value) {
    	matchesProperty().set(value);
    }

    public final Matches<T> getMatches() {
        return matchesProperty().get();
    }

    
    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }

    private DoubleProperty listWidth =
            new SimpleDoubleProperty(this, "listWidth");

    public final DoubleProperty listWidthProperty() {
        return listWidth;
    }

    public final void setListWidth(Double value) {
    	listWidthProperty().set(value);
    }

    public final Double getListWidth() {
        return listWidthProperty().get();
    }
    
    
    private BooleanProperty comboBoxEditable =
            new SimpleBooleanProperty(this, "comboBoxEditable");

    public final BooleanProperty comboBoxEditableProperty() {
        return comboBoxEditable;
    }

    public final void setComboBoxEditable(boolean value) {
        comboBoxEditableProperty().set(value);
    }

    public final boolean isComboBoxEditable() {
        return comboBoxEditableProperty().get();
    }

    private BooleanProperty comboBoxListItemOnly =
            new SimpleBooleanProperty(this, "comboBoxListItemOnly");

    public final BooleanProperty comboBoxListItemOnlyProperty() {
        return comboBoxListItemOnly;
    }

    public final void setComboBoxListItemOnly(boolean value) {
    	comboBoxListItemOnlyProperty().set(value);
    }

    public final boolean isComboBoxListItemOnly() {
        return comboBoxListItemOnlyProperty().get();
    }

    // original itemlist
    public ObservableList<T> getItems() {
        return items;
    }
    
    // get ComboBox element
    public ComboBox<T> getComboBox() {
    	return comboBox;
    }
    
    /**
     * 
     * Override TableCell methods
     *  
     */
    
    @Override
    public void startEdit() {
        if (! isEditable() || ! getTableView().isEditable() || ! getTableColumn().isEditable()) {
            return;
        }

        comboBox.resetPredicate();
        
        Platform.runLater(()->comboBox.getSelectionModel().select(getItem()));

        super.startEdit();
        setText(null);
        setGraphic(comboBox);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(comboBox.getConverter().toString(getItem()));
        setGraphic(null);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        
        if (isEmpty()) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (comboBox != null) {
                    comboBox.getSelectionModel().select(getItem());
                }
                setText(null);
                setGraphic(comboBox);
            } else {
                setText(comboBox.getConverter() == null ? 
                		( getItem() == null ? "" : getItem().toString() ) :
                			comboBox.getConverter().toString(getItem()));
                setGraphic(null);
            }
        }
    }

    
}
