package mp3.id3v2;

import mp3.util.OutputCtr;

/** Reverb frame
 * Yet another subjective one. You may here adjust echoes of different kinds.
 * Reverb left/right is the delay between every bounce in ms. Reverb bounces
 * left/right is the number of bounces that should be made. $FF equals an
 * infinite number of bounces. Feedback is the amount of volume that should
 * be returned to the next echo bounce. $00 is 0%, $FF is 100%. If this value
 * were $7F, there would be 50% volume reduction on the first bounce, 50% of
 * that on the second and so on. Left to left means the sound from the left
 * bounce to be played in the left speaker, while left to right means sound
 * from the left bounce to be played in the right speaker.
 *
 * 'Premix left to right' is the amount of left sound to be mixed in the
 * right before any reverb is applied, where $00 id 0% and $FF is 100%.
 * 'Premix right to left' does the same thing, but right to left. Setting both
 * premix to $FF would result in a mono output (if the reverb is applied
 * symmetric). There may only be one "RVRB" frame in each tag.
 *
 * <Header for 'Reverb', ID: "RVRB">
 * Reverb left (ms) $xx xx
 * Reverb right (ms) $xx xx
 * Reverb bounces, left $xx
 * Reverb bounces, right $xx
 * Reverb feedback, left to left $xx
 * Reverb feedback, left to right $xx
 * Reverb feedback, right to right $xx
 * Reverb feedback, right to left $xx
 * Premix left to right $xx
 * Premix right to left $xx
 *
 * @author Florian Heer
 * @version $Id: FrameRVRB.java,v 1.4 2003/10/22 23:51:02 heer Exp $
 */

public class FrameRVRB extends ID3V2Frame {
    private int reverbLeft =0;
    private int reverbRight = 0;
    private int bouncesLeft = 0;
    private int bouncesRight = 0;
    private int feedbackLeftLeft = 0;
    private int feedbackLeftRight = 0;
    private int feedbackRightRight = 0;
    private int feedbackRightLeft = 0;
    private int premixLeftRight = 0;
    private int premixRightLeft = 0;

    public String getLongName ()  { return "Reverb frame"; }

    public FrameRVRB (ID3V2Frame frm) {
        super (frm);
        if (frm instanceof FrameRVRB) {
            FrameRVRB fr = (FrameRVRB)frm;
            reverbLeft          = fr.reverbLeft;
            reverbRight         = fr.reverbRight;
            bouncesLeft         = fr.bouncesLeft;
            bouncesRight        = fr.bouncesRight;
            feedbackLeftLeft    = fr.feedbackLeftLeft;
            feedbackLeftRight   = fr.feedbackLeftRight;
            feedbackRightRight = fr.feedbackRightRight;
            feedbackRightLeft   = fr.feedbackRightLeft;
            premixLeftRight     = fr.premixLeftRight;
            premixRightLeft     = fr.premixRightLeft;
        }
    }

    public FrameRVRB (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);
	// Decoding our information from the DataSource!
	try {
	    reverbLeft = Helper.byteToUnsignedInt (ds.getByte());
	    reverbRight = Helper.byteToUnsignedInt (ds.getByte());
	    bouncesLeft = Helper.byteToUnsignedInt (ds.getByte());
	    bouncesRight = Helper.byteToUnsignedInt (ds.getByte());
	    feedbackLeftLeft = Helper.byteToUnsignedInt (ds.getByte());
	    feedbackLeftRight = Helper.byteToUnsignedInt (ds.getByte());
	    feedbackRightRight = Helper.byteToUnsignedInt (ds.getByte());
	    feedbackRightLeft = Helper.byteToUnsignedInt (ds.getByte());
	    premixLeftRight = Helper.byteToUnsignedInt (ds.getByte());
	    premixRightLeft = Helper.byteToUnsignedInt (ds.getByte());
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName () + " can't be instantiated! SPEEx!");
	    throw ex;
	}
    }


    public String toString () {
	return getLongName () + "\n";
    }

}
