package com.github.shadowseventh.distributed.lock.utils;

import com.github.shadowseventh.distributed.lock.api.DisLock;
import com.github.shadowseventh.distributed.lock.core.MethodTypeEnum;
import com.github.shadowseventh.distributed.lock.factory.DisLockBeanFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class DisLockUtils {


    public static Method getDisLockMethod(ProceedingJoinPoint pjp) {
        //代理方法
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        if (method.getAnnotation(DisLock.class) == null) {
            //实际方法
            try {
                method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }
        return method;
    }


    public static void invok(ProceedingJoinPoint pjp, MethodTypeEnum methodTypeEnum) {
        Method disLockMethod = DisLockUtils.getDisLockMethod(pjp);
        DisLock disLock = disLockMethod.getAnnotation(DisLock.class);
        String methodName = null;
        switch (methodTypeEnum) {
            case FAILMETHOD:
                methodName = disLock.failMethod();
                break;
            case LOCKMETHOD:
                methodName = disLock.lockMethod();
                break;
            case FINSHEDMETHOD:
                methodName = disLock.finshedMethod();
                break;
            default:
        }
        if (StringUtils.hasText(methodName)) {
            try {
                Method method = pjp.getTarget().getClass().getMethod(methodName, disLockMethod.getParameterTypes());
                method.invoke(DisLockBeanFactory.getBeanByClass(pjp.getTarget().getClass()), pjp.getArgs());
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}
