package mp3.id3v2;

import mp3.util.OutputCtr;
/** Encryption method registration
 * To identify with which method a frame has been encrypted the encryption
 * method must be registered in the tag with this frame. The 'Owner
 * identifier' is a null-terminated string with a URL containing an email
 * address, or a link to a location where an email address can be found, that
 * belongs to the organisation responsible for this specific encryption
 * method. Questions regarding the encryption method should be sent to the
 * indicated email address. The 'Method symbol' contains a value that is
 * associated with this method throughout the whole tag. Values below $80 are
 * reserved. The 'Method symbol' may optionally be followed by encryption
 * specific data. There may be several "ENCR" frames in a tag but only one
 * containing the same symbol and only one containing the same owner
 * identifier. The method must be used somewhere in the tag. See section
 * 3.3.1, flag j for more information.
 *
 * <Header for 'Encryption method registration', ID: "ENCR">
 * Owner identifier <text string> $00
 * Method symbol $xx
 * Encryption data <binary data>
 *
 * @author Florian Heer
 * @version $Id: FrameENCR.java,v 1.5 2004/11/28 21:23:48 heer Exp $
 */

public class FrameENCR extends ID3V2Frame {
    private String ownerIdentifier = "";
    private byte methodSymbol = 0;
    private byte [] encryptionData = null;

    public String getLongName ()  { return "Encryption method registration frame"; }

    public FrameENCR (ID3V2Frame frm) {
        super (frm);
        if (frm instanceof FrameENCR) {
            FrameENCR fr = (FrameENCR)frm;
            ownerIdentifier = fr.ownerIdentifier;
            methodSymbol = fr.methodSymbol;
            encryptionData = new byte [fr.encryptionData.length];
            System.arraycopy (fr.encryptionData, 0, encryptionData, 0, fr.encryptionData.length);
        }
    }

    public FrameENCR (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    byte [] bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		ownerIdentifier = new String (bAr);
	    // Have to skip the terminator byte of the owner identifier
	    ds.getByte ();

	    methodSymbol = ds.getByte ();
	    encryptionData = ds.getBytes (ds.getBytesLeft ());
 	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName () + " can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    // FIXME
    public String toString () {
	return getLongName () + "\n Better toString () to follow!";
    }

}
