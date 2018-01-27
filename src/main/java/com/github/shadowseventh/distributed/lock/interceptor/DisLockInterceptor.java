package com.github.shadowseventh.distributed.lock.interceptor;

import com.github.shadowseventh.distributed.lock.api.DisLock;
import com.github.shadowseventh.distributed.lock.core.LockTypeEnum;
import com.github.shadowseventh.distributed.lock.core.MethodTypeEnum;
import com.github.shadowseventh.distributed.lock.filter.ZookeeperPathFilter;
import com.github.shadowseventh.distributed.lock.prop.DisLockProperties;
import com.github.shadowseventh.distributed.lock.utils.DisLockUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.logging.Logger;

@Service
public class DisLockInterceptor {

    static final Logger logger = Logger.getLogger(DisLockInterceptor.class.getSimpleName());


    @Autowired
    private DisLockProperties properties;

    @Autowired
    private ZookeeperLockRegistry zkLockRegistry;

    @Autowired
    private ZookeeperPathFilter filter;

    /**
     * 拦截器拦截到注解后，主要执行的方法
     */
    public Object interceptDisLockMethod(ProceedingJoinPoint pjp) throws Throwable {

        //获得注解的type
        Method disLockMethod = DisLockUtils.getDisLockMethod(pjp);
        DisLock disLock = disLockMethod.getAnnotation(DisLock.class);
        LockTypeEnum type = disLock.type();

        switch (type) {

            case METHOD:
                return methodProceed(pjp);
            case PARAMETER:
                return parameterProceed(pjp);
            case REQUEST:
                return requestProceed(pjp);
            case CUSTOM:
                return customProceed(pjp);
            default:
                return pjp.proceed();
        }
    }

    /**
     * 为方法级别的锁时
     */
    public Object methodProceed(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = DisLockUtils.getDisLockMethod(pjp).getName();
        String className = pjp.getTarget().getClass().getName();
        String path = className + methodName;
        long waitLockTime = properties.getWaitLockTime();
        lockAndExec(path, waitLockTime, pjp);
        return pjp.proceed();
    }

    public Object parameterProceed(ProceedingJoinPoint pjp) throws Throwable {

        return pjp.proceed();
    }

    public Object requestProceed(ProceedingJoinPoint pjp) throws Throwable {
        String path = filter.getUrl() + "_" + filter.getMethod() + "_" + filter.getIp();
        long waitLockTime = properties.getWaitLockTime();
        lockAndExec(path, waitLockTime, pjp);
        return pjp.proceed();
    }

    public Object customProceed(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }

    /**
     * 获得锁，释放锁，目前使用的是spirng-integration中的zk锁
     */
    public Object lockAndExec(String lockKey, long waitLockTime, ProceedingJoinPoint pjp) {
        logger.info("加锁");
        Lock lock = zkLockRegistry.obtain(lockKey);
        try {
            if (!lock.tryLock(waitLockTime, TimeUnit.MILLISECONDS)) {
                logger.info("加锁失败，任务中止");
                DisLockUtils.invok(pjp, MethodTypeEnum.FAILMETHOD);
                return null;
            }
        } catch (InterruptedException e) {
            logger.warning("在等待加锁时被中止:" + e.getMessage());
            return null;
        }
        // 已经加锁OK，执行
        try {
            return pjp.proceed();
        } catch (Throwable e) {
            logger.warning("执行任务时出现异常，将终止:" + e.getMessage());
        } finally {
            lock.unlock();
            DisLockUtils.invok(pjp, MethodTypeEnum.FINSHEDMETHOD);
        }
        return null;
    }


}
