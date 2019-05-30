package ru.easyxbrl.javafx_ui;

import javafx.scene.control.ComboBox;
import javafx.scene.control.skin.ComboBoxListViewSkin;

public class TableCellComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

	public TableCellComboBoxListViewSkin(ComboBox<T> control) {
		super(control);
	}
}
