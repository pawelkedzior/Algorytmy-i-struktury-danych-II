#include <iostream>
#include <cmath>

using namespace std;

class Punkt
{
public:
  double x,y;
  Punkt(): x(0.0), y(0.0)
  {
  }
  Punkt(double a, double b): x(a), y(b)
  {
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
	Punkt tymcz(p.x-x,p.y-y);
	double r=sqrt(tymcz.x*tymcz.x+tymcz.y*tymcz.y);
	double fi=acos(tymcz.x/r);
	if(tymcz.y<0)
		fi=fi*(-1);
	if(fi>0&&fi<3.14159265)
	   return true;
	if(fi==0||fi==3.14159265)
		if(tymcz.x>0)
			return true;
	return false;
  }

  bool operator< (const Punkt &p)
  {
	Punkt tymcz(p.x-x,p.y-y);
	double r=sqrt(tymcz.x*tymcz.x+tymcz.y*tymcz.y);
	double fi=acos(tymcz.x/r);
	if(tymcz.y<0)
		fi=fi*(-1);
	if(fi<0&&fi>-3.14159265)
	   return true;
	if(fi==0||fi==-3.14159265)
		if(tymcz.x<0)
			return true;
	return false;
  }
};

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
    while(lista->nast!=NULL)
    {
	if(lista->nast->punkt==p)
		return;
    }
    Lista nowy;
    nowy.punkt=p;
    nowy.nast=lista.nast;
    lista.nast=&nowy;
    lelem++;
  }  

  Lista* nalezy(Punkt p)
  {
    Lista *tymcz=&lista;
    while(tymcz!=NULL)
    {
      if(tymcz->nast->punkt==p)
	break;
      else
	tymcz=tymcz->nast;
    }
    return tymcz;
  }

  void usunPunkt(Punkt p)
  {
    Lista *l=nalezy(p);
    if(l!=NULL)
    {
      Lista *tymcz=l;
      l=(l->nast);
      delete tymcz;
    }
    lelem--;
  }

  void sortuj()
  {
    Lista *tymcz=lista.nast;
    Lista *ostatni=NULL;
    Lista *najmn=&(tymcz->nast->punkt);
    while(lista->nast!=ostatni)
    {
	while(tymcz->nast!=ostatni)
	{
		if(tymcz->punkt>tymcz->nast->punkt)
		{
		     Lista *zmiana=tymcz->nast;
		     tymcz->nast=tymcz->nast->nast;
		}
	}
    }
  }

  Stos& Graham()
  {
    Lista *tymcz=&lista;
    int *a=new int(0);
    while(tymcz!=NULL)
    {
      *a=*a+1;
    }
    Stos S(*a);
    delete a;
    delete tymcz;
    
    Stos &s=S;
    return s;
  }
};

int main(void)
{
  
  return 0;
}
