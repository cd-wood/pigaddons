package com.cwoodson.pigaddons.rutils;

import com.cwoodson.pigaddons.RScriptEngine;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.apache.commons.io.*;

/**
 *
 * @author connor-woodson
 */
public class LibraryLoader
{    
    private LibraryLoader()
    {}
    
    public static void extractNativeLibraries(String fromPath, String toPath, boolean overwrite, String... files) throws IOException
    {
        for(String file : files)
        {
            extractNativeLibrary(fromPath + file, toPath + file, overwrite);
        }
    }
    
    public static void addJavaLibraryPath(String path) throws NoSuchFieldException, IllegalAccessException
    {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);
        
        final String[] paths = (String[])usrPathsField.get(null);
        
        for(String p : paths)
        {
            if(p.equals(path))
            {
                return;
            }
        }
        
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[paths.length] = path;
        usrPathsField.set(null, newPaths);
    }
    
    private static void extractNativeLibrary(String from, String to, boolean overwrite) throws IOException
    {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = RScriptEngine.class.getResourceAsStream(from);
            File fileOut = new File(to);
            if(fileOut.exists())
            {
                if(overwrite)
                {
                    fileOut.delete();
                } else
                {
                    return;
                }
            }
            fileOut.getParentFile().mkdirs();
            out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
        } finally {
            if(in != null)
            {
                in.close();
            }
            if(out != null)
            {
                out.close();
            }
        }
    }
}
