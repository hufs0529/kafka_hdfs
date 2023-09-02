package wanted.wantedpreonboardingbackend.domain.kafka;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import wanted.wantedpreonboardingbackend.domain.hdfs.HDFSConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class KafkaConsumer {
    private final HDFSConfig hdfsConfig;

    public KafkaConsumer(HDFSConfig hdfsConfig) {
        this.hdfsConfig = hdfsConfig;
    }

    @KafkaListener(topics = "exam", groupId = "foo")
    public void consume(String message) throws Exception {
        System.out.println(String.format("Consumed message : %s", message));

        try {
            //writeMessageToHDFS(message);
            writeMessageToHDFS(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        return dateFormat.format(currentDate) + "_sample.txt";
    }

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


    private void writeMessageToHDFS(String message) throws Exception {
        System.out.println("connect with hdfs");
        try (FileSystem fs = FileSystem.get(hdfsConfig)) {
            String hdfsPath = "/test"; // Specify the HDFS path where you want to store the data
            String fileName = "output.txt";
            Path outputPath = new Path(hdfsPath + "/" + fileName);
            System.out.println(outputPath);

            // Create or overwrite the file with the message
            try (FSDataOutputStream outputStream = fs.create(outputPath)){
                String processedData = "Processed: " + message;
                System.out.println(processedData);
                outputStream.writeBytes(message);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    
}