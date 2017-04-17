package com.littlefisher.core.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.littlefisher.core.utils.LittleFisherLogger;

/**
 * 
 * Description: 
 *  
 * Created on 2017年2月23日 
 *
 * @author jinyanan
 * @version 1.0
 * @since v1.0
 */
@ControllerAdvice
public class ApplicationControllerExceptionHandler {
    
    /**
     * logger
     */
    private static LittleFisherLogger logger = LittleFisherLogger.getLogger(ApplicationControllerExceptionHandler.class);
    
    /**
     * 
     * Description: 针对BaseAppException类型的异常，该异常一般为业务抛出的异常，主要用于向前台传输异常信息
     * 
     * @author jinyanan
     *
     * @param ex 抛出的异常
     * @return Map<String, Object> 向前台返回的参数，会以JSON的形式返回
     */
    @ExceptionHandler(value = BaseAppException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handlerError(BaseAppException ex) {
        logger.error(ex.getMessage(), ex);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("errorCode", ex.getCode());
        map.put("errorMsg", ex.getLocaleMessage());
        return map;
    }
    
    /**
     * 
     * Description: 针对BaseRuntimeException类型的异常，该异常一般为业务抛出的异常，主要是业务回滚时抛出该异常，需要向前台抛基层的BaseAppException内容
     * 
     * @author jinyanan
     *
     * @param ex 抛出的异常
     * @return Map<String, Object> 向前台返回的参数，会以JSON的形式返回
     */
    @ExceptionHandler(value = BaseRuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handlerError(BaseRuntimeException ex) {
        logger.error(ex.getMessage(), ex);
        Map<String, Object> map = new HashMap<String, Object>();
        if (ex.getCause() instanceof BaseAppException) {
            BaseAppException baseAppException = (BaseAppException) ex.getCause();
            map.put("errorCode", baseAppException.getCode());
            map.put("errorMsg", baseAppException.getLocaleMessage());
        }
        else {
            map.put("errorMsg", ex.getMessage());
        }
        
        return map;
    }
    
    /**
     * 
     * Description: 除了BaseAppException类型之外的异常，该异常一般为内部实现出错时会抛出的异常，例如NummPointerException、SQLException等等，非业务逻辑异常
     * 
     * @author jinyanan
     *
     * @param ex 抛出的异常
     * @return Map<String, Object> 向前台返回的参数，会以JSON的形式返回
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handlerError(Exception ex) {
        logger.error(ex.getMessage(), ex);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("errorMsg", ex.getMessage());
        return map;
    }
    
}