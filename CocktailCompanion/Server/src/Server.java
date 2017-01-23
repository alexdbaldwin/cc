import static spark.Spark.*;

import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server {

	public static void main(String[] args) {
		port(8081);
		enableCORS("*", "*", "*");
		
		Database db = new Database();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		delete("/cocktails/:id", (req,res)->{

			db.removeCocktail(Integer.parseInt(req.params("id")));
			return "{}";
		});
		
		delete("/ingredients/:id", (req,res)->{
			db.removeIngredient(Integer.parseInt(req.params("id")));
			return "{}";
		});
		
		put("/ingredients", (req,res)->{
			res.body("");
			if (req.contentType().equals("application/json")) {
				String json = req.body();
				Ingredient ingredient = (Ingredient) gson.fromJson(json, Ingredient.class);
				db.updateIngredient(ingredient);
				
				res.body(gson.toJson(ingredient)); //NOT SURE ABOUT THIS
			}
			
			return res.body();
		});
		
		put("/cocktails", (req,res)->{
			res.body("");
			if (req.contentType().equals("application/json")) {
				String json = req.body();
				Cocktail cocktail = (Cocktail) gson.fromJson(json, Cocktail.class);
				db.updateCocktail(cocktail);
				
				res.body(gson.toJson(cocktail)); //NOT SURE ABOUT THIS
			}
			
			return res.body();
		});
		
		get("/cocktails", (req,res)-> {
			try{
			return gson.toJson(db.getCocktails());
			} catch (SQLException e){
				e.printStackTrace();
			}
			return null;
		});
		
		get("/ingredients", (req,res)-> {
			return gson.toJson(db.getIngredients());
		});
		
		post("/cocktails", (req,res)-> {
			res.body("");
			if (req.contentType().equals("application/json")) {
				String json = req.body();
				Cocktail cocktail = (Cocktail) gson.fromJson(json, Cocktail.class);
				try{
					db.addCocktail(cocktail);
				} catch (SQLException e){
					e.printStackTrace();
				}
				
				//SHOULD RETURN THE NEW ID
				
				res.body(gson.toJson(cocktail)); //NOT SURE ABOUT THIS
			}
			
			return res.body();
		});
		
		post("/ingredients", (req,res)-> {
			res.body("");
			
			if (req.contentType().equals("application/json")) {
				String json = req.body();
				System.out.println(json);
				Ingredient ingredient = (Ingredient) gson.fromJson(json, Ingredient.class);
				db.addIngredient(ingredient.name, ingredient.unit, ingredient.pantryAmount);
				
				//SHOULD RETURN THE NEW ID
				
				res.body(gson.toJson(ingredient)); //NOT SURE ABOUT THIS
			}
			
			return res.body();
		});
		
		//etc...

	}
	
	private static void enableCORS(final String origin, final String methods, final String headers) {

	    options("/*", (request, response) -> {

	        String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
	        if (accessControlRequestHeaders != null) {
	            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
	        }

	        String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
	        if (accessControlRequestMethod != null) {
	            response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
	        }

	        return "OK";
	    });

	    before((request, response) -> {
	        response.header("Access-Control-Allow-Origin", origin);
	        response.header("Access-Control-Request-Method", methods);
	        response.header("Access-Control-Allow-Headers", headers);
	        // Note: this may or may not be necessary in your particular application
	        response.type("application/json");
	    });
	}

}
