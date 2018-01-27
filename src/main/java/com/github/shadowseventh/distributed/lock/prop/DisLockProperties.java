package com.github.shadowseventh.distributed.lock.prop;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "distributed.lock")
@Component
public class DisLockProperties {

    private String addresses;

    private Integer waitLockTime;

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public Integer getWaitLockTime() {
        return waitLockTime;
    }

    public void setWaitLockTime(Integer waitLockTime) {
        this.waitLockTime = waitLockTime;
    }
}
