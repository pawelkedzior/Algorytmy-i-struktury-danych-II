public class Wierzcholek implements Comparable<Wierzcholek>
{
	int numer;
	double x;
	double y;
	
	Wierzcholek(int n)
	{
		numer=n;
		x=0.0d;
		y=0.0d;
	}
	
	Wierzcholek(int n, double polx, double poly)
	{
		numer=n;
		x=polx;
		y=poly;
	}
	
	public double odleglosc(Wierzcholek w)
	{
		return Math.pow(Math.pow((w.x-x),2.0)+Math.pow((w.y-y),2.0),0.5);
	}
	
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wierzcholek por = (Wierzcholek) obj;
		if (numer != por.numer)
			return false;
		return true;
	}

	public String toString()
	{
		return Integer.toString(numer);
	}

	public int compareTo(Wierzcholek w)
	{
		return this.numer-w.numer;
	}
}