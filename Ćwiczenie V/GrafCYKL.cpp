using namespace std;

#include <iostream>
#include "lab5.h"

class Wierzcholek: public Object
{
public:
  typedef unsigned int Numer;
  Numer numer;
  Wierzcholek(Numer n)
  {
    numer=n;
  }
  
  virtual int CompareTo(Object const &o) const
  {
  	  Wierzcholek const &por=dynamic_cast<Wierzcholek const&>(o);
     return numer-por.numer;
  }

  virtual void Put(ostream& s) const
  {
  	  s<<numer;
  }
  
  operator Numer const& () const
  {
    return numer;
  }
};

class Krawedz: public Object
{
public:
  Wierzcholek &w0,&w1;
  
  Krawedz(Wierzcholek& v0, Wierzcholek& v1): w0(v0), w1(v1)
  {
  }

	virtual int CompareTo (Object const& o) const
	{
		Krawedz const &por=dynamic_cast<Krawedz const&>(o);
		if(W0()==por.W0()&&W1()==por.W1())
			return 0;
		else if(W0()!=por.W0())
			return -1;
		else
			return 1;
	}
	
	virtual void Put(ostream& s) const
  	{
  		s<<"("<<w0<<","<<w1<<")";
  	}

  virtual Wierzcholek& W0() const
  {
    return w0;
  }

  virtual Wierzcholek& W1() const
  {
    return w1;
  }

  virtual Wierzcholek& Kumpel(Wierzcholek& kolega)
  {
    if(kolega==w0)
      return w1;
    else if(kolega==w1)
      return w0;
    else
      throw invalid_argument ("Ten wierzcholek nie należy do tej krawędzi.");
  }
};
/*
----Counting Visitor--------------------------------------------
*/
class LiczacyGoscEtatowy: public Visitor
{
public:
	unsigned int licz;
	LiczacyGoscEtatowy():licz(0)
	{
	}
	
	unsigned int Licz()
	{
		return licz;
	} 
	
	virtual void Visit(Object& o)
	{
		licz++;
	}	
};
/*
----------------------------------------------------------------
*/
class Graf: public Container
{
protected:
  unsigned int liczbaWierzcholkow;
  unsigned int liczbaKrawedzi;
public:
  virtual unsigned int LiczbaKrawedzi() const
  {
    return liczbaKrawedzi;
  }

  virtual unsigned int LiczbaWierzcholkow() const
  {
    return liczbaWierzcholkow;
  }

  virtual void DodajWierzcholek(Wierzcholek&)=0;

  virtual Wierzcholek& WybierzWierzcholek (Wierzcholek::Numer) const=0;

  virtual Wierzcholek& operator [] (Wierzcholek::Numer n) const
  {
    return WybierzWierzcholek(n);
  }

  virtual void DodajKrawedz(Krawedz&)=0;

  virtual Krawedz& WybierzKrawedz (Wierzcholek::Numer, Wierzcholek::Numer) const=0;

  virtual bool JestKrawedzia (Wierzcholek::Numer, Wierzcholek::Numer) const=0;

  virtual bool JestSpojny () const
  {
  	 Visitor& g=*(new LiczacyGoscEtatowy());
    PrePostVisitor& gosc=*(new InOrder(g));
    Array<bool>& sprawdzenie=*(new Array<bool>(liczbaWierzcholkow));
    Iterator& it=Wierzcholki();
    PrzeszukiwanieWglab(gosc, static_cast<Wierzcholek&>(*it), sprawdzenie);
    return (static_cast<LiczacyGoscEtatowy&>(g)).Licz()==liczbaWierzcholkow;  
  }

  virtual bool JestCykliczny () const
  {
    return true;
  }

  virtual Iterator& Wierzcholki () const=0;
  
  virtual Iterator& Krawedzie () const=0;

  virtual Iterator& KrawedzieWchodzace (Wierzcholek const&) const=0;

  virtual Iterator& KrawedzieWychodzace (Wierzcholek const&) const=0;
  
  void PrzeszukiwanieWglab(PrePostVisitor& gosc, Wierzcholek const &poczatek) const
  {
  		Array<bool> odwiedzone(liczbaWierzcholkow);
  		for(unsigned int w=0;w<liczbaWierzcholkow;w++)
  			odwiedzone[w]=false;
  		PrzeszukiwanieWglab(gosc, const_cast<Wierzcholek&>(poczatek), odwiedzone);
  }
  
