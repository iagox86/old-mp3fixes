package mp3.id3v2;

import mp3.util.OutputCtr;

import java.util.Vector;
//import java.util.Iterator;
import java.util.Enumeration;

/** Involved people frame
 * Since there might be a lot of people contributing to an audio file in
 * various ways, such as musicians and technicians, the 'Text information
 * frames' are often insufficient to list everyone involved in a project.
 * The 'Involved people list' is a frame containing the names of those
 * involved, and how they were involved. The body simply contains a
 * terminated string with the involvement directly followed by a terminated
 * string with the involvee followed by a new involvement and so on. There
 * may only be one "IPLS" frame in each tag.
 *
 * @author Florian Heer
 * @version $Id: FrameIPLS.java,v 1.4 2003/10/24 12:46:30 heer Exp $
 */
public class FrameIPLS extends ID3V2Frame {
    Vector involved = new Vector ();

    public String getLongName ()  { return "Involved people frame"; }

    public FrameIPLS (ID3V2Frame frm) {
        super (frm);
        if (frm instanceof FrameIPLS) {
            FrameIPLS fr = (FrameIPLS)frm;
            //Iterator iter = fr.involved.iterator ();
            //while (iter.hasNext()) {
            //    involved.add (new TwoStrings ((TwoStrings)iter.next ()));
            Enumeration iter = fr.involved.elements ();
            while (iter.hasMoreElements()) {
                involved.addElement (new TwoStrings ((TwoStrings)iter.nextElement ()));

            }
        }
    }

    public FrameIPLS (ID3V2Frame frm, DataSource ds)
	throws SeekPastEndException {
	super (frm);

	// Decoding our information from the DataSource!
	try {
	    StringBuffer sb;
	    while (ds.hasMoreBytes ()) {
		TwoStrings ts = new TwoStrings ();
		byte b = ds.getByte();
		// read in the 'description' field... terminated string
		// Unfortunately the documentation doesn't specify WHAT
		// termination
		sb = new StringBuffer ();
		while (b != 0 && b != 0x0d
		       && b != 0x0a && ds.hasMoreBytes ()) {
		    sb.append ((char)b);
		    b = ds.getByte ();
		}
		ts.one = sb.toString ();

		if (ds.hasMoreBytes ()) {
		    b = ds.getByte ();
		    sb = new StringBuffer ();
		    while (b != 0 && b != 0x0d
			   && b != 0x0a && ds.hasMoreBytes ()) {
			sb.append ((char)b);
			b = ds.getByte ();
		    }
		    ts.two = sb.toString ();
		}
		//involved.add (ts);
		involved.addElement (ts);
	    }
	}
	catch (SeekPastEndException ex) {
	    OutputCtr.println (0, getLongName ()
			       +" can't be instantiated! SPEEx!");
	    throw ex;
	}
    }

    public String toString () {
	StringBuffer sb = new StringBuffer ().append (getLongName ());
	//Iterator iter = involved.iterator ();
	Enumeration iter = involved.elements ();
	TwoStrings ts;
	//while (iter.hasNext ()) {
	//    ts = (TwoStrings)iter.next ();
	while (iter.hasMoreElements ()) {
	    ts = (TwoStrings)iter.nextElement ();
	    sb.append ("\n").append (ts.one)
		.append (" : ").append (ts.two);
	}
	sb.append ("\n");
	return sb.toString ();
    }

    public class TwoStrings {
    	public String one = "";
    	public String two = "";
    	public TwoStrings () {};
    	public TwoStrings (TwoStrings ts) {
    	    one = ts.one;
    	    two = ts.two;
    	}
    	public Object clone () {
    	    return new TwoStrings (this);
    	}
    }

}
