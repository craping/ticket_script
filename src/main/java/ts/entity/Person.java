package ts.entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {
	
	private String id;
	
	private String name;
	
	private final StringProperty status = new SimpleStringProperty("未执行");
	
	public Person() {
	}

	public Person(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status.get();
	}

	public StringProperty statusProperty() {
		return status;
	}
	public void setStatus(String status) {
		this.status.set(status);
	}
	
}
