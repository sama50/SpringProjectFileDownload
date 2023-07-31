package com.example.springproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;     
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


import java.io.InputStream;
import java.io.RandomAccessFile; 
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@RestController
@CrossOrigin("*") // Cross origin protection
@RequestMapping("/api") // routes 
public class SpringprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringprojectApplication.class, args);
	}

	@GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }
	private final String FILE_PATH = "C:\\Users\\samad\\OneDrive\\Desktop\\samaaro_project\\demiFile.zip";
    private final String FILE_NAME = "demiFile.zip";

	// @GetMapping("/download")
	// public ResponseEntity<Resource> downloadFile() throws IOException {
    //     File file = new File(FILE_PATH);
    //     InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + FILE_NAME);

    //     return ResponseEntity.ok()
    //             .headers(headers)
    //             .contentLength(file.length())
    //             .body(resource);
    // }
    
    // @RequestMapping("/streamresource")
    // public ResponseEntity<Resource> getStreamResource() throws URISyntaxException, IOException {
     
    // File file = new File(FILE_PATH);
    // Resource resource = new InputStreamResource(new FileInputStream(file));
    
    // HttpHeaders headers = new HttpHeaders();
    // headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receivedFile.zip");
    
    // return ResponseEntity.ok()
    //         .headers(headers)
    //         .contentLength(file.length())
    //         .contentType(MediaType.APPLICATION_OCTET_STREAM)
    //         .body(resource);
    // }

    
    @GetMapping(value = "/downloadlargefile")
    public ResponseEntity<StreamingResponseBody> downloadFilesama(
        @RequestHeader(value = "Range", required = false) String rangeHeader,
        HttpSession session) {
        try {
            String fileName = "large-file.zip";
            String filePathString = FILE_PATH;
            Path filePath = Paths.get(filePathString);
            Long fileSize = Files.size(filePath);
            byte[] buffer = new byte[8192]; // Increased buffer size to 8 KB
            HttpHeaders responseHeaders = new HttpHeaders();
             

            String contentType = "application/zip"; // Set the appropriate content type for ZIP files
            responseHeaders.setContentType(MediaType.parseMediaType(contentType));
            responseHeaders.setContentLength(fileSize);
            responseHeaders.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            if (rangeHeader == null) {
                StreamingResponseBody responseStream = outputStream -> {
                    try (InputStream inputStream = Files.newInputStream(filePath)) {
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        System.out.println("File download is complete!1");
                        

                    } catch (IOException e) {
                        // Handle exception appropriately
                    }
                };

                return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.OK);
            }

            // Code for handling range requests (if required)...
            String[] ranges = rangeHeader.split("-");
            Long rangeStart = Long.parseLong(ranges[0].substring(6));
            Long rangeEnd = fileSize - 1;

            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            }

            if (rangeEnd >= fileSize) {
                rangeEnd = fileSize - 1;
            }

            String contentLength = String.valueOf(rangeEnd - rangeStart + 1);
            responseHeaders.setContentLength(Long.parseLong(contentLength));
            responseHeaders.add("Accept-Ranges", "bytes");
            responseHeaders.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);

            final Long _rangeEnd = rangeEnd;

            StreamingResponseBody responseStream = outputStream -> {
                try (RandomAccessFile file = new RandomAccessFile(filePathString, "r")) {
                    long pos = rangeStart;
                    file.seek(pos);
                    int bytesRead;
                    while (pos <= _rangeEnd && (bytesRead = file.read(buffer)) != -1) {
                        if (pos + bytesRead > _rangeEnd) {
                            bytesRead = (int) (_rangeEnd - pos + 1);
                        }
                        outputStream.write(buffer, 0, bytesRead);
                        pos += bytesRead;
                    }
                    System.out.println("File download is complete!2");
                } catch (IOException e) {
                    // Handle exception appropriately
                }
            };

            return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);

        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    @GetMapping(value = "/downloadvideo")
    public ResponseEntity<StreamingResponseBody> downloadFile(
        @RequestHeader(value = "Range", required = false) String rangeHeader
    ) {
        try {
            String filePathString = FILE_PATH;
            Path filePath = Paths.get(filePathString);
            Long fileSize = Files.size(filePath);
            byte[] buffer = new byte[1024];
            HttpHeaders responseHeaders = new HttpHeaders();

            if (rangeHeader == null) {
                responseHeaders.add("Content-Type", "video/mp4");
                // responseHeaders.add("Content-Type", "application/octet-stream");
                responseHeaders.add("Content-Length", fileSize.toString());
                StreamingResponseBody responseStream = outputStream -> {
                    try (RandomAccessFile file = new RandomAccessFile(filePathString, "r")) {
                        int bytesRead;
                        while ((bytesRead = file.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        // Handle exception appropriately
                    }
                };
                return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.OK);
            }

            String[] ranges = rangeHeader.split("-");
            Long rangeStart = Long.parseLong(ranges[0].substring(6));
            Long rangeEnd = fileSize - 1;

            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            }

            if (rangeEnd >= fileSize) {
                rangeEnd = fileSize - 1;
            }

            String contentLength = String.valueOf(rangeEnd - rangeStart + 1);
            responseHeaders.add("Content-Type", "video/mp4");
            // responseHeaders.add("Content-Type", "application/octet-stream");
            responseHeaders.add("Content-Length", contentLength);
            responseHeaders.add("Accept-Ranges", "bytes");
            responseHeaders.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileSize);
            final Long _rangeEnd = rangeEnd;

            StreamingResponseBody responseStream = outputStream -> {
                try (RandomAccessFile file = new RandomAccessFile(filePathString, "r")) {
                    long pos = rangeStart;
                    file.seek(pos);
                    int bytesRead;
                    while (pos <= _rangeEnd && (bytesRead = file.read(buffer)) != -1) {
                        if (pos + bytesRead > _rangeEnd) {
                            bytesRead = (int) (_rangeEnd - pos + 1);
                        }
                        outputStream.write(buffer, 0, bytesRead);
                        pos += bytesRead;
                    }
                } catch (IOException e) {
                    // Handle exception appropriately
                }
            };

            return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    
     

	@GetMapping(value = "/download1", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<Resource> downloadFile1() throws IOException {
        // Replace 'your_file_path' with the actual path to the file you want to send
        File file = new File(FILE_PATH);
        Resource resource = new FileSystemResource(file);

        // Read the file in chunks using Flux
        Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096);

        // Convert the Flux<DataBuffer> to a single DataBuffer
        Mono<DataBuffer> dataBufferMono = DataBufferUtils.join(dataBufferFlux);

        return dataBufferMono.map(dataBuffer -> resource);
    }
    

	 

}
