package com.github.shadowseventh.distributed.lock.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Aspect
@Service
public class AbstractLockAspect {

    @Autowired
    private DisLockInterceptor disLockInterceptor;

    /**
     * 对DisLock注解进行aop，对切点加锁
     */
    @Pointcut("@annotation(com.github.shadowseventh.distributed.lock.api.DisLock)")
    public void lockPoint() {

    }

    /**
     * 环绕通知
     */
    @Around("lockPoint()")
    public Object interceptorDisLockMethod(ProceedingJoinPoint pjp) throws Throwable {
        return disLockInterceptor.interceptDisLockMethod(pjp);
    }

    public void setDisLockInterceptor(DisLockInterceptor disLockInterceptor) {
        this.disLockInterceptor = disLockInterceptor;
    }

}
