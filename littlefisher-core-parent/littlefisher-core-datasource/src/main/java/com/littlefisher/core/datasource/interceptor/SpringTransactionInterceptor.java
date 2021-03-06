package com.littlefisher.core.datasource.interceptor;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.littlefisher.core.common.utils.ExceptionHandler;
import com.littlefisher.core.common.utils.LittleFisherLogger;

/**
 * Description: Spring事务拦截器
 *
 * Created on 2017年2月10日
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
public class SpringTransactionInterceptor extends AbstractCommandInterceptor {

    /**
     * logger
     */
    private static LittleFisherLogger logger = LittleFisherLogger.getLogger(SpringTransactionInterceptor.class);

    /**
     * transactionManager
     */
    protected PlatformTransactionManager transactionManager;

    public SpringTransactionInterceptor(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * 获取事务
     * @param config 命令配置
     * @return 事务类型
     */
    private Integer getPropagation(CommandConfig config) {
        switch (config.getTransactionPropagation()) {
            case NOT_SUPPORTED:
                return TransactionTemplate.PROPAGATION_NOT_SUPPORTED;
            case REQUIRED:
                return TransactionTemplate.PROPAGATION_REQUIRED;
            case REQUIRES_NEW:
                return TransactionTemplate.PROPAGATION_REQUIRES_NEW;
            default:
                ExceptionHandler.publish("CORE-000006", null, config.getTransactionPropagation().toString());
                return TransactionTemplate.TIMEOUT_DEFAULT;
        }
    }

    @Override
    public <U> U execute(final CommandConfig config, final Command<U> command) {
        logger.debug("Running command with propagation {}", config.getTransactionPropagation());

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        try {
            transactionTemplate.setPropagationBehavior(getPropagation(config));
        } catch (Exception e) {
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
        }

        return transactionTemplate.execute(status -> {
            U result = null;
            try {
                result = next.execute(config, command);
            } catch (Exception e) {
                ExceptionHandler.publish("CORE-000007", e.getMessage(), e);
            }
            return result;
        });
    }
}
