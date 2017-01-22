import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

	Connection connection = null;
	
	public Database(){
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:test.db");
		    setup();
		} catch (Exception e){
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	
	private void setup() throws SQLException{
		Statement statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS Cocktails(id INTEGER PRIMARY KEY, name TEXT, method TEXT); "
				+ "CREATE TABLE IF NOT EXISTS Ingredients(id INTEGER PRIMARY KEY, name TEXT, unit TEXT, pantryamount REAL); "
				+ "CREATE TABLE IF NOT EXISTS CocktailIngredients(cocktail INTEGER, ingredient INTEGER, quantity REAL, unit TEXT, FOREIGN KEY(cocktail) REFERENCES Cocktails(id), FOREIGN KEY(ingredient) REFERENCES Ingredients(id))");
		statement.close();
	}
	
	public void addIngredient(String name, String unit, double pantryAmount) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("INSERT INTO Ingredients(name, unit, pantryamount) VALUES (?, ?, ?);");
		statement.setString(1, name);
		statement.setString(2, unit);
		statement.setDouble(3, pantryAmount);
		statement.executeUpdate();
		statement.close();
	}
	
	public void addCocktail(String name, String method) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("INSERT INTO Cocktails(name, method) VALUES (?,?);");
		statement.setString(1, name);
		statement.setString(2, method);
		statement.executeUpdate();
		statement.close();		
	}
	
	public void updateCocktail(Cocktail cocktail) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("UPDATE Cocktails SET name = ?, method = ? WHERE id = ?");
		statement.setString(1, cocktail.name);
		statement.setString(2, cocktail.method);
		statement.setInt(3, cocktail.id);
		statement.executeUpdate();
		statement.close();
	}
	
	public void updateIngredient(Ingredient ingredient) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("UPDATE Ingredients SET name = ?, unit = ?, pantryamount = ? WHERE id = ?");
		statement.setString(1, ingredient.name);
		statement.setString(2, ingredient.unit);
		statement.setDouble(3, ingredient.pantryAmount);
		statement.setInt(4, ingredient.id);
		statement.executeUpdate();
		statement.close();
	}
	
	public void removeCocktail(int id) throws SQLException{
		//Remove cocktail and nothing else
		PreparedStatement statement = connection.prepareStatement("DELETE FROM Cocktails WHERE id=" + id);
		statement.executeUpdate();
		statement.close();
	}
	
	public void removeIngredient(int id) throws SQLException{
		//Remove ingredient and all cocktails that contain it... eventually!
		
		//TODO: Remove cocktails that rely on this ingredient
		
		PreparedStatement statement = connection.prepareStatement("DELETE FROM Ingredients WHERE id=" + id);
		statement.executeUpdate();
		statement.close();

	}
	
	public void addCocktailIngredient(int cocktailId, int ingredientId, int quantity, String unit) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("INSERT INTO CocktailIngredients(cocktail,ingredient,quantitiy,unit) VALUES (?,?,?,?);");
		statement.setInt(1, cocktailId);
		statement.setInt(2, ingredientId);
		statement.setInt(3, quantity);
		statement.setString(4, unit);
		statement.executeUpdate();
		statement.close();		
	}
	
//	public List<Cocktail> WhatCanIMakeWithThese(List<Ingredient> ingredients) throws SQLException{
//		return null;
//	}
	
	public List<Ingredient> getIngredients() throws SQLException{ 
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM Ingredients ORDER BY name ASC");
		while (rs.next()) {
			Ingredient ingredient = new Ingredient();
			ingredient.id = rs.getInt("id");
			ingredient.name = rs.getString("name");
			ingredient.unit = rs.getString("unit");
			ingredient.pantryAmount = rs.getDouble("pantryamount");
			ingredients.add(ingredient);
		}
		
		statement.close();
		return ingredients;
	}
	
	public List<Cocktail> getCocktails() throws SQLException{
		List<Cocktail> cocktails = new ArrayList<Cocktail>();
		
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM Cocktails ORDER BY name ASC");
		while (rs.next()) {
			Cocktail cocktail = new Cocktail();
			cocktail.id = rs.getInt("id");
			cocktail.name = rs.getString("name");
			cocktail.method = rs.getString("method");
			cocktails.add(cocktail);
		}
		
		statement.close();
		return cocktails;
	}
	
	
	//etc...
	
}
