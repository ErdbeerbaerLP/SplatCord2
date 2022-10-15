package de.erdbeerbaerlp.splatcord2.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderHeaderAware;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class S3TranslationFile {
    private static final File dataFolder = new File("./msbtFiles");

    private final Map<String,String> backend = new HashMap<>();

    public S3TranslationFile(final String lang, final String name) throws Exception {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        final File langDir = new File(dataFolder, lang);
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        final File f = new File(langDir, name + ".csv");
        if (f.exists()) {

            try (CSVReader reader = new CSVReaderHeaderAware(new FileReader(f))) {
                String[] lineInArray;
                while ((lineInArray = reader.readNext()) != null) {

                    backend.put(lineInArray[0], lineInArray[1].replace("\0", ""));
                }
            }
        }else{
            System.err.println("Could not find CSV file "+lang+"/"+name+".csv");
            System.err.println("Please extract the MSBT file from the game as CSV and restart the bot");
        }
    }

    public String getString(final String label){
        return backend.getOrDefault(label, label);
    }

    public Collection<String> getAllValues(){
        return backend.values();
    }


}
