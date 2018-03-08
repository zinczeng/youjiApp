package bean;

public class SuiJi {
private String upData;
private String title;
private String yJGLId;
private String userName;
private String userPT;
private String contentPt1;
private String dianzan;
private String content1;
	private String userId;
	public SuiJi() {
		super();
	}
	public SuiJi(String yJGLId,String title,String dianzan,String userPT,String content1,String userName,String contentPt1,String upData){
		super();
		this.yJGLId=yJGLId;
		this.title=title;
		this.dianzan=dianzan;
		this.userPT=userPT;
		this.content1=content1;
		this.userName=userName;
		this.contentPt1=contentPt1;
		this.upData=upData;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUpData() {
		return upData;
	}

	public void setUpData(String upData) {
		this.upData = upData;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getyJGLId() {
		return yJGLId;
	}

	public void setyJGLId(String yJGLId) {
		this.yJGLId = yJGLId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPT() {
		return userPT;
	}

	public void setUserPT(String userPT) {
		this.userPT = userPT;
	}

	public String getContentPt1() {
		return contentPt1;
	}

	public void setContentPt1(String contentPt1) {
		this.contentPt1 = contentPt1;
	}

	public String getDianzan() {
		return dianzan;
	}

	public void setDianzan(String dianzan) {
		this.dianzan = dianzan;
	}

	public String getContent1() {
		return content1;
	}

	public void setContent1(String content1) {
		this.content1 = content1;
	}
}
