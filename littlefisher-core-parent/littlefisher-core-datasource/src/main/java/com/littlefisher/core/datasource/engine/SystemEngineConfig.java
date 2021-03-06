package com.littlefisher.core.datasource.engine;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.transaction.PlatformTransactionManager;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.littlefisher.core.common.utils.CollectionUtil;
import com.littlefisher.core.common.utils.ExceptionHandler;
import com.littlefisher.core.common.utils.LittleFisherLogger;
import com.littlefisher.core.common.utils.StringUtil;
import com.littlefisher.core.datasource.event.EventDispatcher;
import com.littlefisher.core.datasource.event.EventDispatcherImpl;
import com.littlefisher.core.datasource.event.EventListener;
import com.littlefisher.core.datasource.interceptor.CommandConfig;
import com.littlefisher.core.datasource.interceptor.CommandContextFactory;
import com.littlefisher.core.datasource.interceptor.CommandContextInterceptor;
import com.littlefisher.core.datasource.interceptor.CommandExecutor;
import com.littlefisher.core.datasource.interceptor.CommandExecutorImpl;
import com.littlefisher.core.datasource.interceptor.CommandInterceptor;
import com.littlefisher.core.datasource.interceptor.CommandInvoker;
import com.littlefisher.core.datasource.interceptor.LoggerInterceptor;
import com.littlefisher.core.datasource.interceptor.SpringTransactionInterceptor;
import com.littlefisher.core.datasource.interceptor.ValidationInterceptor;
import com.littlefisher.core.datasource.interceptor.service.ServiceImpl;

