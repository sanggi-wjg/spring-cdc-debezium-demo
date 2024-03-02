# CDC Demo

## Kafka Commands

```shell
# 토픽 리스트 확인
./bin/kafka-topics.sh --list --bootstrap-server localhost:9092

```

## Schema Registry Commands

* API 참고
* https://docs.confluent.io/platform/current/schema-registry/develop/using.html

```shell
curl -X GET localhost:8081/schemas
curl -X GET localhost:8081/subjects

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8081/subjects/my-subjects/versions -d '{  "schema" : "{\"type\": \"record\",\"name\": \"User\",\"namespace\": \"schema.data\",\"doc\": \"유저\",\"fields\": [{\"name\": \"id\", \"type\": [\"int\"], \"doc\": \"pk\"},{\"name\": \"email\", \"type\": [\"string\"], \"doc\": \"이메일\"},{\"name\": \"password\", \"type\": [\"string\"], \"doc\": \"암호\"},{\"name\": \"nickname\", \"type\": [\"string\"], \"doc\": \"별명\"},{\"name\": \"user_status\", \"type\": [\"string\"], \"default\": \"ACTIVE\", \"doc\": \"상태\"}]}"}'
```
