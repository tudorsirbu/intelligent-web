package servlets.util;

public class Template {

	private String content;
	private String title;
	private String head;
	private Doctype doctype;
	
	public enum Doctype {
		HTML,
		XHTML
	}
	
	public Template(String content, String title) {
		this.content = content;
		this.title = title;
		
		this.initHead();
	}

	public Template(String content, String title, Doctype doctype) {
		this.content = content;
		this.title = title;
		this.doctype = doctype;
		this.initHead();
	}
	
	public void initHead() {
		this.head += "<title>" + this.title + "</title>";
		this.head += "<link rel=\"stylesheet\" type=\"text/css\" href=\"CSS/reset.css\">";
		this.head += "<link rel=\"stylesheet\" type=\"text/css\" href=\"CSS/formStyle.css\">";
		this.head += "<link rel=\"stylesheet\" type=\"text/css\" href=\"CSS/map.css\">";
		this.head += "<link rel=\"stylesheet\" type=\"text/css\" href=\"CSS/jquery-ui-1.10.4.custom.min.css\">";
		
		this.head += "<script src=\"javascript/jquery-1.11.0.js\"></script>";
		this.head += "<script src=\"javascript/jquery-ui-1.10.4.custom.min.js\"></script>";
		this.head += "<script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyDJgU59vjkXnSzHlfrR1iaZ6zottGl35Ys&sensor=false\"></script>";
		this.head += "<script src=\"javascript/validation.js\"></script>";
		this.head += "<script src=\"javascript/initializationFunctions.js\"></script>";
		this.head += "<script src=\"javascript/initialize.js\"></script>";
	}
	
	public String getPage() {
		String page = "";
		if(this.doctype == Doctype.HTML) {
			page += "<!doctype html>";
			page += "<html lang=\"en\">";			
		}
		else  {			
			page += "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
			page += "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">";
		}
		
		page += "<head>";
		
		if(this.doctype == Doctype.XHTML) {			
			page += "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=UTF-8\"/>";
		}
		
		page += this.head;
		page += "</head>";
		page += "<body>";
		page += "<a href=\"queryInterface.html\" id=\"back_to_query_interface\">Back to Query Interface</a>";
		page += this.content;
		page += "</body>";
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
	public Doctype getDoctype() {
		return doctype;
	}

	public void setDoctype(Doctype doctype) {
		this.doctype = doctype;
	}

	public void setHead(String head) {
		this.head = head;
	}

}
