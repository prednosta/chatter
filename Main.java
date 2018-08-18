/*
 * @author Bretislav Kral
 * 
 * Hlavni trida programu, vse se spousti odtud
 * Tento program ma slouzit jako jednoduchy chatovaci program,
 * jde nastavit IP adresa a port jednoducheho serveru, stejne tak
 * i klient si musi nastavit stejnou IP adresu a port, na kterem
 * nasloucha server.
 * Po pripojeni dochazi k vytvoreni spojeni a muze probihat samotna
 * komunikace. Ta probiha jednoduse, po napsani zpravy dochazi pri 
 * potvrzeni k jejimu spojeni s jednoduchou signalizaci, zdali se 
 * jedna o prichozi nebo odchozi zpravu, potr prijde na radu jeji odeslani.
 * Jelikoz jsou pouzity vlakna, musi byt spojovani a odesilani textu 
 * reseno synchronizovanim, aby nedochazelo ke kolizim. Na druhe strane
 * ceka na zpravu vstupni stream, ktery ji prijme a zobrazi, opet s
 * indikaci, zdali je to prichozi/odchozi zprava.
 * Jsou zde take jednoduche informace o tom, zdali je spojeni mezi
 * klientem a serverem navazano, ci ne. Tyto slouzi take k rizeni
 * povoleni jednotlivych prvku okna.
 * */

package chatter;

import java.net.*;
import java.io.*;

public class Main extends Chatter {

	// hlavni trida
	public static void main(String args[]) {
		String s;
		GUI();

		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}

			switch (stavPripojeni) {
			case PRIPOJ:
				try {
					// zkousi nastavit server
					if (jeServer) {
						sServer = new ServerSocket(port);
						socket = sServer.accept();
					}
					// pokud je uzivatel klient, zkousi se pripojit
					else {
						socket = new Socket(serverIP, port);
					}

					bufReadVstup = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					prtWriVystup = new PrintWriter(socket.getOutputStream(),
							true);
					zmenaStavuVL(PRIPOJEN, true);
				}
				// nastaneli chyba, vycisti a zmeni stav
				catch (IOException e) {
					uklid();
					zmenaStavuVL(ODPOJEN, false);
				}
				break;

			case PRIPOJEN:
				try {
					// poslani dat
					if (kOdeslani.length() != 0) {
						prtWriVystup.print(kOdeslani);
						prtWriVystup.flush();
						kOdeslani.setLength(0);
						zmenaStavuVL(NEPRIPOJEN, true);
					}

					// prijeti dat
					if (bufReadVstup.ready()) {
						s = bufReadVstup.readLine();
						if ((s != null) && (s.length() != 0)) {
							// zjisti, je li konec prichozich dat
							if (s.equals(KONEC_CHATU)) {
								zmenaStavuVL(ODPOJUJI, true);
							}
							// jinak prijme data
							else {
								spojTexty(" <- " + s + "\n");
								zmenaStavuVL(NEPRIPOJEN, true);
							}
						}
					}
				} catch (IOException e) {
					uklid();
					zmenaStavuVL(ODPOJEN, false);
				}
				break;

			case ODPOJUJI:
				prtWriVystup.print(KONEC_CHATU);
				prtWriVystup.flush();
				uklid();
				zmenaStavuVL(ODPOJEN, true);
				break;
			default:
				break;
			}
		}
	}
}
