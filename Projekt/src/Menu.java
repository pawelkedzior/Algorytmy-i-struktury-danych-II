import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Menu extends JPanel implements ActionListener, KeyListener {

	private static final long serialVersionUID = 1L;
	protected JButton przyc;
	protected PanelMapy mapa;
	protected JTextField pocz,kon;
	protected static JFrame ramka;
	protected static String sciezka="./";
	protected static String nazwa="Kobylanka.db";
	protected JTextField polesciezka, polenazwa;
	protected JFrame wczytywanie=null, tworzenie=null, ustawienia=null;
	protected JTextField tworzWierzcholki, tworzKrawedzie, tworzMiejsca, tworzGranice, tworzNazwe;
	private Grafy.GrafSkierWazony graf;
	private BazaDanych.Miejsca miejsca=null;
	private Boolean czas=false;
	private Boolean znajdz=false;
	private int miejscepocz=0, miejscekon=0;
	private int najblp=-1, najblk=-1;
	private Grafy.DijkstraPomoc []wynikiDijkstry=null;
	private JTextArea wynikowa;
	
	public class PanelMapy extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private BufferedImage mapka;
		private int szerokosc, dlugosc;
		
		public PanelMapy(LayoutManager l)
		{
			super(l);
		}
		
		public void ustawMape(BufferedImage obraz)
		{
			mapka = obraz;
			szerokosc=mapka.getWidth();
			dlugosc=mapka.getHeight();
			setPreferredSize(new Dimension(szerokosc, dlugosc));
			repaint();
		}
		
		public void paint(Graphics g) {
			int dlugpanelu=this.getHeight();
			int szerpanelu=this.getWidth();
			g.drawImage(mapka, szerokosc>szerpanelu?0:(szerpanelu-szerokosc)/2, 
					dlugosc>dlugpanelu?0:(dlugpanelu-dlugosc)/2, null);
			if(znajdz)
			{
				Graphics2D g2=(Graphics2D) g;
	    		String tekst=pocz.getText();
	    		if(!tekst.isEmpty())
	    		{
		    		if(miejscepocz!=0)
		    		{
		        		double x=(szerokosc*miejsca.polozenieX(miejscepocz))+
		        				(double) (szerokosc>szerpanelu?0:(szerpanelu-szerokosc)/2);
		        		double y=(dlugosc*miejsca.polozenieY(miejscepocz))+
		        				(double) (dlugosc>dlugpanelu?0:(dlugpanelu-dlugosc)/2);
		        		Ellipse2D.Double kolo=new Ellipse2D.Double(x-10.0, y-10.0, 20.0, 20.0);
		        		kolo.setFrame(x-10.0,y-10.0,20.0,20.0);
		        		g2.setColor(Color.GREEN);
		        		g2.fill(new Area(kolo));
		    		}
	    		}
	    		tekst=kon.getText();
	    		if(!tekst.isEmpty())
	    		{
		    		if(miejscekon!=0)
		    		{
		        		double x=(szerokosc*miejsca.polozenieX(miejscekon))+
		        				(double) (szerokosc>szerpanelu?0:(szerpanelu-szerokosc)/2);
		        		double y=(dlugosc*miejsca.polozenieY(miejscekon))+
		        				(double) (dlugosc>dlugpanelu?0:(dlugpanelu-dlugosc)/2);
		        		Ellipse2D.Double kolo=new Ellipse2D.Double(x-10.0, y-10.0, 20.0, 20.0);
		        		kolo.setFrame(x-10.0,y-10.0,20.0,20.0);
		        		g2.setColor(Color.BLUE);
		        		g2.fill(new Area(kolo));
		    		}
	    		}
	    		
	    		if(wynikiDijkstry!=null)
	    		{
	    			int obecny=najblk;
	    			Wierzcholek opracowywany=graf.WybierzWierzcholek(obecny);
	    			double x=(szerokosc*opracowywany.x)+
	        				(double) (szerokosc>szerpanelu?0:(szerpanelu-szerokosc)/2);
	        		double y=(dlugosc*opracowywany.y)+
	        				(double) (dlugosc>dlugpanelu?0:(dlugpanelu-dlugosc)/2);
	        		Ellipse2D.Double kolko=new Ellipse2D.Double(x-5.0, y-5.0, 10.0, 10.0);
	        		kolko.setFrame(x-5.0,y-5.0,10.0,10.0);
	        		g2.setColor(Color.BLACK);
	        		g2.fill(new Area(kolko));
	        		Line2D.Double linia;
	        		double starex=x;
	        		double starey=y;
	    			while(obecny!=najblp)
	    			{
		        		g2.setColor(Color.BLACK);
	    				obecny=wynikiDijkstry[obecny].poprzednik;
		    			opracowywany=graf.WybierzWierzcholek(obecny);
		    			x=(szerokosc*opracowywany.x)+
		        				(double) (szerokosc>szerpanelu?0:(szerpanelu-szerokosc)/2);
		        		y=(dlugosc*opracowywany.y)+
		        				(double) (dlugosc>dlugpanelu?0:(dlugpanelu-dlugosc)/2);
		        		kolko=new Ellipse2D.Double(x-5.0, y-5.0, 10.0, 10.0);
		        		kolko.setFrame(x-5.0,y-5.0,10.0,10.0);
		        		linia=new Line2D.Double(x, y, starex, starey);
		        		g2.draw(linia);
		        		starex=x;
		        		starey=y;
		        		g2.fill(new Area(kolko));
	    			}
	    			linia=new Line2D.Double(x, y, starex, starey);
        			g2.draw(linia);
	    		}
			}
	    }
	}
	
	public Menu()
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			GridLayout gl=new GridLayout(1, 1, 2, 2);
			mapa=new PanelMapy(gl);
			mapa.setMinimumSize(new Dimension(1200,800));
			mapa.setPreferredSize(new Dimension(1200,800));
			mapa.setBackground(Color.WHITE);
			mapa.setOpaque(true);
			JScrollPane mapaPrzewijana=new JScrollPane(mapa);
			add(mapaPrzewijana);
			add(Box.createHorizontalStrut(5));
			JSeparator sep=new JSeparator(SwingConstants.VERTICAL);
			sep.setPreferredSize(new Dimension(3,1200));
			sep.setMaximumSize(new Dimension(3,2000));
			sep.setMinimumSize(new Dimension(3,20));
			add(sep);
			add(Box.createHorizontalStrut(5));
			gl=new GridLayout(3, 1, 2, 2);
			pocz=new JTextField(30);
			pocz.addKeyListener(this);
			JLabel tekst=new JLabel("Początek trasy");
			JPanel wyszukiwarka=new JPanel(gl);
			gl=new GridLayout(10, 1, 2, 2);
			JPanel wyszukiwarkag=new JPanel(gl);
			wyszukiwarkag.add(new JLabel());
			wyszukiwarkag.add(tekst);
			wyszukiwarkag.add(pocz);
			tekst=new JLabel("Koniec trasy");
			kon=new JTextField(30);
			kon.addKeyListener(this);
			wyszukiwarkag.add(tekst);
			wyszukiwarkag.add(kon);
			JPanel panel=new JPanel(new GridLayout(1, 3, 0, 0));
			panel.add(new JLabel());
			panel.add(new JLabel());
			przyc=new JButton("Wyszukaj");
				przyc.addActionListener(this);
				przyc.setActionCommand("4");
			panel.add(przyc);
			wyszukiwarkag.add(panel);
			wynikowa=new JTextArea();
			wynikowa.setMaximumSize(new Dimension(30,10000));
			wynikowa.setEditable(false);
			wynikowa.setEnabled(false);
			wynikowa.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			wynikowa.setBackground(Color.WHITE);
			wynikowa.setOpaque(true);
			wyszukiwarka.add(wyszukiwarkag);
			wyszukiwarka.add(wynikowa,-1);
			wyszukiwarka.setSize(new Dimension(300,800));
			wyszukiwarka.setMaximumSize(new Dimension(300,800));
			wyszukiwarka.setMinimumSize(new Dimension(300,800));
			add(wyszukiwarka);
	}
	
	public JMenuBar tworzMenu(){
    	JMenuBar pasekMenu;
        JMenu plik;
        JMenuItem wiersz;
        pasekMenu = new JMenuBar();        
        
        plik = new JMenu("Plik");
        pasekMenu.add(plik);
        wiersz = new JMenuItem("Wczytaj mapę", KeyEvent.VK_M);
    		wiersz.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
    		wiersz.getAccessibleContext().setAccessibleDescription("Wczytuje mapę");
            wiersz.addActionListener(this);
            wiersz.setActionCommand("1");
    	plik.add(wiersz);	
        wiersz = new JMenuItem("Ustawienia", KeyEvent.VK_U);
        	wiersz.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        	wiersz.getAccessibleContext().setAccessibleDescription("Ustawienia programu");
            wiersz.addActionListener(this);
            wiersz.setActionCommand("2");
        plik.add(wiersz);
    	plik.addSeparator();
    	wiersz = new JMenuItem("Stwórz własną mapę", KeyEvent.VK_S);
			wiersz.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			wiersz.getAccessibleContext().setAccessibleDescription("Tworzy mapę użytkownika z wybranych plików.");
            wiersz.addActionListener(this);
            wiersz.setActionCommand("8");
		plik.add(wiersz);
    	plik.addSeparator();
    	wiersz = new JMenuItem("Wyjdź", KeyEvent.VK_W);
			wiersz.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
			wiersz.getAccessibleContext().setAccessibleDescription("Wychodzi z programu");
            wiersz.addActionListener(this);
            wiersz.setActionCommand("3");
		plik.add(wiersz);
		return pasekMenu;
    }
	
	private static void StwIPokGUI() {

        Menu menu= new Menu();
        ramka = new JFrame("Znajdź drogę");
        ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ramka.setJMenuBar(menu.tworzMenu());
        ramka.setSize(1400, 800);
        ramka.add(menu);
        ramka.setLocationRelativeTo(null);
        ramka.setVisible(true);
    }
	
	public void actionPerformed(ActionEvent wyd) {
		switch (Integer.parseInt(wyd.getActionCommand())){
	    	case 1:
	    	{
	    		if(wczytywanie==null)
	    		{
		    		wczytywanie=new JFrame("Wczytaj mapę");
		    		wczytywanie.setLayout(new BorderLayout());
		    		JPanel Panel = new JPanel(new GridLayout(4, 2));
		
		    		JLabel et;
		    		et=new JLabel("Ścieżka");
		    		Panel.add(et);   
		    		polesciezka=new JTextField(15);
		    		polesciezka.setText(sciezka);
		    		Panel.add(polesciezka);    		
		
		    		et=new JLabel("Mapa");
		    		Panel.add(et);   
		    		polenazwa=new JTextField(15);
		    		polenazwa.setText(nazwa);
		    		Panel.add(polenazwa);    
		    		  
		    		et=new JLabel("");
		    		Panel.add(et);
		    		JButton przyc;
		    		przyc=new JButton("Wybierz");
						przyc.addActionListener(this);
						przyc.setActionCommand("5");
		    		Panel.add(przyc);
		    		et=new JLabel("");
		    		Panel.add(et);
		    		wczytywanie.add(Panel, BorderLayout.NORTH);
		
		    		Panel = new JPanel(new GridLayout(1, 1)); 
		    		przyc=new JButton("Wczytaj tę mapę");
						przyc.addActionListener(this);
						przyc.setActionCommand("6");
		    		Panel.add(przyc);
		    		wczytywanie.add(Panel, BorderLayout.SOUTH);
		    		wczytywanie.pack();
		            wczytywanie.setLocationRelativeTo(ramka);
	    		}
	    		wczytywanie.setVisible(true);
	    	}
	    	break;
	    	case 2:
	    	{
	    		if(ustawienia==null)
	    		{
		    		ustawienia=new JFrame("Wczytaj mapę");
		    		ustawienia.setLayout(new BorderLayout());
		    		JPanel Panel = new JPanel(new GridLayout(1,1));
		    		JLabel et;
		    		JRadioButton wybor,wybor2;
		    		ButtonGroup grupa=new ButtonGroup();
		    		et=new JLabel("Tryb wyszukiwania");
		    		et.setHorizontalAlignment(SwingConstants.CENTER);
		    		Panel.add(et);      
		    		ustawienia.add(Panel, BorderLayout.NORTH);		
		
		    		Panel = new JPanel(new GridLayout(2, 1));
		    		wybor=new JRadioButton("Odległość");
		    		wybor.setSelected(!czas);
		    		wybor.addActionListener(this);
		    		wybor.setActionCommand("11");
		    		Panel.add(wybor);      
		    		wybor2=new JRadioButton("Czas");
		    		wybor2.setSelected(czas);
		    		wybor2.addActionListener(this);
		    		wybor2.setActionCommand("12");
		    		Panel.add(wybor2); 
		    		grupa.add(wybor); 
		    		grupa.add(wybor2);
		    		ustawienia.add(Panel, BorderLayout.CENTER);
		
		    		Panel = new JPanel(new GridLayout(1, 1)); 
		    		przyc=new JButton("Zastosuj");
						przyc.addActionListener(this);
						przyc.setActionCommand("7");
		    		Panel.add(przyc);
		    		ustawienia.add(Panel, BorderLayout.SOUTH);
		    		ustawienia.pack();
		            ustawienia.setLocationRelativeTo(ramka);
	    		}
	    		ustawienia.setVisible(true);
	    	}
	    	break;
	    	case 3:
	    	{
	    		System.exit(0);
	    	}
	    	break;
	    	case 4:
	    	{
	    		Wierzcholek wp=new Wierzcholek(190,
						miejsca.polozenieX(miejscepocz),
						miejsca.polozenieY(miejscepocz));
	    		Wierzcholek wk=new Wierzcholek(191,
						miejsca.polozenieX(miejscekon),
						miejsca.polozenieY(miejscekon));
	    		Enumeration<Wierzcholek> wylicz=graf.Wierzcholki();
	    		Wierzcholek wpor=wylicz.nextElement();
	    		double odlnajblp=wp.odleglosc(wpor);
	    		double odlnajblk=wk.odleglosc(wpor);
	    		najblp=wpor.numer;
	    		najblk=wpor.numer;
	    		while(wylicz.hasMoreElements())
	    		{
	    			wpor=wylicz.nextElement();
	    			if(odlnajblp>wp.odleglosc(wpor))
	    			{
	    				odlnajblp=wp.odleglosc(wpor);
	    				najblp=wpor.numer;
	    			}
	    			if(odlnajblk>wk.odleglosc(wpor))
	    			{
	    				odlnajblk=wk.odleglosc(wpor);
	    				najblk=wpor.numer;
	    			}
	    		}
	    		wynikiDijkstry=graf.Dijkstra(graf, graf.WybierzWierzcholek(najblp), !czas);
	    		double wynikWLiczbieOdleglosc=0.0d;
	    		double wynikWLiczbieCzas=0.0d;
	    		int tymcz=new Integer(najblk);
	    		if(!(pocz.getText().isEmpty()&&kon.getText().isEmpty()))
	    		{
	    			znajdz=true;
	    			wynikowa.setEnabled(true);
	    			while(tymcz!=najblp)
	    			{
		    			KrawedzWazona k=graf.WybierzKrawedz(graf.WybierzWierzcholek(tymcz), 
		    					graf.WybierzWierzcholek(wynikiDijkstry[tymcz].poprzednik));
		    			wynikWLiczbieOdleglosc+=k.odleglosc;
		    			wynikWLiczbieCzas+=(k.odleglosc/1000.0/k.ograniczenie*3600.0);
		    			tymcz=wynikiDijkstry[tymcz].poprzednik;
	    			}
	    			String ustawTekst="Odległość do pokonania: "+(wynikWLiczbieOdleglosc/1000.0)+" kilometr";
	    				if(wynikWLiczbieOdleglosc/1000.0!=(Math.floor(wynikWLiczbieOdleglosc/1000.0)))
	    					ustawTekst+="a";
	    				else if((wynikWLiczbieOdleglosc/1000.0%10==2||
	    						wynikWLiczbieOdleglosc/1000.0%10==3||
	    						wynikWLiczbieOdleglosc/1000.0%10==4)&&wynikWLiczbieOdleglosc/1000.0/10%10!=1)
	    				{
	    					ustawTekst+="y";
	    				}
	    				else if(!(wynikWLiczbieOdleglosc/1000.0%10==1&&wynikWLiczbieOdleglosc/1000.0/10%10!=1))
	    				{
	    					ustawTekst+="ów";
	    				}
	    			wynikWLiczbieCzas=Math.floor(wynikWLiczbieCzas);
	    			ustawTekst+="\nCzas na pokonanie tej drogi: ";
	    			if(wynikWLiczbieCzas<180)
	    			{
	    				ustawTekst+=wynikWLiczbieCzas;
		    			ustawTekst+=" sekund";
	    				if((wynikWLiczbieCzas%10==2||
	    						wynikWLiczbieCzas%10==3||
	    						wynikWLiczbieCzas%10==4)&&wynikWLiczbieCzas/10%10!=1)
	    				{
	    					ustawTekst+="y";
	    				}
	    				else if((wynikWLiczbieCzas%10==1&&wynikWLiczbieCzas/10%10!=1))
	    				{
	    					ustawTekst+="a";
	    				}
	    			}
	    			else
	    			{
	    				wynikWLiczbieCzas/=60.0;
	    		        DecimalFormat df = new DecimalFormat("#.##");
	    				ustawTekst+=df.format(wynikWLiczbieCzas);
	    				ustawTekst+=" minut";
	    				if(wynikWLiczbieCzas!=(Math.floor(wynikWLiczbieCzas)))
	    					ustawTekst+="y";
	    				else if((wynikWLiczbieCzas%10==2||
	    						wynikWLiczbieCzas%10==3||
	    						wynikWLiczbieCzas%10==4)&&wynikWLiczbieCzas/10%10!=1)
	    				{
	    					ustawTekst+="y";
	    				}
	    				else if((wynikWLiczbieCzas%10==1&&wynikWLiczbieCzas/10%10!=1))
	    				{
	    					ustawTekst+="a";
	    				}
	    			}
	    			ustawTekst+=".";
	    			wynikowa.setText(ustawTekst);
	    		}
	    		else
	    		{
	    			wynikowa.setEnabled(false);
	    			znajdz=false;
	    		}
	    		mapa.repaint();
	    	}
	    	break;
	    	case 5:
	    	{
	    		JFileChooser wybieracz=new JFileChooser();
	    		FileFilter filtr=new FileNameExtensionFilter("Pliki baz danych","db");
	    		wybieracz.setAcceptAllFileFilterUsed(false);
	    		wybieracz.addChoosableFileFilter(filtr);
	    		if(wybieracz.showOpenDialog(((JButton)wyd.getSource()).getParent())==JFileChooser.APPROVE_OPTION)
	    		{
	    			File plik=wybieracz.getSelectedFile();
	    			polesciezka.setText(plik.getPath().substring(0, plik.getPath().lastIndexOf('/')+1));
	    			polenazwa.setText(plik.getName());
	    		}
	    	}
	    	break;
	    	case 6:
	    	{
    			nazwa=polenazwa.getText();
    			sciezka=polesciezka.getText();
	    		Object []o=BazaDanych.wczytajMape(nazwa);
	    		graf=(Grafy.GrafSkierWazony) o[0];
	    		miejsca=(BazaDanych.Miejsca) o[1];
	    		BufferedImage mapka=null;
				try {
					File obraz=new File(sciezka+nazwa.substring(0,nazwa.lastIndexOf('.'))+".png");
					FileImageInputStream strumien=new FileImageInputStream(obraz);
					mapka = ImageIO.read(strumien);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(mapka!=null)
					mapa.ustawMape(mapka);
	    		mapa.repaint();
	    		wczytywanie.setVisible(false);
	    	}
	    	break;
	    	case 7:
	    	{
	    		ustawienia.setVisible(false);
	    	}
	    	break;
	    	case 8:
	    	{  
	    		if(tworzenie==null)
	    		{
	    			tworzenie=new JFrame("Stwórz mapę");
		    		tworzenie.setLayout(new BorderLayout());
		    		JPanel Panel = new JPanel(new GridLayout(7,2));
		    		JLabel et;
		    		et=new JLabel("Wierzcholki mapy");
		    		et.setHorizontalAlignment(SwingConstants.RIGHT);
		    		Panel.add(et);      
		    		tworzWierzcholki=new JTextField(15);
		    		Panel.add(tworzWierzcholki);
		    		et=new JLabel("Krawędzie mapy");
		    		et.setHorizontalAlignment(SwingConstants.RIGHT);
		    		Panel.add(et);   
		    		tworzKrawedzie=new JTextField(15);
		    		Panel.add(tworzKrawedzie);
		    		et=new JLabel("Miejsca");
		    		et.setHorizontalAlignment(SwingConstants.RIGHT);
		    		Panel.add(et); 
		    		tworzMiejsca=new JTextField(15);
		    		Panel.add(tworzMiejsca);
		    		et=new JLabel("Granice");
		    		et.setHorizontalAlignment(SwingConstants.RIGHT);
		    		Panel.add(et); 
		    		tworzGranice=new JTextField(15);
		    		Panel.add(tworzGranice);
		    		et=new JLabel("");
		    		Panel.add(et);
		    		przyc=new JButton("Wybierz pliki");
						przyc.addActionListener(this);
						przyc.setActionCommand("10");
		    		Panel.add(przyc);
		    		et=new JLabel("");
		    		Panel.add(et);
		    		et=new JLabel("");
		    		Panel.add(et);
		    		et=new JLabel("Podaj nazwę dla mapy");
		    		et.setHorizontalAlignment(SwingConstants.RIGHT);
		    		Panel.add(et); 
		    		tworzNazwe=new JTextField(15);
		    		Panel.add(tworzNazwe);
		    		tworzenie.add(Panel, BorderLayout.NORTH);
		
		    		Panel = new JPanel(new GridLayout(1, 1)); 
		    		przyc=new JButton("Twórz");
						przyc.addActionListener(this);
						przyc.setActionCommand("9");
		    		Panel.add(przyc);
		    		tworzenie.add(Panel, BorderLayout.SOUTH);
		    		tworzenie.pack();
		            tworzenie.setLocationRelativeTo(ramka);
	    		}
	    		tworzenie.setVisible(true);
	    	}
	    	break;
	    	case 9:
	    		tworzenie.setVisible(false);
	    		File[] wierzcholki=new File[4];
	    		wierzcholki[0]=new File(tworzWierzcholki.getText());
	    		wierzcholki[1]=new File(tworzKrawedzie.getText());
	    		wierzcholki[2]=new File(tworzMiejsca.getText());
	    		wierzcholki[3]=new File(tworzGranice.getText());
	    		BazaDanych.tworzMape(wierzcholki, tworzNazwe.getText());
	    	break;
	    	case 10:
	    		JFileChooser wybieracz=new JFileChooser();
	    		FileFilter filtr=new FileNameExtensionFilter("Pliki tekstowe","txt");
	    		wybieracz.setAcceptAllFileFilterUsed(false);
	    		wybieracz.addChoosableFileFilter(filtr);
	    		wybieracz.setDialogTitle("Wybierz plik z wierzchołkami");
	    		if(wybieracz.showOpenDialog(((JButton)wyd.getSource()).getParent())==JFileChooser.APPROVE_OPTION)
	    		{
	    			File plik=wybieracz.getSelectedFile();
	    			tworzWierzcholki.setText(plik.getPath());
	    			wybieracz.setDialogTitle("Wybierz plik z krawędziami");
	        		if(wybieracz.showOpenDialog(((JButton)wyd.getSource()).getParent())==JFileChooser.APPROVE_OPTION)
	        		{
	        			plik=wybieracz.getSelectedFile();
	        			tworzKrawedzie.setText(plik.getPath());
	        			wybieracz.setDialogTitle("Wybierz plik z miejscami");
	            		if(wybieracz.showOpenDialog(((JButton)wyd.getSource()).getParent())==JFileChooser.APPROVE_OPTION)
	            		{
	            			plik=wybieracz.getSelectedFile();
	            			tworzMiejsca.setText(plik.getPath());
	            			wybieracz.setDialogTitle("Wybierz plik z granicami");
	                		if(wybieracz.showOpenDialog(((JButton)wyd.getSource()).getParent())==JFileChooser.APPROVE_OPTION)
	                		{
	                			plik=wybieracz.getSelectedFile();
	                			tworzGranice.setText(plik.getPath());
	                		}
	            		}
	        		}
	    		}
	    	break;
	    	case 11:
	    		czas=false;
	    	break;
	    	case 12:
	    		czas=true;
	    	break;
		}
	}
	
	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
		if(miejsca!=null&&e.getKeyCode()>31&&e.getKeyCode()<127)
		{
			JTextField zrodlo=(JTextField) e.getSource();
    		int i;
    		String tekst=zrodlo.getText();
    		if(!tekst.isEmpty())
    		{
	    		for(i=1;i<miejsca.ileMiejsc()+1;i++)
	    		{
	    			if(miejsca.nazwa(i).startsWith(tekst))
	    				break;
	    		}
	    		if(i<miejsca.ileMiejsc()+1)
	    		{
	        		zrodlo.setText(miejsca.nazwa(i));
	        		if(zrodlo==pocz)
	        			miejscepocz=i;
	        		else
	        			miejscekon=i;
	        		zrodlo.select(tekst.length(), zrodlo.getText().length());
	    		}
    		}
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException
	{
		Class.forName("org.sqlite.JDBC");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StwIPokGUI();
            }
        });
	}
}