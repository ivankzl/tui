package ar.edu.um.model;

public class RespuestaJSON {
	
	private Content content;
	private int status;
	
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "RespuestaJSON [content=" + content + ", status=" + status + "]";
	}
	
	

}
