# Sentinel系统


- 主服务器配置

```shell
# redis.conf

	bind 127.0.0.1
	port 6379
	timeout 0	# 空闲从服务器被关闭连接的上线时间，0表示关闭功能
	
	daemonize yes	# 开启后台运行
	logfile	/var/log/redis/redis-server.log #日记文件的位置
	
	dir /var/lib/redis	# SNAPSHOTING文件夹
		
	appendonly yes #开启AOF增量持久化
	appendsync everysec # AOF持久化策略
```

- 从服务器配置

```shell
# redis7000.conf

	bind 127.0.0.1 
	port 7000
	timeout 0
	
	daemonize yes
	logfile /root/myconfig/redis7000.log
	
	dir /root/myconfig/redis
	
	appendonly yes
	appendfsync everysec
	
	slaveof 127.0.0.1 6379
	 # 如果slave无法与master完成同步，设置为不可读，方便发现问题
	slave-server-stale-data no
	
```

- Sentinel设置

```
# sentinel.conf

	port 26379
	
	# 哨兵程序日志路径
	dir /root/myconfig/redis/sentinel 
	
	# 监视主服务器，主服务器位置为127.0.0.1:6379
	sentinel monitor master6379 127.0.0.1 6379 1
	
	# 每5秒主观检测是否下线
	sentinel down-after-millionseconds master6379 5000
	
	# 故障转移时，最多有多少个从服务器可以进行同步
	sentinel parallel-syncs master6379 2
	
	# 故障转移必须在timeout时间内开始，否则视为转移失败
	sentinel failover-timeout master6379 60000
```

- 启动整个系统

```shell
$ redis-server redis.conf &
$ redis-server redis7000.conf &
$ redis-server sentinel.conf --sentinel &
```

```shell
# 查看redis服务器

root@iZuf614fhehhzmvmvz7btcZ:~/myconfig# ps -ef | grep redis
redis     4997     1  0 12:33 ?        00:00:05 /usr/bin/redis-server 127.0.0.1:6379
root      5110  5059  0 14:14 pts/2    00:00:00 redis-server *:26379 [sentinel]
root      5124     1  0 14:16 ?        00:00:00 redis-server 127.0.0.1:7000
```

```shell
# 查看主服务器状态

127.0.0.1:6379> INFO replication
# Replication
role:master
connected_slaves:1
slave0:ip=127.0.0.1,port=7000,state=online,offset=56921,lag=1
master_repl_offset:56921
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:2
repl_backlog_histlen:56920
```

```shell
# 查看从服务器状态

127.0.0.1:7000> INFO replication
# Replication
role:slave
master_host:127.0.0.1
master_port:6379
master_link_status:up
master_last_io_seconds_ago:0
master_sync_in_progress:0
slave_repl_offset:63392
slave_priority:100
slave_read_only:1
connected_slaves:0
master_repl_offset:0
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0

```

- 模拟主服务器宕机

```shell
# 关闭6379主服务器
$ /etc/init.d/redis-server stop

# 查看7000服务器

127.0.0.1:7000> INFO replication
# Replication
role:master # 6379掉线后，7000服务器升级为主服务器
connected_slaves:0
...

# 让6379服务器从新上线

$ /etc/init.d/redis-server start

# 观察重新上线的6379服务器

127.0.0.1:6379> INFO replication
# Replication
role:slave	# 重新上线后，成为从服务器
master_host:127.0.0.1
master_port:7000
...
```
 