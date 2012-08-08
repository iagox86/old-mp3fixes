/*
 * Created on Jul 26, 2004
 *
 * By iago
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Arrays;

import mp3.ID3Tag;
import mp3.ID3Writer;
import mp3.id3v2.FrameT;
import mp3.id3v2.ID3V2Tag;
import mp3.id3v2.ID3V2Writer;

public class FixMP3 
{
    public final static String TRACK = "TRCK";
    public final static String TITLE = "TIT2";
    public final static String ARTIST = "TPE1";
    public final static String ALBUM = "TALB";
    public final static String YEAR = "TYER";
    public final static String COMMENT = "COMM";
    
    private static boolean fixCase;
    
    public static void main(String []args) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Directory to read from? [/home/iago/music-test] --> ");
        String directory = in.readLine();
        if(directory.length() == 0)
            directory = "/home/iago/music-test";
        
        System.out.print("Write out to the files? [n] --> ");
        boolean write = in.readLine().equalsIgnoreCase("y");
        
        System.out.print("Fix case on song titles? [y] --> ");
        fixCase = !in.readLine().equalsIgnoreCase("n");
        
        go(write, directory);
    }
        
     
    /** Takes the base folder and reads out all the artists. */
    public static void go(boolean write, String location)
    {
        try
        {
	        File baseFolder = new File(location);
	        
	        File[] artists = baseFolder.listFiles();
	        Arrays.sort(artists);
	        
	        for(int i = 0; i < artists.length; i++)
	            processArtistFile(artists[i], write);
        } 
        catch(IOException e)
        {
            e.printStackTrace();
            System.err.println();
            
            System.err.println("There was an error processing, please fix:\n" + e);
            
            System.exit(0);
        }
    }
    
    /** Takes the folder containing all songs for an artist and reads out the artist, year, and album */
    public static void processArtistFile(File file, boolean write) throws IOException
    {
        if(file.isDirectory() == false)
            throw new IOException("File found where directory expected: " + file);
        
        File[] albums = file.listFiles();
        Arrays.sort(albums);
        
        for(int i = 0; i < albums.length; i++)
        {
            String[] splitFile = albums[i].getName().split(" - ", 3);
            
            if(splitFile.length == 3)
            {
	            String artist = fixCase(splitFile[0]);
	            String year = fixCase(splitFile[1]);
	            String album = fixCase(splitFile[2]);
	            
                doSongs(artist, year, album, albums[i], write);
            }
            else if(splitFile.length == 2)
            {
                String artist = fixCase(splitFile[0]);
                String album = fixCase(splitFile[1]);
                //System.out.println("Artist = " + artist + ", album = " + album);
                
                doSongs(artist, "", album, albums[i], write);
            }
            else
            {
                throw new IOException("Invalid Filename: " + albums[i]);
            }
        }
    }
    
    public static void doSongs(String artist, String year, String album, File file, boolean write) throws IOException
    {
        File []songs = file.listFiles();
        Arrays.sort(songs);
        
        for(int i = 0; i < songs.length; i++)
        {
            if(songs[i].isDirectory())
                doSongs(artist, year, album + " - " + songs[i].getName(), songs[i], write);
            else
            {
                String[] track = fixCase(songs[i].getName()).split("-", 2);
                if(track.length != 2)
                    throw new IOException("Invalid filename: " + songs[i]);

                System.out.println();
                System.out.println("File = " + songs[i] + "");
                System.out.println("Track = " + track[0] + "");
                System.out.println("Title = " + track[1].replaceAll("\\.mp3", "") + "");
                System.out.println("Artist = " + artist + "");
                System.out.println("Album = " + album + "");
                System.out.println("Year = " + year + "");


                if(write == true)
                {
                    System.out.println("Writing: " + songs[i]);
                    writeTag(songs[i], track[0], track[1].replaceAll("\\.mp3", ""), artist, album, year);
                }
            }
        }
    }
    
    private static String fixCase(String str)
    {
        if(fixCase == false)
            return str;
            
        StringBuffer s = new StringBuffer();
        
        boolean upper = true;
        
        for(int i = 0; i < str.length(); i++)
        {
            if(upper)
                s.append(Character.toUpperCase(str.charAt(i)));
            else
                s.append(Character.toLowerCase(str.charAt(i)));
            
            if(str.charAt(i) == ' ' || str.charAt(i) == '-')
                upper = true;
            else
                upper = false;
        }
        
        return s.toString();
    }
    
    
//    public static ID3v2Frame makeFrame(String id, String data) throws Exception
//    {
//        return new ID3v2Frame(id, ('\0' + data).getBytes(), false, false, false, ID3v2Frame.NO_COMPRESSION, (byte)0, (byte)0);        
//    }
    
    static int i = 0;
    
    public static void writeTag(File file, String track, String title, String artist, String album, String year) throws IOException
    {
     
        try
        {
            ID3V2Tag tag = new ID3V2Tag();
            
            FrameT trackFrame = new FrameT("TRCK");
            FrameT titleFrame = new FrameT("TIT2");
            FrameT artistFrame = new FrameT("TPE1");
            FrameT albumFrame = new FrameT("TALB");
            FrameT yearFrame = new FrameT("TYER");
            trackFrame.setText(track);
            titleFrame.setText(title);
            artistFrame.setText(artist);
            albumFrame.setText(album);
            yearFrame.setText(year);
            tag.addFrame(trackFrame);
            tag.addFrame(titleFrame);
            tag.addFrame(artistFrame);
            tag.addFrame(albumFrame);
            tag.addFrame(yearFrame);
            ID3V2Writer.writeTag(file, tag);
            
            ID3Tag v1Tag = new ID3Tag();
            v1Tag.setTitle(title);
            v1Tag.setArtist(artist);
            v1Tag.setAlbum(album);
            v1Tag.setYear(year);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            ID3Writer.writeTag(raf, v1Tag);
            raf.close();
            
//            ID3v2 test = new ID3v2(file);
//	        test.removeFrames();	        
//	        test.addFrame(makeFrame("TRCK", track));
//	        test.addFrame(makeFrame("TIT2", "TESTTEST")); // title));
//	        test.addFrame(makeFrame("TPE1", artist));
//	        test.addFrame(makeFrame("TALB", album));
//	        test.addFrame(makeFrame("TYER", year));
//	        test.addFrame(makeFrame("COMM", comment));
//	
//	        test.update();
	        
	        
	        //if(++i > 0)
	        //    throw new IOException("Everything is ok");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
            throw new IOException(e.toString());
        }
        
    }
}
