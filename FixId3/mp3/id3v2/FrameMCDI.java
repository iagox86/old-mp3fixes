package mp3.id3v2;

import mp3.util.OutputCtr;

/** Music CD identifier frame
 * This frame is intended for music that comes from a CD, so that the CD
 * can be identified in databases such as the CDDB. The frame consists of a
 * binary dump of the Table Of Contents, TOC, from the CD, which is a header
 * of 4 bytes and then 8 bytes/track on the CD plus 8 bytes for the 'lead out'
 * making a maximum of 804 bytes. The offset to the beginning of every track
 * on the CD should be described with a four bytes absolute CD-frame address
 * per track, and not with absolute time. This frame requires a present and
 * valid "TRCK" frame, even if the CD's only got one track. There may only
 * be one "MCDI" frame in each tag.
 *
 * @author Florian Heer
 * @version $Id: FrameMCDI.java,v 1.4 2003/08/24 18:26:26 heer Exp $
 */
public class FrameMCDI extends ID3V2Frame {
    private byte [] identifier;

    public String getLongName ()  { return "Music CD identifier frame"; }

    public FrameMCDI (ID3V2Frame frm) {
        super (frm);
        if (frm instanceof FrameMCDI) {
            FrameMCDI fr = (FrameMCDI)frm;
            identifier = new byte [fr.identifier.length];
            System.arraycopy (fr.identifier, 0, identifier, 0, fr.identifier.length);
        }
    }

    public FrameMCDI (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);

	// Decoding our information from the DataSource!
	try {
	    // The value is the rest of the frame
	    identifier = ds.getBytes (ds.getBytesLeft ());
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName ()
			       +" can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    public String toString () {
	return getLongName () + "\nIdentifier : "
	    + Helper.arrayToHexString (identifier);
    }

}
