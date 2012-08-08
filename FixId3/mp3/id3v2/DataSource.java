package mp3.id3v2;

import mp3.Defines;

/** Definition for a source of data
 *
 * @author Florian Heer
 * @version $Id: DataSource.java,v 1.4 2004/11/28 21:21:25 heer Exp $
 */

public interface DataSource extends Defines {
    /** delivers the next available byte 
     * @return next byte
     */
    public byte getByte () throws SeekPastEndException;

    /** delivers the next available bytes
     * @param number number of bytes to deliver
     * @return demanded bytes
     */
    public byte [] getBytes (long number) throws SeekPastEndException;

    /** delivers the next bytes until a certain byte or the end is encountered.
     * The stop byte is not included in the return value.
     * @param b at what byte to stop
     * @return demanded bytes
     */
    public byte [] getBytesTo (byte b);

    /** Sets the internal pointer to the start of the data */
    public void reset ();

    /** informs whether there are more bytes than can be read 
     * @return true if there are bytes to follow
     */
    public boolean hasMoreBytes ();

    /** informs how many more bytes can be read
     * @return number of bytes availabel
     */
    public long getBytesLeft ();

    /** Sets the internal pointer to a given position in the data
     * @param position position to set the internal pointer to
     */
    public void seek (long position) throws SeekPastEndException;
}
