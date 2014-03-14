package servlets.util;

public class Template {

	private String content;
	private String title;
	private String head;
	
	public Template(String content, String title) {
		this.content = content;
		this.title = title;
		
		this.initHead();
	}

	public void initHead() {
		this.head += "<title>" + this.title + "</title>";
		this.head += "<link rel=\"stylesheet\" type=\"text/css\" href=\"CSS/formStyle.css\">";
		this.head += "<link rel=\"stylesheet\" type=\"text/css\" href=\"CSS/reset.css\">";
		this.head += "<script src=\"javascript/jquery-1.11.0.js\"></script>";
		this.head += "<script src=\"javascript/script.js\"></script>";
	}
	
	public String getPage() {
		String page = "<!doctype html>";
		page += "<html lang=\"en\">";
		page += "<head>";
		page += this.head;
		page += "<//head>";
		page += "<body>";
		page += this.content;
		page += "<//body>";
		page += "</html>";
		
		return page;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHead() {
		return head;
	}

	public void addToHead(String head) {
		this.head += head;
	}
	
}
