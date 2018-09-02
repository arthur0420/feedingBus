package arthur.feedingControl.functions;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.ApartmentServiceImp;
import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.EventService;
import arthur.feedingControl.service.EventServiceImp;
import arthur.feedingControl.utils.DateFormat;

/**
 * 
 * @author arthu
 *	quick input
 */

public class Function1009 extends BaseFunction{
	static Logger logger = Logger.getLogger(ApartmentServiceImp.class);
	@Override
	public Result execute() {
		Result result = new Result();
//		console.log(event_no+":"+boar+":"+eventDate+":"+remark+":"+executor+":::"+id);
		String objects = getStrParameter("objects");
		String actions = getStrParameter("actions");
		
		JSONArray oa = JSONArray.fromString(objects);
		JSONArray aa = JSONArray.fromString(actions);
//		System.out.println(oa.toString());
//		System.out.println(aa.toString());
		
		//----------------------------------------------对象
		CellsService cs = new CellsServiceImp();
		Set<String> cellids = new HashSet<String>();
		
		for(int i = 0 ; i<oa.size(); i++){
			JSONObject one = oa.getJSONObject(i);
			String apid = one.getString("apid");
			String no_in_apartment = one.getString("no_in_apartment");
			List<HashMap> cells = cs.getCells(apid, null);
			try {
				if(no_in_apartment.indexOf("-")!=-1){ // 范围
					String[] split = no_in_apartment.split("-");
					if(split.length!=2){
						result.setError_info("编号范围非法");
						result.setError_no("-1");
						return result;
					}
					int start = Integer.parseInt(split[0]);
					int end = Integer.parseInt(split[1]);
					for (int j = start; j <= end; j++) {
						String cellId = getCellId(cells, j);
						cellids.add(cellId);
					}
				}else if(no_in_apartment.indexOf(",")!=-1){
					String[] split = no_in_apartment.split(",");
					for (int j = 0; j <split.length; j++) {
						int cellno = Integer.parseInt(split[j]);
						String cellId = getCellId(cells, cellno);
						cellids.add(cellId);
					}
				}else{
					int cellno = Integer.parseInt(no_in_apartment);
					String cellId = getCellId(cells, cellno);
					cellids.add(cellId);
				}	
			} catch (Exception e) {
				logger.error(e);
				result.setError_info("编号非法");
				result.setError_no("-1");
				return result;
			}
		}
		if(cellids.size() == 0){
			result.setError_info("编号范围非法");
			result.setError_no("-1");
			return result;
		}
		//--------------------------------------------------操作
		
		for(int i = 0 ; i<aa.size();i++){
			JSONObject oneo = aa.getJSONObject(i);
			String actionid = oneo.getString("actionid"); //1 是 添加事件
			String actionvalue = oneo.getString("actionValue"); 
			if(actionid.equals("1")){ // 添加事件
				EventService es = new EventServiceImp();
				Iterator<String> iterator = cellids.iterator();
				while(iterator.hasNext()){
					String cellid = iterator.next();
					HashMap param = new HashMap();
					String nowDate = null;
					nowDate = DateFormat.toString(new Date());
					param.put("cell_id", cellid);
					param.put("event_no", actionvalue);
					param.put("date",nowDate );
					es.addEvent(param);
				}
			}else if(actionid.equals("2")){
				Iterator<String> iterator = cellids.iterator();
				while(iterator.hasNext()){
					String cellid = iterator.next();
					cs.clearCell(Integer.parseInt(cellid));
				}
			}
		}
		
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
	public String getCellId(List<HashMap> cells , int no_in_apartment){
		for (int i = 0; i < cells.size(); i++) {
			HashMap one = cells.get(i);
			String no = one.get("no_in_apartment")+"";
			if(no.equals(no_in_apartment+"")){
				return one.get("id")+"";
			}
		}
		return "";
	}
	
}
