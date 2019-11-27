package vo;

import java.util.List;
/**
 * Config View
 * @author Tea0b0001
 * 2019/11/26
 */
public class RemoteConfigItem {
    private String name;

    private List<String> uriList;

    private int timeout;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUriList() {
        return uriList;
    }

    public void setUriList(List<String> uriList) {
        this.uriList = uriList;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
