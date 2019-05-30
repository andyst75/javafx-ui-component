# javafx-ui &mdash; components for JavaFX

See [more examples!](../../tree/master/javafx-ui-sample/src/main/java/javafx_ui_sample)

# ComboBoxChecked
Simple wrapper for JavaFX ComboBox with check items.

**Usage:**

<details>
  <summary>sample code for ComboBox</summary>

```java

import javafx_ui_sample.Person;

  ObservableList<Person> persons = 
      FXCollections.observableArrayList(Person.observablePropertyCallback);
  var person = new Person();
  persons.add(person);

  ComboBox<Person> cb = new ComboBox<>();
  ComboBoxChecked<Person> cbc = new ComboBoxChecked<>(cb);
  
  var selectedList = cbc.getSelectedItemList();
  cbc.setSelected(person, false);
  System.out.println(cbc.isSelected(person));
```
</details>