/**
 * Description: 所有的系统参数配置都可以在该类中找到
 *
 * Created on 2017年2月10日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
public class SystemEngineConfig {

    /** logger */
    private static LittleFisherLogger logger = LittleFisherLogger.getLogger(SystemEngineConfig.class);

    /**
     * dataSource
     */
    protected DataSource dataSource;

    /**
     * transactionManager
     */
    protected PlatformTransactionManager transactionManager;

    /**
     * sqlSessionFactory
     */
    protected SqlSessionFactory sqlSessionFactory;

    /**
     * dbSqlSessionFactory
     */
    protected DbSqlSessionFactory dbSqlSessionFactory;

    /**
     * commandContextFactory
     */
    protected CommandContextFactory commandContextFactory;

    /**
     * sessionFactories ORM层的session工厂
     */
    protected Map<Class<?>, SessionFactory> sessionFactories;

    /**
     * enableEventDispatcher
     */
    protected boolean enableEventDispatcher = true;

    /**
     * eventDispatcher
     */
    protected EventDispatcher eventDispatcher;

    /**
     * eventListeners
     */
    protected List<EventListener> eventListeners;

    /**
     * typedEventListeners
     */
    protected Map<String, List<EventListener>> typedEventListeners;

    /**
     * services
     */
    protected Map<String, ServiceImpl> services = Maps.newHashMap();

    /**
     * clazzServices 缓存
     */
    protected Map<Class<? extends ServiceImpl>, ServiceImpl> clazzServices = Maps.newHashMap();

    /**
     * systemEngineName
     */
    protected String systemEngineName = "systemEngine";

    /**
     * commandInvoker
     */
    protected CommandInterceptor commandInvoker;

    /**
     * customPreCommandInterceptors 自定义前置Command拦截器
     */
    protected List<CommandInterceptor> customPreCommandInterceptors;

    /**
     * customPostCommandInterceptors 自定义后置Command拦截器
     */
    protected List<CommandInterceptor> customPostCommandInterceptors;

    /**
     * commandInterceptors 排好序的Command拦截器
     */
    protected List<CommandInterceptor> commandInterceptors;

    /**
     * commandExecutor
     */
    protected CommandExecutor commandExecutor;

    /**
     * defaultCommandConfig
     */
    protected CommandConfig defaultCommandConfig;

    /**
     * databaseType
     */
    protected String databaseType;

    public SystemEngine buildSystemEngine() {
        init();
        return new SystemEngineImpl(this);
    }

    /**
     * Description: 初始化方法<br>
     */
    protected void init() {
        logger.info("SystemEngineConfig init");
        // 初始化默认Command配置
        initDefaultCommandConfig();
        // 初始化Command上下文工厂
        initCommandContextFactory();
        // 初始化自定义的sqlSession工厂
        initDbSqlSessionFactory();
        // 初始化session工厂
        initSessionFactories();
        // 初始化命令调用工具
        initCommandInvoker();
        // 初始化命令的拦截器
        initCommandInterceptors();
        // 初始化命令的执行器
        initCommandExecutor();
        // 初始化EventDispatcher，暂不具体关注
        initEventDispatcher();

    }

    /**
     * 初始化Service
     *
     * @param services 初始化Service的Map列表
     */
    public void initServices(Map<String, ServiceImpl> services) {
        if (services != null) {
            services.forEach((serviceBeanName, service) -> {
                initService(service);
                services.put(serviceBeanName, service);
            });
        }
    }

    /**
     * 初始化Service
     *
     * @param serviceBeanName 自定义的业务Command的Bean名称
     * @param registerService 自定义的业务Command
     */
    public void initService(String serviceBeanName, ServiceImpl registerService) {
        if (registerService != null && StringUtil.isNotEmpty(serviceBeanName)) {
            initService(registerService);
            // 对ServiceImpl进行缓存
            services.put(serviceBeanName, registerService);
        }

    }

    /**
     * 初始化Service
     *
     * @param service 自定义的业务Command
     */
    protected void initService(Object service) {
        // 每一个ServiceImpl准备执行时，赋值commandExecutor，让ServiceImpl有开始执行的地方
        if (service instanceof ServiceImpl) {
            ((ServiceImpl) service).setCommandExecutor(commandExecutor);
        }
    }

    /**
     * Description: 初始化默认Command配置
     */
    protected void initDefaultCommandConfig() {
        if (defaultCommandConfig == null) {
            defaultCommandConfig = new CommandConfig();
        }
    }

    /**
     * Description: 初始化自定义的sqlSession工厂
     */
    protected void initDbSqlSessionFactory() {
        if (dbSqlSessionFactory == null) {
            dbSqlSessionFactory = new DbSqlSessionFactory(sqlSessionFactory);
        }

    }

    /**
     * Description: 初始化session工厂
     */
    protected void initSessionFactories() {
        if (sessionFactories == null) {
            sessionFactories = Maps.newHashMap();
            // 由于dbSqlSessionFactory在此次步骤之前进行了初始化，所以把dbSqlSessionFactory添加到sessionFactories中，用于缓存
            addSessionFactory(dbSqlSessionFactory);
        }
    }

    protected void addSessionFactory(SessionFactory sessionFactory) {
        sessionFactories.put(sessionFactory.getSessionType(), sessionFactory);
    }

    /**
     * Description: 初始化Command上下文工厂<br>
     */
    protected void initCommandContextFactory() {
        if (commandContextFactory == null) {
            commandContextFactory = new CommandContextFactory();
            commandContextFactory.setSystemEngineConfig(this);
        }
    }

    /**
     * Description: 初始化命令调用工具
     */
    protected void initCommandInvoker() {
        if (commandInvoker == null) {
            // CommandInvoker继承自AbstractCommandInterceptor，也是拦截器的一种
            commandInvoker = new CommandInvoker();
        }
    }

    /**
     * Description: 初始化命令的拦截器
     */
    protected void initCommandInterceptors() {
        if (commandInterceptors == null) {
            commandInterceptors = Lists.newArrayList();
            // 自定义的前置拦截器
            if (customPreCommandInterceptors != null) {
                commandInterceptors.addAll(customPreCommandInterceptors);
            }
            commandInterceptors.addAll(getDefaultCommandInterceptors());
            // 自定义的后置拦截器
            if (customPostCommandInterceptors != null) {
                commandInterceptors.addAll(customPostCommandInterceptors);
            }
            // 命令调用工具也属于拦截器
            commandInterceptors.add(commandInvoker);

            // 当前commandInterceptors顺序为：
            // CommandContextInterceptor 1. 渲染容器上下文环境
            // SpringTransactionInterceptor 2. 为下步操作添加事务环绕
            // CommandInvoker 3. 执行具体的业务命令，该拦截器必须为最后一个，后续不可再增加其他拦截器
        }
    }

    /**
     * 初始化EventDispatcher
     */
    private void initEventDispatcher() {
        if (this.eventDispatcher == null) {
            this.eventDispatcher = new EventDispatcherImpl();
        }

        this.eventDispatcher.setEnabled(enableEventDispatcher);

        if (eventListeners != null) {
            for (EventListener listenerToAdd : eventListeners) {
                this.eventDispatcher.addEventListener(listenerToAdd);
            }
        }

        if (typedEventListeners != null) {
            for (Entry<String, List<EventListener>> listenersToAdd : typedEventListeners.entrySet()) {
                // Extract types from the given string
                String[] types = getEventTypeListFromString(listenersToAdd.getKey());

                for (EventListener listenerToAdd : listenersToAdd.getValue()) {
                    this.eventDispatcher.addEventListener(listenerToAdd, types);
                }
            }
        }
    }

    private String[] getEventTypeListFromString(String types) {
        return Iterables.toArray(Splitter.on(',').omitEmptyStrings().split(types), String.class);
    }

    /**
     * 初始化默认拦截器
     *
     * @return Collection<? extends CommandInterceptor>
     */
    protected Collection<? extends CommandInterceptor> getDefaultCommandInterceptors() {
        List<CommandInterceptor> interceptors = Lists.newArrayList();
        interceptors.add(new CommandContextInterceptor(commandContextFactory, this));
        // 事务拦截器
        CommandInterceptor transactionInterceptor = createTransactionInterceptor();
        if (transactionInterceptor != null) {
            interceptors.add(transactionInterceptor);
        }
        // 校验拦截器
        CommandInterceptor validationInterceptor = createValidationInterceptor();
        if (validationInterceptor != null) {
            interceptors.add(validationInterceptor);
        }
        // 日志拦截器
        CommandInterceptor loggerInterceptor = createLoggerInterceptor();
        if (loggerInterceptor != null) {
            interceptors.add(loggerInterceptor);
        }

        return interceptors;
    }

    /**
     * 创建事务拦截器
     * 
     * @return CommandInterceptor
     */
    protected CommandInterceptor createTransactionInterceptor() {
        if (transactionManager == null) {
            // transactionManager is required property for SpringProcessEngineConfiguration
            ExceptionHandler.publish("CORE-000003");
        }

        return new SpringTransactionInterceptor(transactionManager);
    }

    /**
     * Description: 创建日志拦截器
     *
     * @return LoggerInterceptor
     */
    protected CommandInterceptor createLoggerInterceptor() {
        return new LoggerInterceptor();
    }

    /**
     * 创建oval校验拦截器
     *
     * @return ValidationInterceptor
     */
    protected CommandInterceptor createValidationInterceptor() {
        return new ValidationInterceptor();
    }

    /**
     * Description: 初始化命令的执行器，之前只定义了拦截器执行链，需要有一个执行器，让执行链能开始执行<br>
     */
    protected void initCommandExecutor() {
        if (commandExecutor == null) {
            // 初始化拦截器调用链，并返回第一个拦截器
            CommandInterceptor first = initInterceptorChain(commandInterceptors);
            // 执行器默认从第一个拦截器开始执行
            commandExecutor = new CommandExecutorImpl(getDefaultCommandConfig(), first);
        }
    }

    public CommandInterceptor getCommandInvoker() {
        return commandInvoker;
    }

    public void setCommandInvoker(CommandInterceptor commandInvoker) {
        this.commandInvoker = commandInvoker;
    }

    public List<CommandInterceptor> getCustomPreCommandInterceptors() {
        return customPreCommandInterceptors;
    }

    public void setCustomPreCommandInterceptors(List<CommandInterceptor> customPreCommandInterceptors) {
        this.customPreCommandInterceptors = customPreCommandInterceptors;
    }

    public List<CommandInterceptor> getCustomPostCommandInterceptors() {
        return customPostCommandInterceptors;
    }

    public void setCustomPostCommandInterceptors(List<CommandInterceptor> customPostCommandInterceptors) {
        this.customPostCommandInterceptors = customPostCommandInterceptors;
    }

    public List<CommandInterceptor> getCommandInterceptors() {
        return commandInterceptors;
    }

    public void setCommandInterceptors(List<CommandInterceptor> commandInterceptors) {
        this.commandInterceptors = commandInterceptors;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public CommandConfig getDefaultCommandConfig() {
        return defaultCommandConfig;
    }

    public void setDefaultCommandConfig(CommandConfig defaultCommandConfig) {
        this.defaultCommandConfig = defaultCommandConfig;
    }

    /**
     * 初始化拦截器调用链，并返回第一个拦截器
     * 
     * @param chain 拦截器列表
     * @return CommandInterceptor
     */
    protected CommandInterceptor initInterceptorChain(List<CommandInterceptor> chain) {
        if (CollectionUtil.isEmpty(chain)) {
            ExceptionHandler.publish("CORE-000004");
        }
        for (int i = 0; i < chain.size() - 1; i++) {
            chain.get(i).setNext(chain.get(i + 1));
        }
        return chain.get(0);
    }

    /**
     * Description: 从缓存中获取Service<br>
     *
     * @param serviceName service的bean名，如果未设置bean名，默认首字母小写的类名
     * @return <br>
     */
    public ServiceImpl getService(String serviceName) {
        return services.get(serviceName);
    }

    /**
     * 获取Service
     * 
     * @param clazzService Serivice的Clazz
     * @param <T> 泛型
     * @return ServiceImpl的实现类
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<? extends ServiceImpl> clazzService) {
        return (T) clazzServices.get(clazzService);
    }

    /**
     * 把ServiceImpl添加缓存
     * 
     * @param serviceName service名称
     * @param service service
     * 
     */
    public void putService(String serviceName, ServiceImpl service) {
        services.putIfAbsent(serviceName, service);
        clazzServices.putIfAbsent(service.getClass(), service);
    }

    /**
     * 销毁操作
     */
    public void destory() {
        //
        // logger.debug("do systemEngineConfig destory");
        // try {
        // ZcacheClient.getInstance().destory();
        // }
        // catch (BaseAppException e) {
        // //
        // logger.error("systemEngineConfig destory error", e);
        // }

    }

    public Map<Class<?>, SessionFactory> getSessionFactories() {
        return sessionFactories;
    }

    public SystemEngineConfig setSessionFactories(Map<Class<?>, SessionFactory> sessionFactories) {
        this.sessionFactories = sessionFactories;
        return this;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public void setEventDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void setEnableEventDispatcher(boolean enableEventDispatcher) {
        this.enableEventDispatcher = enableEventDispatcher;
    }

    public void setTypedEventListeners(Map<String, List<EventListener>> typedListeners) {
        this.typedEventListeners = typedListeners;
    }

    public void setEventListeners(List<EventListener> eventListeners) {
        this.eventListeners = eventListeners;
    }

    public boolean isEnableEventDispatcher() {
        return enableEventDispatcher;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public String getSystemEngineName() {
        return systemEngineName;
    }

    public void setSystemEngineName(String systemEngineName) {
        this.systemEngineName = systemEngineName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PlatformTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