  void PrzeszukiwanieWglab(PrePostVisitor& gosc, Wierzcholek& wierzcholek, Array<bool>& odwiedzone) const
  {
  		gosc.PreVisit(wierzcholek);
  		unsigned int i=0;
		for(Iterator& iw=Wierzcholki();!iw.IsDone();++iw)
			if(i!=(Wierzcholek::Numer) wierzcholek)
				i++;  		
  		if(!odwiedzone[i])
  		{
  			gosc.Visit(wierzcholek);
  			odwiedzone[i]=true;
  			for(Iterator& ikwych=KrawedzieWychodzace(wierzcholek);!ikwych.IsDone();++ikwych)
  				PrzeszukiwanieWglab(gosc, static_cast<Wierzcholek&>(*ikwych), odwiedzone);
  			gosc.PostVisit(wierzcholek);
  		}
  }
};

/*
----------------------------------------------------------------
*/
class IteratorKrawedziowy: public Iterator
{
private:
  Array2D<Krawedz*> const &krawedzie;
  unsigned int obecny;
  const unsigned int wymiar;

public:
  IteratorKrawedziowy(Array2D<Krawedz*> const &tablica2D, const unsigned int wym): 
  		krawedzie(tablica2D),
  		wymiar(wym)
  {
  	 obecny=0;
  	 while(krawedzie.get(obecny/wymiar,obecny%wymiar)==NULL&&obecny<(wymiar*wymiar))
      	obecny++;
  }

  virtual void Reset()
  {
    obecny=0;
  	 while(krawedzie.get(obecny/wymiar,obecny%wymiar)==NULL&&obecny<(wymiar*wymiar))
      	obecny++;
  }

  virtual bool IsDone () const
  {
    bool czy=false;
    if(obecny>=(wymiar*wymiar))
      czy=true;
    return czy;
  }

  virtual Object& operator * () const
  {
    Object& obj=*krawedzie.get(obecny/wymiar,obecny%wymiar);
    return obj;
  }

  virtual void operator ++ ()
  {
      do
      {
      	obecny++;
      	if(IsDone())
      		break;
      }
      while(krawedzie.get(obecny/wymiar,obecny%wymiar)==NULL&&obecny<(wymiar*wymiar));
  }
};

class IteratorWierzcholkowy: public Iterator
{
private:
  Array<Wierzcholek*> const &wierzcholki;
  unsigned int obecny;
  unsigned int const &lwier;

public:
  IteratorWierzcholkowy(Array<Wierzcholek*> const &tablica, unsigned int const &a): wierzcholki(tablica), obecny(0), lwier(a)
  {
  }

  virtual void Reset()
  {
    obecny=0;
  }

  virtual bool IsDone () const
  {
    bool czy=false;
    if(obecny>=lwier)
      czy=true;
    return czy;
  }

  virtual Object& operator * () const
  {
    Object& obj=*wierzcholki[obecny];
    return obj;
  }

  virtual void operator ++ ()
  {
    if(!IsDone())
      obecny++;
  }
};

class IteratorKrawWych: public Iterator
{
private:
  Array2D<Krawedz*> krawedzie;
  unsigned int obecna;
  const unsigned int wymiar;
  const unsigned int rzad;

public:
  IteratorKrawWych(Array2D<Krawedz*> const &tablica, Wierzcholek::Numer n, unsigned int wym): krawedzie(tablica), wymiar(wym), rzad(n)
  {
  		obecna=n*wym;
  		while(obecna<(wymiar*rzad+wymiar)&&(krawedzie.get(rzad, obecna%wymiar)==NULL))
      	obecna++;
  }

  virtual void Reset()
  {
    obecna=rzad*wymiar;
    while(obecna<(wymiar*rzad+wymiar)&&(krawedzie.get(rzad, obecna%wymiar)==NULL))
      	obecna++;
  }

  virtual bool IsDone () const
  {
    bool czy=false;
    if(obecna>=(wymiar*(rzad+1)))
      czy=true;
    return czy;
  }

  virtual Object& operator * () const
  {
    Object& obj=(*krawedzie.get(rzad,obecna%wymiar)).W1();
    return obj;
  }

  virtual void operator ++ ()
  {
   do
   {
     	obecna++;
     	if(IsDone())
     		break;
   }
   while(obecna<(wymiar*rzad+wymiar)&&(krawedzie.get(rzad, obecna%wymiar)==NULL));
  }
};

class IteratorKrawWch: public Iterator
{
private:
  Array2D<Krawedz*> krawedzie;
  unsigned int obecna;
  unsigned int wymiar;

public:
  IteratorKrawWch(const Array2D<Krawedz*> tablica, Wierzcholek::Numer n, unsigned int wym): krawedzie(tablica), obecna(n), wymiar(wym)
  {
  	while((obecna<(wymiar*wymiar))&&(krawedzie.get(obecna/wymiar, obecna%wymiar)==NULL))
      	obecna=obecna+wymiar;
  }

