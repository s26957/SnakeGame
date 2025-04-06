import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Objects;
class S26957Projek04
        extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(S26957Projek04::new);
    }

    public S26957Projek04(){
        int l_pol_szer = 25;
        int l_pol_wys = 16;

        int szer = l_pol_szer*30 + 50;
        int wys = l_pol_wys*30 + 100;

        Ranking ranking = new Ranking();
        ModelPlanszy modelPlanszy = new ModelPlanszy(l_pol_wys, l_pol_szer, ranking);
        GrafikaPlansza rozgrywanaPlansza = new GrafikaPlansza(l_pol_wys, l_pol_szer);
        rozgrywanaPlansza.addKierunekSetListener(modelPlanszy);
        modelPlanszy.addPlanszaSetListener(rozgrywanaPlansza);
        this.add(rozgrywanaPlansza);

        this.setSize(szer, wys);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class EndGameEvent extends EventObject {

    private final int wynik;
    private final Gracz[] ranking;
    public EndGameEvent(Object source, int wynik, Gracz[] ranking) {
        super(source);
        this.wynik = wynik;
        this.ranking = ranking;
    }

    public Gracz[] getRanking() {
        return ranking;
    }

    public int getWynik() {
        return wynik;
    }
}
class KierunekEvent extends EventObject {

    private final Kierunek kierunek;
    public KierunekEvent(Object source, Kierunek kierunek) {
        super(source);
        this.kierunek = kierunek;
    }

    public Kierunek getkierunek() {
        return kierunek;
    }
}


interface KierunekSetListener {
    void KierunekSet(KierunekEvent evt);
    void NickSet(NickEvent evt);
}
class NickEvent extends EventObject {
    final String nick;

    public NickEvent(Object source, String txt) {
        super(source);
        nick = txt;
    }

    public String getNick() {
        return nick;
    }
}

class PlanszaAppleAppearedEvent extends EventObject {
    private final Pozycja apple;
    public PlanszaAppleAppearedEvent(Object source, Pozycja apple) {
        super(source);
        this.apple = apple;
    }
    public Pozycja getApple()
    {
        return apple;
    }
}

class PlanszaAppleEaten extends EventObject {

    final int wynik;
    public PlanszaAppleEaten(Object source, int wynik) {
        super(source);
        this.wynik = wynik;
    }

    public int getWynik() {
        return wynik;
    }
}
interface PlanszaSetListener {
    void PlanszaSetSnake(PlanszaSnakeEvent evt);
    void PlanszaSetApple(PlanszaAppleAppearedEvent evt);
    void PlanszaSetWynik(PlanszaAppleEaten evt);
    void EndGame(EndGameEvent evt);

}
class PlanszaSnakeEvent extends EventObject {
    private final Pozycja head;

    private final Pozycja oldHead;
    private final Pozycja tail;
    public PlanszaSnakeEvent(Object source, Pozycja head, Pozycja oldHead, Pozycja tail) {
        super(source);
        this.head = head;
        this.tail = tail;
        this.oldHead = oldHead;
    }

    public Pozycja getHead()
    {
        return head;
    }
    public Pozycja getTail()
    {
        return tail;
    }

    public Pozycja getOldHead() {
        return oldHead;
    }
}
class GrafikaPlansza extends JPanel implements PlanszaSetListener {
    private final int l_pol_szer;
    private final int l_pol_wys;
    private final boolean nazwaPodana = false;

    private JTable plansza;
    private JPanel panelWyniku;

    private Color backgroundC;
    public GrafikaPlansza(int wysokosc, int szerokosc)
    {
        setLayout(new BorderLayout());
        l_pol_wys = wysokosc;
        l_pol_szer = szerokosc;
        backgroundC = Color.LIGHT_GRAY;

        stworzRozgrywke();

        this.setPreferredSize(new Dimension(l_pol_szer*30 +30, l_pol_wys*30+30 ));
        this.setBackground(backgroundC);

    }

    private void stworzRozgrywke() {
        JPanel pobieranieNicku;
        try {
            Image tlo = new ImageIcon(Objects.requireNonNull(getClass().getResource("tlo1.png"))).getImage();
            pobieranieNicku = new JPanel()
            {
                @Override
                protected void paintComponent(Graphics g) {

                    super.paintComponent(g);
                    g.drawImage(tlo, 0, 0, null);
                }
            };
        } catch (Exception e) {
            pobieranieNicku = new JPanel();
        }
        pobieranieNicku.setLayout(null);
        JTextField nick = new JTextField();
        JButton dodaj = new JButton("dalej");
        dodaj.addActionListener(
                e -> {
                    removeAll();
                    fireNickSet(nick.getText());
                    setPlansza();
                    setPanelWyniku(0);
                    revalidate();
                    repaint();
                }
        );

        pobieranieNicku.setPreferredSize(new Dimension(l_pol_szer*30 +30, l_pol_wys*30+30 ));
        pobieranieNicku.add(nick);
        pobieranieNicku.add(dodaj);
        nick.setBounds(310, 265, 160, 25);
        nick.setOpaque(false);
        nick.setForeground(Color.WHITE);
        dodaj.setBounds(310, 300, 170, 25);
        this.add(pobieranieNicku, BorderLayout.CENTER);
    }
    private void setPlansza()
    {
        String[] column_names = new String[l_pol_szer];
        for(int i = 0; i < l_pol_szer; i++)
        {
            column_names[i] = "";
        }
        String[][] pola = new String[l_pol_wys][l_pol_szer];
        plansza = new JTable(pola, column_names);
        DefaultTableCellRenderer renderer = new MyCellRenderer();
        int bok_pola = 30;
        for(int i = 0; i < l_pol_szer; i++ )
        {
            plansza.getColumnModel().getColumn(i).setCellRenderer(renderer);
            plansza.getColumnModel().getColumn(i).setPreferredWidth(bok_pola);
        }
        for(int i = 0; i < l_pol_wys; i ++)
        {
            plansza.setRowHeight(bok_pola);
        }

        plansza.setCellSelectionEnabled(false);
        plansza.setColumnSelectionAllowed(false);
        plansza.setRowSelectionAllowed(false);
        plansza.setValueAt("1", l_pol_wys/2,l_pol_szer/2);

        plansza.addKeyListener(
                new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        int key = e.getKeyCode();
                        if(key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_UP) {
                            Kierunek kierunek = Kierunek.WEST;
                            if (key == KeyEvent.VK_LEFT) {
                                kierunek = Kierunek.WEST;
                            }

                            if (key == KeyEvent.VK_RIGHT) {
                                kierunek = Kierunek.EAST;
                            }

                            if (key == KeyEvent.VK_UP) {
                                kierunek = Kierunek.NORTH;
                            }
                            if (key == KeyEvent.VK_DOWN) {
                                kierunek = Kierunek.SOUTH;
                            }
                            fireDirectionChange(kierunek);
                        }
                    }
                });
        this.add(plansza, BorderLayout.SOUTH);
    }

    private void setPanelWyniku(Integer wynik) {
        panelWyniku = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawString("wynik: " + wynik.toString(), 50, 20);
            }
        };
        panelWyniku.setPreferredSize(new Dimension(50,25));
        panelWyniku.setBackground(backgroundC);
        this.add(panelWyniku, BorderLayout.NORTH);
    }
    @Override
    public void PlanszaSetSnake(PlanszaSnakeEvent evt) {
        Pozycja head = evt.getHead();
        plansza.setValueAt("1", head.getY(), head.getX());
        if(evt.getTail()!=null)
        {
            plansza.setValueAt("", evt.getTail().getY(),  evt.getTail().getX());
        }
        if(evt.getOldHead()!=null)
        {
            plansza.setValueAt("3", evt.getOldHead().getY(), evt.getOldHead().getX());
        }

        plansza.repaint();
    }
    @Override
    public void PlanszaSetApple(PlanszaAppleAppearedEvent evt) {
        Pozycja pozycja_jablka = evt.getApple();
        plansza.setValueAt("2", pozycja_jablka.getY(), pozycja_jablka.getX());
        plansza.repaint();
    }

    @Override
    public void PlanszaSetWynik(PlanszaAppleEaten evt) {
        this.remove(panelWyniku);
        setPanelWyniku(evt.getWynik());
        revalidate();
        repaint();
    }

    @Override
    public void EndGame(EndGameEvent evt) {
        this.remove(plansza);
        this.remove(panelWyniku);
        JLabel wynik = new JLabel("twoj wynik to: " + evt.getWynik());
        JPanel ranking;
        try {
            Image tlo = new ImageIcon(Objects.requireNonNull(getClass().getResource("tloEnd.png"))).getImage();
            ranking = new JPanel()
            {
                @Override
                protected void paintComponent(Graphics g) {

                    super.paintComponent(g);
                    g.drawImage(tlo, 0, 0, null);
                }
            };
        } catch (Exception e) {
            ranking = new JPanel();
        }

        ranking.setLayout(null);
        for(int i = 0; i < 10; i++ )
        {
            if(evt.getRanking()[i]!=null) {
                JLabel wynikRanking = new JLabel(evt.getRanking()[i].toString());
                ranking.add(wynikRanking);
                wynikRanking.setBounds(310, 170 + i * 25, 170, 25);
            }
        }
        this.add(wynik, BorderLayout.NORTH);
        this.add(ranking, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private final ArrayList<KierunekSetListener> listeners = new ArrayList<>();

    public void addKierunekSetListener(KierunekSetListener listener){
        this.listeners.add(listener);
    }
    public void removeKierunekSetListener(KierunekSetListener listener){
        this.listeners.remove(listener);
    }

    protected void fireDirectionChange(Kierunek kierunek){
        KierunekEvent evt = new KierunekEvent( this, kierunek);
        for(KierunekSetListener listener : listeners)
            listener.KierunekSet(evt);
    }
    private void fireNickSet(String text) {
        NickEvent evt = new NickEvent(this, text);
        for(KierunekSetListener listener : listeners)
        {
            listener.NickSet(evt);
        }
    }
}

class MyCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {

        JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        MatteBorder border = new MatteBorder(1, 1, 1, 1, Color.BLACK);
        cell.setBorder(border);

        TableModel tMod = table.getModel();
        if(tMod.getValueAt(row, column) == "1")
        {
            try
            {
                ImageIcon head = new ImageIcon(Objects.requireNonNull(getClass().getResource("head.png")));
                cell.setIcon(head);
            }
            catch (Exception e)
            {
                cell.setBackground(Color.BLUE);
                cell.setForeground(Color.BLUE);
            }
        }
        else
        {
            if(tMod.getValueAt(row,column) == "2")
            {
                try
                {
                    ImageIcon jablko = new ImageIcon(Objects.requireNonNull(getClass().getResource("jablko.png")));
                    cell.setIcon(jablko);
                }
                catch (Exception e)
                {
                    cell.setBackground(Color.RED);
                    cell.setForeground(Color.RED);
                }
            }
            else
            {
                if(tMod.getValueAt(row,column) == "3")
                {
                    try
                    {
                        ImageIcon body = new ImageIcon(Objects.requireNonNull(getClass().getResource("body.png")));
                        cell.setIcon(body);
                    }
                    catch (Exception e)
                    {
                        cell.setBackground(Color.ORANGE);
                        cell.setForeground(Color.ORANGE);
                    }
                }
                else {
                    try
                    {
                        ImageIcon pusta = new ImageIcon(Objects.requireNonNull(getClass().getResource("img1.png")));
                        cell.setIcon(pusta);
                    }
                    catch (Exception e)
                    {
                        cell.setBackground(Color.BLACK);
                        cell.setForeground(Color.BLACK);
                    }
                }
            }

        }
        return cell;
    }

}

