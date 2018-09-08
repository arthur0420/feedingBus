package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arthur.feedingControl.utils.ConvertHelper;

public class UserServiceImp extends BaseService implements UserService  {
	private static String defaultPsw = "123456";
	@Override
	public Map<String, String> queryUser(String userName) {
		
		Connection con = getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "select * from user where username = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, userName);
			ResultSet executeQuery = ps.executeQuery();
			List<HashMap> list = getList(executeQuery);
			if(list.size()==1){
				return list.get(0);
			}else{
				return null;
			}
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return null;
	}

	@Override
	public void modifyPassword(String username, String newPassword) {
		Connection con = getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String md5psw = ConvertHelper.md5(username, newPassword); 
			String sql = "update user set password = '"+md5psw+"'  where username = '"+username+"'" ;
			ps = con.prepareStatement(sql);
			int executeUpdate = ps.executeUpdate();
			log.info("modify password  executeUpdate:"+executeUpdate);
			return ;
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}

	@Override
	public List<HashMap> queryUserList() {
		Connection con = getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "select id,username,nickname,remark,phone,role from user ";
			ps = con.prepareStatement(sql);
			ResultSet executeQuery = ps.executeQuery();
			List<HashMap> list = getList(executeQuery);
			return list;
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return null;
	}

	@Override
	public void addUser(String username, String nickname, String remark,
			String phone, String role) {
		
		Connection con = getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String md5psw = ConvertHelper.md5(username,defaultPsw );
			String sql = "insert into user(username,password,nickname,remark,phone,role ) values(?,?,?,?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, md5psw);
			if(nickname!=null){
				ps.setString(3, nickname);
			}else{
				ps.setNull(3, Types.VARCHAR);
			}
			if(remark!=null){
				ps.setString(4, remark);
			}else{
				ps.setNull(4, Types.VARCHAR);
			}
			
			if(phone!=null){
				ps.setString(5, phone);
			}else{
				ps.setNull(5, Types.VARCHAR);
			}
			ps.setString(6, role);
			int executeUpdate = ps.executeUpdate();
			log.info("insert user executeUpdate:"+executeUpdate);
			return ;
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}

	@Override
	public void resetUser(String username) {
		Connection con = getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String md5psw = ConvertHelper.md5(username, defaultPsw); 
			String sql = "update user set password = '"+md5psw+"'  where username = '"+username+"'" ;
			ps = con.prepareStatement(sql);
			int executeUpdate = ps.executeUpdate();
			log.info("reset password  executeUpdate:"+executeUpdate);
			return ;
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}

	@Override
	public void deleteUser(String username) {
		Connection con = getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "delete from user  where username = '"+username+"'" ;
			ps = con.prepareStatement(sql);
			int executeUpdate = ps.executeUpdate();
			log.info("delete password  executeUpdate:"+executeUpdate);
			return ;
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}
	
}
