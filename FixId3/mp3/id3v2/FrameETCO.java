package mp3.id3v2;

import mp3.util.OutputCtr;

import java.util.Vector;

/** Event timing codes frame
 * This frame allows synchronisation with key events in a song or sound.
 * The header is:
 *
 * <Header for 'Event timing codes', ID: "ETCO">
 * Time stamp format $xx
 *
 * Abolute time means that every stamp contains the time from the
 * beginning of the file.
 *
 * Followed by a list of key events in the following format:
 *
 * Type of event $xx
 * Time stamp $xx (xx ...)
 *
 * The 'Time stamp' is set to zero if directly at the beginning of the
 * sound or after the previous event. All events should be sorted in
 * chronological order.
 *
 * @author Florian heer
 * @version $Id: FrameETCO.java,v 1.4 2003/10/24 12:46:30 heer Exp $
 */
public class FrameETCO extends ID3V2Frame {
    private byte [] codes;
    private Vector timeCodes = new Vector ();
    private int timeStampFormat = 0;

    public static final String [] timeFormats =
    { "N/A","Absolute time, 32 bit sized, using MPEG frames as unit",
      "Absolute time, 32 bit sized, using milliseconds as unit", "N/A"};

    /** Event definitions for i = 0x00 to i = 0x14 */
    public static final String [] eventsFirstBlock = {
	"padding (has no meaning)", "end of initial silence", "intro start",
	"mainpart start", "outro start", "outro end", "verse start",
	"refrain start", "interlude start", "theme start", "variation start",
	"key change", "time change",
	"momentary unwanted noise (Snap, Crackle & Pop)", "sustained noise",
	"sustained noise end", "intro end", "mainpart end", "verse end",
	"refrain end", "theme end" };
    /** Event definitions ofr i=0xE0 to i = 0xff */
    public static final String [] eventsSecondBlock = {
	"not predefined sync (0xe0)", "not predefined sync (0xe1)",
	"not predefined sync (0xe2)", "not predefined sync (0xe3)",
	"not predefined sync (0xe4)", "not predefined sync (0xe5)",
	"not predefined sync (0xe6)", "not predefined sync (0xe7)",
	"not predefined sync (0xe8)", "not predefined sync (0xe9)",
	"not predefined sync (0xea)", "not predefined sync (0xeb)",
	"not predefined sync (0xec)", "not predefined sync (0xed)",
	"not predefined sync (0xee)", "not predefined sync (0xef)",
	"reserved for future use (0xf0)", "reserved for future use (0xf1)",
	"reserved for future use (0xf2)", "reserved for future use (0xf3)",
	"reserved for future use (0xf4)", "reserved for future use (0xf5)",
	"reserved for future use (0xf6)", "reserved for future use (0xf7)",
	"reserved for future use (0xf8)", "reserved for future use (0xf9)",
	"reserved for future use (0xfa)", "reserved for future use (0xfb)",
	"reserved for future use (0xfc)", "audio end (start of silence)",
	"audio file ends",
	"one more byte of events follows (all the following bytes with the value $FF have the same function)" };


    public FrameETCO (ID3V2Frame frm) {
        super (frm);
        // FIXME!
        // Copying has to be performed!
    }

    public static String getEventName (int i) {
	if (i >= 0 && i < 21)
	    return eventsFirstBlock [i];
	if (i >= 21 && i < 234)
	    return "reserved for future use ("
		+ Helper.byteToHexString (i)
		+ ")";
	if (i < 256)
	    return eventsSecondBlock [i-234];
	// if out of range...
	return "Undefinable Event ("+i+" < 0 || "+i+" > 255)";
    }

    public String getLongName ()  { return "Event timing codes frame"; }

    public FrameETCO (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);

	// Decoding our information from the DataSource!
	try {
	    timeStampFormat = (int)ds.getByte ();

	    // The value is the rest of the frame
	    codes = ds.getBytes (ds.getBytesLeft ());
	    // decoding the data
	    decodeEventData ();
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName ()
			       +" can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    private void decodeEventData () {
	int i = 0;
	Integer timeStamp;
	Integer event=null;
	byte [] bAr;
	EventTime et = null;
	while (i < codes.length) {
	    et = new EventTime ();
	    bAr = new byte [] { codes [i],   codes [i+1],
				codes [i+2], codes [i+3] };

	    et.timeStamp = (int)Helper.decodeBytesToInt (bAr);
	    et.event = Helper.byteToUnsignedInt (codes[i+4]);

	    i+=5;
	    //timeCodes.add (et);
	    timeCodes.addElement (et);
	}
    }

    public String toString () {
	return getLongName () + "\nNumber of events : "
	    + timeCodes.size ();
    }

    public class EventTime {
	public int timeStamp=0;
	public int event=0;
    }

}
