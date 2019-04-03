package arthur.feedingControl.entity;

import java.io.Serializable;
import java.util.HashMap;

public class Task implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String ip ; // ip 
	public int sp ; // 串口号 1-2
	public int rs ; //地址码 1-10  冗余字段 数据里有。
	public byte[] data ; // 数据
	public HashMap[] celldata ;
}
