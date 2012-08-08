package mp3.id3v2;

import mp3.gui.Id3JPanel;

/** Lead artist(s)/Lead performer(s)/Soloist(s)/Performing group frame 
 *
 * @author Florian Heer
 * @version $Id: FrameTPE1.java,v 1.6 2003/10/22 23:51:02 heer Exp $
 */
public class FrameTPE1 extends FrameT {
    public String getLongName ()  { return "Lead artist frame"; }

    public FrameTPE1 () { super ("TPE1"); }

    public FrameTPE1 (ID3V2Frame frame) {
	super (frame);
    }

    public FrameTPE1 (ID3V2Frame frm, DataSource ds) 
	throws SeekPastEndException {
	super (frm, ds);
    }

    public String toString () {
	return getLongName () + " Encoding: "+encoding+"\nArtist : " 
	    + text;
    }

    public boolean canEdit () { return true; }
    public boolean canDisplay () { return true; }

    public Id3JPanel createJPanel (boolean edit, boolean selfupdate) {
	return super.createJPanel (edit, selfupdate, "Lead Artist:");
    }

}
