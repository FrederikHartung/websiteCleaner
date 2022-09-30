package org.example;

import com.jcraft.jsch.JSchException;

import javax.naming.ConfigurationException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            ApplicationConfig.readConfig();
        } catch (ConfigurationException e) {
            System.out.println(e.getMessage());
            return;
        }

        String ressourceName = "files.txt";
        FileNameRessource ressource = new FileNameRessource(ressourceName);
        List<String> fileNames = ressource.getFileContent();
        SftpClient client = null;
        try {
            client = new SftpClient(ApplicationConfig.getUserName(), ApplicationConfig.getPassWord(), ApplicationConfig.getHost());
        } catch (JSchException e) {
            System.out.println(e.getMessage());
        }

        client.zipAllRemoteFile(fileNames);
    }
}