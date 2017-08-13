package recipe.model;

public class QueryResultModel {

	private String object; // Resep
	private String subObject1; 
	private String subObject2; 
	private String subObject3; 
	
	public void setObject(String object) {
		this.object = object;
	}
	
	public String getObject() {
		return this.object;
	}
		
	public void setSubObject1(String subObject1) {
		this.subObject1 = subObject1;
	}
	
	public String getSubObject1() {
		return this.subObject1;
	}
	
	public void setSubObject2(String subObject2) {
		this.subObject2 = subObject2;
	}
	
	public String getSubObject2() {
		return this.subObject2;
	}
	
	public void setSubObject3(String subObject3) {
		this.subObject2 = subObject3;
	}
	
	public String getSubObject3() {
		return this.subObject3;
	}
}
