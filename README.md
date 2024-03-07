# CDC MySQL with debezium

```shell
mkdir docker/plguins
cd docker/plguins 
 
curl https://packages.confluent.io/maven/io/confluent/kafka-connect-avro-converter/7.5.3/kafka-connect-avro-converter-7.5.3.jar --output kafka-connect-avro-converter.jar
curl https://packages.confluent.io/maven/io/confluent/kafka-connect-avro-data/7.5.3/kafka-connect-avro-data-7.5.3.jar --output kafka-connect-avro-data.jar
curl https://packages.confluent.io/maven/io/confluent/common-utils/7.5.3/common-utils-7.5.3.jar --output common-utils.jar
curl https://packages.confluent.io/maven/io/confluent/common-config/7.5.3/common-config-7.5.3.jar --output common-config.jar
curl https://packages.confluent.io/maven/io/confluent/kafka-schema-registry-client/7.5.3/kafka-schema-registry-client-7.5.3.jar --output kafka-schema-registry-client.jar
curl https://packages.confluent.io/maven/io/confluent/kafka-schema-serializer/7.5.3/kafka-schema-serializer-7.5.3.jar --output kafka-schema-serializer.jar
curl https://packages.confluent.io/maven/io/confluent/kafka-avro-serializer/7.5.3/kafka-avro-serializer-7.5.3.jar --output kafka-avro-serializer.jar
```

## Procedure

### Master-Slave Replication

```shell
# [Master]
show master status;

# [Slave]
STOP SLAVE;
# container ip 변경
CHANGE MASTER TO MASTER_HOST='cdc-demo-mysql-master', MASTER_USER='root', MASTER_PASSWORD='rootroot', MASTER_LOG_POS=157, MASTER_LOG_FILE='mysql-bin.000003';

START SLAVE;
SHOW SLAVE STATUS \G;
```

### Register the Debezium MySQL connector

```sh
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d '{
  "name": "demo-mysql-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "cdc-demo-mysql-slave",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "rootroot",
    "database.server.id": "2",
    "topic.prefix": "cdc-demo",
    "database.include.list": "demo",
    "schema.history.internal.kafka.bootstrap.servers": "cdc-kafka:29092",
    "schema.history.internal.kafka.topic": "schema-changes.demo",
    "include.schema.changes": "true",
    "table.whitelist": "demo.user",
  }
}'
```

~~"key.converter": "io.apicurio.registry.utils.converter.AvroConverter",~~
~~"key.converter.schema.registry.url": "http://schema-registry:8081/apis/registry/v2",~~
~~"value.converter": "io.apicurio.registry.utils.converter.AvroConverter",~~
~~"value.converter.schema.registry.url": "http://schema-registry:8081/apis/registry/v2"~~

##### MySQL Connector configuration properties

https://debezium.io/documentation/reference/stable/connectors/mysql.html#_required_debezium_mysql_connector_configuration_properties

```shell
# Response
HTTP/1.1 201 Created
Date: Fri, 09 Feb 2024 12:34:29 GMT
Location: http://localhost:8083/connectors/demo-mysql-connector
Content-Type: application/json
Content-Length: 503
Server: Jetty(9.4.52.v20230823)

{
  "name": "demo-mysql-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "cdc-demo-mysql-slave",
    "database.port": "3306",
    "database.user": "root",
    "database.password": "rootroot",
    "database.server.id": "2",
    "topic.prefix": "cdc-demo",
    "database.include.list": "demo",
    "schema.history.internal.kafka.bootstrap.servers": "cdc-kafka:29092",
    "schema.history.internal.kafka.topic": "schema-changes.demo",
    "include.schema.changes": "true",
    "table.whitelist": "demo.user",
    "name": "demo-mysql-connector"
  },
  "tasks": [],
  "type": "source"
}
```

### Verify that connector

```sh
curl -H "Accept:application/json" localhost:8083/connectors/

# Response
["demo-mysql-connector"]

curl -i -X GET -H "Accept:application/json" localhost:8083/connectors/demo-mysql-connector

# Response
{
  "name": "demo-mysql-connector",
  ...
  "type": "source"
}  
```

