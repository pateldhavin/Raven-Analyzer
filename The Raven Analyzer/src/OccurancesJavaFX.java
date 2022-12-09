import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;


/**This is an application that counts occurances of words in the poem, "The Raven".
 * @author Dhavin Patel
 *@version 1.2  Created on Nov 3, 2022
 */


public class OccurancesJavaFX extends Application {
	
	
	/**
	 * String used during GUI creation
	 */
	public static final String BLANK = "";

	/**
	 * Variables for GUI exportation of occurances results
	 */
	GridPane grid;
	Label wordLabel, countLabel;
	TextField wordField, countField;
	Button countButton;

	Map<String, Integer> map = new HashMap<String, Integer>();

	
	
	@Override
	public void start(Stage stage) throws Exception {

		wordsMap();

		//configuring box for user input
		
		stage.setTitle("Occurances of Words from : The Raven");

		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		wordLabel = new Label("Enter Word from Poem:");
		grid.add(wordLabel, 0, 0);

		wordField = new TextField();
		grid.add(wordField, 1, 0);

		countButton = new Button("Calculate Occurences");
		grid.add(countButton, 1, 1);

		countLabel = new Label("Number of Occurences:");
		grid.add(countLabel, 0, 2);

		countField = new TextField();
		countField.setEditable(false);
		grid.add(countField, 1, 2);

		countButton.setOnAction(actionEvent -> {
			countField.setText(BLANK);
			String word = wordField.getText();
			if (BLANK.equals(word)) {
				this.alert("No Input", "Please enter a word to search", AlertType.ERROR);
				return;
			}
			map.get(word);
			int count = (map.get(word) == null) ? 0 : map.get(word);
			countField.setText(String.valueOf(count));
		});

		Scene scene = new Scene(grid, 700, 275);
		stage.setScene(scene);

		stage.show();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	
	String wordsMap() throws Exception {
	
		String line;
/**
*path to text file
*/
		BufferedReader bufferedReader = new BufferedReader(new FileReader("C:/Users/dgato/OneDrive/Desktop/The Raven.txt"));

		while ((line = bufferedReader.readLine()) != null) {
			String[] words = line.split("[\\s.;,?:!()\"]+");//line split
			for (String word : words)
				if (map.containsKey(word) == false)
					map.put(word, 1);
				else {
					int count = (int) (map.get(word));
					map.remove(word);
					map.put(word, count + 1);
					
				}
		}
		
		
	/**
	 * sort the list
	 */
		Set<Map.Entry<String, Integer>> sortedWordcounts = map.entrySet();
		List<Map.Entry<String, Integer>> sortedList = new ArrayList<Map.Entry<String, Integer>>(sortedWordcounts);
		Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
				return (b.getValue()).compareTo(a.getValue());
			}
		});

		/**
		 * output results
		 */
		System.out.printf("%-20s%15s\n", "Word From Poem", "Frequency");

		System.out.printf("%-20s%15s\n", "--------------", "---------");
		for (Map.Entry<String, Integer> i : sortedList) {
			System.out.printf("%-20s%10s\n", i.getKey(), i.getValue());
			
		}
		try {
			
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/wordoccurances","root","root");
			
			Statement statement = connection.createStatement();
			
			ResultSet resultSet = statement.executeQuery("select * from word");
			System.out.printf("\n" + "words from database");
			System.out.printf("\n");

			
			while (resultSet.next()) {
				
				System.out.printf("\n");
				
				System.out.println(resultSet.getString("word")+ " ---> "+ resultSet.getString("occurances" ));
				
				

			}

			} catch (Exception e) {
				e.printStackTrace();
			}

		bufferedReader.close();
		return line;

	}
	
		
	
	
	/**
	 * @param title Title of alert
	 * @param message Alert message that is displayed
	 * @param alertType The type of alert
	 */
	public void alert(String title, String message, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	
	/**The main method of this application.
	 * @param args Array of string arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}