# JGitUtil
#### 封装JGit(java git客户端)工具类

功能及其对应方法:

- 打开git
  - public static Git openGit(String filePath)
    - 打开git 参数为文件路径
  - public static Git openGit(File file)
    - 打开git 参数为文件
- branch
  - public static List<String> convertRefListToNameList(List<Ref> refList)
    - 分支列表转分支名列表
  - public static List<Ref> localBranchRefList(Git git)
    - 获取本地分支列表
  - public static List<Ref> remoteBranchRefList(Git git)
    - 获取远程分支列表
  - public static List<Ref> allBranchRefList(Git git)
    - 获取所有分支列表
  - public static boolean checkoutBranch(Git git, String branchName, boolean createBranch)
    - 切换分支
  - public static boolean createBranch(Git git, String branchName)
    - 创建分支
  - public static boolean deleteBranch(Git git, String... branchNames)
    - 删除分支
  - public static boolean renameBranch(Git git, String oldBranchName, String newBranchName)
    - 重命名分支
- clone
  - public static boolean gitClone (String remoteUrl, String repoPath, String arg1, String arg2, String branch, int type)
    - 克隆，支持ssh/http
  - public static boolean gitClone(String remoteUrl, File repoDir, String arg1, String arg2, String branch, int type)
    - 克隆，支持ssh/http
- pull
  - public static boolean gitPull(Git git)
    - 拉取
- 源
  - public static boolean changeRemote(Git git, String url, String remoteName)
    - 换源
  - public static boolean addRemote(Git git, String url, String remoteName)
    - 添加源
  - public static List<RemoteConfig> remoteList(Git git)
    - 本地源列表
  - public static boolean removeRemote(Git git, String remoteName)
    - 移除源
  - public static boolean setUrlRemote(Git git, String url, String remoteName)
    - 设定源对应的url
  - public static RemoteConfigItem convertRemoteConfigToItem(RemoteConfig remoteConfig)
    - 源RemoteConfig对象转源视图RemoteConfigItem 
- commit
  - public static boolean gitAdd(Git git)
    - add命令
  - public static boolean gitAdd(Git git, boolean update)
    - add命令
  - public static boolean gitCommit(Git git, String name, String email, String message)
    - commit命令
  - public static boolean gitCommit(Git git, String author, String authorEmail, String committer, String committerEmail, String message)
    - commit命令
  - public static boolean gitCommit(Git git, boolean all, boolean amend, String name, String email, String message)
    - commit命令
  - public static boolean gitCommit(Git git, boolean all, boolean amend, String author, String authorEmail, String committer, String committerEmail, String message)
    - commit命令
  - public static boolean gitPush(Git git, String arg1, String arg2, int type)
    - push命令
  - public static boolean gitPush(Git git, String remote, String localBranchName, String arg1, String arg2, int type)
    - push命令
- diff
  - public static List<DiffEntry> gitDiff(Git git, RevCommit oldCommit, RevCommit newCommit)
    - diff命令，对比两次提交
  - public static DiffStatItem analyzeDiffEntryList(Git git, List<DiffEntry> diffEntryList)
    - 分析对比结果列表
  - public static void analyzeDiffEntry(DiffEntry diffEntry, DiffFormatter diffFormatter, DiffStatItem diffStatItem)
    - 分析单文件对比结果
- log
  - public static List<RevCommit> gitLog(Git git, int limitSize)
    - git记录
  - public static List<ReflogEntry> gitBranchLog(Git git, String branchName)
    - git记录（指定分支）
  - public static CommitLogItem convertRevCommitToCommitLogItem(RevCommit revCommit)
    - git记录RevCommit类型转提交记录视图CommitLogItem 






依赖包:

- org.eclipse.jgit-5.5.1.201910021850-r.jar
- jsch-0.1.55.jar
- slf4j-api-1.7.9.jar




