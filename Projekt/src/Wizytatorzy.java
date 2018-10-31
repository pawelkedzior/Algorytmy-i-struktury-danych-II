public class Wizytatorzy {
	
	private static Wizytatorzy instancja=new Wizytatorzy();
	
	private Wizytatorzy()
	{
	}
	
	public static Wizytatorzy wezInstancje()
	{
		return instancja;
	}
	
	public interface Wizytator
	{
		void Odwiedz(Object o);
		boolean toKoniec();
	}
	
	public abstract class WizytatorPrzedWPo implements Wizytator
	{
		public abstract void Zapowiedz(Object o);
		public abstract void Poprawiny(Object o);
		public boolean toKoniec()
		{
			return false;
		}
	}
	
	public class NormalnyGosc extends WizytatorPrzedWPo
	{
		Wizytator wizytator;
		
		public NormalnyGosc(Wizytator w)
		{
			wizytator=w;
		}
		
		public void ustawWizytatora(Wizytator w)
		{
			wizytator=w;
		}
		
		public void Odwiedz(Object o) {
			wizytator.Odwiedz(o);
		}

		public void Zapowiedz(Object o) {			
		}

		public void Poprawiny(Object o) {			
		}
	}
	
	public class LiczacyGoscEtatowy implements Wizytator
	{
		private int licz;
		public LiczacyGoscEtatowy()
		{
			licz=new Integer(0);
		}
		
		public int Licz()
		{
			return licz;
		} 
		
		public void Odwiedz(Object o)
		{
			licz++;
		}

		public boolean toKoniec() {
			return false;
		}	
	}
}

