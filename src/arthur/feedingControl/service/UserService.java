package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserService {
	public Map<String, String> queryUser(String userName);
	public void modifyPassword(String userName,String newPassword);
	public List<HashMap> queryUserList();
	public void addUser(String username ,String nickname,String remark,String phone ,String role);
	public void resetUser(String username);
	public void deleteUser(String username);
}
