package arthur.feedingControl.service;

import java.util.Map;

public interface UserService {
	public Map<String, String> login(String userName,String password);
	public void modifyPassword(String userName,String oldPassword,String newPassword);
	
}
