#include <iostream>
#include <cmath>

using namespace std;

class Punkt
{
private:
  double punktodniesieniax;
  double punktodniesieniay;
  double r,fi;
public:
  double x,y;
  Punkt(): x(0.0), y(0.0)
  {
  	punktodniesieniax=0;
  	punktodniesieniay=0;
  }
  
  Punkt(double a, double b): x(a), y(b)
  {
  		r=sqrt((x-punktodniesieniax)*(x-punktodniesieniax)+(y-punktodniesieniay)*(y-punktodniesieniay));
  		fi=acos((x-punktodniesieniax)/r);
		if((y-punktodniesieniay)<0)
			fi=fi*(-1)+2*3.14159265;
  }

  void ustaw00(Punkt& p)
  {
  		punktodniesieniax=p.x;
  		punktodniesieniay=p.y;
  		r=sqrt((x-punktodniesieniax)*(x-punktodniesieniax)+(y-punktodniesieniay)*(y-punktodniesieniay));
  		fi=acos((x-punktodniesieniax)/r);
		if((y-punktodniesieniay)<0)
			fi=fi*(-1)+2*3.14159265;
  }
  
  double R()
  {
  		return r;
  }
  
  double FI()
  {
  		return fi;
  }

  bool operator== (const Punkt &p)
  {
    return (p.x==x)&&(p.y==y);
  }
  
  bool operator!= (const Punkt &p)
  {
    return !((p.x==x)&&(p.y==y));
  }

  bool operator> (const Punkt &p)
  {
  	if(fi!=p.fi)
		return fi>p.fi;
	else
		return r>p.r;			
  }

  bool operator< (const Punkt &p)
  {
  	if(fi!=p.fi)
		return fi<p.fi;
	else
		return r<p.r;
  }
};
/*
-------------------------------------------------
*/
class Odcinek
{
public:
  Punkt p;
  Punkt q;
  Odcinek(Punkt a, Punkt b): p(a), q(b)
  {
  }
  Odcinek(double ax, double ay, double bx, double by)
  {
    Punkt a(ax, ay);
    Punkt b(bx, by);
    p=a;
    q=b;
  }
};
/*
-------------------------------------------------
*/
double wyznacznik(Punkt p, Punkt q, Punkt r)
{
  return (p.x*q.y+p.y*r.x+q.x*r.y-q.y*r.x-p.y*q.x-p.x*r.y);
}

bool wewnatrz(Odcinek ab, Punkt c)
{
  if(wyznacznik(ab.p,ab.q,c)==0)
    if((ab.p.x<c.x&&ab.p.y<c.y&&ab.q.x>c.x&&ab.q.y>c.y)||(ab.q.x<c.x&&ab.q.y<c.y&&ab.p.x>c.x&&ab.p.y>c.y))
      return true;
  return false;
}

bool przecinanie(Odcinek ab, Odcinek cd)
{
  if(wewnatrz(ab,cd.p)||wewnatrz(ab,cd.q))
  {
    return true;
  }
  else
  {
    if(((wyznacznik(ab.p,ab.q,cd.p)>0)&&(wyznacznik(ab.p,ab.q,cd.q)<0))||((wyznacznik(ab.p,ab.q,cd.p)<0)&&(wyznacznik(ab.p,ab.q,cd.q)>0)))
      if(((wyznacznik(cd.p,cd.q,ab.p)>0)&&(wyznacznik(cd.p,cd.q,ab.q)<0))||((wyznacznik(cd.p,cd.q,ab.p)<0)&&(wyznacznik(cd.p,cd.q,ab.q)>0)))
	return true;
    return false;
  }
}

bool przecinanie(Punkt a, Punkt b, Punkt c, Punkt d)
{
  Odcinek ab(a,b);
  Odcinek cd(c,d);
  return przecinanie(ab,cd);
}
/*
-------------------------------------------------
*/
class Stos{
public:
  const int maxlength;
  Punkt *s;
  int TOP;
  Stos(int i): maxlength(i){
    s=new Punkt[i];
    TOP=-1;
  }
  
