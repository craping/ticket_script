package ts.entity;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.StringProperty;

public class Person {
	
	private int cellIndex;
	
	private String id;
	
	private String name;
	
	private String type;
	
//	private final StringProperty status = new SimpleStringProperty("未执行");
	
	private Map<String, StringProperty> status = new HashMap<>();

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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, StringProperty> getStatus() {
		return status;
	}

	public void setStatus(Map<String, StringProperty> status) {
		this.status = status;
	}

	public int getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}

	/*public String getStatus() {
		return status.get();
	}

	public StringProperty statusProperty() {
		return status;
	}
	public void setStatus(String status) {
		this.status.set(status);;
	}*/
	
	
}
