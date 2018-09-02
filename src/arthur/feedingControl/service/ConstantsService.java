package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;

public interface ConstantsService {
	public List<HashMap> getConstants();
	public void updateConstant(String enname,Integer days, Integer switchh);
}
