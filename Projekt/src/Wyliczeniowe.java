import java.util.Enumeration;

public class Wyliczeniowe {

	public static class WyliczKrawedziowy implements Enumeration<KrawedzWazona>
	{
		private KrawedzWazona[][] krawedzie;
		private int obecny;
		private int wymiar;

		public WyliczKrawedziowy(KrawedzWazona tablica2D[][], int wym) 
	  	{
			krawedzie=tablica2D;
	  		wymiar=wym;
	  		obecny=0;
			while(krawedzie[obecny/wymiar][obecny%wymiar]==null&&obecny<(wymiar*wymiar))
				obecny++;
	  	}

		void Reset()
		{
			obecny=0;
			while(krawedzie[obecny/wymiar][obecny%wymiar]==null&&obecny<(wymiar*wymiar))
				obecny++;
		}

		public boolean hasMoreElements ()
		{
			return obecny<(wymiar*wymiar)-1;
		}
		
		public KrawedzWazona nextElement()
		{
			KrawedzWazona zwrot=krawedzie[obecny/wymiar][obecny%wymiar];
			do
			{
		      	obecny++;
		      	if(!hasMoreElements())
		      		break;
			}
			while(krawedzie[obecny/wymiar][obecny%wymiar]==null&&obecny<(wymiar*wymiar));
			return zwrot;
		}
	}

	public static class WyliczWierzcholkowy implements Enumeration<Wierzcholek>
	{
		private Wierzcholek[] wierzcholki;
		private int obecny;
		private int lwier;

		public WyliczWierzcholkowy(Wierzcholek[] tablica, int a)
		{
			wierzcholki=tablica;
			obecny=0;
			lwier=a;
		}

		public void Reset()
		{
			obecny=0;
		}

		public boolean hasMoreElements()
		{
			return obecny<lwier-1;
		}

		public Wierzcholek nextElement()
		{
			Wierzcholek zwrot=wierzcholki[obecny];
			if(hasMoreElements())
				obecny++;
			return zwrot;
		}
	}

	public static class WyliczKrawWych implements Enumeration<KrawedzWazona>
	{
		private KrawedzWazona[][] krawedzie;
		private int obecna;
		private int wymiar;
		private int rzad;

		public WyliczKrawWych(KrawedzWazona[][] tablica, int n, int wym)
		{
			krawedzie=tablica;
			wymiar=wym;
			rzad=n;
			obecna=n*wym;
			while(obecna<(wymiar*rzad+wymiar)&&(krawedzie[rzad][obecna%wymiar]==null))
				obecna++;
		}
		
		public void Reset()
		{
			obecna=rzad*wymiar;
			while(obecna<(wymiar*rzad+wymiar)&&(krawedzie[rzad][obecna%wymiar]==null))
				obecna++;
		}

		public boolean hasMoreElements()
		{
			return obecna<(wymiar*(rzad+1)-1);
		}
		
		public KrawedzWazona nextElement()
		{
			KrawedzWazona zwrot=krawedzie[rzad][obecna%wymiar];
			do
			{
				obecna++;
				if(!hasMoreElements())
					break;
			}
			while(obecna<(wymiar*rzad+wymiar)&&(krawedzie[rzad][obecna%wymiar]==null));
			return zwrot;
		}
	}

	public static class WyliczKrawWch implements Enumeration<KrawedzWazona>
	{
		private KrawedzWazona[][] krawedzie;
		private int obecna;
		private int wymiar;

		public WyliczKrawWch(KrawedzWazona[][] tablica, int n, int wym)
		{
			krawedzie=tablica;
			obecna=n;
			wymiar=wym;
			while((obecna<(wymiar*wymiar))&&(krawedzie[obecna/wymiar][obecna%wymiar]==null))
				obecna=obecna+wymiar;
		}

		public void Reset()
		{
			obecna=obecna%wymiar;
			while((obecna<(wymiar*wymiar))&&(krawedzie[obecna/wymiar][obecna%wymiar]==null))
				obecna=obecna+wymiar;
		}
		
		public boolean hasMoreElements()
		{
			return obecna<(wymiar*(wymiar-1));
		}
		
		public KrawedzWazona nextElement()
		{
			KrawedzWazona zwrot=krawedzie[obecna/wymiar][obecna%wymiar];
			do
			{
				obecna=obecna+wymiar;
				if(!hasMoreElements())
					break;	
			}    
			while((obecna<(wymiar*wymiar))&&(krawedzie[obecna/wymiar][obecna%wymiar]==null));
			return zwrot;
		}
	}
}
