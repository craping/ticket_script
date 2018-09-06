package ts.entity;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Ticket {
	
	private List<Train> trains = new ArrayList<>();
	
	private ObservableList<Person> persons = FXCollections.observableArrayList();
	
	private final StringProperty text = new SimpleStringProperty();
	
	public List<Train> getTrains() {
		return trains;
	}

	public void setTrains(List<Train> trains) {
		this.trains = trains;
	}
	
	public ObservableList<Person> getPersons() {
		return persons;
	}

	public void setPersons(ObservableList<Person> persons) {
		this.persons = persons;
	}
	
	public void setText(String text) {
		this.text.set(text);
	}
	
	public String getText() {
		return this.text.get();
	}
	
	public StringProperty textProperty() {
		refresh();
		return text;
	}
	
	public void refresh() {
		this.text.set(toString());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(this.trains != null) {
			
			for (Train train : trains) {
				sb.append(train.toString()).append("\r");
			}
			if(sb.length() > 0)
				sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
}
