import java.util.Enumeration;
import java.util.PriorityQueue;

public class Grafy{
	
	private static Grafy instancja=new Grafy();
	
	private Grafy()
	{
	}
	
	public static Grafy wezInstancje()
	{
		return instancja;
	}
	
	public abstract class Graf
	{
		protected int liczbaWierzcholkow;
		protected int liczbaKrawedzi;
		
		public int LiczbaKrawedzi()
		{
			return new Integer(liczbaKrawedzi);
		}
		
		public int LiczbaWierzcholkow()
		{
			return new Integer(liczbaWierzcholkow);
		}
		
		public abstract void DodajWierzcholek(Wierzcholek w);
		
		public abstract Wierzcholek WybierzWierzcholek (int w);

		public abstract void DodajKrawedz(KrawedzWazona k);

		public abstract KrawedzWazona WybierzKrawedz (Wierzcholek wier1, Wierzcholek wier2);

		public abstract boolean JestKrawedzia (Wierzcholek wier1, Wierzcholek wier2);

		public boolean JestSpojny ()
		{
			Wizytatorzy.Wizytator g=Wizytatorzy.wezInstancje().new LiczacyGoscEtatowy();
			Wizytatorzy.WizytatorPrzedWPo gosc=Wizytatorzy.wezInstancje().new NormalnyGosc(g);
			boolean[] sprawdzenie=new boolean[liczbaWierzcholkow];
			Enumeration<Wierzcholek> it=Wierzcholki();
			PrzeszukiwanieWglab(gosc, (Wierzcholek) it, sprawdzenie);
			return ((Wizytatorzy.LiczacyGoscEtatowy)g).Licz()==liczbaWierzcholkow;  
		}

		public boolean JestCykliczny ()
		{
		    return true;
		}

		public abstract Enumeration<Wierzcholek> Wierzcholki ();
		  
		public abstract Enumeration<KrawedzWazona> Krawedzie ();

		public abstract Enumeration<KrawedzWazona> KrawedzieWchodzace (Wierzcholek w);

		public abstract Enumeration<KrawedzWazona> KrawedzieWychodzace (Wierzcholek w);
		  
		public void PrzeszukiwanieWglab(Wizytatorzy.WizytatorPrzedWPo gosc, Wierzcholek poczatek)
		{
			boolean[] odwiedzone=new boolean[liczbaWierzcholkow];
			for(int w=0;w<liczbaWierzcholkow;w++)
				odwiedzone[w]=false;
			PrzeszukiwanieWglab(gosc, poczatek, odwiedzone);
		}
		  
		public void PrzeszukiwanieWglab(Wizytatorzy.WizytatorPrzedWPo gosc, Wierzcholek wierzcholek, boolean[] odwiedzone)
		{
			gosc.Zapowiedz(wierzcholek);
			int i=0;
			for(Enumeration<Wierzcholek> iw=Wierzcholki();iw.hasMoreElements();iw.nextElement())
				if(i!=((Wierzcholek)iw).numer)
					i++;  		
			if(!odwiedzone[i])
			{
				gosc.Odwiedz(wierzcholek);
				odwiedzone[i]=true;
				for(Enumeration<KrawedzWazona> ikwych=KrawedzieWychodzace(wierzcholek);ikwych.hasMoreElements();ikwych.nextElement())
					PrzeszukiwanieWglab(gosc, (Wierzcholek) ikwych, odwiedzone);
					gosc.Poprawiny(wierzcholek);
			}
		}
	}
		
	public class GrafSkierWazony extends Graf
	{
		protected Wierzcholek tablica[];
		protected KrawedzWazona tablica2D[][];
		
		public GrafSkierWazony(int wierzcholki)
		{
			tablica=new Wierzcholek[wierzcholki];
			tablica2D=new KrawedzWazona[wierzcholki+1][wierzcholki+1];
		}
		
		public void DodajWierzcholek(Wierzcholek wi) {
			try
			{
				WybierzWierzcholek(wi.numer);
			}
			catch(NullPointerException e)
			{
				if(tablica.length!=liczbaWierzcholkow)
		    	{
		      	tablica[liczbaWierzcholkow]=wi;
		      	liczbaWierzcholkow++;
		    	}
			}
		}

		public Wierzcholek WybierzWierzcholek(int w) throws NullPointerException{
			for(int i=0;i<liczbaWierzcholkow;i++)
			{
				if (tablica[i].numer==w)
				{
					Wierzcholek wierch=tablica[i];
					return wierch;
				}
			}
			throw new NullPointerException();
		}
		
