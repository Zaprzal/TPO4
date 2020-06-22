package zad1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Chat extends JFrame{

	private Hashtable<String, String> props = new Hashtable<String, String>();
	private Context context;
	private Connection connection;
	private Session session;
	private Destination destination;
	
	public Chat(String username) {
			
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.exolab.jms.jndi.InitialContextFactory");
	    props.put(Context.PROVIDER_URL, "tcp://localhost:3035/");
	
	    try {
			context = new InitialContext(props);
			ConnectionFactory cf = (ConnectionFactory) context.lookup("ConnectionFactory");
			connection = cf.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = (Destination) context.lookup("chat");
		} catch (NamingException | JMSException e) {
			System.out.println(e);
		}
	    
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chat - " + username);
		JPanel mainPanel = new JPanel(new BorderLayout());
		JTextArea jta = new JTextArea();
		jta.setEditable(false);
		JScrollPane jsp = new JScrollPane(jta);
		mainPanel.add(jsp, BorderLayout.CENTER);
		JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JTextField jtf = new JTextField();
		jtf.setPreferredSize(new Dimension(200,24));
		JButton jb = new JButton("Wyœlij");
		subPanel.add(jtf);
		subPanel.add(jb);
		mainPanel.add(subPanel, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(600, 600));
		add(mainPanel);
		pack();
		setVisible(true);
		
		jb.addActionListener(e -> {
			Send_msg(jtf.getText());
			jtf.setText("");
		});
	
		jtf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Send_msg(jtf.getText());
				jtf.setText("");
			}
		});
		
		try {
	    	MessageConsumer receiver = session.createConsumer(destination);
	        receiver.setMessageListener(new MessageListener() {
	        	
	        	@Override
	            public void onMessage(Message message) {
	                TextMessage text = (TextMessage) message;
	                try {
	                	StringBuilder sb = new StringBuilder();
	                	sb.append(jta.getText());
	                	sb.append("User 1: " + text.getText());
						jta.setText(sb.toString() + "\n");
					} catch (JMSException e) {
						System.out.println(e);
					}
	            }
	        });
	        connection.start();
		} catch (JMSException e1) {
			System.out.println(e1);
		}
	}
	
	
	public void Send_msg(String mes) {
		try {
			connection.start();
		    MessageProducer sender = session.createProducer(destination);
		    TextMessage message = session.createTextMessage(mes);
		    sender.send(message);
		} catch (JMSException e1) {
			System.out.println(e1);
		}
	}
	
	
}
