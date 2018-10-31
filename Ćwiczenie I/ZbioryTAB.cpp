using namespace std;

#include <iostream>
#include "lab2.h"

typedef Wrapper<unsigned int> Element;

class Zbior: public virtual SearchableContainer
{
protected:
  unsigned int rozmiarWszechswiata;
public:
  Zbior()
	{
		rozmiarWszechswiata=0;
	}
  Zbior(unsigned int z)
  {
    rozmiarWszechswiata=z;
  }
};

class ZbiorJakoTablica: public Zbior
{
private:
  Array<bool> tablica;
public:
  ZbiorJakoTablica(unsigned int z):Zbior(z), tablica(z)
  {
    for(unsigned int i=0;i<z;++i)
      {
		tablica[i]=false;
      }
  }

  ZbiorJakoTablica(const ZbiorJakoTablica &zbJakoTab):
	Zbior(zbJakoTab.rozmiarWszechswiata), 
	tablica(zbJakoTab.tablica)
  {
  }

  virtual int CompareTo (Object const& o) const
  {	
	return 0;
  }

  virtual void Accept(Visitor& v) const
  {
	for(unsigned int i=0;i<rozmiarWszechswiata;i++)
	{
		Element e=i;
		if(IsMember(e))
			v.Visit(e);
	}
  }

  virtual void Put(ostream& s)
  {
	PuttingVisitor visitor (s);
  	s << typeid (*this).name () << " {";
  	Accept (visitor);
  	s << "}";
  }

  virtual Object& Find(const Object& o) const
  {
	return const_cast<Object&>(o);
  }

  virtual void Purge()
  {
  }

  virtual void Insert(Object& o)
  {
	unsigned int const z=dynamic_cast<Element&>(o);
    if(!IsMember(o))
      tablica[z]=true;
  }

  virtual bool IsMember(const Object& o) const
  {
	unsigned int const z=dynamic_cast<Element const&>(o);
    return tablica[z];   
  }

  virtual void Withdraw(Object& o)
  {
	unsigned int const z=dynamic_cast<Element&>(o);
    if(IsMember(o))
      tablica[z]=false;
  }

  ZbiorJakoTablica operator + (ZbiorJakoTablica const& zbJakT)
  { 
	ZbiorJakoTablica wynik (zbJakT.rozmiarWszechswiata);
    if(this->tablica.Length()==zbJakT.tablica.Length())
      {
		for (unsigned int i=0;i<zbJakT.tablica.Length();i++)
	  	{
		    wynik.tablica[i]=(this->tablica[i]) || (zbJakT.tablica[i]);
	  	}
      }
    else
      {
			throw invalid_argument("Zbiory znajdują się w innej przestrzeni.");
      }
	return wynik;
  }

  ZbiorJakoTablica operator - (ZbiorJakoTablica const& zbJakT)
	{
		ZbiorJakoTablica wynik (zbJakT.rozmiarWszechswiata);
	    if(this->tablica.Length()==zbJakT.tablica.Length())
	      {
			for (unsigned int i=0;i<zbJakT.tablica.Length();i++)
		  	{
			    wynik.tablica[i]=(this->tablica[i]) && (!zbJakT.tablica[i]);
		  	}
	      }
	    else
	      {
			throw invalid_argument("Zbiory znajdują się w innej przestrzeni.");
	      }
		return wynik;
	}
  
  ZbiorJakoTablica operator * (ZbiorJakoTablica const& zbJakT)
	{
		ZbiorJakoTablica wynik (zbJakT.rozmiarWszechswiata);
	    if(this->tablica.Length()==zbJakT.tablica.Length())
    	  {
			for (unsigned int i=0;i<zbJakT.tablica.Length();i++)
		  	{
			    wynik.tablica[i]=(this->tablica[i]) && (zbJakT.tablica[i]);
		  	}
    	  }
    	else
    	  {
			throw invalid_argument("Zbiory znajdują się w innej przestrzeni.");
    	  }
		return wynik;
	}
};

int main(void)
{
	ZbiorJakoTablica A(10);
	ZbiorJakoTablica B(10);
	Element e;
	for (unsigned int i=0;i<6;i++)
	{
		e=i;
		A.Insert(e);
	}
	for (unsigned int i=0;i<5;i++)
	{
		e=i*2;
		B.Insert(e);
	}
	cout<<"Zbiór A:"<<endl;
	A.Put(cout);
	cout<<endl;
	cout<<"Zbiór B:"<<endl;
	B.Put(cout);
	cout<<endl<<endl;
	cout<<"Suma zbiorów A i B:"<<endl;
	(A+B).Put(cout);
	cout<<endl;
	cout<<"Różnica zbiorów A i B:"<<endl;
	(A-B).Put(cout);
	cout<<endl;
	cout<<"Przecięcie zbiorów A i B:"<<endl;
	(A*B).Put(cout);
	cout<<endl<<endl;

	e=2;
	if(A.IsMember(e))
		cout<<"2 należy do zbioru A"<<endl;
	else
		cout<<"2 nie należy do zbioru A"<<endl;
	e=4;
	B.Withdraw(e);
	cout<<"Usunięto element 4 ze zbioru B"<<endl;
	cout<<"Zbiór B:"<<endl;
	B.Put(cout);
	cout<<endl;
}
