package mp3.id3v2;

import mp3.util.OutputCtr;

/** General encapsulated object
 * In this frame any type of file can be encapsulated. After the header,
 * 'Frame size' and 'Encoding' follows 'MIME type' represented as as a
 * terminated string encoded with ISO-8859-1. The filename is case sensitive
 * and is encoded as 'Encoding'. Then follows a content description as
 * terminated string, encoded as 'Encoding'. The last thing in the frame is
 * the actual object. The first two strings may be omitted, leaving only
 * their terminations. There may be more than one "GEOB" frame in each tag,
 * but only one with the same content descriptor.
 *
 * <Header for 'General encapsulated object', ID: "GEOB">
 * Text encoding $xx
 * MIME type <text string> $00
 * Filename <text string according to encoding> $00 (00)
 * Content description $00 (00)
 * Encapsulated object <binary data>
 *
 * @author Florian Heer
 * @version $Id: FrameGEOB.java,v 1.5 2004/11/28 21:23:48 heer Exp $
 */

public class FrameGEOB extends ID3V2Frame {
    private byte encoding = 0;
    private String mimeType = "";
    private String fileName = "";
    private String contentDescription = "";
    private byte [] fileData = null;

    public String getLongName ()  { return "General encapsulated object frame"; }

    public FrameGEOB (ID3V2Frame frm) {
        super (frm);
        if (frm instanceof FrameGEOB) {
            FrameGEOB fr = (FrameGEOB)frm;
            encoding = fr.encoding;
            mimeType = fr.mimeType;
            fileName = fr.fileName;
            contentDescription = fr.contentDescription;
            fileData = new byte [fr.fileData.length];
            System.arraycopy (fr.fileData, 0, fileData, 0, fr.fileData.length);
        }
    }

    public FrameGEOB (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    encoding = ds.getByte ();
	    byte [] bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		mimeType = new String (bAr);
	    // Skip terminator byte
	    ds.getByte ();

	    bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		fileName = new String (bAr);
	    // Skip terminator
	    ds.getByte ();

	    bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		contentDescription = new String (bAr);
	    // Skip terminator
	    ds.getByte ();
	    fileData = ds.getBytes (ds.getBytesLeft ());
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName () + " can't be instantiated! SPEEx!");
	    throw ex;
	}
    }


    public String toString () {
	return getLongName () + "\n";
    }

}
