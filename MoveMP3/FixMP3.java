/*
 * Created on Jul 26, 2004
 *
 * By iago
 */

import de.vdheide.mp3.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FixMP3 extends JFrame 
{
    public final static String TRACK = "TRCK";
    public final static String TITLE = "TIT2";
    public final static String ARTIST = "TPE1";
    public final static String ALBUM = "TALB";
    public final static String YEAR = "TYER";
    public final static String COMMENT = "COMM";
    
    public static void main(String[] args) throws Exception
    {
        new FixMP3();
    }
    
    private boolean write;
    private String directory;
    
    private JTextField location;
    private JTextArea output;
    private JButton check;
    private JButton go;
    
    public FixMP3()
    {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());
        
        this.getContentPane().add(location = new JTextField("/mnt/archive/hitmen_music"), BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(output = new JTextArea()), BorderLayout.CENTER);
        JPanel buttons = new JPanel();
        buttons.add(check = new JButton("Check files"));
        buttons.add(go = new JButton("Go!"));
        this.getContentPane().add(buttons, BorderLayout.SOUTH);
        
        setSize(780, 500);
        setTitle("ID3 Name Fixer");
        setVisible(true);
        
        
        go.addActionListener(new ActionListener()
                {
            public void actionPerformed(ActionEvent e)
            {
                go(true);
            }
                }
        );

        check.addActionListener(new ActionListener()
                {
            public void actionPerformed(ActionEvent e)
            {
                go(false);
            }
                }
        );
    }
        
     
    
    public void go(boolean write)
    {
        this.write = write;
        directory = location.getText();
        output.setText("");

        if(write == false || JOptionPane.showConfirmDialog(null, "Are you SURE you want to write all ID3 tags in " + directory + "? \nPLEASE make sure you check first to make sure they work!") == JOptionPane.YES_OPTION)
        {
            
            try
            {
		        File baseFolder = new File(directory);
		        
		        File[] artists = baseFolder.listFiles();
		        
		        for(int i = 0; i < artists.length; i++)
		            processArtist(artists[i]);
            } 
            catch(IOException e)
            {
                output.append("There was an error processing, please fix:\n" + e);
            }
        }
        
    }
    
    
    public void processArtist(File file) throws IOException
    {
        File[] albums = file.listFiles();
        
        for(int i = 0; i < albums.length; i++)
        {
            String[] splitFile = albums[i].getName().split(" - ", 3);
            
            if(splitFile.length == 3)
            {
	            String artist = splitFile[0];
	            String year = splitFile[1];
	            String album = splitFile[2];
	            
                doSongs(artist, year, album, albums[i]);
            }
            else if(splitFile.length == 2)
            {
                String artist = splitFile[0];
                String album = splitFile[1];
                //System.out.println("Artist = " + artist + ", album = " + album);
                
                doSongs(artist, "", album, albums[i]);
            }
            else
            {
                throw new IOException("Invalid Filename: " + albums[i].getName());
            }
        }
    }
    
    public void doSongs(String artist, String year, String album, File file) throws IOException
    {
        File []songs = file.listFiles();
        
        for(int i = 0; i < songs.length; i++)
        {
            if(songs[i].isDirectory())
                doSongs(artist, year, album + " - " + songs[i].getName(), songs[i]);
            else
            {
                String[] track = songs[i].getName().split("-", 2);
                if(track.length != 2)
                    throw new IOException("Invalid filename: " + songs[i]);

                if(write == false)
                {
                    output.append("File = " + songs[i] + "\n");
                    output.append("Track = " + track[0] + "\n");
                    output.append("Title = " + track[1].replaceAll("\\.mp3", "") + "\n");
                    output.append("Artist = " + artist + "\n");
                    output.append("Album = " + album + "\n");
                    output.append("Year = " + year + "\n");
                    output.append("\n");
                }
                else
                {
                    output.append("Writing: " + songs[i] + "\n");
                    writeTag(songs[i], track[0], track[1].replaceAll("\\.mp3", ""), artist, album, year, "Created by iago's FixMP3 program.  http://iago.valhallalegends.com");
                }
            }
        }
    }
    
    
    
    
    public static ID3v2Frame makeFrame(String id, String data) throws Exception
    {
        return new ID3v2Frame(id, ('\0' + data).getBytes(), false, false, false, ID3v2Frame.NO_COMPRESSION, (byte)0, (byte)0);        
    }
    
    public static void printInfo(File filename) throws Exception
    {
        ID3v2 test = new ID3v2(filename);
        
        System.out.println("Track: " 	+ new String(((ID3v2Frame)test.getFrame("TRCK").get(0)).getContent()).substring(1));
        System.out.println("Title: " 	+ new String(((ID3v2Frame)test.getFrame("TIT2").get(0)).getContent()).substring(1));
        System.out.println("Artist: " 	+ new String(((ID3v2Frame)test.getFrame("TPE1").get(0)).getContent()).substring(1));
        System.out.println("Album: " 	+ new String(((ID3v2Frame)test.getFrame("TALB").get(0)).getContent()).substring(1));
        System.out.println("Year: " 	+ new String(((ID3v2Frame)test.getFrame("TYER").get(0)).getContent()).substring(1));
        System.out.println("Comment: " 	+ new String(((ID3v2Frame)test.getFrame("COMM").get(0)).getContent()).substring(1));
        System.out.println();
    }
    
    static int i = 0;
    
    public static void writeTag(File file, String track, String title, String artist, String album, String year, String comment) throws IOException
    {
     
        try
        {
	        ID3v2 test = new ID3v2(file);
	        test.removeFrames();
	        
	        test.addFrame(makeFrame("TRCK", track));
	        test.addFrame(makeFrame("TIT2", title));
	        test.addFrame(makeFrame("TPE1", artist));
	        test.addFrame(makeFrame("TALB", album));
	        test.addFrame(makeFrame("TYER", year));
	        test.addFrame(makeFrame("COMM", comment));
	
	        test.update();
	        
	        
	        if(++i > 10)
	            throw new IOException("Everything is ok");
        }
        catch(Exception e)
        {
            throw new IOException(e.toString());
        }
        
    }
}