  virtual void Reset()
  {
    obecna=obecna%wymiar;
    while((obecna<(wymiar*wymiar))&&(krawedzie.get(obecna/wymiar, obecna%wymiar)==NULL))
      	obecna=obecna+wymiar;
  }

  virtual bool IsDone () const
  {
    bool czy=false;
    if(obecna>=(wymiar*wymiar))
      czy=true;
    return czy;
  }

  virtual Object& operator * () const
  {
    Object& obj=(*krawedzie.get(obecna/wymiar,obecna%wymiar)).W0();
    return obj;
  }

  virtual void operator ++ ()
  {
    	do
		{
			obecna=obecna+wymiar;
			if(IsDone())
				break;	
		}    
		while((obecna<(wymiar*wymiar))&&(krawedzie.get(obecna/wymiar, obecna%wymiar)==NULL));
  }
};
/*
---------------------------------------------------------------
*/
class GrafJakoMacierz: public virtual Graf
{
protected:
  Array<Wierzcholek*> tablica;
  Array2D<Krawedz*> tablica2D;
public:
  GrafJakoMacierz(unsigned int n): tablica(n), tablica2D(n,n)
  {
  		liczbaWierzcholkow=0;
  		liczbaKrawedzi=0;
  }

	virtual int CompareTo (Object const&) const
	{
		return 0;
	}
	
	virtual void Purge ()
	{
	}

	virtual void Accept (Visitor&) const
	{
	}

  virtual void DodajWierzcholek(Wierzcholek& w)
  {
  	 try
  	 {
  	 	WybierzWierzcholek(w);
  	 }
  	 catch(out_of_range o)
  	 {
    	Wierzcholek *wi=new Wierzcholek(w);
    	if(tablica.Length()!=liczbaWierzcholkow)
    	{
      	tablica[liczbaWierzcholkow]=wi;
      	liczbaWierzcholkow++;
    	}
    	else
    	{
    		cout<<"Graf jest przepełniony wierzchołkami."<<endl;
    	}
    }
  }

	virtual Wierzcholek& WybierzWierzcholek(Wierzcholek::Numer n) const
	{
		for(unsigned int i=0;i<liczbaWierzcholkow;i++)
		{
			if (*tablica[i]==n)
			{
				Wierzcholek& wierch=*tablica[i];
				return wierch;
			}
		}
		throw out_of_range ("Ten wierzcholek nie należy do grafu");
	}
	
	virtual void DodajKrawedz(Krawedz& k)
	{
		try
  	 	{
  	 		WybierzWierzcholek(k.W0());
  	 		WybierzWierzcholek(k.W1());
  	 	}
  	 	catch(out_of_range o)
  	 	{
  	 		cout<<"Przynajmniej jeden z wierzchołków nie należy do grafu."<<endl;
  	 	}
  	 	try
  	 	{
  	 		WybierzKrawedz(k.W0(),k.W1());
  	 		WybierzKrawedz(k.W1(),k.W0());
  	 	}
  	 	catch(out_of_range o)
  	 	{
	    	Krawedz *kr=&k;
      	tablica2D[k.W0()][k.W1()]=kr;
      	kr=new Krawedz(k.W1(),k.W0());
      	tablica2D[k.W1()][k.W0()]=kr;
      	liczbaKrawedzi++;
    	}
	}

	virtual Krawedz& WybierzKrawedz (Wierzcholek::Numer n0, Wierzcholek::Numer n1) const
	{
		try
  	 	{
  	 		WybierzWierzcholek(n0);
  	 		WybierzWierzcholek(n1);
  	 	}
  	 	catch(out_of_range o)
  	 	{
  	 		throw out_of_range("Przynajmniej jeden z wierzchołków nie należy do grafu.");
  	 	}
  	 	if(JestKrawedzia (n0,n1))
  	 	{
  	 		Krawedz& kraw=*(tablica2D.get(n0,n1));
  	 		return kraw;
  	 	}
  	 	else
  	 		throw out_of_range ("Ta krawędź nie należy do grafu.");
	}

	virtual bool JestKrawedzia (Wierzcholek::Numer n0, Wierzcholek::Numer n1) const
	{
		try
  	 	{
  	 		WybierzWierzcholek(n0);
  	 		WybierzWierzcholek(n1);
  	 	}
  	 	catch(out_of_range o)
  	 	{
  	 		throw out_of_range("Przynajmniej jeden z wierzchołków nie należy do grafu.");
  	 	}
  	 	if(tablica2D.get(n0,n1)!=NULL)
  	 		return true;
  	 	else
  	 		return false;
	}

