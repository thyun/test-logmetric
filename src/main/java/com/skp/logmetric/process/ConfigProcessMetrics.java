package com.skp.logmetric.process;

import java.util.List;

import com.skp.logmetric.config.CommonAddField;
import com.skp.logmetric.config.ConfigItem;
import lombok.Data;

@Data
public class ConfigProcessMetrics implements ConfigItem {
	String type;
	String key;
	List<String> meter;
	List<MeterRange> meter_range;
	List<CommonAddField> add_field;

/*	private List<MeterRange> jsonarray2MeterRangeList(JSONArray jsonArray) {
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
	} */

	public void prepare() {		
	}
}
