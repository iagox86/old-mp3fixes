package mp3.id3v2;

import mp3.util.OutputCtr;

/** Popularimeter
 * The purpose of this frame is to specify how good an audio file is. Many
 * interesting applications could be found to this frame such as a playlist
 * that features better audiofiles more often than others or it could be used
 * to profile a person's taste and find other 'good' files by comparing
 * people's profiles. The frame is very simple. It contains the email address
 * to the user, one rating byte and a four byte play counter, intended to be
 * increased with one for every time the file is played. The email is a
 * terminated string. The rating is 1-255 where 1 is worst and 255 is best.
 * 0 is unknown. If no personal counter is wanted it may be omitted. When the
 * counter reaches all one's, one byte is inserted in front of the counter
 * thus making the counter eight bits bigger in the same away as the play
 * counter ("PCNT"). There may be more than one "POPM" frame in each tag, but
 * only one with the same email address.
 *
 * <Header for 'Popularimeter', ID: "POPM">
 * Email to user <text string> $00
 * Rating $xx
 *Counter $xx xx xx xx (xx ...)
 *
 * @author Florian Heer
 * @version $Id: FramePOPM.java,v 1.5 2004/11/28 21:23:48 heer Exp $
 */

public class FramePOPM extends ID3V2Frame {
    // FIXME must probably get bigger!
    private String mailAdd = "";
    private int rating = 0;
    private int playCounter = 0;

    public String getLongName ()  { return "Popularimeter frame"; }

    public FramePOPM (ID3V2Frame frame) {
        super (frame);
        if (frame instanceof FramePOPM) {
            FramePOPM frm = (FramePOPM)frame;
            mailAdd = frm.mailAdd;
            rating = frm.rating;
            playCounter = frm.playCounter;
        }
    }

    public FramePOPM (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    byte [] bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		mailAdd = new String (bAr);
	    // Skip terminator byte
	    ds.getByte ();

	    rating = Helper.byteToUnsignedInt (ds.getByte());
	    if (ds.hasMoreBytes ()) {
		bAr = ds.getBytes(ds.getBytesLeft ());
		if (bAr != null)
		    playCounter = Helper.decodeBytesToInt (bAr);
	    }
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName () + " can't be instantiated! SPEEx!");
	    throw ex;
	}
    }


    public String toString () {
	return getLongName () + "\nUser: "+mailAdd+"\nRating: "+rating
	    +"\nPlay counter: "+playCounter;
    }

}