  virtual Iterator& Wierzcholki () const
  {
    Iterator* iw=new IteratorWierzcholkowy(tablica, liczbaWierzcholkow);
    return *iw;
  }

  virtual Iterator& Krawedzie () const
  {
    Iterator* ik=new IteratorKrawedziowy(tablica2D, tablica.Length());
    return *ik;
  }
  
  virtual Iterator& KrawedzieWchodzace (Wierzcholek const& w) const
  {
  	 Iterator* kwch=new IteratorKrawWch(tablica2D, /*static_cast<Wierzcholek::Numer>(w)*/w, tablica.Length());
    return *kwch;
  }
  
  virtual Iterator& KrawedzieWychodzace (Wierzcholek const& w) const
  {
  	 Iterator* kwych=new IteratorKrawWych(tablica2D, /*static_cast<Wierzcholek::Numer>(w)*/w, tablica.Length());
    return *kwych;
  }
};
/*
----------------------------------------------------------------
*/
class GrafSkierowany: public virtual Graf
{
public:
	virtual bool JestSpojny () const
  	{
  	 Visitor& g=*(new LiczacyGoscEtatowy());
    PrePostVisitor& gosc=*(new InOrder(g));
    Array<bool>& sprawdzenie=*(new Array<bool>(liczbaWierzcholkow));
    bool czy=true;
    for(Iterator& it=Wierzcholki();!it.IsDone();++it)
	 {    
    PrzeszukiwanieWglab(gosc, static_cast<Wierzcholek&>(*it), sprawdzenie);
    czy=czy&&((static_cast<LiczacyGoscEtatowy&>(g)).Licz()==liczbaWierzcholkow);
    if(!czy)
    	return false;
 	 }
    return czy;    
  	}
  	virtual bool JestCykliczny () const
  	{
    LiczacyGoscEtatowy gosc;
    PrzeszukiwanieTopologiczne(gosc);
    return liczbaWierzcholkow!=gosc.Licz();
  	}
  	void PrzeszukiwanieTopologiczne(Visitor& v) const
  	{
  		Array<bool> odwiedzone(liczbaWierzcholkow);
  		unsigned int nr;
  		for(nr=0;nr<liczbaWierzcholkow;nr++)
  			odwiedzone[nr]=false;
  		int poprzednie=-1;
  		bool koniec=false;
  		do
  		{
  			nr=0;
  			for(Iterator& it=Wierzcholki();!it.IsDone();++it)
  			{
	  			if(!odwiedzone[nr])
  				{
  					bool bezWchodzacych=true;
  					for(Iterator& kwch=KrawedzieWchodzace(dynamic_cast<Wierzcholek&>(*it));!kwch.IsDone();++kwch)
  					{
  						unsigned int nr2=0;
  						for(Iterator& it2=Wierzcholki();!it2.IsDone();++it2)
  							if(*it2!=*kwch)
  								nr2++;
  							else
  								break;
	  					if(!odwiedzone[nr2])
  						{
  							bezWchodzacych=false;
  							break;
  						}
  					}
  					if(bezWchodzacych)
  					{
  						v.Visit(*it);
  						odwiedzone[nr]=true;
  					}
  				}
  				nr++;
  			}
  			int obecne=0;
  			for(unsigned int i=0;i<liczbaWierzcholkow;i++)
  			{
  				if(odwiedzone[i])
  					obecne++;
  			}
  			if(obecne==0||obecne==poprzednie)
  			{
  				koniec=true;
  			}
			poprzednie=obecne;
  		}while(!koniec);
  	}
};

class GrafSkierowanyJakoMacierz: public virtual GrafJakoMacierz, public virtual GrafSkierowany
{
public:
	GrafSkierowanyJakoMacierz(unsigned int n):GrafJakoMacierz(n)
   {
   }
  
	void DodajKrawedz(Krawedz& k)
	{
		try
  	 	{
  	 		WybierzWierzcholek(k.W0());
  	 		WybierzWierzcholek(k.W1());
  	 	}
  	 	catch(out_of_range o)
  	 	{
  	 		cout<<"Przynajmniej jeden z wierzchołków nie należy do grafu."<<endl;
  	 	}
  	 	try
  	 	{
  	 		WybierzKrawedz(k.W0(),k.W1());
  	 	}
  	 	catch(out_of_range o)
  	 	{
	    	Krawedz *kr=&k;
      	tablica2D[k.W0()][k.W1()]=kr;
      	liczbaKrawedzi++;
    	}
	}
};



















