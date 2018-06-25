package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;

public interface ScheduleService {
	public List<HashMap> getSchedule(int scheduleId);
	public List<HashMap> getScheduleDay(int scheduleId);
	public List<HashMap> getScheduleHour(int scheduleId);
}
