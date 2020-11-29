package com.gpch.grpc.fileservice;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.gpch.grpc.protobuf.DataChunk;
import com.gpch.grpc.protobuf.DownloadFileRequest;
import com.gpch.grpc.protobuf.FileName;
import com.gpch.grpc.protobuf.FileServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@GRpcService
@Slf4j
public class FileServiceImpl extends FileServiceGrpc.FileServiceImplBase {

    @Override
    public void downloadFile(DownloadFileRequest request, StreamObserver<DataChunk> responseObserver) {
        try {
            String fileName = request.getFileName();

            // read the file and convert to a byte array

            Path path = Paths.get(fileName);
            byte[] bytes = Files.readAllBytes(path);

            BufferedInputStream fileStream = new BufferedInputStream(new ByteArrayInputStream(bytes));

            int bufferSize = Integer.MAX_VALUE/2;
            byte[] buffer = new byte[bufferSize];
            int length;
            while ((length = fileStream.read(buffer, 0, bufferSize)) != -1) {
                responseObserver.onNext(DataChunk.newBuilder()
                        .setData(ByteString.copyFrom(buffer, 0, length))
                        .setSize(bufferSize)
                        .build());
            }
            fileStream.close();
            responseObserver.onCompleted();
        } catch (Throwable e) {
            responseObserver.onError(Status.ABORTED
                    .withDescription("Unable to acquire the image " + request.getFileName())
                    .withCause(e)
                    .asException());
        }
    }

    @Override
    public void getAvailableFiles(Empty request, StreamObserver<FileName> responseObserver) {
        String[] pathnames;
        File f = new File("files");
        pathnames = f.list();
        for (String pathname : pathnames) {
            responseObserver.onNext(FileName.newBuilder().setFileName(pathname).build());
        }

        responseObserver.onCompleted();

    }
}
