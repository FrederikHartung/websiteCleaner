package org.example;

import javax.naming.ConfigurationException;

public class Main {
    public static void main(String[] args) {
        try {
            ApplicationConfig.readConfig();
        } catch (ConfigurationException e) {
            System.out.println(e.getMessage());
            return;
        }
//        String ressourceName = "files.txt";
//        FileNameRessource ressource = new FileNameRessource(ressourceName);
//        List<String> fileNames = ressource.getFileContent();
//        SftpClient client = null;
//        try {
//            client = new SftpClient();
//        } catch (JSchException e) {
//            System.out.println(e.getMessage());
//        }
//
//        if(client != null){
//            client.zipRemoteFile(List.of(fileNames.get(0)));
//        }
    }
}