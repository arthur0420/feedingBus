package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;

public interface CellsService {
	public List<HashMap> getCells(String apartmentId,String noInApartment);
	public List<HashMap> getCellsEvents(String cellId);
	public HashMap getCellsStatistic(String apartmentId);
	public void modifyCell(int id ,int skip_time ,int offset ,int switchh);
	public void initCell(int id);
	public void clearCell(int id);
	public HashMap getCellLocationById(int id);
}
