package pv211.sockets;
import javafx.application.Application;
import java.io.*;
import javafx.stage.Stage;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.Socket;
import java.lang.NullPointerException;
import java.awt.BorderLayout;
/**
 * JavaFX App
 */
public class App extends Application {
	static DataOutputStream dataOutputStream = null;//application writing primitive java data types to output stream in portable way
	static DataInputStream dataInputStream = null;//application reading primitive java data types from underlying input stream
    @Override
    public void start(Stage stage) {
    	JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame jFrame = new JFrame();//frame that adds support for swing component
        jFrame.setPreferredSize(new Dimension(300, 300));
        jFrame.pack();
        JPanel jPanel = new JPanel();//generic lightweight container
        JButton jButton = new JButton("выбрать файл");//implementation of "push" button
        jButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
        	try {
        		JFileChooser jFileChooser = new JFileChooser();//usual component inherited from component class so it can be included in any place of interface
            	jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            	jFileChooser.showOpenDialog(jFileChooser);
            	String address = "name.txt";//address of file name
            	System.out.println("выбран файл " + jFileChooser.getSelectedFile()
            	.getName() + ". этот файл отправлен на сервер");
            	try (Socket socket = new Socket("192.168.1.146", 900)) {//implementation of client sockets
            		FileOutputStream fileOutputStream = new FileOutputStream(address);//record bytes into file
            		fileOutputStream.write(jFileChooser.getSelectedFile().getName()
            				.getBytes());
            		fileOutputStream.flush();
            		fileOutputStream.close();
            		dataInputStream = new DataInputStream(socket.getInputStream());//data object of input stream
            		dataOutputStream = new DataOutputStream(socket.getOutputStream());//data object of output stream
            		String[] names = { address, jFileChooser.getSelectedFile().getPath() };//names of files which're going to be sent
            		for (int i = 0; i < names.length; i++) sendFile(names[i]);
            		closeStreams();
            	}
            	catch(Exception exception) { }
        	}
        	catch(NullPointerException nullPointerException) { }
        	}
        });
        jFrame.setLayout(new BorderLayout());
        addComp(jPanel, jButton, jFrame);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    public static void main(String[] args) {
        launch();
    }
    static void sendFile(String path) throws Exception {
    	int bytes = 0;//size of empty file
    	File file = new File(path);//management of information about files and catalogs
    	FileInputStream fileInputStream = new FileInputStream(file);//heir of input stream that realize all methods of it
    	dataOutputStream.writeLong(file.length());
    	byte[] buffer = new byte[4 * 1024];//byte array for upload
    	while ((bytes = fileInputStream.read(buffer)) != -1) {
    		dataOutputStream.write(buffer, 0, bytes);
    		dataOutputStream.flush();
    	}
    	fileInputStream.close();
    }
    static void closeStreams() throws IOException {
    	dataInputStream.close();
    	dataOutputStream.close();
    }
    static void addComp(JPanel panel, JButton button, JFrame frame) {
    	panel.add(button);
    	frame.add(panel, BorderLayout.CENTER);
    }
}