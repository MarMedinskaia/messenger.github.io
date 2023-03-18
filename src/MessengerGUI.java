import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;

/**
 * 
 *
 * 		   A class that contains start method used for setting the layout of the messenger in three tabs
 * 		   that include buttons and textfields, event handlers for different buttons of the messenger, 
 * 		   and a helper method to check if a username exists.
 * 
 *         
 *         @author Margarita Medinskaia
 *         
 */
public class MessengerGUI extends Application{
	private Messenger messenger;
	private Text topText;
	private String status;
	private final int fontSize = 22;
	private TextArea messageField;
	private final String noMessages = "No Message Displayed";;
	private TextField username;
	private Button select;
	private String activeUser;
	private Button next,loadAll,loadUnread;
	private TextField receip;
	private TextArea write;
	private RadioButton smileRB,writtenRB;
	private Button send;
	private int count=0;
	private int messageShown=0;
	private boolean userExists,all,unread;
	private Message message;
	
	@Override
	public void start(Stage primaryStage) {		
		messenger = new Messenger();
		messenger.addUser("Chloe");
		messenger.addUser("Ben");
		messenger.sendMessage("Ben", "Chloe", "Let's go to the cinema");
		
		BorderPane root = new BorderPane();
		
		status = "Select A User";
		topText = new Text(status);
		topText.setFont(Font.font("Arial",fontSize));

		BorderPane.setAlignment(topText,Pos.CENTER);
		
		TabPane tp = new TabPane();
		
		Text enterUser = new Text("Enter Username:");
		username = new TextField();
		select = new Button("Select");
		select.setOnAction(new ActiveUserHandler());
		HBox hbT1 = new HBox(5,enterUser,username,select);
		hbT1.setAlignment(Pos.CENTER);
		Tab t1 = new Tab("Choose User",hbT1);
		t1.setClosable(false);
		
		messageField = new TextArea(noMessages);
		messageField.setEditable(false);
		next = new Button("Next");
		next.setDisable(true);
		next.setOnAction(new LoadMessagesHandler());
		HBox messageNext = new HBox(messageField,next);
		messageNext.setMargin(next, new Insets(0,5,0,0));
		HBox.setMargin(messageField, new Insets(5));
		messageNext.setAlignment(Pos.CENTER);
		loadAll = new Button("Load All Messages");
		loadAll.setOnAction(new LoadMessagesHandler());
		loadUnread = new Button("Load Unread Messages");
		loadUnread.setOnAction(new LoadMessagesHandler());
		HBox buttonsT2 = new HBox(5,loadAll,loadUnread);
		buttonsT2.setAlignment(Pos.CENTER);
		VBox vbT2 = new VBox(messageNext,buttonsT2);
		vbT2.setMargin(buttonsT2, new Insets(0));
		Tab t2 = new Tab("Read Messages",vbT2);
		t2.setClosable(false);
		
		Text to = new Text("To:");
		receip = new TextField();
		HBox toReceip = new HBox(to,receip);
		HBox.setMargin(to,new Insets(5));
		write = new TextArea();
		Text messageType = new Text("Message Type");
		smileRB = new RadioButton();
		smileRB.setOnAction(new SendMessageHandler());
		Text smile = new Text("Smile");
		writtenRB = new RadioButton();
		writtenRB.setOnAction(new SendMessageHandler());
		Text written = new Text("Written");
		ToggleGroup smileWritten = new ToggleGroup();
		smileRB.setToggleGroup(smileWritten);
		writtenRB.setToggleGroup(smileWritten);
		writtenRB.setSelected(true);
		send = new Button("Send");
		send.setOnAction(new SendMessageHandler());
		HBox sending = new HBox(2,messageType,smileRB,smile,writtenRB,written,send);
		send.setAlignment(Pos.CENTER_RIGHT);
		sending.setAlignment(Pos.CENTER);
		VBox vbT3 = new VBox(toReceip,write,sending);
		VBox.setMargin(toReceip,new Insets(5));
		VBox.setMargin(write,new Insets(0,5,0,5));
		VBox.setMargin(sending, new Insets(8));
		Tab t3 = new Tab("Send Message",vbT3);
		t3.setClosable(false);
		
		tp.getTabs().addAll(t1,t2,t3);
	
		root.setTop(topText);
		root.setMargin(topText, new Insets(10));
		root.setCenter(tp);
		
		Scene scene = new Scene(root);
		primaryStage.setTitle("Messenger GUI");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private class ActiveUserHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			boolean userExists = ifExists(username.getText());
			if (!userExists) {
				topText.setText("Incorrect Username");
			} else {
				messageField.setText(null);
				receip.setText(null);
				write.setText(null);
				next.setDisable(true);
				topText.setText("Current user: " + username.getText());
				activeUser = username.getText();
				messageShown=0;
				count=0;
			}
		}		
	}
	
	private class LoadMessagesHandler implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent e) {
			if (activeUser==null) {
				throw new NullPointerException("Username is not chosen");
			}
			if (e.getSource()==loadAll || e.getSource()==loadUnread) {
			  messageShown=0;
			  count=0;
			  all=false;
		      unread=false;
			  if(e.getSource()==loadAll) {
				all=true;
				for(Message m: messenger.getReceivedMessages(activeUser)) {
					count++;
				}
			  } else if (e.getSource()==loadUnread) {
				 unread=true;
				 for(Message m: messenger.getReceivedMessages(activeUser)) {
					 if (m.getStatus()==Message.Status.unread) {
					   count++;
					 }
				 }
			  }
			   if (count==0) {
				 messageField.setText(noMessages);
				 next.setDisable(true);
			   }
			   topText.setText(count + " message(s) loaded");
			}
			
			if (messageShown<count) {
				message = messenger.getReceivedMessages(activeUser).get(messageShown);
				if (all || message.getStatus()==Message.Status.unread) {
				  messageField.setText(message.toString());
				} else if (unread && message.getStatus()==Message.Status.read){
					messageShown++;  
					message = messenger.getReceivedMessages(activeUser).get(messageShown);
					while (message.getStatus()==Message.Status.read && messageShown<count) {
						messageShown++;  
						message = messenger.getReceivedMessages(activeUser).get(messageShown);
					}
					if (message.getStatus()==Message.Status.unread) {
						messageField.setText(message.toString());
					}
				}
				if (messageShown<count-1) {
					next.setDisable(false);
					message.setStatus(Message.Status.read);
					messageShown++;
				} else {
					next.setDisable(true);
					message.setStatus(Message.Status.read);
					messageShown++;
				}
			} else if (messageShown==count){
				next.setDisable(true);
				messageShown=0;
				if (unread) {
					count=0;
					messageField.setText(noMessages);
				}
			}
		}
	}
	
	private class SendMessageHandler implements EventHandler<ActionEvent> {
		
		@Override
		public void handle(ActionEvent e) {
			if (e.getSource() == smileRB) {
				write.setEditable(false);
				String smMessage = " @  @ \n"+
								   "      \n"+
								   "@    @\n"+
								   " @  @ \n"+
								   "  @@  \n";
				write.setText(smMessage);
			} else if (e.getSource() == writtenRB) {
				write.setText(null);
				write.setEditable(true);
			} else {
			  boolean userExists = ifExists(receip.getText());
			  if (!userExists) {
				topText.setText("Recipient Username Not Found");
			  } else {
				if (write.getText()==null) {
					throw new IllegalArgumentException("Message cannot be empty");
				}
			    if (smileRB.isSelected()) {
				  messenger.sendSmile(activeUser,receip.getText());
			    } else if (writtenRB.isSelected()) {
				  messenger.sendMessage(activeUser,receip.getText(),write.getText());
			    }
			    topText.setText("Message Successfully Sent");
			  }
			}	
		}
	}
	
	private boolean ifExists(String user) {
		boolean exists=false;
		for (int i=0; i<messenger.getUserList().size(); i++) {
			if (exists==false) {
				if (user.equals(messenger.getUserList().get(i))) {
					exists=true;
				}
			}
		}
		return exists;
	}
	
	public static void main(String[] args) {
		launch(args);
	}	
	
}

	
	
