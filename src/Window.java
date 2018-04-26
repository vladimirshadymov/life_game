import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Window extends JFrame {
    private Color color;
    private Color wcolor;
    private int x;
    private int y;
    private int cell_size;
    private int[][] field;
    private int[][] init_field;
    private boolean is_running;


    final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    ScheduledFuture<?> life_scheduler;

    final Runnable life_process = new Runnable() {
        public void run() {
            LifeField core = new LifeField();
            field = core.makeIter(field);
            Window.this.repaint();
        }
    };


    private void stopLife(){
        if (is_running==true) {
            life_scheduler.cancel(false);
            is_running = false;
        }
    }

    public Window(int[][] input_field, int cell_size){
        this.init_field = input_field;
        this.is_running = false;
        this.field = input_field;
        this.x = field.length;
        this.y = field[0].length;
        this.cell_size = cell_size;
        setSize(cell_size*x+100,cell_size*y+140);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        color = Color.BLACK;
        wcolor = Color.WHITE;
        JPanel panel = new JPanel();
        getContentPane().add(panel);

        JButton start_button = new JButton("Start!");
        panel.add(start_button);

        JButton stop_button = new JButton("Stop!");
        panel.add(stop_button);

        JButton reset_button = new JButton("Reset!");
        panel.add(reset_button);

        JButton clear_button = new JButton("Clear field");
        panel.add(clear_button);

        JButton set_glider = new JButton("Set simple glider");
        panel.add(set_glider);

        JButton load_file_button = new JButton("Load file");
        panel.add(load_file_button);

        JButton save_to_file_button = new JButton("Save to file");
        panel.add(save_to_file_button);

        start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (is_running==false) {
                    life_scheduler =
                            scheduler.scheduleWithFixedDelay(life_process, 1, 100, TimeUnit.MILLISECONDS);
                    is_running=true;
                }
            }
        });

        stop_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopLife();
            }
        });

        reset_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field = init_field;
                Window.super.repaint();
            }
        });

        set_glider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field[8][4] = 1;
                field[8][5] = 1;
                field[8][6] = 1;
                field[9][6] = 1;
                field[10][5] = 1;
                Window.super.repaint();
            }
        });

        clear_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                field = new int[input_field.length][input_field[0].length];
                stopLife();
                Window.super.repaint();
            }
        });

        load_file_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Open file");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    String file_path = fileopen.getSelectedFile().getAbsolutePath();
                    try {
                        List<List<Integer>> new_listed_field = new ArrayList<List<Integer>>();
                        BufferedReader input_file_br = new BufferedReader(new FileReader(file_path));
                        for(String line; (line = input_file_br.readLine()) != null; ) {
                            List<Integer> tmp_line = new ArrayList<Integer>();
                            Scanner scanner = new Scanner(line);
                            while (scanner.hasNextInt()) {
                                tmp_line.add(scanner.nextInt());
                            }
                            new_listed_field.add(tmp_line);
                        }
                        x = new_listed_field.size();
                        y = new_listed_field.get(0).size();
                        field = new int[x][y];
                        init_field = new int[x][y];

                        for (int i=0; i<x; ++i){
                            for (int j=0; j<y; ++j){x = new_listed_field.size();
                                field[i][j] = new_listed_field.get(i).get(j);
                                init_field[i][j] = new_listed_field.get(i).get(j);
                            }
                        }
                        Window.super.repaint();
                    } catch (IOException ex){
                        System.out.println("File READ error found");
                    }
                }
            }
        });


        save_to_file_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                //chooser.setCurrentDirectory(new File("/home/me/Documents"));
                int retrival = chooser.showSaveDialog(null);
                if (retrival == JFileChooser.APPROVE_OPTION) {
                    try {
                        FileWriter file_to_write = new FileWriter(chooser.getSelectedFile()+".txt");
                        for (int i=0; i<x; ++i){
                            StringBuilder sb = new StringBuilder();
                            for (int j=0; j<y; ++j){
                                sb.append(field[i][j]);
                                sb.append(" ");
                            }
                            file_to_write.write(sb.toString()+"\n");
                        }
                        file_to_write.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                PointerInfo a = MouseInfo.getPointerInfo();
                Point b = a.getLocation();
                int x = (int) b.getX();
                int y = (int) b.getY();
                int x_cell = (x-50)/cell_size;
                int y_cell = (y-120)/cell_size;
                if (x_cell>=0 && x_cell<field.length && y_cell>=0 && y_cell<field[0].length) field[x_cell][y_cell] = (field[x_cell][y_cell]+1)%2;
                Window.super.repaint();
            }
        });

    }

    public void paint(Graphics g){

        int width = field.length*cell_size+1;
        int height = field[0].length*cell_size+1;

        Image offscreen =  createImage(width, height);
        Graphics2D g2d = (Graphics2D) offscreen.getGraphics();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);

        for(int i = 0; i <= x*cell_size; i+=cell_size){
            Line2D lin  = new Line2D.Float(i, 0, i, cell_size*y);
            g2d.draw(lin);

        }
        for(int i = 0; i <= y*cell_size ; i+=cell_size){
            Line2D lin  = new Line2D.Float(0, i, x*cell_size, i);
            g2d.draw(lin);

        }
        for(int i = 0; i < field.length;i++){
            for (int j = 0; j < field[0].length; j++){
                if(field[i][j] == 1){
                    g2d.setColor(color);
                    g2d.fillRect(i*cell_size+1, j*cell_size+1, cell_size-1,cell_size-1);
                }else if (field[i][j] == 0){
                    g2d.setColor(wcolor);
                    g2d.fillRect(i*cell_size+1, j*cell_size+1, cell_size-1,cell_size-1);
                    g2d.setColor(color);
                }else {
                    System.out.print("Uncorrect data!");
                }

            }

        }

        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.drawImage( offscreen, 50, 100, this );
    }
}



