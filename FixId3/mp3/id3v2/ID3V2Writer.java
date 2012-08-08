
package mp3.id3v2;

import mp3.util.OutputCtr;
import mp3.Defines;
import mp3.UndersizedException;

import java.io.*;

/**
 * The API for writing an ID3V2 tag to a file. Currently there is only the
 * option to update a tag. You cannot write a tag to a file that has none.
 * Additionally there is no way to resize a tag. I.e.: if the file already has a
 * tag that is 2 K (with padding) and you want to write a tag that is 3 k, it
 * will fail.
 */
// FIXME! See description.
public class ID3V2Writer implements Defines
{
    public static void writeTag(File f, ID3V2Tag tag) throws IOException
    {
        if (tag == null)
        {
            OutputCtr.println(0, "Can't write null-Tag!");
            return;
        }

        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        // First we need to make sure the file starts with a tag
        byte[] b = new byte[3];
        raf.seek(0);
        raf.read(b);
        if (new String(b).equalsIgnoreCase("ID3"))
        {
            try
            {
                raf.seek(0);
                b = new byte[ID3V2_HEADER_SIZE];
                raf.read(b);
                Header head = new Header(b);
                long originalSize = head.getSize() + ID3V2_HEADER_SIZE;

                if (tag.getEffectiveSize() <= originalSize)
                {
                    // Okay, I think we can write directly :-)
                    b = tag.toByteArray();
                    if (b.length <= originalSize)
                    {
                        raf.seek(0);
                        raf.write(b);
                    }
                    else
                    {
                        copyWrite(raf, f, tag, originalSize);
                    }
                }
                else
                {
                    copyWrite(raf, f, tag, originalSize);
                }
            }
            catch (UndersizedException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            copyWrite(raf, f, tag, 0);
        }
        raf.close();
    }

    private static void copyWrite(RandomAccessFile raf, File f, ID3V2Tag tag, long originalsize) throws IOException
    {
        // first we open a temporary file
        //File directory = f.getParentFile ();
        File directory = getParentFile(f);
        OutputCtr.println(3, "Path: " + directory);
        //File tempFile = File.createTempFile ("mp3info_", ".mp3", directory);
        File tempFile = createTempFile("mp3info_", ".mp3", directory);
        RandomAccessFile raf2 = new RandomAccessFile(tempFile, "rw");
        raf2.seek(0);
        byte[] b = tag.toByteArray();
        OutputCtr.println(2, "tagsize: " + tag.getEffectiveSize() + " arraysize: " + b.length);
        raf2.write(b);
        raf.seek(originalsize);
        byte[] buffer = new byte[16384];
        int bytesRead = raf.read(buffer);
        while (bytesRead > -1)
        {
            raf2.write(buffer, 0, bytesRead);
            bytesRead = raf.read(buffer);
        }

        raf2.close();
        raf2 = null;
        raf.close();
        String name = f.getPath();
        f.delete();
        tempFile.renameTo(new File(name));

    }

    /**
     * Same as f.getParentFile() in JDK1.3.
     * 
     * @requires the File parameter must be a File object constructed with the
     *           canonical path.
     */
    private static File getParentFile(File f)
    {
        // slight changes to get around getParent bug on Mac
        String name = f.getParent();
        if (name == null)
            return null;
        try
        {
            return getCanonicalFile(new File(name));
        }
        catch (IOException ioe)
        {
            //if the exception occurs, simply return null
            return null;
        }
    }

    /** Same as f.getCanonicalFile() in JDK1.3. */
    private static File getCanonicalFile(File f) throws IOException
    {
        return new File(f.getCanonicalPath());
    }

    /** We don't have JDK1.3 createTempFile(), we'll just do it here....
     */
    private static File createTempFile(String prefix, String suffix, File directory) throws IOException
    {

        for (int i = 0; i < 1000; i++)
        {
            String newFileName = prefix + i + suffix;
            File newFile = new File(directory, newFileName);
            if (newFile.exists())
                continue;
            return newFile;
        }
        throw new IOException("Couldn't create file!!!");
    }

}