package org.example;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SftpClient {

    private final String userName;
    private final String password;
    private final String host;

    public final static String LOCAL_PICTURE_PATH = "/Users/frederikhartung/Downloads/";
    public final static String REMOTE_PICTURE_PATH = "/";
    public final static String FILENAME = "Cat03.jpeg";
    private final static  String LOCAL_PATH_GZIP_TEMP_FOLDER = "/Users/frederikhartung/Downloads/temp/gzip/";
    private ChannelSftp channel;

    public SftpClient(String userName, String password, String host) throws JSchException {
        this.userName = userName;
        this.password = password;
        this.host = host;
        this.channel = setupJsch();
    }

    private ChannelSftp setupJsch() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts("/Users/frederikhartung/.ssh/known_hosts");
        Session jschSession = jsch.getSession(userName, host);
        jschSession.setPassword(password);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }

    public void zipAllRemoteFile(List<String> filePaths){
        connect();
        try {
            for(String filePath : filePaths){
                if(checkIfFileExists(filePath)){
                    InputStream stream = channel.get(filePath);
                    compressGzipFile(stream, filePath);
                }
                else {
                    System.out.println("file " + filePath + " does not exists on the remote server");
                }
            }
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
        System.out.println("compressing of all files finished");
        exit();
    }

    private void compressGzipFile(InputStream stream, String filePath) {
        if(filePath.contains("/")){
            filePath = filePath.replace("/", "_tTt_");
        }

        String gzipFile = LOCAL_PATH_GZIP_TEMP_FOLDER + filePath + ".gz";
        System.out.println("compressing " + filePath + " to file" + gzipFile + " ...");
        try {
            FileOutputStream fos = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=stream.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("compressing done");
    }

    private void decompressGzipFile(String gzipFile, String newFile) {
        System.out.println("decompressing gzip file " + gzipFile + " to file" + gzipFile + " ...");
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("decompressing done");
    }


    private boolean checkIfFileExists(String filePath){
        try{
            channel.ls(filePath);
            return true;
        }
        catch (SftpException ex){
            return false;
        }
    }

    public void checkIfFilesExists(List<String> fileNames){
        this.connect();
        int steps = fileNames.size();
        int counter_step = 0;

        int anzahlVorhanden = 0;
        for(String s : fileNames){
            counter_step++;
            System.out.println("step " + counter_step + "/" + steps);
            if(this.checkIfFileExists(s)){
                anzahlVorhanden++;
                System.out.println("Anzahl vorhanden: " + anzahlVorhanden);
            }
        }
        System.out.println("finished checking, Anzahl vorhanden: " + anzahlVorhanden);

        this.exit();
    }

    public void uploadAllZipedFile(List<String> fileNames){
        connect();
        int steps = fileNames.size();
        int counter_step = 0;
        int counter_successful = 0;
        for(String fileName : fileNames){
            try {
                if(fileName.contains("/")){
                    fileName = fileName.replace("/", "_tTt_");
                }
                counter_step++;
                System.out.println("uploading file " + counter_step + "/" + steps);
                String gzipFile = LOCAL_PATH_GZIP_TEMP_FOLDER + fileName + ".gz";
                if(fileName.contains("_tTt_")){
                    fileName = fileName.replace("_tTt_", "/");
                }
                String remoteFileName = fileName + ".gz";
                channel.put(gzipFile, remoteFileName);
                counter_successful++;
            } catch (SftpException e) {
                System.out.println("error uploading file " + fileName + ", cause: " + e.getMessage());
            }
        }
        System.out.println("successful: " + counter_successful);
        System.out.println("uploaded all zipped files finished");
        exit();
    }

    public void deleteAllEvilFiles(List<String> fileNames){
        connect();
        int steps = fileNames.size();
        int counter_steps = 0;
        for(String fileName:fileNames){
            try {
                counter_steps++;
                System.out.println("deleting evil files " + counter_steps + "/" + steps);
                channel.rm(fileName);
            } catch (SftpException e) {
                System.out.println("error removing file: " + fileName + ", cause: " + e.getMessage());
            }
        }
        System.out.println("finished removing evil files");
        exit();
    }

    public void connect(){
        try {
            channel.connect();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    public void exit(){
        channel.disconnect();
        try {
            channel.getSession().disconnect();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    private void uploadFile(String pathLocalFile, String pathRemoteDir) throws JSchException, SftpException {
        //String pathLocalFile = "src/main/resources/sample.txt";
        //String pathRemoteDir = "remote_sftp_test/jschFile.txt";
        ChannelSftp channelSftp = setupJsch();
        channelSftp.connect();

        channelSftp.put(pathLocalFile, pathRemoteDir);

        channelSftp.exit();
    }
}
