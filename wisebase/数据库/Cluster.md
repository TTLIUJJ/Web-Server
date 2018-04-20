# 集群 Cluster


- 集群方案，每个节点配合Sentinel做容灾策略
	- node1
		- 主服务器：7000
		- 从服务器：7001
		- Sentinel：7777
	- node2：
		- 主服务器：8000
		- 从服务器：8001
		- Sentinel：8888
	- node3：
		- 主服务器：9000
		- 从服务器：9001
		- Sentinel：9999
		
- 集群有3个节点，先创建3个Sentinel系统

- 修改配置文件，配置集群功能

```shell
# 普通的Redis实例不能称为集群的一部分，除非开启实例开启集群功能
cluster-enabled yes

# 每一个节点都有一个配置文件，注意集群中的节点配置文件不能相同
cluster-config-file nodes-7000.conf

# 集群节点在15000ms无响应会被疑似下线
cluster-node-timeout 15000
```
	
```shell
# 集群预备节点已经上线

root@iZuf614fhehhzmvmvz7btcZ:~/redisconf# ps -ef | grep redis
root      6020     1  0 20:38 ?        00:00:00 redis-server 127.0.0.1:7000
root      6024     1  0 20:38 ?        00:00:00 redis-server 127.0.0.1:7001
root      6032     1  0 20:38 ?        00:00:00 redis-server 127.0.0.1:8000
root      6036     1  0 20:38 ?        00:00:00 redis-server 127.0.0.1:8001
root      6042     1  0 20:38 ?        00:00:00 redis-server 127.0.0.1:9000
root      6046     1  0 20:39 ?        00:00:00 redis-server 127.0.0.1:9001
root      6060  5589  0 20:41 pts/0    00:00:00 redis-server *:7777 [sentinel]
root      6063  5589  0 20:41 pts/0    00:00:00 redis-server *:8888 [sentinel]
root      6066  5589  0 20:41 pts/0    00:00:00 redis-server *:9999 [sentinel]
```

- 安装工具

```shell
# Redis集群需要使用Ruby命令，需要安装Ruby接口和相关接口

$ apt install yum
$ apt-get install ruby
$ apt-get install rubygems
$ apt-get install redis
```

- 集群创建

```shell
# 使用集群模式登录，进行集群操作
$ redis-cli - c -p 7000	
127.0.0.1:7000 > cluster meet 127.0.0.1 8000
127.0.0.1:7000 > cluster meet 127.0.0.1 9000

# {m, n}  --> [m, n]
$ redis-cli -h 127.0.0.1 -p 7000 cluster addslots {0..5000}
$ redis-cli -h 127.0.0.1 -p 8000 cluster addslots {5001, 10000}
$ redis-cli -h 127.0.0.1 -p 9000 cluster addslots {10001, 16383}
```

```shell
127.0.0.1:7000> cluster info
cluster_state:ok
...
cluster_known_nodes:3
...
```

- 测试

```shell
127.0.0.1:7000> get apple
-> Redirected to slot [7092] located at 127.0.0.1:8000
"juice"
```

- 9个服务器，3个Sentinel，3个主服务器（节点）和3个从节点（容灾）

```shell
root@iZuf614fhehhzmvmvz7btcZ:~/redisconf/sentinel# ps -ef | grep redis
root      7553     1  0 21:34 ?        00:00:01 redis-server 127.0.0.1:7000 [cluster]
root      7557     1  0 21:34 ?        00:00:01 redis-server 127.0.0.1:8000 [cluster]
root      7561     1  0 21:34 ?        00:00:01 redis-server 127.0.0.1:9000 [cluster]
root      7568     1  0 21:34 ?        00:00:01 redis-server 127.0.0.1:7001
root      7574     1  0 21:34 ?        00:00:01 redis-server 127.0.0.1:8001
root      7580     1  0 21:34 ?        00:00:01 redis-server 127.0.0.1:9001
root      7737  5589  0 22:22 pts/0    00:00:00 redis-server *:9999 [sentinel]
root      7740  5589  0 22:22 pts/0    00:00:00 redis-server *:8888 [sentinel]
root      7743  5589  0 22:22 pts/0    00:00:00 redis-server *:7777 [sentinel]
```