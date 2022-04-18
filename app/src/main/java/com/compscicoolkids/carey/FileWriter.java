package com.compscicoolkids.carey;

import android.content.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileWriter {
    private final String fileName = "runs.txt";
    private final File path;

    public FileWriter(Context c){
        path = c.getFilesDir();
    }

    public void runsToBytes(ArrayList<Run> runs){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        byte[] runsToBytes = null;
        try{
            out = new ObjectOutputStream(bos);
            out.writeObject(runs);
            out.flush();
            runsToBytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                writeRuns(runsToBytes);
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    private void writeRuns(byte[] runsBytes){
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, fileName));
            writer.write(runsBytes);
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Run> bytesToRuns(){
        File readFrom = new File(path, fileName);
        byte[] runsContents =  new byte[(int) readFrom.length()];
        try {
            FileInputStream is = new FileInputStream(readFrom);
            is.read(runsContents);
            is.close();
        }catch(Exception ignored){
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(runsContents);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            Object o = in.readObject();
            if (o instanceof ArrayList) {
                return (ArrayList<Run>) o;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
