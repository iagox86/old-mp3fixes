package mp3.id3v2;

import mp3.Configuration;
import mp3.util.OutputCtr;
import mp3.gui.Id3JPanel;
import mp3.gui.event.UpdateEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;

import java.io.UnsupportedEncodingException;

/** Base class for all text related frames. 
 *
 * @author Florian Heer
 * @version $Id: FrameT.java,v 1.3 2004/04/30 12:42:19 heer Exp $
 */

// FIXME! Encoding does not get evaluated
public class FrameT extends ID3V2Frame {
    byte encoding=0;
    String text = "";

    public String getLongName ()  { return "Generic text frame"; }

    public FrameT (String id) { super(id); }

    public FrameT (ID3V2Frame frame) {
	super (frame);
	if (frame instanceof FrameT) {
	    FrameT frm = (FrameT)frame;
	    encoding = frm.encoding;
	    text = new String (frm.text);
	}
    }

    public FrameT (ID3V2Frame frm, DataSource ds) 
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    encoding = ds.getByte ();
	    byte [] bAr = ds.getBytes (ds.getBytesLeft()); // instead of reading to 0, because unicode strings can contain zeros
	    // hack for texts not started with encoding byte (2.3 allows this)
	    if (encoding > 3) {
	    	byte[] newb = new byte[bAr.length+1];
	    	newb[0] = encoding;
	    	System.arraycopy(bAr, 0, newb, 1, bAr.length);
	    	bAr = newb;
	    }
	    if (bAr != null) {
		try { 
			String enc = DEFAULT_ENCODING;
			switch (encoding) {
				case 1:
					enc = "UTF-16";
					break;
				case 2:
					enc = "UnicodeBigUnmarked";
					break;
				case 3:
					enc = "UTF8";
					break;
			}
			text = new String (bAr, enc); 
			text = text.trim();
		} catch (UnsupportedEncodingException ex) {
		    text = new String (bAr);
		}
	    }
	    else text = "";

	    // If the textstring is followed by a termination ($00) all the 
	    //following information should be ignored and not be displayed.

	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, "Text frame can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    public String getText () { return new String (text); }
    public void setText (String txt) { text = new String (txt.trim ()); }

    public String toString () {
	return getLongName () + " Encoding: "+encoding+"\ntext: " + text;
    }

    public byte [] getData () {
	byte [] data = new byte [text.length()+1];
	data [0] = (byte)encoding;
	byte [] textBytes = null;
	try { textBytes = text.getBytes (DEFAULT_ENCODING); }
	catch (UnsupportedEncodingException ex) {
	    textBytes = text.getBytes ();
	}
	System.arraycopy (textBytes, 0, data, 1, textBytes.length);
	return data;
    }

    public boolean containsData () {
	return (text.trim ().length () > 0);
    }

    protected Id3JPanel createJPanel (boolean edit, boolean selfupdate, String caption) {
	int lWidth = Configuration.labelWidth;
	int lHeight = Configuration.labelHeight;
	Dimension lDim = new Dimension (lWidth,lHeight);
	int tWidth = Configuration.textWidth;
	int tHeight = Configuration.textHeight;
	Dimension tDim = new Dimension (tWidth,tHeight);
	int bWidth = Configuration.buttonWidth;
	int bHeight = Configuration.buttonHeight;
	Dimension bDim = new Dimension (bWidth,bHeight);


	if (!canEdit ()) edit = false;
	if (!edit) selfupdate = false;
	JLabel lbl = new JLabel (caption);
	final JTextField txt = new JTextField (text);
	final Id3JPanel pnl = new Id3JPanel (new FlowLayout (FlowLayout.LEFT), true) 
	    {  public void updateData () {
		text = txt.getText ();
		dataChanged = true;
		fireUpdateEvent (new UpdateEvent (this));
	    }
	    };
	int width = lWidth+tWidth+20;
	int height = (lHeight > tHeight?lHeight:tHeight)+6;
	if (selfupdate) {
	    width += bWidth+5;
	    height = bHeight+10;
	}

	txt.setEditable (edit);
	txt.setEnabled (edit);
	txt.setMinimumSize (tDim);
	txt.setPreferredSize (tDim);
	lbl.setMinimumSize (lDim);
	lbl.setPreferredSize (lDim);

	pnl.setMinimumSize (new Dimension (width, height));
	pnl.setPreferredSize (new Dimension (width, height));
	pnl.add (lbl);
	pnl.add (txt);
	if (selfupdate) {
	    JButton btn = new JButton ("OK");
	    btn.setMinimumSize (bDim);
	    btn.setPreferredSize (bDim);
	    btn.addActionListener 
		( new ActionListener () {
			public void actionPerformed (ActionEvent ae) {
			    pnl.updateData ();
			}
		    });
	    pnl.add (btn);
	}
	return pnl;
    }
}
