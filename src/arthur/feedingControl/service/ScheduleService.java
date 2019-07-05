package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

public interface ScheduleService {
	public List<HashMap> getScheduleList();
	public List<HashMap> getSchedule(int scheduleId);
	public List<HashMap> getScheduleDay(int scheduleId);
	public List<HashMap> getScheduleHour(int scheduleId);
	public void modifyShcedule(int scheduleId,HashMap<String,Integer> param);
	public void modifyShceduleDay(int scheduleId,JSONArray dataList);
	public void modifyShceduleHour(int scheduleId,JSONArray dataList);
}
