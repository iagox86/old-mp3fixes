package mp3.id3v2;

import mp3.Defines;

//import java.util.HashMap;
import java.util.Hashtable;

/** Provides some functionality that is necessary for decoding ID3v2 
 *
 * @author Florian Heer
 * @version $Id: Helper.java,v 1.12 2003/10/24 12:46:30 heer Exp $
 */
public class Helper implements Defines {
    // FIXME! Is this still neccessary now that we have the FrameFactory?
    //    static HashMap frameTypes = null;
    //    static Hashtable frameTypes = null;

    /** creates a long from a number of synchsafe bytes.
     * A synchsafe byte has its highest bit zeroed. In accordance
     * to the ID3V2-standard the byte-order is MSB first.
     * @param b any number of bytes together denoting a number not
     * bigger than can be stored in a long.
     * @return the integer contained in the byte-array (or an erroneous 
     * number if too many bytes were supplied).
     */
    public static long unsynchsafe (byte [] b) {
        long result = 0;
        // to clarify, a temp var
        long temp = 0;
        // to clarify, a position marker. This denotes the significance of the
        // currently processed byte.
        long pos = 0;
        for (long i = (b.length-1); i >= 0; i--) {
            temp = (long)b[(int)i];
            result += (temp * (pow(2,pos*7)));
	    pos++;
        }
        return result;
    }

    /** creates a number of synchsafe bytes from a long
     * @param value a value.. Sensibly one that fits into the given number 
     * of bytes
     * @param length number of bytes to create.
     * @return the array containing the snychsafed value
     */
    public static byte [] synchsafe (long value, int length) {
	byte[] b = new byte[length];
	int j=0;
	for (int i = length - 1;i >= 0; i--) {
	    b [i] = (byte)((value >> (j * 7)) & 0x7f);
	    j++;
	}
	return b;
    }

    /** Probably bullshit, but I was quicker writing this POW function than finding it in the JDK documentation
     */
    public static long pow (long number, long exp) {
        long result=1;
        for (long i=1;i<=exp;i++) 
            result*=number;
        return result;
    }

    public static int decodeBytesToInt (byte [] bAr) {
	int retval=0;
	int temp=0;
	int pos=0;
	byte b = 0;
	for (int i = 0; i<bAr.length; i++) {
	    temp = (int)bAr[i];
	    pos = (bAr.length-1)-i;
	    retval += (temp * (pow(2,pos*8)));
	}
	return retval;
    }

    /** converts a byte array to a Hex string. With a line by Andreas Mandel
     * at sourceforge (thanks!).
     */
    public static String arrayToHexString (byte [] bAr) {
	StringBuffer sb = new StringBuffer ().append ("0x");
	for (int i = bAr.length; i>0;i--)
	    // the charAt-idea by Andreas Mandel
	    sb.append ("0123456789ABCDEF".charAt(0xf&bAr[i-1]>>4))
		.append ("0123456789ABCDEF".charAt(bAr[i-1]&0xF));

	return sb.toString ();
    }

    /** converts a byte to a Hex string */
    public static String byteToHexString (byte b) {
	return "0x" + "0123456789ABCDEF".charAt (0x0f&(b>>4))
	    + "0123456789ABCDEF".charAt (b & 0x0f);
    }

    /** Converts an unsigned int-wrapped byte to a Hex String 
     * i.e. the int must be between 0 and 255.
     * if the int is out of range, an error string is returned
     */
    public static String byteToHexString (int i) {
	if (i >= 0 && i <=255)
	    return byteToHexString (unsignedIntToByte (i));
	return "Can't convert "+i+" to byte!";
    }

    /** converts a byte to an unsigned int */
    public static int byteToUnsignedInt (byte b) {
	if (b >= 0 && b < 128 )
	    return (int)b;
	return ((int)b)+256;
    }

    /** converts an unsigned int (between 0 and 255) to a byte
     * any value out of range will result in -128!!!
     */
    public static byte unsignedIntToByte (int i) {
	if (i >=0 && i < 128)
	    return (byte)i;
	if (i >=128 && i < 255)
	    return (byte)(i-256);
	return (byte)-128;
    }

    /** creates a new byte array as buffer
     * @param size number of bytes the new buffer should be bigger than the
     *  old
     * @param oldBuffer old Buffer
     * @return new Buffer with size = size + oldBuffer.length <br>
     *            null if an error occured;
     */
    public static byte [] reallocateByteBuffer (int size, byte [] oldBuffer) {
	byte [] newBuffer=null;
	int newSize = 0;
	if (oldBuffer != null)
	    newSize += oldBuffer.length;

	newSize += size;

	newBuffer = new byte [newSize];
        if (oldBuffer != null) {
	    System.arraycopy (oldBuffer, 0, newBuffer, 0, oldBuffer.length);
	}
	return newBuffer;
    }

}
