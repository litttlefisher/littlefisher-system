package com.yc.room1000.core.interceptor;

import com.yc.room1000.core.engine.SystemEngineConfig;

/**
 * 
 * Description: 
 *  
 * Created on 2017年2月10日 
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
public class CommandContextFactory {

    /**
     * systemEngineConfig
     */
    protected SystemEngineConfig systemEngineConfig;
    
    /**
     * Description: <br>
     * 
     * @author zeng.ligeng<br>
     * @taskId <br>
     * @param cmd <br>
     * @return <br>
     */
    public CommandContext createCommandContext(Command cmd) {
        return new CommandContext(cmd, systemEngineConfig);
    }
    
    public SystemEngineConfig getSystemEngineConfig() {
        return systemEngineConfig;
    }

    public void setSystemEngineConfig(SystemEngineConfig systemEngineConfig) {
        this.systemEngineConfig = systemEngineConfig;
    }
}