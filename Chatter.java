/* 
 * 
 * @author Bretislav Kral
 * 
 * Zde je hlavni cast programu, rozvrzeni okna na jednotlive casti a take
 * ovladani odesilani/prijimani zprav. Funkce pro zmenu stavu (odpojen/pripojen atp)
 * funkce pro sloceni retezce do jednoho a pro odesilani. Predposledni funkce
 * slouzi k uklidu a zavreni jiz nepotrebnych proudu a promennych. Posledni
 * funkce zajistuje povoleni/zamitnuti jednotlivych prvku na zaklade toho,
 * jestli je klient nebo server v behu, ci stoji.
 * 
 * */

package chatter;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Chatter extends Promenne implements Runnable {

	private static JPanel vytvorPanelNastaveni() {
		JPanel panel = null;
		ActionAdapter akceTlacitek = null;

		// vytvori panel nastaveni
		JPanel panelNastaveni = new JPanel(new GridLayout(4, 1));

		// vstup ip adresy
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(new JLabel("IP serveru:"));
		vstupIP = new JTextField(10);
		vstupIP.setText(serverIP);
		vstupIP.setEnabled(false);
		vstupIP.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				vstupIP.selectAll();
				// bude editovatelny, jen kdyz neni pripojen
				if (stavPripojeni != ODPOJEN) {
					zmenaStavuBVL(NEPRIPOJEN, true);
				} else {
					serverIP = vstupIP.getText();
				}
			}
		});
		panel.add(vstupIP);
		panelNastaveni.add(panel);

		// nastaveni portu
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(new JLabel("Port:"));
		vstupPort = new JTextField(10);
		vstupPort.setEditable(true);
		vstupPort.setText((new Integer(port)).toString());
		vstupPort.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if (stavPripojeni != ODPOJEN) {
					zmenaStavuBVL(NEPRIPOJEN, true);
				} else {
					int temp;
					try {
						temp = Integer.parseInt(vstupPort.getText());
						port = temp;
					} catch (NumberFormatException nfe) {
						vstupPort.setText((new Integer(port)).toString());
						hlavniRam.repaint();
					}
				}
			}
		});
		panel.add(vstupPort);
		panelNastaveni.add(panel);

		// nastaveni hostitel/klient
		akceTlacitek = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (stavPripojeni != ODPOJEN) {
					zmenaStavuBVL(NEPRIPOJEN, true);
				} else {
					jeServer = e.getActionCommand().equals("hostitel");
					if (jeServer) {
						vstupIP.setEnabled(false);
						vstupIP.setText("localhost");
						serverIP = "localhost";
					} else {
						vstupIP.setEnabled(true);
					}
				}
			}
		};
		ButtonGroup tl = new ButtonGroup();
		server = new JRadioButton("Server", true);
		server.setActionCommand("server");
		server.addActionListener(akceTlacitek);
		klient = new JRadioButton("Klient", false);
		klient.setActionCommand("klient");
		klient.addActionListener(akceTlacitek);
		tl.add(server);
		tl.add(klient);
		panel = new JPanel(new GridLayout(1, 2));
		panel.add(server);
		panel.add(klient);
		panelNastaveni.add(panel);

		// tlacitka pripojit/odpojit
		JPanel tlacitka = new JPanel(new GridLayout(1, 2));
		akceTlacitek = new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				// vytvoreni pripojeni
				if (e.getActionCommand().equals("pripojit")) {
					zmenaStavuBVL(PRIPOJ, true);
				}
				// odpojeni
				else {
					zmenaStavuBVL(ODPOJUJI, true);
				}
			}
		};
		pripojit = new JButton("Pripojit");
		pripojit.setActionCommand("pripojit");
		pripojit.addActionListener(akceTlacitek);
		pripojit.setEnabled(true);
		odpojit = new JButton("Odpojit");
		odpojit.setActionCommand("odpojit");
		odpojit.addActionListener(akceTlacitek);
		odpojit.setEnabled(false);
		tlacitka.add(pripojit);
		tlacitka.add(odpojit);
		panelNastaveni.add(tlacitka);
		return panelNastaveni;
	}

	// nastavi vsechny prvky gui a zobrazi je
	protected static void GUI() {
		stavText = new JLabel();
		stavText.setText(zpravy[ODPOJEN]);
		podpis = new JTextPane();
		podpis.setText("Vytvoril Bretislav Kral @ KRA843");
		podpis.setEditable(false);
		podpis.setBackground(Color.lightGray);
		podpis.setEnabled(true);
		podpis.setForeground(Color.darkGray);
		podpis.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
		stavPanel = new JPanel(new BorderLayout());
		stavPanel.setBackground(Color.lightGray);
		stavPanel.add(stavText, BorderLayout.EAST);
		stavPanel.add(podpis, BorderLayout.WEST);

		JPanel panelNastaveni = vytvorPanelNastaveni();

		JPanel oknoChatu = new JPanel(new BorderLayout());
		vystup = new JTextArea(10, 20);
		vystup.setLineWrap(true);
		vystup.setEditable(false);
		JScrollPane oknoChatuScr = new JScrollPane(vystup,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		vstup = new JTextField();
		vstup.setEnabled(false);
		vstup.addActionListener(new ActionAdapter() {
			public void actionPerformed(ActionEvent e) {
				String s = vstup.getText();
				if (!s.equals("")) {
					spojTexty(" -> " + s + "\n");
					vstup.selectAll();
					odeslatText(s);
				}
			}
		});
		oknoChatu.add(vstup, BorderLayout.SOUTH);
		oknoChatu.add(oknoChatuScr, BorderLayout.CENTER);
		oknoChatu.setPreferredSize(new Dimension(220, 180));

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(stavPanel, BorderLayout.SOUTH);
		mainPane.add(panelNastaveni, BorderLayout.WEST);
		mainPane.add(oknoChatu, BorderLayout.CENTER);
		hlavniRam = new JFrame("Chatter");
		hlavniRam.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hlavniRam.setContentPane(mainPane);
		hlavniRam.setSize(hlavniRam.getPreferredSize());
		hlavniRam.setLocation(320, 240);
		hlavniRam.pack();
		hlavniRam.setVisible(true);
	}

	// bezpecna zmena gui pri zmene statusu (pomoci vlaken)
	protected static void zmenaStavuVL(int novyStav, boolean bezChyby) {
		if (novyStav != NEPRIPOJEN) {
			stavPripojeni = novyStav;
		}
		if (bezChyby) {
			stavZprava = zpravy[stavPripojeni];
		} else {
			stavZprava = zpravy[NEPRIPOJEN];
		}
		SwingUtilities.invokeLater(tcpChat);
	}

	// zmena statusu bez vlaken
	private static void zmenaStavuBVL(int novyStav, boolean bezChyby) {
		if (novyStav != NEPRIPOJEN) {
			stavPripojeni = novyStav;
		}
		if (bezChyby) {
			stavZprava = zpravy[stavPripojeni];
		} else {
			stavZprava = zpravy[NEPRIPOJEN];
		}
		tcpChat.run();
	}

	// synchronizovane spojeni textu
	protected static void spojTexty(String s) {
		synchronized (keSpojeni) {
			keSpojeni.append(s);
		}
	}

	// prirazeni textu do odesilaci promenne
	private static void odeslatText(String s) {
		synchronized (kOdeslani) {
			kOdeslani.append(s + "\n");
		}
	}

	// uklid pro odpojeni
	protected static void uklid() {
		try {
			if (sServer != null) {
				sServer.close();
				sServer = null;
			}
		} catch (IOException e) {
			sServer = null;
		}

		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			socket = null;
		}

		try {
			if (bufReadVstup != null) {
				bufReadVstup.close();
				bufReadVstup = null;
			}
		} catch (IOException e) {
			bufReadVstup = null;
		}

		if (prtWriVystup != null) {
			prtWriVystup.close();
			prtWriVystup = null;
		}
	}

	// zjistuje aktualni stav a podle toho natavi, zdali je povoleno/zamitnuto
	public void run() {
		switch (stavPripojeni) {
		case ODPOJEN:
			pripojit.setEnabled(true);
			odpojit.setEnabled(false);
			vstupIP.setEnabled(true);
			vstupPort.setEnabled(true);
			server.setEnabled(true);
			klient.setEnabled(true);
			vstup.setText("");
			vstup.setEnabled(false);
			break;

		case ODPOJUJI:
			pripojit.setEnabled(false);
			odpojit.setEnabled(false);
			vstupIP.setEnabled(false);
			vstupPort.setEnabled(false);
			server.setEnabled(false);
			klient.setEnabled(false);
			vstup.setEnabled(false);
			break;

		case PRIPOJEN:
			pripojit.setEnabled(false);
			odpojit.setEnabled(true);
			vstupIP.setEnabled(false);
			vstupPort.setEnabled(false);
			server.setEnabled(false);
			klient.setEnabled(false);
			vstup.setEnabled(true);
			break;

		case PRIPOJ:
			pripojit.setEnabled(false);
			odpojit.setEnabled(false);
			vstupIP.setEnabled(false);
			vstupPort.setEnabled(false);
			server.setEnabled(false);
			klient.setEnabled(false);
			vstup.setEnabled(false);
			vstup.grabFocus();
			break;
		}

		// ujisti se, ze jsou stavy tak jak maji byt
		vstupIP.setText(serverIP);
		vstupPort.setText((new Integer(port)).toString());
		server.setSelected(jeServer);
		klient.setSelected(!jeServer);
		stavText.setText(stavZprava);
		vystup.append(keSpojeni.toString());
		keSpojeni.setLength(0);
		hlavniRam.repaint();
	}
}