package bean;

import java.util.Date;
import java.util.UUID;

public class Todo {
    private UUID mId;
    private String mTitle;
    private String mContent;
    private Date mDate;
    private boolean mSolved;

    public Todo(UUID id, String title, String content, Date date, boolean solved) {
        mId = id;
        mTitle = title;
        mContent = content;
        mDate = date;
        mSolved = solved;
    }

    public Todo(String title, String content) {
        this(UUID.randomUUID(), title, content, new Date(), false);
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    @Override
    public String toString() {
        return mTitle + "\n" +
                mContent + "\n" +
                mDate.toString() + "\n" +
                mSolved + "\n" +
                mId.toString() + "\n" +
                super.toString();
    }
}
