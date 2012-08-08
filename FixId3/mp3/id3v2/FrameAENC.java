package mp3.id3v2;

//import mp3.Defines;
import mp3.util.OutputCtr;
import mp3.Configuration;
import mp3.gui.Id3JPanel;

//import java.io.UnsupportedEncodingException;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

/** Audio encryption
 * This frame indicates if the actual audio stream is encrypted, and by whom.
 * Since standardisation of such encrypion scheme is beyond this document,
 * all "AENC" frames begin with a terminated string with a URL containing an
 * email address, or a link to a location where an email address can be found,
 * that belongs to the organisation responsible for this specific encrypted
 * audio file. Questions regarding the encrypted audio should be sent to the
 * email address specified. If a $00 is found directly after the 'Frame size'
 * and the audiofile indeed is encrypted, the whole file may be considered
 * useless.
 *
 * After the 'Owner identifier', a pointer to an unencrypted part of the audio
 * can be specified. The 'Preview start' and 'Preview length' is described in
 * frames. If no part is unencrypted, these fields should be left zeroed.
 * After the 'preview length' field follows optionally a datablock required
 * for decryption of the audio. There may be more than one "AENC" frames in a
 * tag, but only one with the same 'Owner identifier'.
 *
 * <Header for 'Audio encryption', ID: "AENC">
 * Owner identifier <text string> $00
 * Preview start $xx xx
 * Preview length $xx xx
 * Encryption info <binary data>
 *
 * @author Florian Heer
 * @version $Id: FrameAENC.java,v 1.8 2004/11/28 21:23:48 heer Exp $
 */

public class FrameAENC extends ID3V2Frame {
    private String ownerIdentifier = "";
    private int previewStart = 0;
    private int previewLength = 0;
    private byte [] encryptionInfo = null;


    public String getLongName ()  { return "Audio Encryption frame"; }
    public int legalSince () { return 3; }

    public FrameAENC (ID3V2Frame frame) {
	super (frame);
	if (frame instanceof FrameAENC) {
	    FrameAENC frm = (FrameAENC)frame;
	    ownerIdentifier = new String (frm.ownerIdentifier);
	    previewStart = frm.previewStart;
	    previewLength = frm.previewLength;
	    encryptionInfo = new byte [frm.encryptionInfo.length];
	    System.arraycopy (frm.encryptionInfo,0,encryptionInfo,0,
			      frm.encryptionInfo.length);
	}
    }

    public FrameAENC (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    byte [] bAr = ds.getBytesTo ((byte)0);
	    if (bAr != null)
		ownerIdentifier = new String (bAr);
	    // Skip terminator byte
	    ds.getByte ();

	    previewStart = Helper.decodeBytesToInt (ds.getBytes(2));
	    previewLength = Helper.decodeBytesToInt (ds.getBytes(2));
	    encryptionInfo = ds.getBytes (ds.getBytesLeft ());
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName () + " can't be instantiated! SPEEx!");
	    throw ex;
	}
    }


    public String toString () {
	return getLongName () + "\nOnwer: "+ownerIdentifier
	    +"\nPreviewStart: "+previewStart
	    + "\nPreviewLength: "+previewLength
	    +"\nEncryptionInfo: "+Helper.arrayToHexString (encryptionInfo);
    }

    public String getOwnerIdentifier () { return new String (ownerIdentifier); }
    public void setOwnerIdentifier (String s) {
	ownerIdentifier = new String (s);
    }
    public int getPreviewStart () { return previewStart; }
    public void setPreviewStart (int s) { if (s >=0) previewStart = s; }
    public int getPreviewLength () { return previewLength; }
    public void setPreviewLength (int l) { if (l >=0) previewLength = l; }

    /**
     * @return copy of the contained data
     */
    public byte [] getEncryptionInfo () {
	if (encryptionInfo != null) {
	    byte [] r = new byte [encryptionInfo.length];
	    System.arraycopy (encryptionInfo, 0, r, 0, r.length);
	    return r;
	}
	return null;
    }
    public void setEncryptionInfo (byte [] b) {
	if (b != null) {
	    encryptionInfo = new byte [b.length];
	    System.arraycopy (b,0,encryptionInfo, 0, b.length);
	}
	else encryptionInfo = null;
    }

    /** Can be displayed */
    public boolean canDisplay () { return true; }
    /** Can not be edited */
    public boolean canEdit () { return false; }

    public Id3JPanel createJPanel (boolean edit, boolean selfupdate) {
	int lWidth = Configuration.labelWidth;
	int lHeight = Configuration.labelHeight;
	Dimension lDim = new Dimension (lWidth,lHeight);
	int tWidth = Configuration.textWidth;
	int tHeight = Configuration.textHeight;
	Dimension tDim = new Dimension (tWidth,tHeight);
	int bWidth = Configuration.buttonWidth;
	int bHeight = Configuration.buttonHeight;
	Dimension bDim = new Dimension (bWidth,bHeight);
	int taWidth = Configuration.textAreaWidth;
	int taHeight = Configuration.textAreaHeight;
	Dimension taDim = new Dimension (taWidth, taHeight);


	if (!canEdit ()) edit = false;
	if (!edit) selfupdate = false;
	JLabel lbl1 = new JLabel ("Owner:");
	JLabel lbl2 = new JLabel ("Preview start:");
	JLabel lbl3 = new JLabel ("Preview length:");
	JLabel lbl4 = new JLabel ("Encrypt info:");

	final JTextField txt1 = new JTextField (ownerIdentifier);
	final JTextField txt2 = new JTextField (previewStart);
	final JTextField txt3 = new JTextField (previewLength);
	final JTextArea txt4 = new JTextArea (new String (encryptionInfo));
	final Id3JPanel pnl = new Id3JPanel (new FlowLayout (FlowLayout.LEFT), true)
	    {  public void updateData () {
		// There is no updating!
	    }
	    };
	int width = lWidth+(tWidth>taWidth?tWidth:taWidth)+20;
	int height = 3*(lHeight > tHeight?lHeight:tHeight)+taHeight+6;

	txt1.setEditable (edit);
	txt1.setEnabled (edit);
	txt1.setMinimumSize (tDim);
	txt1.setPreferredSize (tDim);
	txt2.setEditable (edit);
	txt2.setEnabled (edit);
	txt2.setMinimumSize (tDim);
	txt2.setPreferredSize (tDim);
	txt3.setEditable (edit);
	txt3.setEnabled (edit);
	txt3.setMinimumSize (tDim);
	txt3.setPreferredSize (tDim);

	txt4.setEditable (edit);
	txt4.setEnabled (edit);
	txt4.setMinimumSize (taDim);
	txt4.setPreferredSize (taDim);

	lbl1.setMinimumSize (lDim);
	lbl1.setPreferredSize (lDim);
	lbl2.setMinimumSize (lDim);
	lbl2.setPreferredSize (lDim);
	lbl3.setMinimumSize (lDim);
	lbl3.setPreferredSize (lDim);
	lbl4.setMinimumSize (lDim);
	lbl4.setPreferredSize (lDim);

	pnl.setMinimumSize (new Dimension (width, height));
	pnl.setPreferredSize (new Dimension (width, height));
	pnl.add (lbl1);pnl.add (txt1);
	pnl.add (lbl2);pnl.add(txt2);
	pnl.add (lbl3);pnl.add(txt3);
	pnl.add (lbl4);pnl.add(txt4);

	return pnl;
    }

}
