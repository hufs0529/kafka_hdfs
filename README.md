<br />
<div align="center">
  <a href="https://zenml.io">
    <img alt="spring" src="https://github.com/hufs0529/penterest/assets/81501114/7d3522e9-9e2a-4bbf-b06b-6748f23a84c6" alt="Logo" width="150" height="100">
    <img alt="kafka" src="https://github.com/hufs0529/kafka_hdfs/assets/81501114/c93a49a1-767a-4ba5-b7e9-1020f619dbc8" alt="Logo" width="150" height="100">
    <img alt="hdfs" src="https://github.com/hufs0529/kafka_hdfs/assets/81501114/3ca933e2-81a6-4d23-81b5-d987bfbbb5bb" alt="Logo" width="150" height="100">
    <img alt="docker-compose" src="https://github.com/hufs0529/kafka_hdfs/assets/81501114/82a79a6b-823e-4ada-a36d-2a98183c778d" alt="Logo" width="150" height="100">
    <img alt=s3" src="https://github.com/hufs0529/kafka_hdfs/assets/81501114/9127a01e-f3a0-48ca-a48d-d226595e6880" alt="Logo" width="150" height="100">


  </a>

<h3 align="center">Data Lake With Hadoop & KAFKA</h3>

  <h2 align="center">
    Spring의로부터의 사용자 Log 수집 및 Kafka, Hadoop을 이용한 데이터 레이크
    <br />
    <br />
    <br />
  </h2>
  <h3>Role: Spring API, Kafka, Hadoop, S3</h3>

</div>

<br />

