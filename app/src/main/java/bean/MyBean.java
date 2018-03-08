package bean;

import java.io.Serializable;

/**
 * author: sunjian
 * created on: 2017/8/24 下午3:14
 * description:
 */

public class MyBean implements Serializable {
    private long id;
    private String img;
private String url;
    public MyBean(String img,String url) {
        this.img = img;
        this.url = url;
    }


    public String getImg() {
        return img;
    }
    public String geturl() {
        return url;
    }


}
