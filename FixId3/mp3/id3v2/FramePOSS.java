package mp3.id3v2;

import mp3.util.OutputCtr;

/** Position synchronisation frame
 * This frame delivers information to the listener of how far into the audio
 * stream he picked up; in effect, it states the time offset of the first
 * frame in the stream. The frame layout is:
 *
 * <Head for 'Position synchronisation', ID: "POSS">
 * Time stamp format $xx
 * Position $xx (xx ...)
 *
 * Where time stamp format is:
 *
 * $01 Absolute time, 32 bit sized, using MPEG frames as unit
 * $02 Absolute time, 32 bit sized, using milliseconds as unit
 *
 * and position is where in the audio the listener starts to receive, i.e.
 * the beginning of the next frame. If this frame is used in the beginning of
 * a file the value is always 0. There may only be one "POSS" frame in each
 * tag.
 *
 * @author Florian Heer
 * @version $Id: FramePOSS.java,v 1.4 2003/10/22 23:51:02 heer Exp $
 */

public class FramePOSS extends ID3V2Frame {
    private byte timeStampFormat = 0;
    private int position = 0;

    public String getLongName ()  { return "Position synchronisation frame"; }

    public FramePOSS (ID3V2Frame frm) {
        super (frm);
        if (frm instanceof FramePOSS) {
            FramePOSS fr = (FramePOSS)frm;
            timeStampFormat = fr.timeStampFormat;
            position = fr.position;
        }
    }

    public FramePOSS (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    timeStampFormat = ds.getByte ();
	    position = Helper.decodeBytesToInt
		(ds.getBytes(ds.getBytesLeft ()));
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName () + " can't be instantiated! SPEEx!");
	    throw ex;
	}
    }


    public String toString () {
	return getLongName () + "\ntimeStampFormat: "+timeStampFormat
	    +"\nPosition:  "+position;
    }

}