  ~Stos()
  {
    delete[] s;
  }

  Punkt& POP(){
    TOP--;
    return s[TOP+1];
  }

  void PUSH(Punkt &x){
    if(TOP<maxlength){
      TOP++;
      s[TOP]=x;
    }
  }

  Punkt& TOPELEM(){
    return s[TOP];
  }

  Punkt& NEXTTOTOP(){
    return s[TOP-1];
  }

  void MAKENULL(){
    TOP=-1;
  }
  bool EMPTY(){
    if(TOP==-1)
      return true;
    else
      return false;
  }
};
/*
-------------------------------------------------
*/
class Zbior
{
public:
  typedef struct Lista
  {
    Punkt punkt;
    Lista *nast;
  }Lista;

  Lista lista;
  int lelem;
  Zbior()
  {
    lista.punkt=Punkt();
    lista.nast=NULL;
    lelem=0;
  }

  void dodajPunkt(Punkt p)
  {
  	 Lista* tymcz=&lista;
    while(tymcz->nast!=NULL)
    {
	 	if(tymcz->nast->punkt==p)
			return;
		tymcz=tymcz->nast;	
    }
    Lista *nowy=new Lista;
    nowy->punkt=p;
    nowy->nast=lista.nast;
    lista.nast=nowy;
    lelem++;
  }  

  Lista* nalezy(Punkt p)
  {
    Lista *tymcz=&lista;
    if(tymcz->nast->punkt!=p)
    	while(tymcz!=NULL)
    	{
			tymcz=tymcz->nast;
      	if(tymcz->nast->punkt==p)
				break;
   	}
    return tymcz;
  }

  void usunPunkt(Punkt p)
  {
    Lista *l=nalezy(p);
    if(l!=NULL)
    {
      Lista *tymcz=l->nast;
      l->nast=(tymcz->nast);
      delete tymcz;
      lelem--;
    }
  }

  Punkt& najmniejszy()
  {
  		Lista *tymcz=lista.nast;
		while(tymcz->nast!=NULL)
		{
			if((tymcz->nast->punkt.y<lista.nast->punkt.y)||
			(tymcz->nast->punkt.y==lista.nast->punkt.y&&tymcz->nast->punkt.x<lista.nast->punkt.x))
			{
				Lista* zamien=tymcz->nast;
				tymcz->nast=tymcz->nast->nast;
				zamien->nast=lista.nast;
				lista.nast=zamien;
			}
			else
				tymcz=tymcz->nast;
		}
		return lista.nast->punkt;
  }

  void sortujmal(Punkt& p)
  {
		if(lista.nast!=NULL)
  		{
  			if(lista.nast->punkt!=p)
  			{
  				Lista* podstawa=nalezy(p);
  				Lista* przestaw=podstawa->nast;
  				podstawa->nast=podstawa->nast->nast;
  				przestaw->nast=lista.nast;
  				lista.nast=przestaw;
  			}
			Lista *tymcz=lista.nast;
			tymcz->punkt.ustaw00(lista.nast->punkt);
			while(tymcz->nast!=NULL)
			{
				tymcz->nast->punkt.ustaw00(lista.nast->punkt);				
				tymcz=tymcz->nast;
			}
  			Lista *ostatni=NULL;
		  	while(lista.nast->nast!=ostatni)
  			{
				tymcz=lista.nast;
				while(tymcz->nast->nast!=ostatni)
				{
					if(tymcz->nast->punkt<tymcz->nast->nast->punkt)
					{
			    		Lista *zmiana=tymcz->nast;	    	
				    	tymcz->nast=tymcz->nast->nast;	    	
				    	zmiana->nast=tymcz->nast->nast;
			   	 	tymcz->nast->nast=zmiana;			    	 
					}
					tymcz=tymcz->nast;
				}
				ostatni=tymcz->nast;
   		}
		}
  }
  
