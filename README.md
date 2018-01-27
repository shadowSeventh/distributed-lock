
0.0.1版本

### 功能：

可以通过在方法上添加 `@DisLock` 这个注解来使用分布式锁，可以减少开发成本.

### 使用方法：

在你需要加分布式锁的方法上加上@DisLock

@DisLock目前支持三个参数`lockMethod`   `failMethod`   `finshedMethod`

可以分别赋值你 获得锁  加锁失败 释放锁 需要调用的方法的方法名 

要求方法都在同一个类中，且，方法参数与被加锁的方法一致

### 配置：

需要在 `application.yml`中加入相关`zookeeper`相关配置

```yaml
distributed.lock:
    addresses: 127.0.0.1:2110
    waitLockTime: 1000
```

### 相关加锁方式说明：

`LockTypeEnum.METHOD` : 根据全类名，方法名获得锁，方法级别锁，为默认值

`LockTypeEnum.PARAMETER` : 参数类型的锁，需要注解中声明参数位置，需要配置`DisLock value()`  ------ 待实现

`LockTypeEnum.CUSTOM` : 自定义方法类型的锁，需要在同一个类中声明自定义获得锁的方法名，而且方法需要返回一个String,需要配置`DisLock lockMethod()`  ------ 待实现

`LockTypeEnum.REQUEST` : 请求级别类型的锁，需要配置一个`filter`，拦截`request`，获得`path`以及`method`，根据`path`和`method`针对同一个接口的`http`请求加锁,应用案例：防止浏览器多次点击，重复发出请求  ------ 待实现