### SQL

```sql
CREATE TABLE user
(
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(124) NOT NULL,
    password    VARCHAR(256) NOT NULL,
    nickname    VARCHAR(124) NULL,
    user_status VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE'
);

use demo;

INSERT INTO user (email, password, nickname, user_status)
VALUES ('user@dev.com', 'hashed_password', 'user-3', 'ACTIVE');
```

```
2024-02-10 02:50:38,690 INFO   MySQL|cdc-demo|binlog  Connected to binlog at cdc-demo-mysql-slave:3306, starting at MySqlOffsetContext [sourceInfoSchema=Schema{io.debezium.connector.mysql.Source:STRUCT}, sourceInfo=SourceInfo [currentGtid=null, currentBinlogFilename=mysql-bin.000003, currentBinlogPosition=2267, currentRowNumber=0, serverId=0, sourceTime=2024-02-10T02:50:38Z, threadId=-1, currentQuery=null, tableIds=[demo.user], databaseName=demo], snapshotCompleted=true, transactionContext=TransactionContext [currentTransactionId=null, perTableEventCount={}, totalEventCount=0], restartGtidSet=null, currentGtidSet=null, restartBinlogFilename=mysql-bin.000003, restartBinlogPosition=2267, restartRowsToSkip=0, restartEventsToSkip=0, currentEventLengthInBytes=0, inTransaction=false, transactionId=null, incrementalSnapshotContext =IncrementalSnapshotContext [windowOpened=false, chunkEndPosition=null, dataCollectionsToSnapshot=[], lastEventKeySent=null, maximumKey=null]]   [io.debezium.connector.mysql.MySqlStreamingChangeEventSource]
2024-02-10T02:50:38.691973221Z 2024-02-10 02:50:38,691 INFO   MySQL|cdc-demo|streaming  Waiting for keepalive thread to start   [io.debezium.connector.mysql.MySqlStreamingChangeEventSource]
2024-02-10T02:50:38.692482013Z 2024-02-10 02:50:38,691 INFO   MySQL|cdc-demo|binlog  Creating thread debezium-mysqlconnector-cdc-demo-binlog-client   [io.debezium.util.Threads]
2024-02-10T02:50:38.693345555Z 2024-02-10 02:50:38,693 INFO   MySQL|cdc-demo|snapshot  11 records sent during previous 00:00:55.709, last recorded offset of {server=cdc-demo} partition is {ts_sec=1707533438, file=mysql-bin.000003, pos=2267}   [io.debezium.connector.common.BaseSourceTask]
2024-02-10T02:50:38.713383055Z 2024-02-10 02:50:38,713 WARN   ||  [Producer clientId=connector-producer-demo-mysql-connector-0] Error while fetching metadata with correlation id 3 : {cdc-demo=LEADER_NOT_AVAILABLE}   [org.apache.kafka.clients.NetworkClient]
2024-02-10T02:50:38.793284721Z 2024-02-10 02:50:38,793 INFO   MySQL|cdc-demo|streaming  Keepalive thread is running   [io.debezium.connector.mysql.MySqlStreamingChangeEventSource]
2024-02-10T02:50:38.840658180Z 2024-02-10 02:50:38,840 WARN   ||  [Producer clientId=connector-producer-demo-mysql-connector-0] Error while fetching metadata with correlation id 6 : {cdc-demo.demo.user=LEADER_NOT_AVAILABLE}   [org.apache.kafka.clients.NetworkClient]
2024-02-10T02:50:38.942719346Z 2024-02-10 02:50:38,942 INFO   ||  [Producer clientId=connector-producer-demo-mysql-connector-0] Resetting the last seen epoch of partition cdc-demo-0 to 0 since the associated topicId changed from null to VHOTRwrvRnesMCamT0L4lw   [org.apache.kafka.clients.Metadata]
2024-02-10T02:50:42.992670667Z 2024-02-10 02:50:42,992 INFO   ||  WorkerSourceTask{id=demo-mysql-connector-0} Committing offsets for 11 acknowledged messages   [org.apache.kafka.connect.runtime.WorkerSourceTask]
2024-02-10T02:50:46.406971127Z 2024-02-10 02:50:46,406 INFO   ||  192.168.112.9 - - [10/Feb/2024:02:50:46 +0000] "GET /connectors HTTP/1.1" 200 24 "-" "Apache-HttpClient/4.5.13 (Java/11.0.18)" 6   [org.apache.kafka.connect.runtime.rest.RestServer]
2024-02-10T02:50:46.411062961Z 2024-02-10 02:50:46,410 INFO   ||  192.168.112.9 - - [10/Feb/2024:02:50:46 +0000] "GET /connectors/demo-mysql-connector HTTP/1.1" 200 552 "-" "Apache-HttpClient/4.5.13 (Java/11.0.18)" 2   [org.apache.kafka.connect.runtime.rest.RestServer]
2024-02-10T02:50:46.413901002Z 2024-02-10 02:50:46,413 INFO   ||  192.168.112.9 - - [10/Feb/2024:02:50:46 +0000] "GET /connectors/demo-mysql-connector/status HTTP/1.1" 200 182 "-" "Apache-HttpClient/4.5.13 (Java/11.0.18)" 1   [org.apache.kafka.connect.runtime.rest.RestServer]
2024-02-10T02:50:56.407709507Z 2024-02-10 02:50:56,407 INFO   ||  192.168.112.9 - - [10/Feb/2024:02:50:56 +0000] "GET /connectors HTTP/1.1" 200 24 "-" "Apache-HttpClient/4.5.13 (Java/11.0.18)" 8   [org.apache.kafka.connect.runtime.rest.RestServer]
2024-02-10T02:50:56.409490715Z 2024-02-10 02:50:56,409 INFO   ||  192.168.112.9 - - [10/Feb/2024:02:50:56 +0000] "GET /connectors/demo-mysql-connector HTTP/1.1" 200 552 "-" "Apache-HttpClient/4.5.13 (Java/11.0.18)" 2   [org.apache.kafka.connect.runtime.rest.RestServer]
2024-02-10T02:50:56.413279299Z 2024-02-10 02:50:56,412 INFO   ||  192.168.112.9 - - [10/Feb/2024:02:50:56 +0000] "GET /connectors/demo-mysql-connector/status HTTP/1.1" 200 182 "-" "Apache-HttpClient/4.5.13 (Java/11.0.18)" 2   [org.apache.kafka.connect.runtime.rest.RestServer]
2024-02-10T02:51:00.020035467Z 2024-02-10 02:51:00,012 INFO   MySQL|cdc-demo|snapshot  1 records sent during previous 00:00:21.319, last recorded offset of {server=cdc-demo} partition is {transaction_id=null, ts_sec=1707533459, file=mysql-bin.000003, pos=2353, row=1, server_id=1, event=2}   [io.debezium.connector.common.BaseSourceTask]
```

