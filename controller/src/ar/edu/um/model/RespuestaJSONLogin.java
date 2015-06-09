package ar.edu.um.model;

public class RespuestaJSONLogin {
	
	private int status;
	private String session_token;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status_code) {
		this.status = status_code;
	}
	public String getSession_token() {
		return session_token;
	}
	public void setSession_token(String session_token) {
		this.session_token = session_token;
	}
	
	@Override
	public String toString() {
		return "RespuestaJSONUserInfo [status_code=" + status
				+ ", session_token=" + session_token + "]";
	}
	
	
}

