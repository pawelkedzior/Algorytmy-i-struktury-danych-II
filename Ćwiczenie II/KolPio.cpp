#include <iostream>
using namespace std;

#include "lab3.h"

typedef Wrapper<int> Inte;

class KolejkaPriorytetowa: public virtual Container
{
  virtual void Zakolejkuj (Object&)=0;
  virtual Object& ZnajdzNajmn() const = 0;
  virtual Object& WykolejkujNajmn()=0;
};

class KopiecBinarny: public KolejkaPriorytetowa
{
private:
  Array<Object*> tablica;
public:
  KopiecBinarny(unsigned int z): tablica(z)
  {
  }

  ~KopiecBinarny()
  {
    Purge();
  }

  virtual int CompareTo (Object const& o) const
  {
		return 0;
  }

  virtual void Accept(Visitor& v) const
  {
  }

  virtual void Put(ostream& s)
  {
  }
	
  void Purge()
  {
    if(this->IsOwner())
    {
      for(unsigned int i=1;i<count+1;i++)
      {
		delete tablica[i];
      } 
    }
      count=0;
  }

  /*virtual*/ void Zakolejkuj (Object& obj)
  {
    if(count==tablica.Length())
      throw domain_error ("Kolejka priorytetowa jest pełna.");
    ++count;
    unsigned int i=count;
    while(i>1 && *tablica[i/2]>obj)
    {
      tablica[i]=tablica[i/2];
      i/=2;
    }
    tablica[i]=&obj;
  }

  virtual Object& ZnajdzNajmn() const
  {
    return *tablica[1];
  }

  virtual Object& WykolejkujNajmn()
  {
    if(count==0)
      throw domain_error ("Kolejka priorytetowa pusta");
    Object& wynik=*tablica[1];
    Object& ostatni=*tablica[count];
    --count;
    unsigned int i=1;
    while(2*i<count+1)
    {
      unsigned int dziecko=2*i;
      if(dziecko+1<count+1 && *tablica[dziecko+1] < *tablica[dziecko])
	dziecko+=1;
      if(ostatni <= *tablica[dziecko])
	break;
      tablica[i]=tablica[dziecko];
      i=dziecko;
    }
    tablica[i]=&ostatni;
    return wynik;
  }
};  

int main(void)
{
    KopiecBinarny kp(10);
	cout<<"Podaj 5 liczb całkowitych."<<endl;
	int a;
	cin>>a;
	Object* o=new Inte(a);
	kp.Zakolejkuj(*o);
	cin>>a;
	o=new Inte(a);
	kp.Zakolejkuj(*o);
	cin>>a;
	o=new Inte(a);
	kp.Zakolejkuj(*o);
	cin>>a;
	o=new Inte(a);
	kp.Zakolejkuj(*o);
	cin>>a;
	o=new Inte(a);
	kp.Zakolejkuj(*o);
	cout<<"Teraz twoje elementy zostaną wypisane na ekran."<<endl;
	cout<<kp.WykolejkujNajmn()<<", ";
	cout<<kp.WykolejkujNajmn()<<", ";
	cout<<kp.WykolejkujNajmn()<<", ";
	cout<<kp.WykolejkujNajmn()<<", ";
	cout<<kp.WykolejkujNajmn()<<"."<<endl;
}
