package wanted.wantedpreonboardingbackend.domain.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.function.Consumer;

@org.springframework.context.annotation.Configuration
public class HDFSConfig extends Configuration {

    @Bean(name = "conf")
    public static Configuration getConfiguration() {
        Configuration config = new Configuration();
        config.set("fs.defaultFS", "hdfs://127.0.0.1:9000"); // HDFS 네임노드 주소로 변경
        return config;
    }

}
