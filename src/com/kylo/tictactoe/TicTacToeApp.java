package com.kylo.tictactoe;


import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TicTacToeApp extends Application {
	
	private boolean playable = true; //flag to check if the game is over 
	private boolean turnX = true;	//player with x starts and makes a flag to create taking turns between x and o players
	private Tile[][] board = new Tile[3][3];  //3x3 grid or 2d array for the playing board, used to populate the combo possibilities
	private List<Combo> combos = new ArrayList<>();	//holds all of the possible combos in tictactoe
	
	private Pane root = new Pane();  //creates the pane to put the tiles in the window
	
	//creates the window
	private Parent createContent() {
		root.setPrefSize(600,  600); //set the size of pane to 600 x 600 
		
		//creates the grid in the application for the tiles to show 3x3
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				Tile tile = new Tile();	//creates new tile
				tile.setTranslateX(j * 200);  //j is x value
				tile.setTranslateY(i * 200);	//i is y value
				
				root.getChildren().add(tile);	//adds more tiles to the scene
			
				//Assigning the tile spots to the 2d array of board.
				board[j][i] = tile;
			}
		}
		//Horizontal combos
		for (int y = 0; y < 3; y++) { //using the y values which are the ones going across
			combos.add(new Combo(board[0][y], board[1][y], board[2][y]));	//creates the combos with the three tiles  
		}
		
		//Vertical combos
		for (int x = 0; x < 3; x++) { //using the x values which are the ones going down
			combos.add(new Combo(board[x][0], board[x][1], board[x][2]));	//creates the combos with the three tiles  
		}
		
		//Diagonal combos
		combos.add(new Combo(board[0][0], board[1][1], board[2][2]));	//creates the combos with the three tiles  
		combos.add(new Combo(board[2][0], board[1][1], board[0][2]));	//creates the combos with the three tiles  
		
		
		return root;
	}
	
	//the start method
	@Override
	public void start(Stage primaryStage) throws Exception {
		//method to display application on the stage in the window on the pane
		primaryStage.setScene (new Scene(createContent()));	//creates stage and scene
		primaryStage.show();	//shows the stage
	}
	
	//method to check if the game is over or playable by checking the possible combos
	private void checkState() {
		//goes through each combo in the list 
		for (Combo combo : combos) {
			if (combo.isComplete()) {	//if the combo is complete then someone has won
				playable = false;	//makes playable false to end game 
				playWinAnimation(combo);	//draws a line through the winning combo
				break;	//breaks the loop for checking combos
			}
		}
		
	}
	
	//method to draw the line through the winning combo
	private void playWinAnimation(Combo combo) {
		Line line = new Line();	//creates line 
		line.setStartX(combo.tiles[0].getCenterX()); //starts line with the first element 
		line.setStartY(combo.tiles[0].getCenterY());
		line.setEndX(combo.tiles[0].getCenterX());	//ends the line in the last element
		line.setEndY(combo.tiles[0].getCenterY());
		
		root.getChildren().add(line); //adds the line to the screen as a node 
		
		Timeline timeline = new Timeline(); //controls animation showing over the amount of time specified draws the line through the tiles
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
				new KeyValue(line.endXProperty(), combo.tiles[2].getCenterX()),
				new KeyValue(line.endYProperty(), combo.tiles[2].getCenterY())));
		timeline.play(); //starts the animation
	}
	
	//data structure to check the possible combos for winning or a tie game
	private class Combo {
		private Tile[] tiles; //array of 3 tiles 
		public Combo(Tile... tiles) {  //constructor of three tiles
			this.tiles = tiles;
		}
		
		//checks if there are 3 in a row and a winner
		public boolean isComplete() { 
			if (tiles[0].getValue().isEmpty())
				return false; 	//returns a non complete row of three because one is empty
			
			//the following checks if the values of the three tiles are equal whether x or o and if they all match
			return tiles[0].getValue().equals(tiles[1].getValue())
					&& tiles[0].getValue().equals(tiles[2].getValue());
			
		}
	}
	
	//class to create the tiles in the tictactoe board for play
	private class Tile extends StackPane {
		private Text text = new Text(); //creates the tile with empty text area 
		
		
		public Tile() {		//contructor for the tile during play
			Rectangle border = new Rectangle(200, 200);  //creates border for the squares perfect 3x3 tile arrangement on pane
			border.setFill(null);  //setting the fill color to null makes border completely transparent
			border.setStroke(Color.BLACK);  // the color of the border is set  to black
			
			text.setFont(Font.font(72));  //set the font size of the x and o
			
			setAlignment(Pos.CENTER);	//set the tiles to center in the stack pane
			getChildren().addAll(border, text); //adds the other tile objects or children created in the game
			
			//this method writes to the tile when the tile is clicked with the mouse and it finds the point that is clicked to know where in the scene  
			setOnMouseClicked(event -> {
				if (!playable)	//checks if it is end of game
					return;
				
				
				if (event.getButton() == MouseButton.PRIMARY) { //finds the place the mouse is clicked with the left button and draws x
					if (getValue() != "")
						return;
					
					if (!turnX)  //checks if o's turn then skips the draw x
						return;
						
					drawX();
					turnX = false;  //make the turnx false and goes to o turn
					checkState(); //checks if the game is over
				}
				else if (event.getButton() == MouseButton.SECONDARY) {  //finds the node where clicked and draws an O
					if (getValue() != "")
						return;
					
					if (turnX)	//checks if x turn then skips draw o
						return;
						
					drawO();
					turnX = true; //makes the turnX true to make it x's turn.
					checkState(); //checks if the game is over.
				}
			});
			
		}
		
		//get the value of the center of the tile
		public double getCenterX() {
			return getTranslateX() + 100; //returns the center of the tile from x and adds half size of tile
		}
		
		//get the value of the center of the tile
		public double getCenterY() {
			return getTranslateY() + 100; //returns the center of the tile from y and adds half size of tile
		}
		
		//get the value of the test in the tiles
		public String getValue() {
			return text.getText();
		}
		
		//draws the x in the tile
		private void drawX() {
			text.setText("X");
		}
		
		//draws the o in the tile
		private void drawO() {
			text.setText("O");
		}
		
	}
	
	
//this is where main starts the application
	public static void main(String[] args) {
		launch(args); //

	}

}
