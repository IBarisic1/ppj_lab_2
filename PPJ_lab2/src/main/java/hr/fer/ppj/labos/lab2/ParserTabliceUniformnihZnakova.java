package hr.fer.ppj.labos.lab2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ParserTabliceUniformnihZnakova {

	private List<ZapisTabliceUniformnihZnakova> uniformniZnakoviUlaznogNiza;

	public ParserTabliceUniformnihZnakova(String put) {
		uniformniZnakoviUlaznogNiza = new ArrayList<>();
		List<String> sveLinije = null;

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(put)))) {
			// definicija liste u koju skupljam linije
			Supplier<List<String>> definicija = new Supplier<List<String>>() {
				@Override
				public List<String> get() {
					return new ArrayList<String>();
				}
			};
			// skupljanje u jednu listu
			sveLinije = br.lines().collect(Collectors.toCollection(definicija));
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		String[] splittaniZapis;
		for (String zapis : sveLinije) {
			splittaniZapis = zapis.split(" ");
			uniformniZnakoviUlaznogNiza.add(new ZapisTabliceUniformnihZnakova(splittaniZapis[0],
					Integer.parseInt(splittaniZapis[1]), zapis.substring(splittaniZapis[0].length() +
							splittaniZapis[1].length() + 2)));
		}
		
		//dodaj oznaku kraja niza na kraj
		uniformniZnakoviUlaznogNiza.add(new ZapisTabliceUniformnihZnakova("#", -1, ""));
	}

	public List<ZapisTabliceUniformnihZnakova> getUniformniZnakoviUlaznogNiza() {
		return uniformniZnakoviUlaznogNiza;
	}

	public static class ZapisTabliceUniformnihZnakova {
		private String uniformniZnak;
		private int redak;
		private String leksickaJedinka;

		public ZapisTabliceUniformnihZnakova(String uniformniZnak, int redak, String leksickaJedinka) {
			this.uniformniZnak = uniformniZnak;
			this.redak = redak;
			this.leksickaJedinka = leksickaJedinka;
		}

		public String getUniformniZnak() {
			return uniformniZnak;
		}

		public int getRedak() {
			return redak;
		}

		public String getLeksickaJedinka() {
			return leksickaJedinka;
		}

	}
}
