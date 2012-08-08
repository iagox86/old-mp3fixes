package mp3.id3v2;

import mp3.util.OutputCtr;

/** Commercial frame
 * This frame enables several competing offers in the same tag by bundling
 * all needed information. That makes this frame rather complex but it's an
 * easier solution than if one tries to achieve the same result with several
 * frames. The frame begins, after the frame ID, size and encoding fields,
 * with a price string field. A price is constructed by one three character
 * currency code, encoded according to ISO-4217 alphabetic currency code,
 * followed by a numerical value where "." is used as decimal seperator. In
 * the price string several prices may be concatenated, seperated by a "/"
 * character, but there may only be one currency of each type.
 *
 * The price string is followed by an 8 character date string in the format
 * YYYYMMDD, describing for how long the price is valid. After that is a
 * contact URL, with which the user can contact the seller, followed by a one
 * byte 'received as' field. It describes how the audio is delivered when
 * bought according to the following list:
 *
 * $00 Other
 * $01 Standard CD album with other songs
 * $02 Compressed audio on CD
 * $03 File over the Internet
 * $04 Stream over the Internet
 * $05 As note sheets
 * $06 As note sheets in a book with other sheets
 * $07 Music on other media
 * $08 Non-musical merchandise
 *
 * Next follows a terminated string with the name of the seller followed by a
 * terminated string with a short description of the product. The last thing
 * is the ability to include a company logotype. The first of them is the
 * 'Picture MIME type' field containing information about which picture
 * format is used. In the event that the MIME media type name is omitted,
 * "image/" will be implied. Currently only "image/png" and "image/jpeg" are
 * allowed. This format string is followed by the binary picture data. This
 * two last fields may be omitted if no picture is to attach.
 *
 * <Header for 'Commercial frame', ID: "COMR">
 * Text encoding $xx
 * Price string <text string> $00
 * Valid until <text string>
 * Contact URL <text string> $00
 * Received as $xx
 * Name of seller <text string according to encoding> $00 (00)
 * Description <text string according to encoding> $00 (00)
 * Picture MIME type <string> $00
 * Seller logo <binary data>
 *
 * @author Florian Heer
 * @version $Id: FrameCOMR.java,v 1.5 2004/11/28 21:23:48 heer Exp $
 */

public class FrameCOMR extends ID3V2Frame {
    private byte    encoding = 0;
    private String  price = "";
    private String  validUntil = "";
    private String  contactURL = "";
    private byte    received = 0;
    private String  sellerName = "";
    private String  description = "";
    private String  pictureType = "";
    private byte [] sellerLogo = null;

    public String getLongName ()  { return "Commercial frame"; }

    FrameCOMR (ID3V2Frame frm) {
        super (frm);
        if (frm instanceof FrameCOMR) {
            FrameCOMR fr = (FrameCOMR)frm;
            encoding    = fr.encoding;
            price       = fr.price;
            validUntil  = fr.validUntil;
            contactURL  = fr.contactURL;
            received    = fr.received;
            sellerName  = fr.sellerName;
            description = fr.description;
            pictureType = fr.pictureType;
            sellerLogo  = new byte [fr.sellerLogo.length];
            System.arraycopy (fr.sellerLogo, 0, sellerLogo, 0, fr.sellerLogo.length);
        }
    }

    public FrameCOMR (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    encoding = ds.getByte ();
	    byte [] bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		price = new String (bAr);
	    // Skip terminating zero byte
	    ds.getByte ();

	    validUntil = new String (ds.getBytes(8));
	    bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		contactURL = new String (bAr);
	    // Skip terminating zero byte
	    ds.getByte ();

	    received = ds.getByte ();
	    bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		sellerName = new String (bAr);
	    // Skip terminating zero byte
	    ds.getByte ();

	    bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		description = new String (bAr);
	    // Skip terminating zero byte
	    ds.getByte ();

	    bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		pictureType = new String (bAr);
	    // Skip terminating zero byte
	    ds.getByte ();

	    sellerLogo = ds.getBytes (ds.getBytesLeft ());
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
