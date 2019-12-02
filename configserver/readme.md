# 启动方法
1. docker启动
2. docker-compose启动

## docker启动
先启动eureka server的容器
```aidl
docker run -d -p 4060:4060 -m 200M --name eurekaserver -e JAVA_OPTIONS='-Dlogback_home="/deployments/'  eurekaserver
```
再启动config server容器，由于同时在容器启动，他们之间如果要通讯，需要使用link的方式来实现，如果希望把配置文件动态挂载到本地磁盘，
方便后期修改，如果使用-v来进行实现，我们在config server项目里的目录变量是/config_repo
```aidl
docker run -d -p 4069:8700  -e "SPRING_PROFILES_ACTIVE=dev" -v d:/config_host_dir:/config_repo --name configserver  --link eurekaserver:eurekaserver config-server 
```

## docker-compose启动
使用docker-compose启动时，只要注册它与eureka为同一网络，然后挂好目录就行了
```aidl
version: "3.3"
services:

 eurekaserver1:
   build: ./eureka-server
   restart: on-failure
   ports:
     - "6761:6761"
     - "6762:6762"
   networks:
     - dev
   environment:
     - PORT=6761
     - BG_PORT=6762  
     - NAME=eureka1
     - URL=http://eureka2:6761/eureka #集群地址配置

 eurekaserver2:
   build: ./eureka-server
   restart: on-failure
   ports:
     - "5761:5761"
     - "5762:5762"
   networks:
     - dev
   environment:
     - PORT=5761
     - BG_PORT=5762  
     - NAME=eureka2
     - URL=http://eureka1:6761/eureka #集群地址配置

 configserver:
   build: ./config-server
   ports:
     - "6200:6200"
     - "6201:6201"
   volumes:
     - /Users/lind.zhang/github/config_repo:/config_repo
     - /Users/lind.zhang/github/dockerDeploy/springcloud/config-server:/config-encrypt
   networks:
     - dev
   depends_on:
     - eurekaserver1 #依赖服务
     - eurekaserver2
   environment:
     - PORT=6200
     - BG_PORT=6201

networks:
  dev:
    driver: bridge
```