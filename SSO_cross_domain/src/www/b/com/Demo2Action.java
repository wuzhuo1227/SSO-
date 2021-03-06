package www.b.com;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//先校验cookie的有效性  在校验登录
public class Demo2Action extends ActionSupport{

	private String gotourl;
	private String username;
	private String password;
	private String path;
	private List<String> hiddenUrl;

	public List<String> getHiddenUrl() {
		return hiddenUrl;
	}

	public void setHiddenUrl(List<String> hiddenUrl) {
		this.hiddenUrl = hiddenUrl;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String main(){
		HttpServletRequest request = ServletActionContext.getRequest();
		Cookie[] cookies = request.getCookies();
		if(cookies!=null){
			for(Cookie cookie:cookies){
				if(cookie.getName().equals("ssocookie")&&
						cookie.getValue().equals("sso")){
					Map<String,String> map=new HashMap<String,String>();
					map.put("cookieName",cookie.getName());
					map.put("cookieValue",cookie.getValue());
					String result = doget("http://www.x.com/sso/checkcookie.action",
							map);
					if(result.equals("1")){
						return SUCCESS;
					}
				}
			}
		}
		path = "demo2";
		gotourl = "http://www.b.com/demo2/main.action";
		return "login";
	}

	public String doLogin() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("username",username);
		map.put("password",password);
		String result=doget("http://www.x.com/sso/doLogin.action",map);
		if(result.equals("1")){
			hiddenUrl=new ArrayList<String>();
			hiddenUrl.add("http://www.a.com/demo1/addCookie.action");
			hiddenUrl.add("http://www.b.com/demo2/addCookie.action");
			return SUCCESS;
		}
		return "login";
	}

	/*public void addCookie() {
		Cookie cookie=new Cookie("ssocookie","sso");
		cookie.setPath("/");
		HttpServletResponse response=ServletActionContext.getResponse();
		response.addCookie(cookie);
	}*/

	public String getGotourl() {
		return gotourl;
	}

	public void setGotourl(String gotourl) {
		this.gotourl = gotourl;
	}
	
	public String doget(String url,Map<String,String> map){
		HttpURLConnection httpURLConnection = null;
		StringBuffer sb = new StringBuffer();
		try{
			StringBuffer t_s=new StringBuffer(url).append("?");
			for (Map.Entry<String,String> entry:map.entrySet()) {
				t_s.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			url=t_s.substring(0,t_s.length()-1);
			URL urls =new URL(url);
			httpURLConnection = (HttpURLConnection) urls.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
			InputStream in  =  httpURLConnection.getInputStream();
			InputStreamReader is= new InputStreamReader(in);
			BufferedReader br = new BufferedReader(is);
			String temp = null;
			while((temp=br.readLine())!=null){
				sb.append(temp);
			}
			br.close();
			is.close();
			in.close();
			
		}catch (IOException c) {
			// TODO: handle exception
		}finally{
			if(httpURLConnection!=null){
				httpURLConnection.disconnect();
			}
		}
		return sb.toString();
	}


	public String addCookie() throws Exception {
		Cookie cookie=new Cookie("ssocookie","sso");
		cookie.setPath("/");
		HttpServletResponse response=ServletActionContext.getResponse();
		response.addCookie(cookie);
		return "success";
	}
}
