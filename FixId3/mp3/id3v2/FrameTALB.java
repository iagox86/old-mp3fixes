package mp3.id3v2;


import mp3.gui.Id3JPanel;

/** Album/Movie/Show title frame 
 *
 * @author Florian Heer
 * @version $Id: FrameTALB.java,v 1.6 2003/10/22 23:51:02 heer Exp $
 */
public class FrameTALB extends FrameT {
    public String getLongName ()  { return "Album frame"; }

    public FrameTALB () { super ("TALB"); }

    public FrameTALB (ID3V2Frame frame) {
	super (frame);
    }

    public FrameTALB (ID3V2Frame frm, DataSource ds) 
	throws SeekPastEndException {
	super (frm, ds);
    }

    public String toString () {
	return getLongName () + " Encoding: "+encoding+"\nAlbum : " 
	    + text;
    }

    public boolean canEdit () { return true; }
    public boolean canDisplay () { return true; }

    public Id3JPanel createJPanel (boolean edit, boolean selfupdate) {
	return super.createJPanel (edit, selfupdate, "Album:");
    }

}
