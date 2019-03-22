package modelio2java;

public class Attribute {
	private String type;
	private String name;
	public boolean isEnum = false;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

}
