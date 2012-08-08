import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import de.vdheide.mp3.MP3File;

/*
 * Created on Jan 7, 2005
 * By iago
 */

/** This is going to take Mp3's and put them in folders based on the id3 information.
 * It'll copy them or, optionally, move them.
 * @author iago
 *
 */
public class MoveMp3
{
    private static boolean move = false;
    private static boolean recursive = true;
    private static String output = "/tmp/music-test";
    private static String input = "/tmp/music-old";
    
    private static int randomNumber = 0;
    
    public static void main(String []args) throws Exception
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Move or copy? [copy] -->");
        if(in.readLine().equalsIgnoreCase("move"))
            move = true;
        
        System.out.print("Include sub-directories? [yes] -->");
        if(in.readLine().equalsIgnoreCase("no"))
            recursive = false;
        
        System.out.print("Source folder? -->");
        input = in.readLine();
        
        System.out.print("Output folder? -->");
        output = in.readLine();
        
        
        processDirectory(new File(input));
    }

    private static void processDirectory(File f) 
    {
        try
        {
            if(f.isDirectory() == false)
                throw new Exception(f + " is not a directory!");
            
            File []files = f.listFiles();
            
            if(files == null)
                return;
                   
            
            for(int i = 0; i < files.length; i++)
            {
	            if(files[i].isDirectory())
	            {
	                if(recursive)
	                    processDirectory(files[i]);
	            }
	            else if(files[i].getName().matches(".*\\.mp3"))
	            {
	                processFile(files[i]);
	            }
	            else
	            {
	                System.out.println("Unrecognized file, ignoring: " + files[i]);
	            }
	        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private static void processFile(File oldFile) throws Exception
    {
        if(oldFile.isDirectory())
            throw new Exception("This isn't a file: " + oldFile);
        
        MP3File mp3 = new MP3File(oldFile.toString());
        
        String track = mp3.getTrack().getTextContent();
        String artist = mp3.getArtist().getTextContent();
        String album = mp3.getAlbum().getTextContent();
        String year = mp3.getYear().getTextContent();
        String title = mp3.getTitle().getTextContent();
        
        
        if(track == null)
            track = "00";
        if(artist == null)
            artist = "Unknown Artist";
        if(album == null)
            album = "Unknown Album";
        if(year == null)
            year = "";
        if(title == null)
            title = "Unknown Title" + (++randomNumber);
        
        track = PadString.padString(track, 2, '0');
        
        File newFile = new File(output + "/" + artist + "/" + artist + " - " + album + "/" + track + "-" + title + ".mp3");
        
        if(year.length() > 0)
            newFile = new File(output + "/" + artist + "/" + artist + " - " + year + " - " + album + "/" + track + "-" + title + ".mp3");
        
        newFile.getParentFile().mkdirs();
        
        System.out.println(oldFile + " --> " + newFile);
        
        if(move)
        {
            oldFile.renameTo(newFile);
        }
        else
        {
            byte []contents = new byte[(int)oldFile.length()];
            
            FileInputStream in = new FileInputStream(oldFile);
            in.read(contents);
            in.close();
            
            FileOutputStream out = new FileOutputStream(newFile);
            out.write(contents);
            out.close();
            
            contents = null;
        }
    }
}
