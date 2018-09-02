package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;

public interface EventService {
	public List<HashMap> getEvent(String id);
	public void addEvent(HashMap param);
	
}
