package recipe.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

public class QueryResultData extends QueryResultModel {

	private String about;
	private LinkedHashMap<String, String> data;
	private LinkedHashMap<String, JSONObject> objectData;
	
	public void setSubject(String about) {
		this.about = about;
	}
	
	public void addData(LinkedHashMap<String, String> data) {
		this.data = data;
	}
	
	public void addObjectData(LinkedHashMap<String, JSONObject> data) {
		this.objectData = data;
	}
	
	public void putData(String key, String value) {
		if (this.data == null ) {
			this.data = new LinkedHashMap<String, String>();
		}
		
		this.data.put(key, value);
	}
	
	public void putObjectData(String key, JSONObject data) {
		if( this.objectData == null ) {
			this.objectData = new LinkedHashMap<String, JSONObject>();
		}
		
		this.objectData.put(key, data);
	}
	
	public String getSubject() {
		return this.about;
	}
	
	public Map<String, String> getData() {
		return this.data;
	}
	
	public String getData(String key) {
		return this.data.get(key);
	}
	
	public Map<String, JSONObject> getObjectData() {
		return this.objectData;
	}
	
	public JSONObject getJSONData(String property) {
		return this.objectData.get(property);
	}
}
