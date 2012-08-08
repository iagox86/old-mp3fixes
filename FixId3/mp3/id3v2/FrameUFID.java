package mp3.id3v2;

import mp3.util.OutputCtr;

/** Unique file identifier frame
 *
 * @author Florian Heer
 * @version $Id: FrameUFID.java,v 1.4 2003/10/22 23:51:02 heer Exp $
 */

public class FrameUFID extends ID3V2Frame {
    private String ownerIdentifier = "";
    private byte [] identifier = null;

    public String getLongName ()  { return "Unique file identifier"; }

    public FrameUFID (ID3V2Frame frm, DataSource ds) 
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    StringBuffer sb = new StringBuffer ();
	    byte b = ds.getByte();
	    // read in the 'Owner identifier'... 0 terminated string
	    while (b != 0) {
		sb.append ((char)b);
		b = ds.getByte ();
	    }
	    ownerIdentifier = sb.toString ();
	    identifier = ds.getBytes (ds.getBytesLeft ());
	    if (identifier.length > 64)
		OutputCtr.println 
		    (1, "Something's strange. FrameUFIDs identifier is " 
		     + identifier.length + " bytes long...");
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, "FrameUFID can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    public String toString () {
	return getLongName () + " OI: "+ownerIdentifier+"\nident: "
	    + Helper.arrayToHexString (identifier);
    }

}
