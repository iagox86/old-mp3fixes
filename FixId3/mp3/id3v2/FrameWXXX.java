package mp3.id3v2;

import mp3.util.OutputCtr;

import mp3.gui.*;

import java.io.UnsupportedEncodingException;

/** User defined url frame
 * This frame is intended for URL links concerning the audiofile in a 
 * similar way to the other "W"-frames. The frame body consists of a 
 * description of the string, represented as a terminated string, followed 
 * by the actual URL. The URL is always encoded with ISO-8859-1. There may 
 * be more than one "WXXX" frame in each tag, but only one with the same 
 * description.
 *
 * @author Florian Heer
 * @version $Id: FrameWXXX.java,v 1.9 2004/11/28 17:24:58 heer Exp $
 */

// FIXME! encoding is not evaluated.
public class FrameWXXX extends ID3V2Frame implements EncDescValFrame {
    private byte encoding = 0;
    private String description="";
    private String value="";

    /** the text to be displayed in this Frames JComponent */
    private static final String caption1 = "URL description:";
    /** the text to be displayed in this Frames JComponent */
    private static final String caption2 = "URL:";

    public String getLongName ()  { return "User defined url frame"; }

    public FrameWXXX () { super ("WXXX"); }

    public FrameWXXX (ID3V2Frame frame) {
	super (frame);
	if (frame instanceof FrameWXXX) {
	    FrameWXXX frm = (FrameWXXX)frame;
	    encoding = frm.encoding;
	    description = new String (frm.description);
	    value = new String (frm.value);
	}
    }

    public FrameWXXX (ID3V2Frame frm, DataSource ds) 
	throws SeekPastEndException {
	super (frm);

	// Decoding our information from the DataSource!
	try {
	    byte [] bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null) {
		try { description = new String (bAr, DEFAULT_ENCODING); }
		catch (UnsupportedEncodingException ex) { 
		    description = new String (bAr); 
		}
	    }
	    else description = "";
	    
	    // We have to skip one byte, the terminator-byte of the description.
	    ds.getByte ();

	    // The value is the rest of the frame
	    bAr = ds.getBytes (ds.getBytesLeft ());
	    try { value = new String (bAr, DEFAULT_ENCODING); }
	    catch (UnsupportedEncodingException ex) { value = new String (bAr); }
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName () 
			       +" can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    public String toString () {
	return getLongName () + 
	    "\nEncoding   : "+encoding
            + "\nDescription: "+description
	    + "\nURL        : "+value;
    }

    public boolean containsData () {
	return (description.length () > 0 || value.length () > 0);
    }

    public byte [] getData () {
	byte [] data = new byte [description.length() + value.length () +2];
	data [0] = encoding;
	byte [] b = null;
	try { b = description.getBytes (DEFAULT_ENCODING); }
	catch (UnsupportedEncodingException ex) {
	    b = description.getBytes ();
	}
	System.arraycopy (b, 0, data, 1, b.length);
	// Stringtermination
	data [description.length()+1]=0x00;
	try { b = value.getBytes (DEFAULT_ENCODING); }
	catch (UnsupportedEncodingException ex) { b = value.getBytes (); }
	System.arraycopy (b, 0, data, description.length()+2,value.length());
	return data;
    }

    public boolean canDisplay () { return true; }
    public boolean canEdit () { return true; }
    public Id3JPanel createJPanel (boolean edit, boolean selfupdate) {
	return new EncDescValJPanel (edit, selfupdate, caption1,
				     description, caption2, value, this);
    }

    public void setDescription (String txt) {
	if (!txt.equals(description)) {
	    dataChanged=true;
	    description = new String (txt);
	}
    }

    public String getDescription () {
	return new String (description);
    }

    public void setValue (String txt) {
	if (!txt.equals(value)) {
	    dataChanged=true;
	    value = new String (txt);
	}
    }

    public String getValue () {
	return new String (value);
    }
}
