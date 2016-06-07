package crawlerUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class Window extends JFrame{
    
    private boolean status;
    private int numCrawled;
    private int numDetected;
    private StringBuilder crawlLog;
    private Box numberBox;
    private Box logBox;
    private String origin;
    private JTextField originUrl;
    
    public boolean getStatus(){
        return status;
    }
    
    public String getOrigin(){
        return origin;
    }
    
    public void setStatus(boolean status){
        this.status = status;
    }
    
    public void setCrawled(int crawled){
        this.numCrawled = crawled;
    }
    
    public void setDetected(int detected){
        this.numDetected = detected;
    }
    
    public void addLog(String log){
        crawlLog.append(log + "\n");
    }
     
    public Window(){ //constructor
        this.status = false;
        this.numCrawled = 0;
        this.numDetected = 0;
        this.crawlLog = new StringBuilder();
    }
    
    
    public void createWindow(){
        this.setSize(600,400);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("11-Dimension Technology Web Crawler Control Panel");
        
        JPanel logoPanel = new JPanel();
        logoPanel.setSize(600, 200);
            JLabel logoLabel = new JLabel();
            logoLabel.setSize(600,200);
        
        JPanel displayPanel = new JPanel();    
        displayPanel.setSize(600, 150);
            Box displayBox = Box.createHorizontalBox();
                Box titleBox = Box.createVerticalBox();
                numberBox = Box.createVerticalBox();
                
            displayBox.add(titleBox);
            displayBox.add(numberBox);
                
                    JLabel crawledTitle = new JLabel("Number of Domains Crawled: ");
                    JLabel detectedTitle = new JLabel("Number of Domains Detected: ");
                    JTextField crawled = new JTextField();
                    JTextField detected = new JTextField();

                    crawled.setColumns(20);
                    detected.setColumns(20);
                    crawled.setEditable(false);
                    detected.setEditable(false);
            
                    crawled.setText(Integer.toString(this.numCrawled));
                    detected.setText(Integer.toString(this.numDetected));
                    
                    titleBox.add(crawledTitle);
                    titleBox.add(detectedTitle);
                numberBox.add(crawled);
                numberBox.add(detected);
            
            logBox = Box.createHorizontalBox();
            
                JTextArea viewPort = new JTextArea();
                viewPort.setLineWrap(true);
                viewPort.setWrapStyleWord(true);
                viewPort.setFont(new Font("monospaced", Font.PLAIN, 12));
                viewPort.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                JScrollPane logScroll = new JScrollPane(viewPort, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                logScroll.setPreferredSize(new Dimension(450,160));
            logBox.add(logScroll);
            
        JPanel controlPanel = new JPanel();
        controlPanel.setSize(600, 50);
        controlPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
            JLabel origin = new JLabel("Origin URL: ");
            originUrl = new JTextField("http://www.wimp.com/");
            originUrl.setColumns(15);
            JButton launch = new JButton("Launch Crawler");
            JButton stop = new JButton("Stop Crawling");
            
            buttonListener launchCrawler = new buttonListener();
            buttonListener stopCrawler = new buttonListener();
            
            launch.addActionListener(launchCrawler);
            stop.addActionListener(stopCrawler);
        
        BufferedImage bufferLogo = null;
        try {
            bufferLogo = ImageIO.read(new File("E:\\Technical Program\\NetBeans 8.1\\NetBeansProjects\\11dcrawler\\src\\crawlerUI\\logo_en.png"));
        } catch (IOException ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Image scaleLogo = bufferLogo.getScaledInstance(400, 115, Image.SCALE_SMOOTH);

        ImageIcon logo = new ImageIcon(scaleLogo);
        logoLabel.setIcon(logo);
        
        logoPanel.add(logoLabel);
        
        displayPanel.add(displayBox);
        displayPanel.add(logBox);
        
        controlPanel.add(origin);
        controlPanel.add(originUrl);
        controlPanel.add(launch);
        controlPanel.add(stop);

        this.add(logoPanel, BorderLayout.NORTH);
        this.add(displayPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);
        
        this.setVisible(true);
    }
    
    
    public void update(){
        
        //numberBox.removeAll();
        //logBox.removeAll();
        
        JTextField crawled = new JTextField();
        JTextField detected = new JTextField();
        crawled.setColumns(20);
        detected.setColumns(20);
        crawled.setEditable(false);
        detected.setEditable(false);
        crawled.setText(Integer.toString(this.numCrawled));
        detected.setText(Integer.toString(this.numDetected));
        
        numberBox.add(crawled);
        numberBox.add(detected);
        numberBox.remove(0);
        numberBox.remove(0);
        
        numberBox.revalidate();
        
        JTextArea viewPort = new JTextArea();
        viewPort.setLineWrap(true);
        viewPort.setWrapStyleWord(true);
        viewPort.setFont(new Font("monospaced", Font.PLAIN, 12));
        viewPort.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        viewPort.setText(crawlLog.toString());
        JScrollPane logScroll = new JScrollPane(viewPort, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        logScroll.setPreferredSize(new Dimension(450,160));
        
        
        logBox.add(logScroll);
        logBox.remove(0);
       
        logBox.revalidate();
        
        JScrollBar Vsb = logScroll.getVerticalScrollBar();
        Vsb.setValue(Vsb.getMaximum());
    }
    
    
    public class buttonListener implements ActionListener{
        
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("Launch Crawler")){
                origin = originUrl.getText();
                status = true;
            }
            if(e.getActionCommand().equals("Stop Crawling")){
                status = false;
            }
        }
    
    }
}