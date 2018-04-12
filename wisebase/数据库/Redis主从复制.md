# Redis主从复制


- 拷贝一份新的配置文件(/etc/redis/redis.conf)，命名为slave01.conf

- 修改配置
	- pidfile：/var/run/redis7000.pid
	- port：7000
	- dbfilename：redis7000.rdb
	
- 启动新的服务器
	
```shell
$ redis-server redis7000.conf	
```

- 查看端口是否被正常启动

```shell
$ ps -ef | grep redis
```

- 6379作为主服务器，7000作为从服务器

```shell
127.0.0.1:7000> SLAVEOF 127.0.0.1 6379 
``` 
	
- 从Redis2.6开始，从服务器默认Read-Only模式，可以在主服务器中设置

```text
# redis.conf of Master
slave-read-only yes
```