## Kafka Commands

```shell
# 토픽 리스트 확인
./kafka_sh/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

```

## Schema Registry Commands

* API 참고
* https://docs.confluent.io/platform/current/schema-registry/develop/using.html

```shell
curl -X GET localhost:8081/schemas
curl -X GET localhost:8081/subjects

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8081/subjects/my-test-subjects/versions -d '{  "schema" : "{\"type\": \"record\",\"name\": \"User\",\"namespace\": \"schema.data\",\"doc\": \"유저\",\"fields\": [{\"name\": \"id\", \"type\": [\"int\"], \"doc\": \"pk\"},{\"name\": \"email\", \"type\": [\"string\"], \"doc\": \"이메일\"},{\"name\": \"password\", \"type\": [\"string\"], \"doc\": \"암호\"},{\"name\": \"nickname\", \"type\": [\"string\"], \"doc\": \"별명\"},{\"name\": \"user_status\", \"type\": [\"string\"], \"default\": \"ACTIVE\", \"doc\": \"상태\"}]}"}'

curl -X DELETE localhost:8081/subjects/user-topic-value
```

### Ref

* https://debezium.io/documentation/reference/stable/tutorial.html
* https://debezium.io/documentation/reference/stable/connectors/mysql.html
* https://github.com/debezium/debezium-examples