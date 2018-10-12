# spring-boot-starter-flyway-plus
flyway starter for spring boot

### 说明


 > 基于 Flyway 的 Spring Boot Starter 实现

1. 兼容druid数据源开启安全过滤器后引起的脚本无法执行错误
2. 增加Flyway模块化支持，实现了各个模块独立的版本迁移（注：模块较多时 flyway_模块名_schema_history 表会比较多）

### Maven

``` xml
<dependency>
	<groupId>com.github.vindell</groupId>
	<artifactId>spring-boot-starter-flyway-plus</artifactId>
	<version>1.0.3.RELEASE</version>
</dependency>
```

### Simple

[https://github.com/vindell/spring-boot-starter-samples/tree/master/spring-boot-sample-flyway](https://github.com/vindell/spring-boot-starter-samples/tree/master/spring-boot-sample-flyway "spring-boot-sample-flyway")

