package mp3;

/** Contains various switches and values that can be changed by developers.
 *
 * @author Florian Heer
 * @version $Id: Configuration.java,v 1.4 2002/07/07 12:40:02 mos Exp $
 */

public class Configuration {

    /** Width of a JLabel in the GUI-elements */
    public static int labelWidth = 110;
    /** Height of a JLabel in the GUI-elements */
    public static int labelHeight = 21;
    /** Width of a JTextField in the GUI-elements */
    public static int textWidth = 230;
    /** Height of a JTextField in the GUI-elements */
    public static int textHeight = 21;
    /** Width of a JButton ("OK") in the GUI-elements */
    public static int buttonWidth = 60;
    /** Height of a JButton ("OK") in the GUI-elements */
    public static int buttonHeight = 25;
    /** Width of a JTextArea in the GUI-elements */
    public static int textAreaWidth = 230;
    /** Height of a JTextArea in the GUI-elements */
    public static int textAreaHeight = 66;

    /** Switches the behaviour of the ID3V2Writer.
     * If this is set to false (the default), only Frames
     * that contain data are written by the ID3V2Writer
     */
    public static boolean keepAllFrames = false;

    /** The default language for descriptions in Frames.
     * Used if unsane values were found.
     */
    public static String defaultLanguage = "ENG";
}
