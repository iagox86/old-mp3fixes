package mp3.id3v2;

/** A very simple interface to mark frames.
 * Frames implementing this interface contain a description/value pair,
 * both encoded in a creatin String encoding
 *
 * @author Florian Heer
 * @version $Id: EncDescValFrame.java,v 1.2 2002/12/19 23:04:33 heer Exp $
 */
public interface EncDescValFrame {
    // Already defined in Frame...
    /** @return true if the frame in question can have its contents displayed.
     */
    public boolean canDisplay ();
    /** @return true if the frame in question can have its contents edited
     */
    public boolean canEdit ();

    // Special to these Frames..:
    /** Sets the description value of this frame 
     * @param description value for the description field
     */
    public void setDescription (String description);
    /** Sets the value of this frame
     * @param value the value for the value field
     */
    public void setValue (String value);
}
