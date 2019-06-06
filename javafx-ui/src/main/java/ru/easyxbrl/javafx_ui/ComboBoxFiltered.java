package ru.easyxbrl.javafx_ui;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * 
 * ComboBox with filtered list (autocomplete)
 * 
 * @author a.starikov
 *
 * @param <T>
 */
public class ComboBoxFiltered<T> extends ComboBox<T> {
	
	public interface Matches<T> {
	    boolean matches(String typedText, T p);
	}	

	private FilteredList<T> filter;
	
	public ComboBoxFiltered(){
		this(FXCollections.observableArrayList());
	}
	
	public ComboBoxFiltered(ObservableList<T> items){
		this(items, null);
	}

	public ComboBoxFiltered(ObservableList<T> items, Matches<T> matches){
		this(null, items, matches);
	}

	public ComboBoxFiltered(StringConverter<T> converter){
		this(converter, FXCollections.observableArrayList(), null);
	}

	public ComboBoxFiltered(StringConverter<T> converter, Matches<T> matches){
		this(converter, FXCollections.observableArrayList(), matches);
	}
	
	public ComboBoxFiltered(StringConverter<T> converter, ObservableList<T> items, Matches<T> matches){
		super();
		
		setConverter(converter != null ? converter : (new StringConverter<T>() {
            @Override public String toString(T t) {
                return t == null ? null : t.toString();
            }

            @SuppressWarnings("unchecked")
			@Override public T fromString(String string) {
                return (T) string;
            }
        }));
		
		setMatches(matches != null ? matches : (text,o) -> o == null ? true : getConverter().toString(o).contains(text));

		// TODO: capture item add && addAll
		
		// reset filter for new itemList
        itemsProperty().addListener(new ChangeListener<ObservableList<T>>() {
			@Override
			public void changed(ObservableValue<? extends ObservableList<T>> observable, ObservableList<T> oldValue,
					ObservableList<T> newValue) {
				if (newValue!=null) {
					Platform.runLater(() -> {
						filter = new FilteredList<>(FXCollections.observableArrayList(newValue), p -> true);
						setItems(filter);
					});
				}
			}
		});

    	setItems(items);
        
		getEditor().addEventHandler(KeyEvent.KEY_PRESSED, t -> hide());
		getEditor().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

	        private boolean moveCaretToPos = false;
	        private int caretPos;

	        @Override
	        public void handle(KeyEvent event) {
	            if (event.getCode() == KeyCode.UP) {
	                caretPos = -1;
	                moveCaret(getEditor().getText().length());
	                return;
	            } else if (event.getCode() == KeyCode.DOWN) {
	                if (!isShowing()) {
	                    show();
	                }
	                caretPos = -1;
	                moveCaret(getEditor().getText().length());
	                return;
	            } else if (event.getCode() == KeyCode.BACK_SPACE) {
	                moveCaretToPos = true;
	                caretPos = getEditor().getCaretPosition();
	            } else if (event.getCode() == KeyCode.DELETE) {
	                moveCaretToPos = true;
	                caretPos = getEditor().getCaretPosition();
	            } else if (event.getCode() == KeyCode.ENTER) {
	                return;
	            }

	            if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.getCode().equals(KeyCode.SHIFT) || event.getCode().equals(KeyCode.CONTROL)
	                    || event.isControlDown() || event.getCode() == KeyCode.HOME
	                    || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
	                return;
	            }

	            Platform.runLater(() -> {
	            	
		            final String t = getEditor().getText();
		            filter.setPredicate( p -> getMatches().matches(t, p));
		            getEditor().setText(t);
		            
		            if (!moveCaretToPos) {
		                caretPos = -1;
		            }
		            moveCaret(t.length());
		            if (!getItems().isEmpty()) {
		                show();
		            }
	            });
	            
	        }

	        private void moveCaret(int textLength) {
	            if (caretPos == -1) {
	                getEditor().positionCaret(textLength);
	            } else {
	                getEditor().positionCaret(caretPos);
	            }
	            moveCaretToPos = false;
	        }
	    });

        setCellFactory(item -> {
			ListCell<T> cell = new ListCell<T>() {
				@Override
				protected void updateItem(T item, boolean empty) {
					super.updateItem(item, empty);
					setGraphic(null);
					if (item != null && !empty) {
						final Text text = new Text(getConverter().toString(item));
						text.wrappingWidthProperty().bind(wrapWidthTextProperty());
						setGraphic(text);
					}
				}
			};

			cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			
			return cell;

		});
        this.
        setMaxWidth(Double.MAX_VALUE);
   }
	
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

    private DoubleProperty wrapWidthText =
            new SimpleDoubleProperty(this, "wrapWidthText");
	
    public final DoubleProperty wrapWidthTextProperty() {
        return wrapWidthText;
    }

    public final void setWrapWidthText(Double value) {
    	wrapWidthTextProperty().set(value);
    }

    public final Double getWrapWidthText() {
        return wrapWidthTextProperty().get();
    }

    public ObservableList<? extends T> getSourceItems(){
    	return filter.getSource();
    }
    
    public void resetPredicate() {
    	Platform.runLater( () -> filter.setPredicate( p -> true ) );
    }
    
}