enum Kierunek {
    NORTH, EAST, WEST, SOUTH
}

class Pozycja {
    private int x;
    private int y;
    public Pozycja(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
class Gracz {
    private final String nazwa;
    private final int wynik;
    public Gracz(String n, int w)
    {
        nazwa = n;
        wynik = w;
    }
    public int getWynik() {
        return wynik;
    }

    public String getNazwa() {
        return nazwa;
    }

    public String toString()
    {
        return nazwa + ": " + wynik;
    }
}

class ModelPlanszy implements KierunekSetListener {
    String nazwaGracza;
    private Ranking ranking;
    final int wys;
    final int szer;
    final int[][] plansza;
    int wynik;
    final Snake snake;

    boolean start = false;
    public ModelPlanszy(int wysokosc, int szerokosc, Ranking ranking) {
        this.ranking = ranking;
        plansza= new int[wysokosc][szerokosc];
        snake = new Snake(szerokosc/2, wysokosc/2, this);
        wys = wysokosc;
        szer = szerokosc;
        wynik = 0;
    }

    public void wyswietlPlansza()
    {
        for (int[] ints : plansza) {
            for (int j = 0; j < ints.length; j++) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }
        System.out.println("\n");
    }
    private boolean wPlanszy(Pozycja pozycja) {
        return pozycja.getY() >= 0 && pozycja.getY() < plansza.length && pozycja.getX() >= 0 && pozycja.getX() < plansza[0].length;
    }

    public void updatePozWeza(Pozycja head, Pozycja oldHead, Pozycja tail)
    {
        if(tail!=null)
        {
            plansza[tail.getY()][tail.getX()] = 0;
        }
        if(oldHead!= null)
        {
            plansza[oldHead.getY()][oldHead.getX()] = 3;
        }
        if(wPlanszy(head) && plansza[head.getY()][head.getX()] != 3 && plansza[head.getY()][head.getX()] != 1 )
        {
            if(plansza[head.getY()][head.getX()] == 2)
            {
                snake.wydluz();
                wynik++;
                fireAppleEaten(wynik);
                losuj_jablko();
            }
            plansza[head.getY()][head.getX()] = 1;
            fireSnakeChange(head, oldHead, tail);
        }
        else
        {
            zakonczGre();
        }
    }
    private void losuj_jablko() {
        boolean poprawnosc = false;
        int x = 0;
        int y = 0;
        while(!poprawnosc) {
            x = (int) (Math.random() * szer);
            y = (int) (Math.random() * wys);
            if (plansza[y][x] != 1 && plansza[y][x]!=3)
            {
                poprawnosc = true;
            }
        }
        plansza[y][x] = 2;
        fireAppleChange(new Pozycja(x, y));
    }
    private void zakonczGre(){
        snake.zatrzymaj();
        ranking.addGracz(new Gracz(nazwaGracza, wynik));
        Gracz[] gracze = new Gracz[10];
        try {
            ranking.zapisz();
            gracze = Ranking.wczytajRanking();
        } catch (IOException e) {
            System.out.println("problem z plikiem - nie mozna wczytac raningu");
        }
        fireEndGame(wynik, gracze);
    }
    private final ArrayList<PlanszaSetListener> listeners = new ArrayList<>();

    public void addPlanszaSetListener(PlanszaSetListener listener){
        this.listeners.add(listener);
    }
    public void removePlanszaSetListener(PlanszaSetListener listener){
        this.listeners.remove(listener);
    }
    protected void fireSnakeChange(Pozycja head, Pozycja oldHead, Pozycja tail){
        PlanszaSnakeEvent evt = new PlanszaSnakeEvent( this, head, oldHead, tail);
        for(PlanszaSetListener listener : listeners)
            listener.PlanszaSetSnake(evt);
    }
    protected void fireAppleChange(Pozycja pozycja){
        PlanszaAppleAppearedEvent evt = new PlanszaAppleAppearedEvent( this, pozycja);
        for(PlanszaSetListener listener : listeners)
            listener.PlanszaSetApple(evt);
    }

    protected void fireAppleEaten(int wynik)
    {
        PlanszaAppleEaten evt = new PlanszaAppleEaten(this, wynik);
        for(PlanszaSetListener listener : listeners)
        {
            listener.PlanszaSetWynik(evt);
        }
    }

    protected void fireEndGame(int wynik, Gracz[] ranking)
    {
        EndGameEvent evt = new EndGameEvent(this, wynik, ranking);
        for(PlanszaSetListener listener: listeners)
        {
            listener.EndGame(evt);
        }
    }
    @Override
    public void KierunekSet(KierunekEvent evt) {
        if(!start)
        {
            start = true;
            losuj_jablko();
            snake.startWeza(evt.getkierunek());
        }
        if(!sprPrzeciwnoscKierunkow(evt.getkierunek(), snake.getKierunek())) {
            snake.setKierunek(evt.getkierunek());
        }
    }

    @Override
    public void NickSet(NickEvent evt) {
        nazwaGracza = evt.getNick();
    }

    private boolean sprPrzeciwnoscKierunkow(Kierunek kierunek, Kierunek kierunek1) {
        if(kierunek == Kierunek.SOUTH && kierunek1 == Kierunek.NORTH)
        {
            return true;
        }
        if(kierunek == Kierunek.NORTH && kierunek1 == Kierunek.SOUTH)
        {
            return true;
        }
        if(kierunek == Kierunek.WEST && kierunek1 == Kierunek.EAST)
        {
            return true;
        }
        return kierunek == Kierunek.EAST && kierunek1 == Kierunek.WEST;
    }
}
class Ranking {
    Gracz[] ranking;
    int l_graczy;

    public Ranking()
    {
        try {
            ranking = wczytajRanking();
        } catch (IOException e) {
            ranking = new Gracz[10];
            Gracz gracz = new Gracz("", 0);
            for(int i = 0; i < 10; i ++)
            {
                ranking[i] = gracz;
            }
            System.out.println("nie mozna odczytac pliku");
        }
    }

    public static Gracz[] wczytajRanking() throws IOException
    {
        Gracz[] gracze = new Gracz[10];
        FileInputStream fis = new FileInputStream("dane.txt");
        for(int i = 0; i < 10; i ++)
        {
            String nick = "";
            int wynik = 0;
            int n = fis.read();
            for(int j = 0; j < n; j ++)
            {
                nick = nick + (char)fis.read();
            }
            for(int j = 0; j < 4; j ++)
            {
                wynik = wynik + (int)(fis.read()*Math.pow(256,j));
            }
            gracze[i] = new Gracz(nick, wynik);
        }
        fis.close();
        return gracze;
    }

    public void addGracz(Gracz wczyt)
    {
        boolean znaleziono = false;
        Gracz tmp = new Gracz("", 0);
        for(int i = 0; i < 10; i ++)
        {
            if(!znaleziono)
            {
                if(ranking[i].getWynik() < wczyt.getWynik())
                {
                    znaleziono = true;
                    tmp = ranking[i];
                    ranking[i] = wczyt;
                }
            }
            else
            {
                Gracz tmp2 = ranking[i];
                ranking[i] = tmp;
                tmp = tmp2;
            }
        }
        if(l_graczy<10)
        {
            l_graczy++;
        }
    }

    public void zapisz() throws IOException {
        FileOutputStream fos = new FileOutputStream("dane.txt");
        for(int i = 0; i < 10; i ++)
        {
            fos.write(zapisGracza(ranking[i]));
        }
        fos.close();
    }
    private byte[] zapisGracza(Gracz gracz) {
        int n = gracz.getWynik();

        byte[] gracz_byte = new byte[1 + gracz.getNazwa().length() + 4];
        gracz_byte[0] = (byte)(gracz.getNazwa().length());
        for(int i = 0; i < gracz.getNazwa().length();i++)
        {
            gracz_byte[i + 1] = (byte)(gracz.getNazwa().charAt(i));
        }
        //obecny indeks = dlugosz nazwy + 1
        for(int i = 0; i < 4; i ++)
        {
            gracz_byte[i+ 1 + gracz.getNazwa().length() ] = (byte)((n) & 0xFF);
            n = n>>8;
        }
        return gracz_byte;
    }


}

class Snake extends Thread{
    private final ModelPlanszy plansza;
    private boolean biegnij = true;
    private Kierunek kierunek;
    private final ArrayList<Pozycja> Waz;
    private Pozycja lastTail;
    private boolean wydluz;

    //x, y poczatkowe ustawienie weza
    public Snake(int x, int y, ModelPlanszy plansza)
    {
        this.plansza = plansza;
        Waz = new ArrayList<>();
        Waz.add(new Pozycja(x, y));
        wydluz = false;
    }

    public Kierunek getKierunek()
    {
        return kierunek;
    }
    public void startWeza(Kierunek kierunek)
    {
        this.kierunek = kierunek;
        start();
    }

    public void setKierunek(Kierunek kierunek)
    {
        this.kierunek = kierunek;
    }

    public void zatrzymaj()
    {
        biegnij = false;
    }

    public void wydluz()
    {
        wydluz = true;
    }

    @Override
    public synchronized void run()
    {
        while(biegnij)
        {
            int x = Waz.get(Waz.size()-1).getX();
            int y = Waz.get(Waz.size()-1).getY();
            switch(kierunek) {
                case NORTH -> y-=1;
                case SOUTH -> y+=1;
                case EAST -> x+=1;
                case WEST -> x-=1;
            }
            Waz.add(new Pozycja(x, y));
            if(!wydluz)
            {
                lastTail = Waz.get(0);
                Waz.remove(0);
            }
            else
            {
                lastTail = null;
                wydluz = false;
            }

            Pozycja oldHead = null;
            if(Waz.size() > 1)
            {
                oldHead = Waz.get(Waz.size() - 2);
            }
            plansza.updatePozWeza(Waz.get(Waz.size()-1), oldHead, lastTail);
            try {
                this.wait(150);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
