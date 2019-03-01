package com.skp.logmetric.process;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skp.logmetric.config.CommonAddField;
import com.skp.logmetric.config.ConfigItem;
import com.skp.util.CommonHelper;

import lombok.Data;
import lombok.Getter;

@Data
public class ConfigProcessMetrics implements ConfigItem {
	String type;
	String key;
	List<String> meter;
	List<MeterRange> meter_range;
	List<CommonAddField> add_field;
	
/*	@Data
	public class MeterRange {
		String field;
		long unit;
		
		public MeterRange(String field, long unit) {
			this.field = field;
			this.unit = unit;
		} 

	} */

/*	public ConfigProcessMetrics(JSONObject j) {
		super();
		init(j);
	} */

/*	public void init(JSONObject j) {
		type = (String) j.get("type");
		key = (String) j.get("key");
		meter = CommonHelper.jsonarray2StringList(j.optJSONArray("meter"));
		meter_range = jsonarray2MeterRangeList(j.optJSONArray("meter_range"));
		
		add_field = CommonAddField.jsonarray2List(j.optJSONArray(CommonAddField.NAME));
	} */

	private List<MeterRange> jsonarray2MeterRangeList(JSONArray jsonArray) {
		ArrayList<MeterRange> r = new ArrayList<>();
		if (jsonArray == null)
			return r;
		for (int i=0; i<jsonArray.length(); i++) {
			JSONObject jo = (JSONObject) jsonArray.get(i);
			
			MeterRange mr = new MeterRange();
			mr.setField(jo.getString("field"));
			mr.setUnit(jo.getLong("unit"));
			r.add(mr);
		}
		return r;
	}

	public void prepare() {		
	}
}
