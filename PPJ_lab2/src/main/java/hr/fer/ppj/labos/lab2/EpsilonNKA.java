package hr.fer.ppj.labos.lab2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EpsilonNKA implements Serializable {

	private static final long serialVersionUID = 987208477361655362L;
	private List<LR1Stavka> LR1Stavke;
	private List<String> nezavrsni;
	private Map<String, Set<String>> zapocinjeSkupovi;
	private Map<String, Map<Integer, List<String>>> produkcije;
	private Set<String> prazniZnakovi;

	public EpsilonNKA(List<String> nezavrsni, Map<String, Set<String>> zapocinjeSkupovi,
			Map<String, Map<Integer, List<String>>> produkcije, Set<String> prazniZnakovi) {
		this.LR1Stavke = new ArrayList<>();
		this.nezavrsni = nezavrsni;
		this.zapocinjeSkupovi = zapocinjeSkupovi;
		this.produkcije = produkcije;
		this.prazniZnakovi = prazniZnakovi;
		izgradiEpsilonNKA();
	}

	private void izgradiEpsilonNKA() {
		String pocetniNezavrsniZnak = "<%>";
		List<String> desnaStranaPocetneProdukcije = produkcije.get("<%>").get(1);
		Set<String> znakoviIzaPocetneLR1Stavke = new HashSet<>(); // samo oznaka
																	// kraja
																	// niza
		znakoviIzaPocetneLR1Stavke.add("#");
		LR1Stavka pocetnaLR1Stavka = new LR1Stavka(pocetniNezavrsniZnak, desnaStranaPocetneProdukcije, 0,
				znakoviIzaPocetneLR1Stavke, 1);
		LR1Stavke.add(pocetnaLR1Stavka);
		int brojLR1Stavki = 1;
		int i = 0;
		do {
			for (; i < brojLR1Stavki; i++) {
				LR1Stavke.get(i).pomakniTocku();
			}
			brojLR1Stavki = LR1Stavke.size();
		} while (i < brojLR1Stavki);

		for (LR1Stavka stavka : LR1Stavke) {
			System.out.println("trenutno stanje: " + stavka);
			System.out.println("prijelaz prema:");
			if (stavka.prijelaz != null) {
				System.out.println(stavka.prijelaz.sljedecaStavka);
			}
			System.out.println("epsilon prijelazi prema:");
			for (LR1Stavka epsilonStavka : stavka.stavkeUKojePrelaziSEpsilon) {
				System.out.println(epsilonStavka);
			}
			System.out.println();
		}
	}

	
	public List<LR1Stavka> getLR1Stavke() {
		return LR1Stavke;
	}


	// nisam stavio da je static jer mora pristupati vise stvari iz epsilonNka
	class LR1Stavka {
		private String znakLijeveStraneProdukcije;
		private List<String> znakoviDesneStraneProdukcije;
		private int indeksTocke;
		private Set<String> znakoviIzaProdukcije;
		private Par prijelaz;
		private List<LR1Stavka> stavkeUKojePrelaziSEpsilon;
		private boolean jeLiDodanaUStanjeDKA;
		private int indeksProdukcije;

		public LR1Stavka(String znakLijeveStraneProdukcije, List<String> znakoviDesneStraneProdukcije, int indeksTocke,
				Set<String> znakoviIzaProdukcije, int indeksProdukcije) {
			this.znakLijeveStraneProdukcije = znakLijeveStraneProdukcije;
			this.znakoviDesneStraneProdukcije = znakoviDesneStraneProdukcije;
			this.indeksTocke = indeksTocke;
			this.znakoviIzaProdukcije = znakoviIzaProdukcije;
			this.stavkeUKojePrelaziSEpsilon = new ArrayList<>();
			this.jeLiDodanaUStanjeDKA = false;
			this.indeksProdukcije = indeksProdukcije;
		}

		// ovaj konstruktor sluzi za stvaranje sljedece LR stavke na temelju
		// prethodne
		private LR1Stavka(LR1Stavka prethodnaStavka) {
			this(prethodnaStavka.znakLijeveStraneProdukcije, prethodnaStavka.znakoviDesneStraneProdukcije,
					prethodnaStavka.indeksTocke + 1, prethodnaStavka.znakoviIzaProdukcije,
					prethodnaStavka.indeksProdukcije);
		}

		private void pomakniTocku() {
			// provjera je li tocka na kraju desne strane produkcije
			if (indeksTocke == znakoviDesneStraneProdukcije.size()) {
				return;
			}

			// za epsilon produkcije
			if (znakoviDesneStraneProdukcije.get(0).equals("$")) {
				return;
			}

			// provjera je li znak desno od tocke nezavrsan zbog pokretanja
			// epsilon prijelaza
			if (nezavrsni.contains(znakoviDesneStraneProdukcije.get(indeksTocke))) {
				obradaEpsilonPrijelaza(znakoviDesneStraneProdukcije.get(indeksTocke));
			}

			// stvaranje nove LR1 stavke pomicanjem tocke za jedno mjesto udesno
			LR1Stavka sljedecaStavka = new LR1Stavka(this);
			this.prijelaz = new Par(znakoviDesneStraneProdukcije.get(indeksTocke), sljedecaStavka);
			LR1Stavke.add(sljedecaStavka);
		}

		// za svaku produkciju koja zapocinje danim nezavrsnim znakom stvori
		// novu LR1stavku ako vec ne postoji
		private void obradaEpsilonPrijelaza(String nezavrsniZnak) {
			for (Map.Entry<Integer, List<String>> produkcija : produkcije.get(nezavrsniZnak).entrySet()) {
				List<String> noviZnakoviDesneStraneProdukcije = produkcija.getValue();
				Integer indeksProdukcije = produkcija.getKey();
				Set<String> noviZnakoviIzaProdukcije = new HashSet<String>();

				//dodavanje znakova kojima moze zapoceti ostatak niza iza nezavrsnog znaka koji je pokrenuo prijelaz
				for (int i = indeksTocke + 1, n = znakoviDesneStraneProdukcije.size(); i < n; i++) {
					noviZnakoviIzaProdukcije.addAll(zapocinjeSkupovi.get(znakoviDesneStraneProdukcije.get(i)));
					if (!prazniZnakovi.contains(znakoviDesneStraneProdukcije.get(i))) {
						break;
					}
				}

				// provjera jesu li svi znakovi iza danog nezavrsnog u
				// originalnoj LR1 stavci prazni znakovi, ako jesu treba dodati
				// i znakove iza originalne LR1 stavke
				boolean sviSuPrazni = true;
				for (int i = indeksTocke + 1, n = znakoviDesneStraneProdukcije.size(); i < n; i++) {
					if (!prazniZnakovi.contains(znakoviDesneStraneProdukcije.get(i))) {
						sviSuPrazni = false;
						break;
					}
				}
				if (sviSuPrazni) {
					noviZnakoviIzaProdukcije.addAll(znakoviIzaProdukcije);
				}
				LR1Stavka novaStavka = new LR1Stavka(nezavrsniZnak, noviZnakoviDesneStraneProdukcije, 0,
						noviZnakoviIzaProdukcije, indeksProdukcije);
				// istovremeno provjeravamo nalazi li se novaStavka vec u listi
				// svih stavki te ako postoji saznajemo indeks na kojem se
				// nalazi u listi
				int indeksNoveStavkeUListiSvihStavki = LR1Stavke.indexOf(novaStavka);
				if (indeksNoveStavkeUListiSvihStavki != -1) {
					stavkeUKojePrelaziSEpsilon.add(LR1Stavke.get(indeksNoveStavkeUListiSvihStavki));
				} else {
					LR1Stavke.add(novaStavka);
					stavkeUKojePrelaziSEpsilon.add(novaStavka);
				}
			}

		}

		@Override
		public String toString() {
			StringBuilder string = new StringBuilder(znakLijeveStraneProdukcije);
			string.append(" -> ");
			int i = 0;
			for (String znak : znakoviDesneStraneProdukcije) {
				if (i == indeksTocke) {
					string.append('*');
				}
				string.append(znak);
				i++;
			}
			if (i == indeksTocke) {
				string.append('*');
			}
			string.append(znakoviIzaProdukcije);
			return string.toString();
		}

		public Set<String> getZnakoviIzaProdukcije() {
			return znakoviIzaProdukcije;
		}

		public boolean isJeLiDodanaUStanjeDKA() {
			return jeLiDodanaUStanjeDKA;
		}

		public void setJeLiDodanaUStanjeDKA(boolean jeLiDodanaUStanjeDKA) {
			this.jeLiDodanaUStanjeDKA = jeLiDodanaUStanjeDKA;
		}

		public Par getPrijelaz() {
			return prijelaz;
		}

		public String getZnakLijeveStraneProdukcije() {
			return znakLijeveStraneProdukcije;
		}

		public int getIndeksProdukcije() {
			return indeksProdukcije;
		}

		public List<LR1Stavka> getStavkeUKojePrelaziSEpsilon() {
			return stavkeUKojePrelaziSEpsilon;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (!(o instanceof LR1Stavka)) {
				return false;
			}
			LR1Stavka that = (LR1Stavka) o;
			if (that.indeksProdukcije != this.indeksProdukcije) {
				return false;
			}
			if (that.indeksTocke != this.indeksTocke) {
				return false;
			}
			if (!that.znakoviIzaProdukcije.equals(this.znakoviIzaProdukcije)) {
				return false;
			}
			return true;
		}
	}

	static class Par {
		private String znakKojiPokrecePrijelaz;
		private LR1Stavka sljedecaStavka;

		public Par(String znakKojiPokrecePrijelaz, LR1Stavka sljedecaStavka) {
			this.znakKojiPokrecePrijelaz = znakKojiPokrecePrijelaz;
			this.sljedecaStavka = sljedecaStavka;
		}

		public String getZnakKojiPokrecePrijelaz() {
			return znakKojiPokrecePrijelaz;
		}

		public LR1Stavka getSljedecaStavka() {
			return sljedecaStavka;
		}
	}
}