		public void DodajKrawedz(KrawedzWazona kr) {
			try
	  	 	{
	  	 		WybierzWierzcholek(kr.poczatek().numer);
	  	 		WybierzWierzcholek(kr.koniec().numer);
	  	 	}
	  	 	catch(NullPointerException e)
	  	 	{
	  	 		System.out.println("Przynajmniej jeden z wierzchołków nie należy do grafu.");
	  	 	}
	  	 	try
	  	 	{
	  	 		WybierzKrawedz(kr.poczatek(),kr.koniec());
	  	 	}
	  	 	catch(NullPointerException o)
	  	 	{
		    	tablica2D[kr.poczatek().numer][kr.koniec().numer]=kr;
		    	liczbaKrawedzi++;
	    	}
		}

		public KrawedzWazona WybierzKrawedz(Wierzcholek wier1, Wierzcholek wier2) throws NullPointerException{
			try
	  	 	{
	  	 		WybierzWierzcholek(wier1.numer);
	  	 		WybierzWierzcholek(wier2.numer);
	  	 	}
	  	 	catch(NullPointerException e)
	  	 	{
	  	 		System.out.println("Przynajmniej jeden z wierzchołków nie należy do grafu.");
	  	 		throw e;
	  	 	}
	  	 	KrawedzWazona kraw=tablica2D[wier1.numer][wier2.numer];
	  	 	if(kraw==null)
	  	 		throw new NullPointerException();
	  	 	return kraw;
		}

		public boolean JestKrawedzia(Wierzcholek wier1, Wierzcholek wier2) {
			try
	  	 	{
	  	 		WybierzWierzcholek(wier1.numer);
	  	 		WybierzWierzcholek(wier2.numer);
	  	 	}
	  	 	catch(NullPointerException o)
	  	 	{
	  	 		return false;
	  	 	}
	  	 	try
	  	 	{
	  	 		WybierzKrawedz(wier1,wier2);
	  	 	}
	  	 	catch(NullPointerException e)
	  	 	{
	  	 		return false;
			}	
	  	 	return true;
		}

		public Enumeration<Wierzcholek> Wierzcholki() {
			return new Wyliczeniowe.WyliczWierzcholkowy(tablica, liczbaWierzcholkow);
		}

		public Enumeration<KrawedzWazona> Krawedzie() {
			return new Wyliczeniowe.WyliczKrawedziowy(tablica2D, tablica.length+1);
		}

		public Enumeration<KrawedzWazona> KrawedzieWchodzace(Wierzcholek w) {
			return new Wyliczeniowe.WyliczKrawWch(tablica2D, w.numer, tablica.length+1);
		}

		public Enumeration<KrawedzWazona> KrawedzieWychodzace(Wierzcholek w) {
			return new Wyliczeniowe.WyliczKrawWych(tablica2D, w.numer, tablica.length+1);
		}	
		
		public DijkstraPomoc[] Dijkstra(Graf g, Wierzcholek w, boolean odleglosc)
		{
			int n=g.LiczbaWierzcholkow();
			DijkstraPomoc[] tablice=new DijkstraPomoc[n+1];
			for(int i=0;i<n+1;i++)
				tablice[i]=new DijkstraPomoc();
			tablice[w.numer].odleglosc=0;
			PriorityQueue<Para> kolejka=new PriorityQueue<Para>(g.liczbaKrawedzi);
			kolejka.add(new Para(0,w));
			while(!kolejka.isEmpty())
			{
				Para badany=kolejka.poll();
				Wierzcholek wi=badany.wezWartosc();
				if(!tablice[wi.numer].sprawdzony)
				{
					Enumeration<KrawedzWazona> e=g.KrawedzieWychodzace(wi);
					while(e.hasMoreElements())
					{
						KrawedzWazona krawedz=e.nextElement();
						Wierzcholek cel=krawedz.koniec();
						double waga=(odleglosc?krawedz.odleglosc:
							(krawedz.odleglosc/1000.0/krawedz.ograniczenie*3600.0));
						double mozenowa=waga+tablice[wi.numer].odleglosc;
						if(tablice[cel.numer].odleglosc>mozenowa)
						{
							tablice[cel.numer].odleglosc=mozenowa;
							tablice[cel.numer].poprzednik=wi.numer;
							kolejka.add(new Para(new Double(tablice[cel.numer].odleglosc),cel));
						}
					}
					tablice[wi.numer].sprawdzony=true;
				}
			}
			return tablice;
		}
	}	
	
	public static final class DijkstraPomoc
	{
		public boolean sprawdzony=false;
		public int poprzednik=Integer.MAX_VALUE;
		public double odleglosc=Double.MAX_VALUE;
	}
	
	public static class Para implements Comparable<Para>
	{
		private double klucz;
		private Wierzcholek wartosc;

		public Para(double kl, Wierzcholek w)
		{
			klucz=kl;
			wartosc=w;
		}
		
		public double wezKlucz()
		{
			return klucz;
		}
		
		public Wierzcholek wezWartosc()
		{
			return wartosc;
		}
		
		public int compareTo(Para p) {
			return (int)(klucz-p.klucz);
		}
		
		public String toString()
		{
			return "("+klucz+", "+wartosc.numer+")";
		}
	}
}