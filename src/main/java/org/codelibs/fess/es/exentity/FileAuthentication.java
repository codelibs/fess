package org.codelibs.fess.es.exentity;

import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.es.bsentity.BsFileAuthentication;
import org.codelibs.fess.util.ComponentUtil;

/**
 * @author FreeGen
 */
public class FileAuthentication extends BsFileAuthentication {

    private static final long serialVersionUID = 1L;
    private FileConfig fileConfig;

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    @Override
    public void setId(String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(Long version) {
        asDocMeta().version(version);
    }

    public FileConfig getFileConfig() {
        if (fileConfig == null) {
            final FileConfigService fileConfigService = ComponentUtil.getComponent(FileConfigService.class);
            fileConfig = fileConfigService.getFileConfig(getFileConfigId());
        }
        return fileConfig;
    }
}
