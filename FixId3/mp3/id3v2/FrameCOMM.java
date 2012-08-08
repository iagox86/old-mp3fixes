package mp3.id3v2;

import mp3.Configuration;
import mp3.util.OutputCtr;
import mp3.gui.*;

import java.io.UnsupportedEncodingException;

/** Comments frame
 * This frame is indended for any kind of full text information that does 
 * not fit in any other frame. It consists of a frame header followed by 
 * encoding, language and content descriptors and is ended with the actual 
 * comment as a text string. Newline characters are allowed in the comment 
 * text string. There may be more than one comment frame in each tag, but 
 * only one with the same language and content descriptor.
 * 
 * <Header for 'Comment', ID: "COMM">
 * Text encoding $xx
 * Language $xx xx xx
 * Short content descrip. <text string according to encoding> $00 (00)
 * The actual text <full text string according to encoding>
 *
 * @author Florian Heer
 * @version $Id: FrameCOMM.java,v 1.10 2004/11/28 21:23:48 heer Exp $
 */

// FIXME! Encoding is not evaluated. Languge is fixed.
public class FrameCOMM extends ID3V2Frame implements EncDescValFrame {
    private byte encoding=0;
    private String language="eng";
    private String description = "";
    private String text = "";

    private static final String caption1 = "Comment description";
    private static final String caption2 = "Comment";

    public String getLongName ()  { return "Comments frame"; }

    public FrameCOMM () { super ("COMM"); }

    public FrameCOMM (ID3V2Frame frame) {
	super (frame);
	if (frame instanceof FrameCOMM) {
	    FrameCOMM frm = (FrameCOMM)frame;
	    encoding = frm.encoding;
	    language = new String (frm.language);
	    description = new String (frm.description);
	    text = new String (frm.text);
	}
    }

    public FrameCOMM (ID3V2Frame frm, DataSource ds) 
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    encoding = ds.getByte ();
	    language = new String (ds.getBytes (3));
	    byte [] bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null) {
		try {
		    description = new String (bAr, DEFAULT_ENCODING);
		}
		catch (UnsupportedEncodingException ex) {
		    description = new String (bAr);
		}
	    }
	    // Skip terminator byte
	    ds.getByte ();

	    try {
		bAr = ds.getBytes (ds.getBytesLeft ());
		text = new String (bAr, DEFAULT_ENCODING);
	    }
	    catch (UnsupportedEncodingException ex) {
		text = new String (bAr);
	    }
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, "Text frame can't be instantiated! SPEEx!");
	    throw ex;
	}

	// Setting language to a 'sane' something
	if (!Languages.checkLanguage (language))
	    language = Configuration.defaultLanguage;
    }



    public String toString () {
	return getLongName () + " Encoding: "+encoding
	    +"\nlanguage: "+language
	    +"\ndescription: "+description
	    +"\ntext: " + text;
    }

    public boolean containsData () {
	return (description.length () > 0 || text.length () > 0);
    }

    public byte [] getData () {
	byte [] data = new byte [5+description.length () + text.length ()];
	data [0] = encoding;
	data [1] = (byte)language.charAt (0);
	data [2] = (byte)language.charAt (1);
	data [3] = (byte)language.charAt (2);
	byte [] bAr=null;
	try {  bAr = description.getBytes (DEFAULT_ENCODING); }
	catch (UnsupportedEncodingException ex) {
	    bAr = description.getBytes ();
	}
	System.arraycopy (bAr, 0, data, 4,  bAr.length);
	data [description.length ()+4]=0;
	try { bAr = text.getBytes (DEFAULT_ENCODING); }
	catch (UnsupportedEncodingException ex) { bAr = text.getBytes (); }
	System.arraycopy (bAr,0,data,description.length()+5, bAr.length);
	return data;
    }

    public boolean canEdit () { return true; }
    public boolean canDisplay () { return true; }

    public Id3JPanel createJPanel (boolean edit, boolean selfupdate) {
	return new EncDescValJPanel (edit, selfupdate, caption1, description,
				     caption2, text, this);
    }

    public void setDescription (String txt) {
	if (!txt.equals(description)) dataChanged = true;
	description = new String (txt.trim ());
    }

    public void setValue (String txt) {
	setText (txt);
    }

    public String getValue () {
	return getText ();
    }

    /** Sets the content of the comment, not its description
     */
    public void setText (String t) {
	if (!t.equals (text)) {
	    text = new String(t.trim ());
	    dataChanged = true;
	}
    }

    /** @return the comment, not its description */
    public String getText () {
	return new String (text);
    }
}
