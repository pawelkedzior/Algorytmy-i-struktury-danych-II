public class KrawedzWazona
{
	public Wierzcholek w0,w1;
	public double odleglosc;
	public double ograniczenie;
 
	public KrawedzWazona(Wierzcholek v0, Wierzcholek v1, int odl, int ogrpred)
	{
		w0=v0;
		w1=v1;
		odleglosc=odl;
		ograniczenie=ogrpred;
	}

	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KrawedzWazona por = (KrawedzWazona) obj;
		if (w0 == null) {
			if (por.w0 != null)
				return false;
		} else if (!w0.equals(por.w0))
			return false;
		if (w1 == null) {
			if (por.w1 != null)
				return false;
		} else if (!w1.equals(por.w1))
			return false;
		if (odleglosc != por.odleglosc)
			return false;
		if (ograniczenie != por.ograniczenie)
			return false;
		return true;
	}
	
	public String toString()
	{
		return "("+w0+", "+w1+")";
	}

	public Wierzcholek poczatek()
	{
		return w0;
	}

	public Wierzcholek koniec()
	{
		return w1;
	}

	public Wierzcholek Kumpel(Wierzcholek kolega) throws NullPointerException
	{
		if(kolega==w0)
			return w1;
		else if(kolega==w1)
			return w0;
		else
			throw new NullPointerException();
	}
}