int main(void)
{ 
	Array<Wierzcholek*> tabl(10);
	for (unsigned int i=0;i<10;i++)
	{
		Wierzcholek *w=new Wierzcholek(i);
		tabl[i]=w;
	}
	Array<Krawedz*> tabl2(10);
	for (unsigned int i=0;i<10;i++)
	{
		if(i!=9)
		{
			Krawedz *k=new Krawedz(*tabl[i],*tabl[i+1]);
			tabl2[i]=k;
		}
		else
		{
			Krawedz *k=new Krawedz(*tabl[i],*tabl[0]);
			tabl2[i]=k;
		}
	}
	GrafSkierowanyJakoMacierz graf(10);
	for (unsigned int i=0;i<10;i++)
		graf.DodajWierzcholek(*tabl[i]);
	Wierzcholek wierch=graf.WybierzWierzcholek(*tabl[5]);
	cout<<wierch<<endl;
	
	for (unsigned int i=0;i<10;i++)
		if(i!=5)
			graf.DodajKrawedz(*tabl2[i]);	
	if(graf.JestKrawedzia(*tabl[5],*tabl[6]))
		cout<<"Krawędź ("<<*tabl[5]<<", "<<*tabl[6]<<") należy do grafu."<<endl;
	else
		cout<<"Krawędź ("<<*tabl[5]<<", "<<*tabl[6]<<") nie należy do grafu."<<endl;
	cout<<graf[7]<<endl;
	cout<<graf.WybierzKrawedz(*tabl[1],*tabl[2])<<endl;
	cout<<endl<<"Oto lista wierzchołków"<<endl;
	for(Iterator &iw=graf.Wierzcholki();!iw.IsDone();++iw)
	{
		cout<<*iw<<", ";
	}
	cout<<"\b\b "<<endl<<"Graf ma "<<graf.LiczbaWierzcholkow()<<" wierzchołków"<<endl;
	cout<<endl<<"Oto lista krawędzi"<<endl;
	for(Iterator &ik=graf.Krawedzie();!ik.IsDone();++ik)
	{
		cout<<*ik<<", ";
	}
	cout<<"\b\b "<<endl<<"Graf ma "<<graf.LiczbaKrawedzi()<<" krawędzi"<<endl;
	if(graf.JestSpojny())
		cout<<"Graf jest spójny."<<endl;
	else
		cout<<"Graf nie jest spójny."<<endl;
	if(graf.JestCykliczny())
		cout<<"Graf jest cykliczny."<<endl;
	else
		cout<<"Graf nie jest cykliczny."<<endl;
	
	cout<<endl<<"Dodaję krawędzie (1,7), (1,4), (3,7)."<<endl;
	graf.DodajKrawedz(*(new Krawedz(*tabl[1],*tabl[7])));
	graf.DodajKrawedz(*(new Krawedz(*tabl[1],*tabl[4])));
	graf.DodajKrawedz(*(new Krawedz(*tabl[3],*tabl[7])));	
	cout<<endl<<"Oto obecna lista krawędzi"<<endl;
	for(Iterator &ik=graf.Krawedzie();!ik.IsDone();++ik)
	{
		cout<<*ik<<", ";
	}
	cout<<"\b\b "<<endl<<"Graf ma "<<graf.LiczbaKrawedzi()<<" krawędzi"<<endl;
	if(graf.JestSpojny())
		cout<<"Graf jest spójny."<<endl;
	else
		cout<<"Graf nie jest spójny."<<endl;
	if(graf.JestCykliczny())
		cout<<"Graf jest cykliczny."<<endl;
	else
		cout<<"Graf nie jest cykliczny."<<endl;	
		
	cout<<endl<<"Dodaję krawędź (5,6)"<<endl;
	graf.DodajKrawedz(*tabl2[5]);
	
	cout<<endl<<"Oto obecna lista krawędzi"<<endl;
	for(Iterator &ik=graf.Krawedzie();!ik.IsDone();++ik)
	{
		cout<<*ik<<", ";
	}
	cout<<"\b\b "<<endl<<"Graf ma "<<graf.LiczbaKrawedzi()<<" krawędzi"<<endl;
	if(graf.JestSpojny())
		cout<<"Graf jest spójny."<<endl;
	else
		cout<<"Graf nie jest spójny."<<endl;
	if(graf.JestCykliczny())
		cout<<"Graf jest cykliczny."<<endl;
	else
		cout<<"Graf nie jest cykliczny."<<endl;
  return 0;
}
