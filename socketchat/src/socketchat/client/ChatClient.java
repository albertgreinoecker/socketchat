package socketchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatClient extends Application {
	TextField userField;
	TextField inputField;
	TextArea chatTrace;
	ListView<String> users;
	TextField feedBack;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	Label timeLabel;
	int seconds = 0;

	public void start(Stage primaryStage) {
		try {
			netzwerkEinrichten();

			BorderPane root = new BorderPane();

			VBox top = new VBox();
			top.setSpacing(10);

			HBox userBox = new HBox();
			userField = new TextField();
			userField.setPromptText("Benutzernamen eingeben");

			Button userButton = new Button("Namen setzen");
			userButton.setOnAction((ActionEvent e) -> {
				System.out.println("WRITE OUT:" + userField.getText() + "#" + inputField.getText());
				writer.println("#B" + userField.getText());
				writer.flush();

				inputField.setText("");
				inputField.requestFocus();
			});
			userBox.getChildren().addAll(userField, userButton);

			inputField = new TextField();
			inputField.setPromptText("Nachricht eingeben");

			HBox areas = new HBox();
			chatTrace = new TextArea();
			users = new ListView<String>();
			// chatTrace.setMaxWidth(450);
			// chatTrace.setMaxHeight(250);
			chatTrace.setEditable(false);

			areas.getChildren().addAll(chatTrace, users);
			users.setEditable(false);

			feedBack = new TextField();
			Button send = new Button("send");
			send.setOnAction((ActionEvent e) -> {
				System.out.println("WRITE OUT:" + userField.getText() + "#" + inputField.getText());
				writer.println(userField.getText() + "#" + inputField.getText());
				writer.flush();

				inputField.setText("");
				inputField.requestFocus();
			});

			top.getChildren().addAll(userBox, inputField, areas, send);
			root.setTop(top);

			HBox bottom = new HBox();
			timeLabel = new Label(seconds + "");

			bottom.getChildren().addAll(feedBack, timeLabel);
			root.setBottom(bottom);
			Thread readerThread = new Thread(new EingehendReader());
			readerThread.setDaemon(true);
			readerThread.start();

			Scene scene = new Scene(root, 800, 600);

			primaryStage.setScene(scene);
			primaryStage.show();

			Timer t = new Timer(true);
			SecondsTask st = new SecondsTask();
			t.schedule(st, 0, 1000);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void stop() {
		System.out.println("Schliessen des Sockets und Writers....");
		try {
			sock.close();
			//reader.close();
			//writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void netzwerkEinrichten() {
		try {
			sock = new Socket("127.0.0.1", 5001);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("Netzwerkverbindung steht...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class EingehendReader implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {

					String nachricht = reader.readLine();
					if (nachricht == null)
						continue;

					System.out.printf("Nachricht: %s\n", nachricht);

					if (nachricht.startsWith("#BUDDY")) {
						String[] nParts = nachricht.split(",");
						String[] buddies = new String[nParts.length - 1];
						for (int i = 1; i < nParts.length; i++) {
							buddies[i - 1] = nParts[i];
						}
						System.out.println(Arrays.toString(buddies));
						Platform.runLater(() -> {
							users.setItems(FXCollections.observableArrayList(buddies));
						});
					} else {
						chatTrace.appendText(nachricht + "\n");
					}

				}
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Socket bereits geschlossen");
			}
		}
	}

	public class SecondsTask extends TimerTask {

		@Override
		public void run() {
			Platform.runLater(() -> {
				timeLabel.setText(seconds++ + "");
			});
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
