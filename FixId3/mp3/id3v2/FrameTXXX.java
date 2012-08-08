package mp3.id3v2;

import mp3.util.OutputCtr;

/** User defined text frame
 * This frame is intended for one-string text information concerning
 * the audiofile in a similar way to the other "T"-frames. The frame body
 * consists of a description of the string, represented as a terminated
 * string, followed by the actual string. There may be more than one
 * "TXXX" frame in each tag, but only one with the same description.
 * @author Florian Heer
 * @version $Id: FrameTXXX.java,v 1.3 2003/08/24 18:26:27 heer Exp $
 */
public class FrameTXXX extends ID3V2Frame {
    private byte encoding=0;
    private String description="";
    private String value="";

    public String getLongName ()  { return "User defined text frame"; }

    public FrameTXXX (ID3V2Frame frame) {
        super (frame);
        if (frame instanceof FrameTXXX) {
            FrameTXXX frm = (FrameTXXX)frame;
            encoding = frm.encoding;
            description = frm.description;
            value = frm.value;
        }
    }

    public FrameTXXX (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);

	// Decoding our information from the DataSource!
	try {
	    StringBuffer sb = new StringBuffer ();
	    encoding = ds.getByte ();

	    byte b = ds.getByte();
	    // read in the 'description' field... 0 terminated string
	    while (b != 0) {
		sb.append ((char)b);
		b = ds.getByte ();
	    }
	    description = sb.toString ();

	    // The value is the rest of the frame
	    value = new String (ds.getBytes (ds.getBytesLeft ()));
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName ()
			       +" can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    public String toString () {
	return getLongName () + "\nDescription: "+description
	    + "\nValue      : "+value;
    }

}
