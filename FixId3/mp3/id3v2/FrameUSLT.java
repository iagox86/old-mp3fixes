package mp3.id3v2;

import mp3.util.OutputCtr;
/** Unsychronised lyrics/text transcription
 * This frame contains the lyrics of the song or a text transcription of
 * other vocal activities. The head includes an encoding descriptor and a
 * content descriptor. The body consists of the actual text. The
 * 'Content descriptor' is a terminated string. If no descriptor is
 * entered, 'Content descriptor' is $00 (00) only. Newline characters are
 * allowed in the text. There may be more than one
 * 'Unsynchronised lyrics/text transcription' frame in each tag, but only
 * one with the same language and content descriptor.
 *
 * <Header for 'Unsynchronised lyrics/text transcription', ID: "USLT">
 * Text encoding $xx
 * Language $xx xx xx
 * Content descriptor <text string according to encoding> $00 (00)
 * Lyrics/text <full text string according to encoding>
 *
 * @author Florian Heer
 * @version $Id: FrameUSLT.java,v 1.5 2004/11/28 21:23:48 heer Exp $
 */
public class FrameUSLT extends ID3V2Frame {
    private byte encoding=0;
    private String language="";
    private String description="";
    private String text = "";

    public String getLongName ()  { return "Unsychronised lyrics/text transcription frame"; }

    public FrameUSLT (ID3V2Frame frame) {
        super (frame);
        if (frame instanceof FrameUSLT) {
            FrameUSLT frm = (FrameUSLT)frame;
            language = frm.language;
            description = frm.description;
            text = frm.text;
            encoding = frm.encoding;
        }
    }

    public FrameUSLT (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    encoding = ds.getByte ();
	    language = new String (ds.getBytes(3));
	    byte [] bAr = ds.getBytesTo ((byte)0x00);
	    if (bAr != null)
		description = new String (bAr);
	    // skip terminator byte
	    ds.getByte ();

	    text = new String (ds.getBytes (ds.getBytesLeft ()));
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName ()
			       + " can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    public String toString () {
	return getLongName () + " Encoding: "+encoding+"\nlanguage: "
	    + language + "\ntext: "+ text;
    }

}
