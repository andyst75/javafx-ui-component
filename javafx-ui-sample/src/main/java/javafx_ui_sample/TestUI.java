package javafx_ui_sample;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import ru.easyxbrl.javafx_ui.ComboBoxChecked;
import ru.easyxbrl.javafx_ui.ComboBoxFiltered;
import ru.easyxbrl.javafx_ui.TableCellComboBoxFiltered;
import ru.easyxbrl.javafx_ui.TableCellDataPicker;

public class TestUI extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		 FlowPane pane = new FlowPane();
		 pane.setPadding(new Insets(10));
		 pane.setHgap(5);
		 pane.setVgap(10);
		 
		 final Random random = new Random();
		 
		 ObservableList<Person> persons = FXCollections.observableArrayList(Person.observablePropertyCallback);
		 persons.add(new Person("firstName1", "lastName1", LocalDate.of(1980, 02, 11)));
		 persons.add(new Person("firstName2", "lastName2", LocalDate.of(1968, 11, 03)));
		 persons.add(new Person("firstName3", "lastName3", LocalDate.of(1973, 03, 17)));
		 persons.add(new Person("firstName4", "lastName4", LocalDate.of(1992, 06, 06)));
		 persons.add(new Person("firstName5", "lastName5", LocalDate.of(1988, 05, 23)));
		 
		 ComboBox<Person> cb = new ComboBox<>();
		 cb.prefWidthProperty().set(300);
		 ComboBoxChecked<Person> cbc = new ComboBoxChecked<>(cb);
		 cb.setItems(persons);
		 
		 Button btAdd = new Button("add person");
		 btAdd.setOnAction(e->{
			 persons.add(new Person("first"+random.nextInt(),"last"+random.nextInt(), LocalDate.of(ThreadLocalRandom.current().nextInt(1940, 2000), 01, 01)));
		 });
		 
		 Button btRem = new Button("remove person");
		 btRem.setOnAction(e->{
			 if (!cb.getSelectionModel().isEmpty()) {
				 cb.getItems().remove(cb.getSelectionModel().getSelectedIndex());
			 }
		 });
		 Button btRemSel = new Button("remove selected");
		 btRemSel.setOnAction(e->{ persons.removeAll(cbc.getSelectedItemList()); });
		 
		 Button btViewSel = new Button("view selected");
		 btViewSel.setOnAction(e->{
			 Alert alert = new Alert(AlertType.INFORMATION);
			 alert.setContentText(cbc.getSelectedItemList().stream().map(p -> p.toString()).collect(Collectors.toList()).toString());
			 alert.showAndWait();
		 });

		 ObservableList<String> items = FXCollections.observableArrayList();
		 for (int i=1; i<10; i++) {
			 items.add("item"+i);
		 }
		 
		 HBox hbox1 = new HBox();
		 
		 ComboBoxFiltered<String> cbf = new ComboBoxFiltered<String>(items);
		 hbox1.getChildren().add(cbf);
		 cbf.setEditable(true);
		 
		 TableView<Person> tbl = new TableView<>();
		 tbl.setItems(persons);
		 tbl.setEditable(true);

		 TableColumn<Person, String> col1 = new TableColumn<>();
		 col1.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		 col1.setCellFactory(TableCellComboBoxFiltered.forTableColumn(items));
		 col1.setEditable(true);
		 col1.prefWidthProperty().setValue(200);

		 TableColumn<Person, String> col2 = new TableColumn<>();
		 col2.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		 col2.setCellFactory(new Callback<TableColumn<Person,String>,TableCell<Person,String>>(){
			@Override
			public TableCell<Person, String> call(TableColumn<Person, String> param) {
				final TableCellComboBoxFiltered<Person, String> tcbf = new TableCellComboBoxFiltered<Person, String>(items);
				tcbf.setComboBoxListItemOnly(true);
				return tcbf;
			}
			 
		 });
		 col2.setEditable(true);
		 col2.prefWidthProperty().setValue(200);

		 TableColumn<Person, LocalDate> col3 = new TableColumn<>();
		 col3.setCellValueFactory(new PropertyValueFactory<>("birthday"));
		 col3.setCellFactory(TableCellDataPicker.forTableColumn());
		 col3.setEditable(true);
		 col3.prefWidthProperty().setValue(200);
		 
		 tbl.getColumns().add(col1);
		 tbl.getColumns().add(col2);
		 tbl.getColumns().add(col3);
		 
		 tbl.prefWidthProperty().set(780);
		 
		 pane.getChildren().addAll(new Label("Persons:"), cb, btAdd, btRem, btRemSel, btViewSel, hbox1, tbl);
		 
		 Scene scene = new Scene(pane,800,600);
		 
		 scene.getStylesheets().add(getClass().getResource("sample.css").toExternalForm());
		 
		 primaryStage.setScene(scene);
		 primaryStage.setTitle("Test UI");
		 primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
