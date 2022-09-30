package org.example;

import com.jcraft.jsch.*;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SftpClient {

    private final static String USERNAME = "sftp_user@bmwi-vhp-china.de";
    private final static String REMOTEHOST = "ssh.strato.de";
    private final static String PASSWORD = "Thinkport123!?";
    public final static String LOCAL_PICTURE_PATH = "/Users/frederikhartung/Downloads/";
    public final static String REMOTE_PICTURE_PATH = "/";
    public final static String FILENAME = "Cat03.jpeg";
    private final static  String LOCAL_PATH_GZIP_TEMP_FOLDER = "/Users/frederikhartung/Downloads/temp/gzip/";
    private ChannelSftp channel;

    public SftpClient() throws JSchException {
        this.channel = setupJsch();
    }

    private ChannelSftp setupJsch() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts("/Users/frederikhartung/.ssh/known_hosts");
        Session jschSession = jsch.getSession(USERNAME, REMOTEHOST);
        jschSession.setPassword(PASSWORD);
        //jschSession.setConfig("kex", "diffie-hellman-group1-sha1");
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }

    public void zipRemoteFile(List<String> filePaths){
        connect();
        try {
            for(String filePath : filePaths){
                InputStream stream = channel.get(filePath);
                compressGzipFile(stream, filePath);
            }
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
        exit();
    }

//    public void zipFile(){
//        String fileName = "/Users/frederikhartung/Downloads/Cat03.jpeg";
//        Long MILLS_IN_DAY = 86400000L;
//
//        try{
//            FileInputStream  fis = new FileInputStream(fileName);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            ZipInputStream zis = new ZipInputStream(bis);
//            ZipEntry ze;
//
//            while ((ze = zis.getNextEntry()) != null) {
//                System.out.println(ze.getName());
//            }
//
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

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

    public void decompressGzipFile(String gzipFile, String newFile) {
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


    public boolean checkIfFileExists(String filePath){
        try{
            channel.ls(filePath);
        }
        catch (SftpException ex){
            return false;
        }

        return true;
    }

    public void checkIfFileSExists(List<String> fileNames){
        this.connect();

        int anzahlVorhanden = 0;
        for(String s : fileNames){
            if(this.checkIfFileExists(s)){
                anzahlVorhanden++;
                System.out.println("Anzahl vorhanden: " + anzahlVorhanden);
            }
        }

        this.exit();
    }

    public void uploadDummyFile(){
        connect();
        try {
            channel.put(LOCAL_PICTURE_PATH + FILENAME, REMOTE_PICTURE_PATH + FILENAME);
        } catch (SftpException e) {
            throw new RuntimeException(e);
        }
        System.out.println("uploaded file successfull");
        exit();
    }

    public boolean deleteFile(String path){
        connect();
        try {
            channel.rm(path);
        } catch (SftpException e) {
            System.out.println("error removing file: " + path);
            return false;
        }
        System.out.println("removed file successfully: " + path);
        exit();
        return true;
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
