package ru.easyxbrl.javafx_ui;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

/**
 * Wrapper for ComboBox with selected item
 * @author a.starikov
 *
 * @param <T>
 */
public class ComboBoxChecked<T> {
	
	ComboBox<T> comboBox;
	final ObservableMap<T,BooleanProperty> itemsMap = FXCollections.observableHashMap();
	final StringProperty comboBoxTextPrompt = new SimpleStringProperty();
	final BooleanProperty hideOnClick = new SimpleBooleanProperty(false);
	
	@SuppressWarnings("unchecked")
	public ComboBoxChecked(ComboBox<T> comboBox) {
		this.comboBox = comboBox;
		
		comboBox.setButtonCell(new ListCell<T>() {
			@Override
	        protected void updateItem(T item, boolean empty) {
	            super.updateItem(item, empty);
	            setText(comboBoxTextPrompt.getValue());
	        }
		});
		
		// bind combobox text caption
		comboBox.promptTextProperty().bind(comboBoxTextPrompt);
		
		// change items datasource
		comboBox.itemsProperty().addListener(new ChangeListener<ObservableList<T>>() {
			@Override
			public void changed(ObservableValue<? extends ObservableList<T>> observable, ObservableList<T> oldValue,
					ObservableList<T> newValue) {
				init();
			}
		});
		
		// set checked listView
		comboBox.setCellFactory(listView);

		// check/uncheck on SPACE or ENTER
    		comboBox.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
    		if (!comboBox.getSelectionModel().isEmpty() 
    				&& (event.getCode().equals(KeyCode.SPACE) || event.getCode().equals(KeyCode.ENTER))) {
    			T item = comboBox.getSelectionModel().getSelectedItem();
    			itemsMap.get(item).set(!itemsMap.get(item).get());
				setComboBoxPromptText();
    		}
    	});

    	comboBox.setSkin( new ComboBoxListViewSkin<T>( comboBox ));
    	((ComboBoxListViewSkin<T>)comboBox.getSkin()).setHideOnClick(hideOnClick.get());
    	hideOnClick.addListener(listener -> ((ComboBoxListViewSkin<T>)comboBox.getSkin()).setHideOnClick(hideOnClick.get()));
    	
		init();
	}

	// init
	private void init() {
		itemsMap.clear(); // clear for change itemList
		comboBox.getItems().forEach( item -> itemsMap.put(item, new SimpleBooleanProperty(false)));
		comboBox.getItems().addListener(new ListChangeListener<T>() {
			@Override
			public void onChanged(Change<? extends T> c) {
		         while (c.next()) {
		             if (c.wasPermutated() || c.wasUpdated()) {
		            	 setComboBoxPromptText();
		             } else {
		            	 //  c.wasReplaced() == c.wasRemoved() + c.wasAdded()
                     	for (T remitem : c.getRemoved()) { itemsMap.remove(remitem); }
                     	for (T additem : c.getAddedSubList()) { 
                     		itemsMap.put(additem, new SimpleBooleanProperty(false));
                     	}
                     	if ( !c.getRemoved().isEmpty() || !c.getAddedSubList().isEmpty()) { 
                     		setComboBoxPromptText();
                     	}
		             }
		         }
		    }
		});
		setComboBoxPromptText();
	}

	// text caption
	private void setComboBoxPromptText() {
		StringBuilder sb = new StringBuilder();
		comboBox.getItems().filtered( f -> itemsMap.get(f).get()).forEach( p -> {
			if (sb.length()!=0) {
				sb.append("; ");
			}
			sb.append(p);
		});
		comboBoxTextPrompt.set(sb.toString());
	}
	
	// view checkbox on listCell with mouse check/uncheck
	Callback<ListView<T>, ListCell<T>> listView = new Callback<>() {
		@Override
		public ListCell<T> call(ListView<T> param) {
	    	ListCell<T> cell = new ListCell<>(){
	    		@Override
	    		protected void updateItem(T item, boolean empty) {
	    			super.updateItem(item, empty);
	    			if (!empty) {
	    				final CheckBox cb = new CheckBox(item.toString());
	    				cb.setWrapText(true);
	    				cb.selectedProperty().bindBidirectional(itemsMap.get(item));
	    				cb.selectedProperty().addListener( l -> {
	    					itemsMap.get(item).setValue(cb.selectedProperty().getValue());
	    					setComboBoxPromptText();
	    				});
	    				setGraphic(cb);
	    			}
	    		}
	    	};
	    	cell.prefWidthProperty().bind(comboBox.widthProperty());
	    	return cell;
		}
	};
	
	// set check/uncheck for item
	public void setSelected(T item, boolean sel) {
		if (itemsMap.containsKey(item)) {
			itemsMap.get(item).set(sel);
		}
	}

	// get check/uncheck for item
	public boolean isSelected(T item) {
		return itemsMap.get(item).get();
	}
	
	// get selected map
	public Map<T,T> getSelectedItemMap(){
		return itemsMap.entrySet()
				.stream()
				.filter( p -> p.getValue().getValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getKey));
	}
	
	// get selected list
	public List<T> getSelectedItemList(){
		return itemsMap.entrySet()
				.stream()
				.filter( p -> p.getValue().getValue())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	// hideOnClickProperty
	public BooleanProperty hideOnClickProperty() {
		return hideOnClick;
	}
	
}