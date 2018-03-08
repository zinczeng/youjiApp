package bean;

public class Review {
private String ReviewTime;
public String getReviewTime() {
	return ReviewTime;
}
public void setReviewTime(String reviewTime) {
	ReviewTime = reviewTime;
}
public String getUserId() {
	return UserId;
}
public void setUserId(String userId) {
	UserId = userId;
}
public String getContent() {
	return Content;
}
public void setContent(String content) {
	Content = content;
}
public String getReviewId() {
	return ReviewId;
}
public void setReviewId(String reviewId) {
	ReviewId = reviewId;
}
public String getYJGLId() {
	return YJGLId;
}
public void setYJGLId(String yJGLId) {
	YJGLId = yJGLId;
}
private String UserId;
private String Content;
private String ReviewId;
private String YJGLId;

}
