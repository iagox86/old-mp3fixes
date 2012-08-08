package mp3.id3v2;

import mp3.util.OutputCtr;

import java.util.Vector;

/** Sychronised lyrics/text transcription
 *
 * @author Florian Heer
 * @version $Id: FrameSYLT.java,v 1.7 2004/11/28 21:23:48 heer Exp $
 */
public class FrameSYLT extends ID3V2Frame {
    private byte encoding=0;
    private byte timeStampFormat = 0;
    private String language="";
    private String description="";
    private byte contentType = 0;
    private Vector texts = new Vector ();
    private byte [] data = null;

    public String getLongName ()  { return "Sychronised lyrics/text transcription frame"; }

    public FrameSYLT (ID3V2Frame frm) {
        super (frm);
        // FIXME!
        // Copying has to be performed!
    }

    public FrameSYLT (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    encoding = ds.getByte ();
	    language = new String (ds.getBytes(3));
	    timeStampFormat = ds.getByte ();
	    contentType = ds.getByte ();
	    byte [] bAr = ds.getBytesTo ((byte)0x00);
	    if (bAr != null)
		description = new String (bAr);
	    // Have to skip the terminator byte
	    ds.getByte ();

	    data = ds.getBytes (ds.getBytesLeft ());
	    decodeData ();
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName ()
			       + " can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    private void decodeData () {
	int i = 0;
	int j = 0;
	TextData td;
	byte [] buffer, buffer2;
	byte b;
	boolean stop;
	while (i<data.length) {
	    td = new TextData ();
	    buffer = Helper.reallocateByteBuffer (40, (byte [])null);
	    stop = false;
	    j = 0;

	    // Read the 0-terminated string
	    while ((i+j)<data.length && ! stop) {
		b = data [i+j];
		// text encoding not done yet!
		if (b != 0x00) {
		    if (j >= buffer.length)
			buffer = Helper.reallocateByteBuffer (40, buffer);
		    if (buffer != null)
			buffer[j]=b;
		}
		else stop = true;
		j++;
	    }

	    i+=j;

	    buffer2 = new byte [j];
	    System.arraycopy (buffer,0,buffer2,0,j);
	    td.timeStamp = Helper.decodeBytesToInt
		(new byte [] { data[i], data[i+1],
			       data [i+2], data[i+3] });
	    //texts.add (td);
	    texts.addElement (td);
	}
    }


    public String toString () {
	return getLongName () + " Encoding: "+encoding+"\nlanguage: "
	    + language + "\nEntries: "+texts.size ();
    }

    public class TextData {
	public int timeStamp=0;
	public String text="";
    }

}
