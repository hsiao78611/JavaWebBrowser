
public class HistoryBean {
	private String zonedDateTime;
	private String url;
	private String title;
	
	public void setDateTime(String s) {
		zonedDateTime = s;
	}
	public void setURL(String s) {
		url = s;	
	}
	public void setTitle(String s) {
		title = s;	
	}
	
	public String getDateTime() {
		return zonedDateTime.toString();
	}
	public String getURL() {
		return url;
	}
	public String getTitle() {
		return title;
	}
}