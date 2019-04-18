/*
 * @author Bretislav Kral
 * 
 * V teto tride jsem pro jednoduchost ulozil vetsinu nejpouzivanejsich promennych a konstant
 *  
 * */


import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class Promenne {
	// hodnoty pripojeni
	public final static int NEPRIPOJEN = 0;
	public final static int ODPOJEN = 1;
	public final static int ODPOJUJI = 2;
	public final static int PRIPOJ = 3;
	public final static int PRIPOJEN = 4;
	public final static String zpravy[] = { "Chyba! Nemohu se pripojit! ",
			"Odpojen ", "Odpojuji... ", "Pripojuji... ", "Pripojen " };
	public final static Chatter tcpChat = new Chatter();
	public final static String KONEC_CHATU = new Character((char) 0).toString();

	// informace k pripojeni
	public static String serverIP = "localhost";
	public static int port = 1234;
	public static int stavPripojeni = ODPOJEN;
	public static boolean jeServer = true;
	public static String stavZprava = zpravy[stavPripojeni];
	public static StringBuffer keSpojeni = new StringBuffer("");
	public static StringBuffer kOdeslani = new StringBuffer("");

	// ruzne prvky gui
	public static JFrame hlavniRam = null;
	public static JTextArea vystup = null;
	public static JTextField vstup = null;
	public static JPanel stavPanel = null;
	public static JLabel stavText = null;
	public static JTextField vstupIP = null;
	public static JTextField vstupPort = null;
	public static JTextPane podpis = null;
	public static JRadioButton server = null;
	public static JRadioButton klient = null;
	public static JButton pripojit = null;
	public static JButton odpojit = null;

	// komponenty pripojeni
	public static ServerSocket sServer = null;
	public static Socket socket = null;
	public static BufferedReader bufReadVstup = null;
	public static PrintWriter prtWriVystup = null;

}
