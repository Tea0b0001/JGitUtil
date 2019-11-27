package session;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;

import org.eclipse.jgit.util.FS;

/**
 * SshSession建立工厂所需
 * @author Tea0b0001
 * 2019/11/26
 */
public class SshFileSessionFactory extends JschConfigSessionFactory{

    private String keyPath;

    private String knowsHostPath;

    /**
     * 设定工厂ssh信息源(key, host)
     * @param keyPath ssh密钥文件,通常为id_rsa文件
     * @param knowsHostPath ssh信任的host,通常为known_hosts文件
     */
    public SshFileSessionFactory(String keyPath, String knowsHostPath) {
        this.keyPath = keyPath;
        this.knowsHostPath = knowsHostPath;
    }

    @Override
    protected JSch getJSch(final OpenSshConfig.Host hc, FS fs) throws JSchException {
        JSch jsch = new JSch();
        jsch.removeAllIdentity();
        jsch.addIdentity(keyPath);
        if (knowsHostPath != null && knowsHostPath.length() != 0) {
            jsch.setKnownHosts(knowsHostPath);
        }
        return jsch;
    }

    @Override
    protected void configure(OpenSshConfig.Host host, Session session ) {
        //如何没有符合要求的knowsHost,开启不检查host模式
        if (knowsHostPath == null || knowsHostPath.length() == 0) {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
        }
    }
}
