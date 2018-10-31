import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BazaDanych {
	
	public static class Miejsca
	{
		String nazwa[];
		double x[];
		double y[];
		
		public Miejsca(int i)
		{
			nazwa=new String[i];
			x=new double[i];
			y=new double[i];
		}
		
		public void noweMiejsce(int i, String naz, double polx, double poly)
		{
			nazwa[i-1]=naz;
			x[i-1]=polx;
			y[i-1]=poly;
		}
		
		public int ileMiejsc()
		{
			return x.length;
		}
		
		public String nazwa(int i)
		{
			return nazwa[i-1];
		}
		
		public double polozenieX(int i)
		{
			return x[i-1];
		}
		
		public double polozenieY(int i)
		{
			return y[i-1];
		}
	}
	
	private static double konwertujNaDouble(String konwertowany)
	{
		double przekonwertowany=0.0d;
		String podzielony[]=konwertowany.split("st");
		konwertowany=podzielony[1];
		przekonwertowany+=Double.parseDouble(podzielony[0]);
		podzielony=konwertowany.split("m");
		konwertowany=podzielony[1];
		przekonwertowany+=Double.parseDouble(podzielony[0])/60;
		podzielony=konwertowany.split("sek");
		przekonwertowany+=Double.parseDouble(podzielony[0])/3600;
		if(podzielony[1]=="S" || podzielony[1]=="W")
			przekonwertowany*=(-1.0);
		return przekonwertowany;
	}
	
	public static Object[] wczytajMape(String nazwa)
	{
		Connection pol = null;
		Grafy.GrafSkierWazony graf;
		Miejsca miejsca;
		int i=0;
		try
		{
			pol = DriverManager.getConnection("jdbc:sqlite:"+nazwa);
			Statement instrukcja = pol.createStatement();
			instrukcja.setQueryTimeout(30);
			ResultSet wyniki = instrukcja.executeQuery("SELECT * FROM wierzcholki");
			while(wyniki.next())
			{
				i++;
			}
			wyniki.close();
			instrukcja.close();
			pol.close();
		}catch(SQLException e)
		{
		}
		graf=Grafy.wezInstancje().new GrafSkierWazony(i);
		i=0;
		try
		{
			pol = DriverManager.getConnection("jdbc:sqlite:"+nazwa);
			Statement instrukcja = pol.createStatement();
			instrukcja.setQueryTimeout(30);
			ResultSet wyniki = instrukcja.executeQuery("SELECT * FROM miejsca");
			while(wyniki.next())
			{
				i++;
			}
			wyniki.close();
			instrukcja.close();
			pol.close();
		}catch(SQLException e)
		{
		}
		miejsca=new Miejsca(i);
		try
		{
			pol = DriverManager.getConnection("jdbc:sqlite:"+nazwa);
			Statement instrukcja = pol.createStatement();
			instrukcja.setQueryTimeout(30);
			String pn=null,pd=null,ws=null,za=null;
			double pN,pD,wS,zA;
			ResultSet wyniki= instrukcja.executeQuery("SELECT * FROM granice");
			while(wyniki.next())
			{
				switch(wyniki.getString("Kierunek"))
				{
					case "PN":
						pn=wyniki.getString("Wymiar");
					break;
					case "PD":
						pd=wyniki.getString("Wymiar");
					break;
					case "ZA":
						za=wyniki.getString("Wymiar");
					break;
					case "WS":
						ws=wyniki.getString("Wymiar");
					break;
				}
			}
			wyniki.close();
			pN=konwertujNaDouble(pn);
			pD=konwertujNaDouble(pd);
			zA=konwertujNaDouble(za);
			wS=konwertujNaDouble(ws);
			wyniki = instrukcja.executeQuery("SELECT * FROM wierzcholki");
			while(wyniki.next())
			{
				Wierzcholek w=new Wierzcholek(wyniki.getInt("Lp"),
						((konwertujNaDouble(wyniki.getString("Dlugosc"))-zA)/(wS-zA)),
						1.0-((konwertujNaDouble(wyniki.getString("Szerokosc"))-pD)/(pN-pD)));
				graf.DodajWierzcholek(w);
			}
			wyniki.close();
			wyniki = instrukcja.executeQuery("SELECT * FROM krawedzie");
			while(wyniki.next())
			{
				KrawedzWazona k=new KrawedzWazona(graf.WybierzWierzcholek(wyniki.getInt("Poczatek")),
						graf.WybierzWierzcholek(wyniki.getInt("Koniec")),
						wyniki.getInt("Odleglosc"),wyniki.getInt("Ograniczenie"));
				graf.DodajKrawedz(k);
				k=new KrawedzWazona(graf.WybierzWierzcholek(wyniki.getInt("Koniec")),
						graf.WybierzWierzcholek(wyniki.getInt("Poczatek")),
						wyniki.getInt("Odleglosc"),wyniki.getInt("Ograniczenie"));
				graf.DodajKrawedz(k);
			}
			wyniki.close();
			wyniki = instrukcja.executeQuery("SELECT * FROM miejsca");
			i=1;
			while(wyniki.next())
			{
				miejsca.noweMiejsce(i, wyniki.getString("Nazwa"), 
						((konwertujNaDouble(wyniki.getString("Dlugosc"))-zA)/(wS-zA)), 
						1.0-((konwertujNaDouble(wyniki.getString("Szerokosc"))-pD)/(pN-pD)));
				i++;
			}
			wyniki.close();
			instrukcja.close();
			pol.close();
		}catch(SQLException e)
		{
		}
		Object []zwrot=new Object[2];
		zwrot[0]=graf;
		zwrot[1]=miejsca;
		return zwrot;
	}
	
	public static void tworzMape(File bazaDanych[], String nazwa)
	{
		Connection pol = null;
		try
		{
			pol = DriverManager.getConnection("jdbc:sqlite:"+nazwa+".db");
			Statement instrukcja = pol.createStatement();
			instrukcja.setQueryTimeout(30);
			instrukcja.executeUpdate("CREATE TABLE wierzcholki(Lp INT, Szerokosc TEXT, Dlugosc TEXT)");
			instrukcja.executeUpdate("CREATE TABLE krawedzie(Poczatek INT, Koniec INT, Odleglosc INT, Ograniczenie INT)");
			instrukcja.executeUpdate("CREATE TABLE miejsca(Nazwa TEXT, Szerokosc TEXT, Dlugosc TEXT)");
			instrukcja.executeUpdate("CREATE TABLE granice(Kierunek CHAR(2), Wymiar TEXT)");
			
			BufferedReader czytacz=new BufferedReader(new FileReader(bazaDanych[0]));
			czytacz.readLine();
			String linia=czytacz.readLine();
			do
			{
				String []podział=new String[3];
				podział=linia.split(";",3);
				instrukcja.executeUpdate("INSERT INTO wierzcholki VALUES ("+podział[0]+", '"+podział[1]+"', '"+podział[2]+"')");
			    linia = czytacz.readLine();
			}while(linia!=null);
			czytacz.close();
			czytacz=new BufferedReader(new FileReader(bazaDanych[1]));
			czytacz.readLine();
			linia=czytacz.readLine();
			do
			{
				String []podział=new String[4];
				podział=linia.split(";",4);
				instrukcja.executeUpdate("INSERT INTO krawedzie VALUES ("+podział[0]+", "+podział[1]+", "+podział[2]+", "+podział[3]+")");
			    linia = czytacz.readLine();
			}while(linia!=null);
			czytacz.close();
			czytacz=new BufferedReader(new FileReader(bazaDanych[2]));
			czytacz.readLine();
			linia=czytacz.readLine();
			do
			{
				String []podział=new String[3];
				podział=linia.split(";",3);
				instrukcja.executeUpdate("INSERT INTO miejsca VALUES ('"+podział[0]+"', '"+podział[1]+"', '"+podział[2]+"')");
			    linia = czytacz.readLine();
			}while(linia!=null);
			czytacz.close();
			czytacz=new BufferedReader(new FileReader(bazaDanych[3]));
			czytacz.readLine();
			linia=czytacz.readLine();
			do
			{
				String []podział=new String[2];
				podział=linia.split(";",2);
				instrukcja.executeUpdate("INSERT INTO granice VALUES ('"+podział[0]+"', '"+podział[1]+"')");
			    linia = czytacz.readLine();
			}while(linia!=null);
			czytacz.close();
			instrukcja.close();
			pol.close();
		}catch(SQLException e)
		{
			e.printStackTrace();
		}catch(FileNotFoundException e)
		{			
		}catch(IOException e)
		{
		}
	}
}
