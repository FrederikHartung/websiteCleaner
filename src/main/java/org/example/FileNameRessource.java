package org.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileNameRessource {
    private String fileName;

    public FileNameRessource(String fileName) {
        this.fileName = fileName;
    }

    private InputStream getFileAsIOStream()
    {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }

    public List<String> getFileContent()
    {
        InputStream is = getFileAsIOStream();
        List<String> fileNames = new ArrayList<>();
        try (InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr);)
        {
            String line;
            while ((line = br.readLine()) != null) {
                fileNames.add(line);
            }
            is.close();
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return fileNames;
    }
}