  void sortujros(Punkt& p)
  {
		if(lista.nast!=NULL)
  		{
  			if(lista.nast->punkt!=p)
  			{
  				Lista* podstawa=nalezy(p);
  				Lista* przestaw=podstawa->nast;
  				podstawa->nast=podstawa->nast->nast;
  				przestaw->nast=lista.nast;
  				lista.nast=przestaw;
  			}
			Lista *tymcz=lista.nast;
			tymcz->punkt.ustaw00(lista.nast->punkt);
			while(tymcz->nast!=NULL)
			{
				tymcz->nast->punkt.ustaw00(lista.nast->punkt);				
				tymcz=tymcz->nast;
			}
  			Lista *ostatni=NULL;
		  	while(lista.nast->nast!=ostatni)
  			{
				tymcz=lista.nast;
				while(tymcz->nast->nast!=ostatni)
				{
					if(tymcz->nast->punkt>tymcz->nast->nast->punkt)
					{
			    		Lista *zmiana=tymcz->nast;	    	
				    	tymcz->nast=tymcz->nast->nast;	    	
				    	zmiana->nast=tymcz->nast->nast;
			   	 	tymcz->nast->nast=zmiana;			    	 
					}
					tymcz=tymcz->nast;
				}
				ostatni=tymcz->nast;
   		}
		}
  }

  Stos& Graham()
  {
    Stos *stos=new Stos(lelem);
    sortujmal(najmniejszy());
    Lista *tymcz=lista.nast;
    int n=0;
    for(int i=0;i<lelem;i++)
    {
    	if(n<3)
    	{
    		if((n<2)||(tymcz->punkt.FI()!=stos->TOPELEM().FI()))
    		{
    			stos->PUSH(tymcz->punkt);
    			n++;
    		}
    	}
    	else
    	{
    		if(tymcz->punkt.FI()!=stos->TOPELEM().FI())
    		{
	    		while(wyznacznik(stos->TOPELEM(),stos->NEXTTOTOP(),tymcz->punkt)<0)
   	 		{
    				stos->POP();
    			}
    			stos->PUSH(tymcz->punkt);
    		}
    	}	
    	tymcz=tymcz->nast;
 	 }
    return *stos;
  }
  
  Stos& Jarvis2D()
  {
  	Stos *stos=new Stos(lelem);
  	najmniejszy();
  	Lista *N=lista.nast;
  	Lista *pocz=lista.nast;
  	int i=2;
  	do
  	{
  		stos->PUSH(N->punkt);
  		sortujros(stos->TOPELEM());
  		if(i==2)
  		{
  			N=lista.nast->nast;
  			i++;
  		}
  		else
  		{
  			Lista *kandydat=lista.nast->nast;
  			while((wyznacznik(pocz->punkt,stos->TOPELEM(),kandydat->punkt)+0.000000000000001)<0)
  			{
  				kandydat=kandydat->nast;
  			}
  			N=kandydat;
  		}
  	}
  	while(N!=pocz);
  	return *stos;
  }
};
/*
-------------------------------------------------
*/
int main(void)
{
  Zbior z;
  /*for(int i=0;i<3;i++)
  {
  		double a;
  		double b;
  		cout<<"Podaj dwie współrzędne punktów."<<endl;
  		cin>>a;
  		cin>>b;
  		z.dodajPunkt(*(new Punkt(a,b)));
  }*/
  z.dodajPunkt(*new Punkt(5.6,5.0));
  z.dodajPunkt(*new Punkt(1.7,5.8));
  z.dodajPunkt(*new Punkt(8.2,6.3));
  z.dodajPunkt(*new Punkt(6.3,-1.2));
  z.dodajPunkt(*new Punkt(2.0,-3.6));
  z.dodajPunkt(*new Punkt(1.7,5.0));
  z.dodajPunkt(*new Punkt(1.0,2.3));
  z.dodajPunkt(*new Punkt(6.6,2.7));
  z.dodajPunkt(*new Punkt(5.0,-1.3));
  z.dodajPunkt(*new Punkt(5.0,2.7));
  Stos &s=z.Jarvis2D();
  while(!s.EMPTY())
  {
  		Punkt p=s.POP();
  		cout<<"("<<p.x<<","<<p.y<<")"<<endl;
  }
  return 0;
}