# 🤖 API
  [Wanted Pre On Boarding 사전 과제 API 이용](https://github.com/hufs0529/penterest/wanted-pre-onboarding-backend)

# 🤹 Introduction
-  logback-kafka-appender(https://github.com/danielwegener/logback-kafka-appender) 을 이용하여 유저로부터 Log 데이터 수집
-  docker-compose로 구성한 kafka 서버를 통해서 데이터 주입
-  HDFS Sink connector을 통해서 데이터 HDFS에 적재


# 🔋 Architecture
<img width="527" alt="화면 캡처 2023-08-22 133146" src="https://github.com/hufs0529/kafka_hdfs/assets/81501114/9870c107-a185-4874-ada1-f60f4e413b90">


# 🗺 docker-compose
        # kafka 클러스터링
        cd /kafka/docker compose up -d

        # hadoop 클러스터링
        cd /hadoop/docker compose up -d


# 🏇 Composition
#### Kafka 클러스터 및 Kafka Connect, HDFS Sink Connector 설정
- Kafka 실행
  
       cd /kafka/docker compose up -d

- Kafka Connect 설치

        $ wget https://packages.confluent.io/archive/6.1/confluent-6.1.0.tar.gz
        $ tar xvf confluent-6.1.0.tar.gz
- Kafka Connect 실행(Kafka 서버가 실행 되어 있어야 한다)
        $ {KAFKA_HOME}/bin/connect-distributed -daemon ./etc/kafka/connect-distributed.properties

- Kafka Connect 확인
        $ ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
        <img width="527" alt="화면 캡처 2023-08-22 133146" src="https://github.com/hufs0529/kafka_hdfs/assets/81501114/12f9a676-8847-4197-b20e-21db775a4c0b">
        - 4개의 default 토픽 생성

- Confluent-Hub 설치
##### Confluent-Hub를 이용하면 Connector를 명령어로 설치할 수 있다
        # 명령어 입력하면 confluent-hub 압축 파일 받을 수 있다
        curl -L --http1.1 -o confluent-hub-client-latest.tar.gz https://packages.confluent.io/archive/5.5/confluent-hub-client-5.5.3.tar.gz

        # 압축해제 후 디렉토리 입장 후 /bin디렉토리 입장!!!
        # confluent-hub 파일 이 있는데 docker command처럼 /bin디렉토리에서 사용 가능

        confluent-hub install confluentinc/kafka-connect-s3-source:latest

- HDFS Sink Connector 설치

  <img width="527" alt="화면 캡처 2023-08-22 133146" src="https://github.com/hufs0529/kafka_hdfs/assets/81501114/3bb94563-87d9-485f-a4bb-277567f27a84">
##### /usr/share/confluent-hub-components PATH에 다운로드 할거냐고 묻는데 N 입력시 직접 원하는 위치에 설치 가능하다
##### 주의해야 할점은 /usr/share/confluent-hub-components 디렉토리가 자동 생성이 되지 않으므로 mkdir /usr/share/confluent-hub-component 필수!!!


- Config 설정
  
            curl -X POST -H "Content-Type: application/json" --data '{
              "name": "hdfs3-sink",
              "config": {
                "connector.class": "io.confluent.connect.hdfs3.Hdfs3SinkConnector",
                "tasks.max": "1",
                "topics": "{TOPIC}",
                "hdfs.url": "hdfs://{namenode}:9000",
                "flush.size": "3",
                "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                "value.converter": "io.confluent.connect.avro.AvroConverter",
                "value.converter.schema.registry.url":"http://localhost:8081",
                "confluent.topic.bootstrap.servers": "localhost:9092",
                "confluent.topic.replication.factor": "1"
              }
            }' http://localhost:8083/connectors
  ##### Kafka Connector은 API기반으로 통신하기 때문에 POST를 통해서 Config 설정이 가능하다

### Spring 내 Kafka Consumer 설정

      # "exam" 토픽과 "foo" Group에 대해서 Kafka Consumer 동작
      public KafkaConsumer(HDFSConfig hdfsConfig) {
              this.hdfsConfig = hdfsConfig;
          }
      
          @KafkaListener(topics = "exam", groupId = "foo")
          public void consume(String message) throws Exception {
              System.out.println(String.format("Consumed message : %s", message));
      
              try {
                  writeMessageToS3(message);
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }

    


#### Spring 로그 추출
##### maven설정을 통해서 의존성 주입
        <dependency>
    			<groupId>com.github.danielwegener</groupId>
    			<artifactId>logback-kafka-appender</artifactId>
    			<version>0.1.0</version>
    		</dependency>
    		<dependency>
    			<groupId>net.logstash.logback</groupId>
    			<artifactId>logstash-logback-encoder</artifactId>
    			<version>5.2</version> <!-- Use the version you need -->
    		</dependency>
    		<dependency>
    			<groupId>ch.qos.logback</groupId>
    			<artifactId>logback-classic</artifactId>
    			<version>1.2.3</version>
    			<scope>runtime</scope>
    		</dependency>

##### resources/logback.xml을 통해서 로그가 찍힐 패턴 및 topic을 설정해준다

        # logback.xml
        <?xml version="1.0" encoding="UTF-8"?>
          <configuration>
              <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
                  <layout class="ch.qos.logback.classic.PatternLayout">
                      <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>
                  </layout>
              </appender>
          
              <!-- Layout 1  :  kafkaAppender -->
              <appender name="kafkaAppender" class="com.github.danielwegener.logback.kafka.KafkaAppender">
                  <encoder class="com.github.danielwegener.logback.kafka.encoding.LayoutKafkaMessageEncoder">
                      <layout class="ch.qos.logback.classic.PatternLayout">
                          <Pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>
                          <!--                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>-->
                      </layout>
                  </encoder>
                  <topic>{topic}</topic>
                  <producerConfig>bootstrap.servers=localhost:9092</producerConfig>
              </appender>
          
              <!-- Async 한 KafkaAppender 추가 -->
              <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
                  <appender-ref ref="kafkaAppender"/>
              </appender>
          
          
              <!-- logger -->
              <logger name="org.apache.kafka" level="ERROR"/>
              <logger name="com.minsub.java.logger.kafka" level="DEBUG">
                  <!-- Layout 1 -->
                  <appender-ref ref="kafkaAppender"/>
                  <!-- Layout 2 -->
                  <!--        <appender-ref ref="logstashKafkaAppender" />-->
              </logger>
          
              <root level="DEBUG">
                  <appender-ref ref="STDOUT"/>
              </root>
          
              <root level="info">
                  <appender-ref ref="ASYNC" />
              </root>

          
          </configuration>

##### LoggerFactory 설정
        # 로그가 찍힐 Controller에 다음과 같이 설정
        private static final Logger kafkaLogger = LoggerFactory.getLogger("kafkaLogger");
        

##### Log 찍기
        # 여기서는 사용자가 게시물을 검색(search)했을 시 관련 로그가 찍히는 것을 활용하였다
        @ResponseStatus(HttpStatus.OK)
        @GetMapping("/search")
        public ResponseEntity search(
                @RequestParam(value = "sort", defaultValue = "createDate") String sort,
                @RequestParam(value = "page", defaultValue = "1") Integer page,
                @RequestParam(value = "size", defaultValue = "5") Integer size,
                @ModelAttribute PostSearchCondition postSearchCondition) {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sort).ascending());
    
            String searcher = checkWriter();
    
            kafkaLogger.info("Search request - Sort: {}, Page: {}, Size: {}, Condition: {}, Searcher: {}", sort, page, size, postSearchCondition, searcher);
    
            return ResponseEntity.ok(postService.getPostList(pageable, postSearchCondition));
        }


##### kafka consumer
![kafka log](https://github.com/hufs0529/kafka_hdfs/assets/81501114/eb82f20e-a854-40a2-8f47-139564c27015)



### S3 적재
##### 앞선 KafkaConsumer 클래스 내에 존재하는 s3 적재 함수를 이용해서 생성되는 날짜별로 s3에 적재
        private void writeMessageToS3(String message) throws Exception {

            BasicAWSCredentials awsCreds = new BasicAWSCredentials("", "");
            AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();
    
            String s3Bucket = "{bucket}";
            String s3ObjectKey = generateFileName();// S3 버킷에 저장될 객체의 키 (디렉토리 포함)
    
            // 메시지를 바이트 배열로 변환
            byte[] messageBytes = message.getBytes();
    
            // S3 객체 메타데이터 설정 (선택 사항)
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(messageBytes.length);
            metadata.setContentType("text/plain"); // MIME 타입 설정 (예: 텍스트)
    
            // S3에 객체 업로드
            amazonS3.putObject(new PutObjectRequest(s3Bucket, s3ObjectKey, new ByteArrayInputStream(messageBytes), metadata));
        }
