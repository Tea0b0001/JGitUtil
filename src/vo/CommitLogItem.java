package vo;

import java.util.List;
/**
 * @author Tea0b0001
 * 2019/11/26
 */

public class CommitLogItem {
    //提交时间
    private int commitTime;

    //提交附加信息+Change-Id
    private String fullMessage;

    //提交附加信息
    private String shortMessage;

    //作者名字与邮箱地址
    private String authorName;
    private String authorEmail;

    //提交者名字与邮箱地址
    private String committerName;
    private String committerEmail;

    //commit记录信息
    private String buffer;

    //Change-Id列表
    private List<String> changeIds;

    public int getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(int commitTime) {
        this.commitTime = commitTime;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public void setShortMessage(String shortMessage) {
        this.shortMessage = shortMessage;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getCommitterName() {
        return committerName;
    }

    public void setCommitterName(String committerName) {
        this.committerName = committerName;
    }

    public String getCommitterEmail() {
        return committerEmail;
    }

    public void setCommitterEmail(String committerEmail) {
        this.committerEmail = committerEmail;
    }

    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public List<String> getChangeIds() {
        return changeIds;
    }

    public void setChangeIds(List<String> changeIds) {
        this.changeIds = changeIds;
    }
}
