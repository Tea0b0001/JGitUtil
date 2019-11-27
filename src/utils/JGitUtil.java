package utils;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import session.SshFileSessionFactory;
import vo.CommitLogItem;
import vo.DiffStatItem;
import vo.RemoteConfigItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * JGit Util
 * @author Tea0b0001
 * 2019/11/26
 */

public class JGitUtil {

    //--------------------------------git open相关----------------------------------
    /**
     * 打开git
     * @param filePath
     * @return
     */
    public static Git openGit(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("filePath为null");
        }
        return openGit(new File(filePath));
    }

    /**
     * 打开git
     * @param file
     * @return
     */

    public static Git openGit(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("file为null或file不存在");
        }
        try {
            return Git.open(file);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }



    //--------------------------------branch相关----------------------------------
    /**
     * 本地branch name列表
     * @param refList
     * @return
     */
    public static List<String> convertRefListToNameList(List<Ref> refList) {
        return refList == null ? null : new LinkedList<String>(){{
            for (Ref ref : refList) {
                add(ref.getName());
            }
        }};
    }

    /**
     * 本地branch列表
     * @param git
     * @return
     */
    public static List<Ref> localBranchRefList(Git git) {
        return BranchRefList(git, null);
    }

    /**
     * 远程branch列表
     * @param git
     * @return
     */
    public static List<Ref> remoteBranchRefList(Git git) {
        return BranchRefList(git, ListBranchCommand.ListMode.REMOTE);
    }

    /**
     * 所有branch列表
     * @param git
     * @return
     */
    public static List<Ref> allBranchRefList(Git git) {
        return BranchRefList(git, ListBranchCommand.ListMode.ALL);
    }

    /**
     * branch列表
     * @param git
     * @param listMode
     * @return
     */
    private static List<Ref> BranchRefList(Git git, ListBranchCommand.ListMode listMode) {
        checkNull(git);
        ListBranchCommand listBranchCommand = git.branchList();
        listBranchCommand.setListMode(listMode);
        try {
            return listBranchCommand.call();
        } catch (GitAPIException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param git
     * @param branchName
     * @param createBranch true:如分支不存在会创建分支 false:不会创建新的分支
     * @return
     */
    public static boolean checkoutBranch(Git git, String branchName, boolean createBranch) {
        CheckoutCommand checkoutCommand = git.checkout();
        checkoutCommand.setName(branchName);
        checkoutCommand.setCreateBranch(createBranch);
        return commandCall(checkoutCommand);
    }

    /**
     * 创建分支
     * @param git
     * @param branchName
     * @return
     */
    public static boolean createBranch(Git git, String branchName) {
        CreateBranchCommand createBranchCommand = git.branchCreate();
        createBranchCommand.setName(branchName);
        return commandCall(createBranchCommand);
    }

    /**
     * 删除分支
     * @param git
     * @param branchNames
     * @return
     */
    public static boolean deleteBranch(Git git, String... branchNames) {
        DeleteBranchCommand deleteBranchCommand = git.branchDelete();
        deleteBranchCommand.setBranchNames(branchNames);
        return commandCall(deleteBranchCommand);
    }

    /**
     * branch重命名
     * @param git
     * @param oldBranchName 旧名
     * @param newBranchName 新名
     * @return
     */
    public static boolean renameBranch(Git git, String oldBranchName, String newBranchName) {
        RenameBranchCommand renameBranchCommand = git.branchRename();
        renameBranchCommand.setOldName(oldBranchName);
        renameBranchCommand.setNewName(newBranchName);
        return commandCall(renameBranchCommand);
    }




    //--------------------------------clone相关----------------------------------
    /**
     * gitClone通用类 支持ssh/http
     * ssh相关参数详情可看session.SshFileSessionFactory中的注释
     * @param remoteUrl 克隆url
     *                  example: []表示视情况可省略部分
     *                  ssh://[user@]host.xz[:port]/path/to/repo.git/
     *                  http[s]://[username:password@]host.xz[:port]/path/to/repo.git/
     * @param repoPath 克隆文件夹路径
     * @param arg1 type == 0 arg1 -> ssh的keyPath, type == 1 arg1 -> username
     * @param arg2 type == 0 arg2 -> ssh的knowsHostPath, type == 1 arg2 -> password
     * @param branch 指定分支,不指定设值为null
     * @param type type == 0 ssh方式克隆, type == 1 http方式克隆
     * @return
     */
    public static boolean gitClone (String remoteUrl, String repoPath, String arg1, String arg2, String branch, int type) {
        if (repoPath == null) {
            throw new IllegalArgumentException("repo path cannot be null");
        }

        return gitClone(remoteUrl, new File(repoPath), arg1, arg2, branch, type);
    }

    /**
     * gitClone通用类 支持ssh/http
     * @param remoteUrl 克隆url
     * @param repoDir 克隆文件夹
     * @param arg1 type == 0 arg1 -> ssh的keyPath, type == 1 arg1 -> username
     * @param arg2 type == 0 arg2 -> ssh的knowsHostPath, type == 1 arg2 -> password
     * @param branch 指定分支,不指定设值为null
     * @param type type == 0 ssh方式克隆, type == 1 http方式克隆
     * @return
     */
    public static boolean gitClone(String remoteUrl, File repoDir, String arg1, String arg2, String branch, int type) {
        if (!checkEmptyDir(repoDir)) {
            return false;
        }
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(remoteUrl);
        cloneCommand.setDirectory(repoDir);
        cloneCommand.setBranch(branch);
        if (type == 0) {
            cloneCommand.setTransportConfigCallback(createSshTransport(arg1, arg2));
        } else {
            cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(arg1, arg2));
        }
        return commandCall(cloneCommand);
    }

    /**
     * 检查是否为空文件夹（git clone所需）
     * @param file
     * @return
     */
    private static boolean checkEmptyDir(File file) {
        if (file.exists()) {
            if (file.isDirectory()){
                File[] files = file.listFiles();
                return !(files != null && files.length > 0);
            }
            return false;
        }
        return true;
    }

    /**
     * 创建ssh会话工厂
     * @param keyPath
     * @param knowsHostPath
     * @return
     */
    private static TransportConfigCallback createSshTransport(String keyPath, String knowsHostPath) {
        SshFileSessionFactory sshFileSessionFactory = new SshFileSessionFactory(keyPath, knowsHostPath);
        return new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport=(SshTransport)transport;
                sshTransport.setSshSessionFactory(sshFileSessionFactory);
            }
        };
    }



    //--------------------------------pull相关----------------------------------
    /**
     * 拉取
     * @param git
     * @return
     */
    public static boolean gitPull(Git git) {
        checkNull(git);
        PullCommand pullCommand = git.pull();
        return commandCall(pullCommand);
    }



    //--------------------------------源相关----------------------------------
    /**
     * 换源-先清空源,再添加源
     * @param git
     * @param url
     * @param remoteName
     * @return
     */
    public static boolean changeRemote(Git git, String url, String remoteName) {
        return removeRemote(git, remoteName) && addRemote(git, url, remoteName);
    }

    /**
     * 添加源
     * @param git
     * @param url
     * @param remoteName
     * @return
     */
    public static boolean addRemote(Git git, String url, String remoteName) {
        RemoteAddCommand remoteAddCommand = git.remoteAdd();
        remoteAddCommand.setName(remoteName);
        try {
            remoteAddCommand.setUri(new URIish(url));
            remoteAddCommand.call();
            return true;
        } catch (URISyntaxException uriex) {
            uriex.printStackTrace();
            return false;
        } catch (GitAPIException gitapiex) {
            gitapiex.printStackTrace();
            return false;
        }
    }

    /**
     * 获取源列表
     * @param git
     * @return
     */
    public static List<RemoteConfig> remoteList(Git git) {
        RemoteListCommand remoteListCommand = git.remoteList();
        try {
            return remoteListCommand.call();
        } catch (GitAPIException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 移除源
     * @param git
     * @param remoteName
     * @return
     */
    public static boolean removeRemote(Git git, String remoteName) {
        RemoteRemoveCommand remoteRemoveCommand = git.remoteRemove();
        remoteRemoveCommand.setRemoteName(remoteName);
        //remoteRemoveCommand.setName(remoteName);旧版本可能会要使用这一条语句set远程源名
        return commandCall(remoteRemoveCommand);
    }

    /**
     * 设置源url
     * @param git
     * @param url
     * @param remoteName
     * @return
     */
    public static boolean setUrlRemote(Git git, String url, String remoteName) {
        RemoteSetUrlCommand remoteSetUrlCommand = git.remoteSetUrl();
        remoteSetUrlCommand.setRemoteName(remoteName);
        //remoteRemoveCommand.setName(remoteName);旧版本可能会要使用这一条语句set远程源名
        try {
            remoteSetUrlCommand.setRemoteUri(new URIish(url));
            //remoteSetUrlCommand.setUri(new URIish(url));旧版本可能会要使用这一条语句set远程url
            remoteSetUrlCommand.call();
            return true;
        } catch (URISyntaxException uriex) {
            uriex.printStackTrace();
            return false;
        } catch (GitAPIException gitapiex) {
            gitapiex.printStackTrace();
            return false;
        }
    }

    /**
     * RemoteConfig转RemoteConfigItem
     * @param remoteConfig
     * @return
     */
    public static RemoteConfigItem convertRemoteConfigToItem(RemoteConfig remoteConfig) {
        checkNull(remoteConfig);
        RemoteConfigItem remoteConfigItem = new RemoteConfigItem();
        remoteConfigItem.setName(remoteConfig.getName());
        remoteConfigItem.setTimeout(remoteConfig.getTimeout());
        remoteConfigItem.setUriList(new LinkedList<String>(){{
            for (URIish urIish : remoteConfig.getURIs()) {
                add(urIish.toString());
            }
        }});
        return remoteConfigItem;
    }



    //---------------------------------commit相关-----------------------------------

    /**
     * add命令
     * @param git
     * @return
     */
    public static boolean gitAdd(Git git) {
        return gitAdd(git, false);
    }

    /**
     * add命令
     * @param git
     * @param update true:不会添加新文件 false:会添加新文件 类似于-u和-A的区别
     * @return
     */
    public static boolean gitAdd(Git git, boolean update) {
        AddCommand addCommand = git.add();
        addCommand.setUpdate(update);
        return commandCall(addCommand);
    }


    /**
     * commit命令
     * 不考虑all, amend.author与committer默认一人.
     * @param git
     * @param name
     * @param email
     * @param message
     * @return
     */
    public static boolean gitCommit(Git git,
                                    String name, String email,
                                    String message) {
        return gitCommit(git, false, false, name, email, name, email, message);
    }

    /**
     * commit命令
     * 不考虑all, amend
     * @param git
     * @param author
     * @param authorEmail
     * @param committer
     * @param committerEmail
     * @param message
     * @return
     */
    public static boolean gitCommit(Git git,
                                    String author, String authorEmail,
                                    String committer, String committerEmail,
                                    String message) {
        return gitCommit(git, false, false, author, authorEmail, committer, committerEmail, message);
    }

    /**
     * commit命令
     * author与committer默认一人
     * @param git
     * @param all 默认为false true:执行修改或删除操作的文件都会commit,即使没有经过"git add" false:只有经过"git add"才会commit
     * @param amend 默认为false true: 追加模式,追加到前一次的commit false:不追加,新建一次commit
     * @param name
     * @param email
     * @param message
     * @return
     */
    public static boolean gitCommit(Git git,
                                    boolean all, boolean amend,
                                    String name, String email,
                                    String message) {
        return gitCommit(git, all, amend, name, email, name, email, message);
    }

    /**
     * commit命令
     * 所有常用参数考虑
     * @param git
     * @param all 默认为false true:执行修改或删除操作的文件都会commit,即使没有经过"git add" false:只有经过"git add"才会commit
     * @param amend 默认为false true: 追加模式,追加到前一次的commit false:不追加,新建一次commit
     * @param author
     * @param authorEmail
     * @param committer
     * @param committerEmail
     * @param message
     * @return
     */
    public static boolean gitCommit(Git git,
                                    boolean all, boolean amend,
                                    String author, String authorEmail,
                                    String committer, String committerEmail,
                                    String message) {
        CommitCommand commitCommand = git.commit();
        commitCommand.setAll(all);
        commitCommand.setAuthor(author, authorEmail);
        commitCommand.setCommitter(committer, committerEmail);
        commitCommand.setMessage(message);

        commitCommand.setAmend(amend);
        return commandCall(commitCommand);
    }

    /**
     * push命令
     * @param git
     * @param arg1 type == 0 arg1 -> ssh的keyPath, type == 1 arg1 -> username
     * @param arg2 type == 0 arg2 -> ssh的knowsHostPath, type == 1 arg2 -> password
     * @param type type == 0 ssh方式push, type == 1 http方式push
     * @return
     */
    public static boolean gitPush(Git git, String arg1, String arg2, int type) {
        return gitPush(git, "origin", null, arg1, arg2, type);
    }

    /**
     * push命令
     * @param git
     * @param remote 远程源
     * @param localBranchName 本地分支名
     * @param arg1 type == 0 arg1 -> ssh的keyPath, type == 1 arg1 -> username
     * @param arg2 type == 0 arg2 -> ssh的knowsHostPath, type == 1 arg2 -> password
     * @param type type == 0 ssh方式push, type == 1 http方式push
     * @return
     */
    public static boolean gitPush(Git git, String remote, String localBranchName, String arg1, String arg2, int type) {
        PushCommand pushCommand = git.push();
        pushCommand.setRemote(remote);
        if (localBranchName != null) {
            pushCommand.add(localBranchName);
        }
        if (type == 0) {
            pushCommand.setTransportConfigCallback(createSshTransport(arg1, arg2));
        } else {
            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(arg1, arg2));
        }
        return commandCall(pushCommand);
    }




    //-------------------------------diff相关----------------------------------
    /**
     * 获取两次提交的差异
     * @param git
     * @param oldCommit 旧commit
     * @param newCommit 新commit
     * @return
     */
    public static List<DiffEntry> gitDiff(Git git, RevCommit oldCommit, RevCommit newCommit) {
        Repository repository = git.getRepository();
        DiffCommand diffCommand = git.diff();
        AbstractTreeIterator oldTree = prepareTreeParser(repository, oldCommit);
        AbstractTreeIterator newTree = prepareTreeParser(repository, newCommit);

        diffCommand.setOldTree(oldTree);
        diffCommand.setNewTree(newTree);
        try {
            return diffCommand.call();
        } catch (GitAPIException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * 生成commit对应的TreeIterator
     * @param repository
     * @param commit
     * @return
     */
    private static AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) {
        RevWalk walk = new RevWalk(repository);
        try {
            RevTree tree = walk.parseTree(commit.getTree().getId());
            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
            try (ObjectReader oldReader = repository.newObjectReader()) {
                oldTreeParser.reset(oldReader, tree.getId());
            }
            walk.dispose();
            return oldTreeParser;
        }catch (IOException ex) {
           ex.printStackTrace();
           return null;
        }
    }

    /**
     * 分析diff代码对比结果,得到统计结果
     * @param git
     * @param diffEntryList
     * @return
     */
    public static DiffStatItem analyzeDiffEntryList(Git git, List<DiffEntry> diffEntryList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DiffFormatter diffFormatter = new DiffFormatter(byteArrayOutputStream);
        diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);
        diffFormatter.setRepository(git.getRepository());

        DiffStatItem diffStatItem = new DiffStatItem();
        for (DiffEntry diffEntry : diffEntryList) {
            analyzeDiffEntry(diffEntry, diffFormatter, diffStatItem);
            byteArrayOutputStream.reset();
        }
        return diffStatItem;
    }

    /**
     * 分析一个代码文件对比结果,加入统计结果
     * @param diffEntry
     * @param diffFormatter
     * @param diffStatItem
     */
    public static void analyzeDiffEntry(DiffEntry diffEntry, DiffFormatter diffFormatter, DiffStatItem diffStatItem) {
        try {
            FileHeader fileReader = diffFormatter.toFileHeader(diffEntry);
            List<HunkHeader> hunkHeaderList = (List<HunkHeader>)fileReader.getHunks();
            for (HunkHeader hunkHeader : hunkHeaderList) {
                EditList editList = hunkHeader.toEditList();
                for (Edit edit : editList) {
                    //根据edit类型计算统计结果
                    switch (edit.getType()) {
                        case INSERT: {
                            diffStatItem.addInsertLines(edit.getEndB() - edit.getBeginB());
                            break;
                        } case REPLACE: {
                            diffStatItem.addModifyLines(edit.getEndB() - edit.getBeginB());
                            break;
                        } case DELETE: {
                            diffStatItem.addDeleteLines(edit.getEndA() - edit.getBeginA());
                            break;
                        } default: {
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }




    //--------------------------------log相关----------------------------------
    /**
     * 获取当前分支提交记录
     * @param git
     * @param limitSize
     * @return
     */
    public static List<RevCommit> gitLog(Git git, int limitSize) {
        checkNull(git);
        List<RevCommit> revCommitList = new LinkedList<>();
        LogCommand logCommand = git.log();
        logCommand.setMaxCount(limitSize);
        try {
            Iterable<RevCommit> revCommitIterable = logCommand.call();
            Iterator<RevCommit> revCommitIterator = revCommitIterable.iterator();
            while(revCommitIterator.hasNext()) {
                revCommitList.add(revCommitIterator.next());
            }
            return revCommitList;
        } catch (GitAPIException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取分支本地创建记录
     * @param git
     * @param branchName
     * @return
     */
    public static List<ReflogEntry> gitBranchLog(Git git, String branchName) {
        checkNull(git);
        ReflogCommand reflogCommand = git.reflog();
        reflogCommand.setRef(branchName);
        try {
            Collection<ReflogEntry> reflogEntryCollection = reflogCommand.call();
            return new LinkedList<>(reflogEntryCollection);
        } catch (GitAPIException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * RevCommit转log的vo
     * @param revCommit
     * @return
     */
    public static CommitLogItem convertRevCommitToCommitLogItem(RevCommit revCommit) {
        checkNull(revCommit);
        CommitLogItem commitLogItem = new CommitLogItem();
        PersonIdent authorIdent = revCommit.getAuthorIdent();
        PersonIdent committerIdent = revCommit.getCommitterIdent();
        commitLogItem.setCommitId(revCommit.getId().getName());
        commitLogItem.setAuthorEmail(authorIdent.getEmailAddress());
        commitLogItem.setAuthorName(authorIdent.getName());
        commitLogItem.setBuffer(new String(revCommit.getRawBuffer()));
        commitLogItem.setChangeIds(revCommit.getFooterLines("Change-Id"));
        commitLogItem.setCommitterEmail(committerIdent.getEmailAddress());
        commitLogItem.setCommitterName(committerIdent.getName());
        commitLogItem.setCommitTime(revCommit.getCommitTime());
        commitLogItem.setFullMessage(revCommit.getFullMessage());
        commitLogItem.setShortMessage(revCommit.getShortMessage());
        return commitLogItem;
    }



    //------------------------------------通用--------------------------------------
    /**
     * 通用git command执行代码
     * @param gitCommand
     * @return
     */
    private static boolean commandCall(GitCommand gitCommand) {
        try {
            gitCommand.call();
            return true;
        } catch (GitAPIException ex) {
            ex.printStackTrace();
            return false;
        }
    }



    //--------------------------------参数检测相关----------------------------------
    /**
     * 检查git是否为空
     * @param git
     */
    private static void checkNull(Git git) {
        if (git == null) {
            throw new IllegalArgumentException("git为null");
        }
    }

    /**
     * 检查revCommit是否为空
     * @param revCommit
     */
    private static void checkNull(RevCommit revCommit) {
        if (revCommit == null) {
            throw new IllegalArgumentException("revCommit为null");
        }
    }

    /**
     * 检查remoteConfig是否为空
     * @param remoteConfig
     */
    private static void checkNull(RemoteConfig remoteConfig) {
        if (remoteConfig == null) {
            throw new IllegalArgumentException("remoteConfig为null");
        }
    }

}