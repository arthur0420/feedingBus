package arthur.feedingControl.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.xml.internal.ws.api.server.SDDocumentFilter;

import arthur.feedingControl.main.RequestProcessor;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;

/**
 * Servlet implementation class EnterServlet
 */
@WebServlet("/feedData")
public class FeedDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeedDataServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		System.out.println("one request");
		File f= new File("/opt/tempFile");
		if(!f.exists()) {
			f.mkdirs();
		}
		String replaceAll = UUID.randomUUID().toString().replaceAll("-", "");
		
		File df = new File("/opt/tempFile/"+replaceAll+".csv");
		LogService ls = new LogServiceImp();
		List<HashMap> record = ls.getRecord();
		BufferedWriter bw = new BufferedWriter(new FileWriter(df));
		bw.write('\ufeff');
		bw.write("栏位id,饲喂时间,饲喂量（克）");
		bw.newLine();
		bw.flush();
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		for(int i = 0 ; i< record.size() ;i++) {
			HashMap one= record.get(i);
			int id = (int)one.get("cellid");
			long time = (long)one.get("time");
			int weight = (int)one.get("weight");
			String date= sdf.format(new Date(time));
			bw.write(id+","+date+","+weight);
			bw.newLine();
			bw.flush();
		}
		bw.close();
		response.setHeader("Content-Disposition", "attachment; filename=feedData.csv");
		
		InputStream is = new FileInputStream(df);
		OutputStream os = response.getOutputStream();
		
		int len = 0 ;
		byte[]buffer = new byte[1024];
		while( (len = is.read(buffer)) != -1){
			os.write(buffer, 0, len);
		}
		
		os.close();
		is.close();
	}
}
