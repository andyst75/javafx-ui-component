package javafx_ui_sample;

import java.time.LocalDate;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/**
 * Sample class Person
 * @author a.starikov
 *
 */
public class Person implements Comparable<Person> {

	private StringProperty firstName = new SimpleStringProperty();
	private StringProperty lastName = new SimpleStringProperty();
	private ObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>();

	public Person() {
	}
	
	public Person(String firstName, String lastName, LocalDate birthday) {
		setFirstNameValue(firstName);
		setLastNameValue(lastName);
		setBirthdayValue(birthday);
	}
	
	public StringProperty firstNameProperty() {
		return firstName;
	}
	
	public String getFirstNameValue() {
		return firstName.getValue();
	}
	
	public void setFirstNameValue(String value) {
		firstName.setValue(value);
	}
	
	public StringProperty lastNameProperty() {
		return lastName;
	}
	
	public String getLastNameValue() {
		return lastName.getValue();
	}
	
	public void setLastNameValue(String value) {
		lastName.setValue(value);
	}
	
	public ObjectProperty<LocalDate> birthdayProperty() {
		return birthday;
	}
	
	public LocalDate getBirthdayValue() {
		return birthday.getValue();
	}
	
	public void setBirthdayValue(LocalDate value) {
		birthday.setValue(value);
	}
	
	@Override
	public String toString() {
		return getFirstNameValue()+" "+getLastNameValue()+" ("+getBirthdayValue()+")";
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (!(other instanceof Person))return false;
		Person otherPerson = (Person)other;
		return firstName.getValueSafe().equals(otherPerson.getFirstNameValue())
				&& lastName.getValueSafe().equals(otherPerson.getLastNameValue())
				&& birthday.getValue().equals(otherPerson.getBirthdayValue());
	}
	
	@Override
	public int compareTo(Person other) {
		return (firstName.getValue()+lastName.getValue()+birthday.getValue().toEpochDay()).compareToIgnoreCase(
				other.firstName.getValue()+other.lastName.getValue()+other.birthday.getValue().toEpochDay());
	}	

	public static Callback<Person, Observable[]> observablePropertyCallback = new Callback<Person, Observable[]>() {

	    @Override
	    public Observable[] call(Person p) {
	        return new Observable[] {p.lastNameProperty(), p.firstNameProperty(), p.birthdayProperty()};
	    }
	};
}