
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class GenerativnoStablo {
	private Stack<Cvor> stog;
	private final List<String> nezavrsni;
	private final List<String> zavrsniIKraj;
	private final List<String> sinkronizacijski;
	private final Par[][] tablicaAkcija;
	private final Integer[][] tablicaNovoStanje;
	private Cvor korijen;
	private Map<String, Map<Integer, List<String>>> produkcije;
	private List<ZapisTabliceUniformnihZnakova> uniformniZnakoviUlaznogNiza;

	public GenerativnoStablo(Akcija tablicaAkcija, NovoStanje tablicaNovoStanje,
			List<ZapisTabliceUniformnihZnakova> uniformniZnakoviUlaznogNiza) {
		this.nezavrsni = tablicaNovoStanje.getNezavrsniZnakovi();
		this.zavrsniIKraj = tablicaAkcija.getZavrsniIKraj();
		this.sinkronizacijski = tablicaAkcija.getSinkronizacijski();
		this.stog = new Stack<>();
		this.tablicaAkcija = tablicaAkcija.getTablica();
		this.tablicaNovoStanje = tablicaNovoStanje.getTablica();
		this.produkcije = tablicaAkcija.getProdukcije();
		this.uniformniZnakoviUlaznogNiza = uniformniZnakoviUlaznogNiza;
		izgradiStablo();
	}

	public void pomakni(String uniformniZnak, int stanje, int brojRetka, String leksickaJedinka) {
		Cvor noviCvor = new Cvor(stanje, uniformniZnak, brojRetka, leksickaJedinka, false);
		stog.push(noviCvor);
	}

	public void reduciraj(int indeksProdukcije) {
		String lijeviZnakProdukcije = null;
		List<String> desnaStranaProdukcije = null;
		int stanjeNaVrhuStoga;
		Integer sljedeceStanje;
		for (Map.Entry<String, Map<Integer, List<String>>> produkcija : produkcije.entrySet()) {
			if (produkcija.getValue().containsKey(indeksProdukcije)) {
				lijeviZnakProdukcije = produkcija.getKey();
				desnaStranaProdukcije = produkcija.getValue().get(indeksProdukcije);
				break;
			}
		}

		if (desnaStranaProdukcije.contains("$")) {
			if (stog.isEmpty()) {
				stanjeNaVrhuStoga = 0;
			} else {
				stanjeNaVrhuStoga = stog.peek().getStanje();
			}
			sljedeceStanje = tablicaNovoStanje[stanjeNaVrhuStoga][nezavrsni.indexOf(lijeviZnakProdukcije)];
			Cvor noviCvor = new Cvor(sljedeceStanje, lijeviZnakProdukcije, true);
			noviCvor.dodajDijete(new Cvor(-1, "$", true));
			stog.push(noviCvor);
		} else {
			// djecu novog cvora treba dodavati u obrnutom redoslijedu od onog
			// kako su poredani na vrhu stoga gledajuci od gore prema dolje
			List<Cvor> skinutiZnakoviDesneStraneProdukcije = new ArrayList<>();
			for (int i = 0; i < desnaStranaProdukcije.size(); i++) {
				skinutiZnakoviDesneStraneProdukcije.add(stog.pop());
			}
			if (stog.isEmpty()) {
				stanjeNaVrhuStoga = 0;
			} else {
				stanjeNaVrhuStoga = stog.peek().getStanje();
			}
			sljedeceStanje = tablicaNovoStanje[stanjeNaVrhuStoga][nezavrsni.indexOf(lijeviZnakProdukcije)];
			Cvor noviCvor = new Cvor(sljedeceStanje, lijeviZnakProdukcije, true);
			for (int i = desnaStranaProdukcije.size() - 1; i >= 0; i--) {
				noviCvor.dodajDijete(skinutiZnakoviDesneStraneProdukcije.get(i));
			}
			stog.push(noviCvor);
		}
	}

	public void prihvati() {
		// this.reduciraj(1);
		this.korijen = stog.pop();
	}

	private void izgradiStablo() {
		int indeksUniformnogZnakaUTabliciAkcija;
		int stanjeNaVrhuStoga;
		ZapisTabliceUniformnihZnakova uniformniZnak;
		Cvor vrhStoga;
		Par akcija;
		for (int i = 0, n = uniformniZnakoviUlaznogNiza.size(); i < n; i++) {
			if (stog.isEmpty()) {
				stanjeNaVrhuStoga = 0;
			} else {
				vrhStoga = stog.peek();
				stanjeNaVrhuStoga = vrhStoga.stanje;
			}
			uniformniZnak = uniformniZnakoviUlaznogNiza.get(i);
			indeksUniformnogZnakaUTabliciAkcija = zavrsniIKraj.indexOf(uniformniZnak.getUniformniZnak());
			akcija = tablicaAkcija[stanjeNaVrhuStoga][indeksUniformnogZnakaUTabliciAkcija];
			if (akcija.getAkcija() == AkcijaParsera.POMAKNI) {
				pomakni(uniformniZnak.getUniformniZnak(), akcija.getIndex(), uniformniZnak.getRedak(),
						uniformniZnak.getLeksickaJedinka());
			} else if (akcija.getAkcija() == AkcijaParsera.REDUCIRAJ) {
				reduciraj(akcija.getIndex());
				// kazaljka treba ostati na istom mjestu u ulaznom nizu pa se
				// smanjuje za 1 jer cu ju petlja povecati za 1
				i--;
			} else if (akcija.getAkcija() == AkcijaParsera.PRIHVATI) {
				prihvati();
			} else {
				// akcija == ODBACI

				// ispis na syserr jer se na sprutu ne evaluira ispis pogreske,
				// ali na usmenom to mogu pitati
				System.err.println("broj retka pogreske: " + uniformniZnak.getRedak());
				// -1 jer ne zelim provjeravati za oznaku kraja niza koja je na
				// kraju liste
				System.err.println("uniformni znakovi koji ne bi izazvali pogresku: ");
				for (int j = 0, m = zavrsniIKraj.size() - 1; j < m; j++) {
					if (tablicaAkcija[stanjeNaVrhuStoga][j].getAkcija() != AkcijaParsera.ODBACI) {
						System.err.println(" " + zavrsniIKraj.get(j));
					}
				}
				System.err.println("procitani uniformni znak i leksicka jedinka: " + uniformniZnak.getUniformniZnak()
						+ " " + uniformniZnak.getLeksickaJedinka());

				// oporavak od pogreske
				// odbacivanje znakova ulaznog niza do prvog sinkornizacijskog
				while (!sinkronizacijski.contains(uniformniZnak.getUniformniZnak())) {
					++i;
					if (i >= n) {
						// mislim da do ovoga ne bi trebalo moci doci
						break;
					}
					uniformniZnak = uniformniZnakoviUlaznogNiza.get(i);
				}

				// odbacivanje cvorova sa stoga
				int indeksSinkronizacijskogZnaka = zavrsniIKraj.indexOf(uniformniZnak.getUniformniZnak());
				while (tablicaAkcija[stanjeNaVrhuStoga][indeksSinkronizacijskogZnaka]
						.getAkcija() == AkcijaParsera.ODBACI) {
					stog.pop();
					stanjeNaVrhuStoga = stog.peek().getStanje();
				}

				// kazaljka ulaznog niza pokazuje na sinkronizacijski znak, a
				// petlja ce ju pomaknuti na sljedeci znak pa ju zato vracamo za
				// jedan unatrag
				--i;
			}
		}
	}

	// poziva se s korijenom stabla i pomakom od 0
	public void ispisiStablo(Cvor korijen, int pomak) {
		if (!korijen.getUniformniZnak().equals("<%>")) {
			for (int i = 0; i < pomak; i++) {
				System.out.print(" ");
			}
			System.out.println(korijen);
		}
		for (Cvor dijete : korijen.getDjeca()) {
			this.ispisiStablo(dijete, pomak + 1);
		}
	}

	public Cvor getKorijen() {
		return korijen;
	}

	static class Cvor {

		private int stanje;
		private String uniformniZnak;
		private int brojRetka;
		private String leksickaJedinka;
		private boolean ispisatiSamoUniformniZnak; // da znam je li unutrasnji
													// cvor ili list

		private List<Cvor> djeca;

		// kod ispisa paziti je li unutrasnji cvor ili list i je li $
		public Cvor(int stanje, String uniformniZnak, int brojRetka, String leksickaJedinka,
				boolean ispisatiSamoUniformniZnak) {
			this.stanje = stanje;
			this.uniformniZnak = uniformniZnak;
			this.brojRetka = brojRetka;
			this.leksickaJedinka = leksickaJedinka;
			this.ispisatiSamoUniformniZnak = ispisatiSamoUniformniZnak;
			djeca = new LinkedList<>();
		}

		public Cvor(int stanje, String uniformniZnak, boolean jeNezavrsniZnak) {
			this(stanje, uniformniZnak, -1, null, jeNezavrsniZnak);
		}

		public void dodajDijete(Cvor dijete) {
			djeca.add(dijete);
		}

		public int getStanje() {
			return stanje;
		}

		public String getUniformniZnak() {
			return uniformniZnak;
		}

		public int getBrojRetka() {
			return brojRetka;
		}

		public String getLeksickaJedinka() {
			return leksickaJedinka;
		}

		public boolean isIspisatiSamoUniformniZnak() {
			return ispisatiSamoUniformniZnak;
		}

		public List<Cvor> getDjeca() {
			return djeca;
		}

		public void isIspisatiSamoUniformniZnak(boolean jeNezavrsniZnak) {
			this.ispisatiSamoUniformniZnak = jeNezavrsniZnak;
		}

		@Override
		public String toString() {
			if (ispisatiSamoUniformniZnak)
				return uniformniZnak;
			else {
				StringBuilder sb = new StringBuilder();
				sb.append(uniformniZnak);
				sb.append(" ");
				sb.append(brojRetka);
				sb.append(" ");
				sb.append(leksickaJedinka);
				return sb.toString();
			}
		}
	}
}
