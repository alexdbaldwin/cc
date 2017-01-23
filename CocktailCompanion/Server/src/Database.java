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
				+ "CREATE TABLE IF NOT EXISTS CocktailIngredients(cocktail INTEGER, ingredient INTEGER, quantity REAL, FOREIGN KEY(cocktail) REFERENCES Cocktails(id), FOREIGN KEY(ingredient) REFERENCES Ingredients(id))");
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
	
	public void addCocktail(Cocktail cocktail) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("INSERT INTO Cocktails(name, method) VALUES (?,?);");
		statement.setString(1, cocktail.name);
		statement.setString(2, cocktail.method);
		statement.executeUpdate();
		
		Statement s = connection.createStatement();
		ResultSet rs = s.executeQuery("SELECT last_insert_rowid() AS last");
		cocktail.id = rs.getInt("last");
		System.out.println(cocktail.id);
		
		//file_entry = query_db('SELECT last_insert_rowid()')
		statement.close();
		for(int i = 0; i < cocktail.ingredients.size(); i++){
			addCocktailIngredient(cocktail, cocktail.ingredients.get(i));
		}
	}
	
	private void addCocktailIngredient(Cocktail cocktail, Ingredient ingredient) throws SQLException{
		PreparedStatement statement = connection.prepareStatement("INSERT INTO CocktailIngredients(cocktail, ingredient, quantity) VALUES (?,?,?);");
		statement.setInt(1, cocktail.id);
		statement.setInt(2, ingredient.id);
		statement.setDouble(3, ingredient.amount);
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
		
		//TODO: Remove existing ingredients, add in the new ones....
		
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
			
			Statement ingredientS =  connection.createStatement();
			ResultSet iRS = ingredientS.executeQuery("SELECT Ingredients.id AS iid, CocktailIngredients.quantity AS ciamount, Ingredients.name AS iname, Ingredients.unit AS iunit FROM ((Cocktails INNER JOIN CocktailIngredients ON Cocktails.id = CocktailIngredients.cocktail) INNER JOIN Ingredients ON CocktailIngredients.ingredient = Ingredients.id)");
			while(iRS.next()){
				Ingredient ingredient = new Ingredient();
				ingredient.id = iRS.getInt("iid");
				ingredient.amount = iRS.getDouble("ciamount");
				ingredient.name = iRS.getString("iname");
				ingredient.unit = iRS.getString("iunit");
				cocktail.ingredients.add(ingredient);
			}
			ingredientS.close();
			
			cocktails.add(cocktail);
			
		}
		
		statement.close();
		return cocktails;
	}
	
	
	//etc...
	
}
