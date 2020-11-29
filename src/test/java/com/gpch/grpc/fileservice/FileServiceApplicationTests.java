package com.gpch.grpc.fileservice;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import org.assertj.core.api.Assertions;
import org.junit.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lognet.springboot.grpc.context.LocalRunningGrpcPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"grpc.port=0"})
public class FileServiceApplicationTests {

    @LocalRunningGrpcPort
    private int port;

    private FileServiceClient fileServiceClient;

    @Before
    public void setup(){
        fileServiceClient = new FileServiceClient("localhost", port);
    }

    @Test
    public void downloadFile() {

        try {
            String fileName = "BLG.docx";
            ByteArrayOutputStream imageOutputStream = fileServiceClient.downloadFile(fileName);
            byte[] bytes = imageOutputStream.toByteArray();
            log.info("File has been downloaded ");
            Assertions.assertThatCode(() -> Files.write(bytes, new File("BLG.docx"))).doesNotThrowAnyException();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getAvailableFiles(){

        try{
            List<String> fileList = fileServiceClient.getFiles();
            log.info("File list has been downloaded ");
            assert fileList.size() > 0;

        }catch (Exception e){
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }


}
