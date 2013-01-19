package com.cwoodson.pigaddons.rutils;

import com.cwoodson.pigaddons.RScriptEngine;
import java.io.*;
import java.lang.reflect.Field;
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
    
    public static void forceSetJavaLibraryPath(String path) throws NoSuchFieldException, IllegalAccessException
    {
        String oldPath = System.getProperty("java.library.path");
        String newPath;
        if(oldPath == null || oldPath.isEmpty())
        {
            newPath = path;
        } else
        {
            newPath = oldPath + System.getProperty("path.separator") + path;
        }
        System.setProperty("java.library.path", newPath);
 
        Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
        fieldSysPath.setAccessible( true );
        fieldSysPath.set( null, null );
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
