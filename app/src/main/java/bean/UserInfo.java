package bean;

/**
 * Created by Administrator on 2017/5/17.
 */

public class UserInfo {
    private String userAddress;
    private String sign;
    private String sex;
    private String userPW;
    private String userId;
    private String userPT;
    private String userName;
    private String userAge;
    private static Boolean IsLogin = false;

    private UserInfo() {

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

    public static void setIsLogin(Boolean IsLogin) {
        UserInfo.IsLogin = IsLogin;
    }

    public static Boolean getIsLogin() {
        return IsLogin;
    }

}
