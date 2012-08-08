package mp3.id3v2;

import mp3.util.OutputCtr;

import java.util.Vector;

/** Synchronised tempo codes
 * For a more accurate description of the tempo of a musical piece this
 * frame might be used. After the header follows one byte describing which
 * time stamp format should be used. Then follows one or more tempo codes.
 * Each tempo code consists of one tempo part and one time part. The tempo
 * is in BPM described with one or two bytes. If the first byte has the
 * value $FF, one more byte follows, which is added to the first giving a
 * range from 2 - 510 BPM, since $00 and $01 is reserved. $00 is used to
 * describe a beat-free time period, which is not the same as a music-free
 * time period. $01 is used to indicate one single beat-stroke followed by
 * a beat-free period.
 *
 * The tempo descriptor is followed by a time stamp. Every time the tempo
 * in the music changes, a tempo descriptor may indicate this for the
 * player. All tempo descriptors should be sorted in chronological order.
 * The first beat-stroke in a time-period is at the same time as the beat
 * description occurs. There may only be one "SYTC" frame in each tag.
 *
 * <Header for 'Synchronised tempo codes', ID: "SYTC">
 * Time stamp format $xx
 * Tempo data <binary data>
 *
 * Where time stamp format is:
 *
 * $01 Absolute time, 32 bit sized, using MPEG frames as unit
 * $02 Absolute time, 32 bit sized, using milliseconds as unit
 *
 * Abolute time means that every stamp contains the time from the
 * beginning of the file.
 *
 * @author Florian Heer
 * @version $Id: FrameSYTC.java,v 1.4 2003/10/24 12:46:30 heer Exp $
 */
public class FrameSYTC extends ID3V2Frame {
    private byte [] codes;
    private Vector tempoCodes = new Vector ();
    private int timeStampFormat = 0;

    public static final String [] timeFormats =
    { "N/A","Absolute time, 32 bit sized, using MPEG frames as unit",
      "Absolute time, 32 bit sized, using milliseconds as unit", "N/A"};

    public String getLongName ()  { return "Synchronized tempo codes frame"; }

    public FrameSYTC (ID3V2Frame frm) {
        super (frm);
        // FIXME!
        // Copying has to be performed.
    }

    public FrameSYTC (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);

	// Decoding our information from the DataSource!
	try {
	    timeStampFormat = (int)ds.getByte ();

	    // The value is the rest of the frame
	    codes = ds.getBytes (ds.getBytesLeft ());
	    // Decoding the values contained in the data
	    decodeCodeData ();
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName ()
			       +" can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    private void decodeCodeData () {
	int i = 0;
	TimeCode tc = null;
	byte [] bAr;
	while (i < codes.length) {
	    tc = new TimeCode ();
	    bAr = new byte [] { codes [i],   codes [i+1],
				codes [i+2], codes [i+3] };

	    tc.timeStamp = (int)Helper.decodeBytesToInt (bAr);
	    if (codes[i+4] == 0xff) {
		tc.bpm = 256+Helper.byteToUnsignedInt (codes[i+5]);
		i += 5;
	    }
	    else {
		tc.bpm = Helper.byteToUnsignedInt (codes[i+4]);
		i+=5;
	    }
	    //tempoCodes.add (tc);
	    tempoCodes.addElement (tc);
	}
    }



    public String toString () {
	return getLongName () + "\nNumber of events : "
	    + tempoCodes.size ();
    }

    public class TimeCode {
	public int timeStamp = 0;
	public int bpm = 0;
    }

}
