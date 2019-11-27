package practice;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import utils.JGitUtil;
import vo.CommitLogItem;
import vo.DiffStatItem;

import java.util.List;

public class BestPractice {
    /**
     * 获取最新修改的分支
     * @param git
     * @return
     */
    public static String getLatesBranch(Git git) {
        List<Ref> refList = JGitUtil.remoteBranchRefList(git);
        if (refList == null || refList.size() == 0) {
            return null;
        }
        List<String> stringList = JGitUtil.convertRefListToNameList(refList);
        int latestCommitTime = Integer.MIN_VALUE;
        int tempCommitTime;
        String latestBranchName = null;
        String actualBranchName;
        String[] stringArray;
        for (String string : stringList) {
            stringArray = string.split("/");
            actualBranchName = stringArray[stringArray.length-1];
            JGitUtil.checkoutBranch (git, actualBranchName, false);
            JGitUtil.gitPull(git);
            List<RevCommit> revCommitList = JGitUtil.gitLog(git, 1);
            if (revCommitList == null || revCommitList.size() == 0) {
                continue;
            }
            tempCommitTime = revCommitList.get(0).getCommitTime();
            CommitLogItem commitLogItem = JGitUtil.convertRevCommitToCommitLogItem(revCommitList.get(0));
            if (tempCommitTime > latestCommitTime) {
                latestCommitTime = tempCommitTime;
                latestBranchName = actualBranchName;
            }
        }
        JGitUtil.checkoutBranch(git, latestBranchName, false);
        return latestBranchName;
    }

    /**
     * 对比两次提交的文件代码,得到统计结果(取最新两次提交)
     * @param git
     * @return
     */
    public static DiffStatItem compareTwoCommit(Git git) {
        List<RevCommit> revCommitList = JGitUtil.gitLog(git, 2);
        return compareTwoCommit(git, revCommitList.get(1), revCommitList.get(0));
    }

    /**
     * 对比两次提交的文件代码,得到统计结果(指定两次提交)
     * @param git
     * @return
     */
    public static DiffStatItem compareTwoCommit(Git git, RevCommit oldCommit, RevCommit newCommit) {
        List<DiffEntry> diffEntryList = JGitUtil.gitDiff(git, oldCommit, newCommit);
        if (diffEntryList == null) {
            return new DiffStatItem();
        }
        return JGitUtil.analyzeDiffEntryList(git, diffEntryList);
    }
}
