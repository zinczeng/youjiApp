package bean;


public class User  {
	private  static User instance;
	public User(){
		super();
	}
	private String userAddress;
	private String sign;
	private String sex;
	private String userPW;
	private String userId;
	private String userPT;
	private String userName;
	private String userAge;
	private String token;
	private boolean islogin;
	public static synchronized User getInstance() {
		if (instance == null) {
			instance = new User();
		}
		return instance;
	}


	public static void setInstance(User instance) {
		User.instance = instance;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUserPW() {
		return userPW;
	}

	public void setUserPW(String userPW) {
		this.userPW = userPW;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPT() {
		return userPT;
	}

	public void setUserPT(String userPT) {
		this.userPT = userPT;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAge() {
		return userAge;
	}

	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}

	public boolean islogin() {
		return islogin;
	}

	public void setIslogin(boolean islogin) {
		this.islogin = islogin;
	}
}
