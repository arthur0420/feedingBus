package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;

public interface CellsService {
	public List<HashMap> getCells(String apartmentId,String noInApartment);
	public List<HashMap> getCellsEvents(String apartmentId,String noInApartment);
}
