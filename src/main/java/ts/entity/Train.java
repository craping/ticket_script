package ts.entity;

import java.util.Arrays;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class Train {
	
	private String[] date;
	
	private String[] no;
	
	private String from;
	
	private String to;
	
	private String seat;
	
	private ObservableList<Person> persons = FXCollections.observableArrayList();
	
	private final StringProperty text = new SimpleStringProperty();
	
	public String[] getDate() {
		return date;
	}

	public void setDate(String[] date) {
		this.date = date;
	}

	public String[] getNo() {
		return no;
	}

	public void setNo(String[] no) {
		this.no = no;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
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
		FilteredList<Person> undo = persons.filtered(p -> !p.getStatus().equals("未执行"));
		FilteredList<Person> success = persons.filtered(p -> !p.getStatus().equals("成功"));
		
		return "日期："+Arrays.toString(date) 
		+ " 车次：" + Arrays.toString(no) 
		+ " 站点：["+from+"→"+to+"] "
		+ "座位：["+seat +"] "
		+ "票数：[共"+persons.size()+"张，执行"+undo.size()+"张，成功"+success.size()+"张，剩余"+(persons.size()-undo.size())+"张]";
	}
}
