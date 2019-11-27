package vo;


/**
 * diff统计结果
 * @author Tea0b0001
 * 2019/11/27
 */
public class DiffStatItem {
    int insertLines = 0;
    int deleteLines = 0;
    int modifyLines = 0;

    public int getInsertLines() {
        return insertLines;
    }

    public int getDeleteLines() {
        return deleteLines;
    }

    public int getModifyLines() {
        return modifyLines;
    }

    public void addInsertLines(int delta) {
        this.insertLines += delta;
    }

    public void addDeleteLines(int delta) {
        this.deleteLines += delta;
    }

    public void addModifyLines(int delta) {
        this.modifyLines += delta;
    }

}
