import static spark.Spark.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server {

	public static void main(String[] args) {
		port(8080);
		enableCORS("*", "*", "*");
		
		Database db = new Database();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		get("/cocktails", (req,res)-> {
			return gson.toJson(db.getCocktails());
		});
		
		get("/ingredients", (req,res)-> {
			return gson.toJson(db.getIngredients());
		});
		
		post("/cocktails", (req,res)-> {
			res.body("");
			System.out.println("Cocktails!");
			if (req.contentType().equals("application/json")) {
				System.out.println("Made it!");
				String json = req.body();
				Cocktail cocktail = (Cocktail) gson.fromJson(json, Cocktail.class);
				db.addCocktail(cocktail.name, cocktail.method);
				
				//SHOULD RETURN THE NEW ID
				
				res.body(gson.toJson(cocktail)); //NOT SURE ABOUT THIS
			}
			
			return res.body();
		});
		
		post("/ingredients", (req,res)-> {
			res.body("");
			
			if (req.contentType().equals("application/json")) {
				String json = req.body();
				Ingredient ingredient = (Ingredient) gson.fromJson(json, Ingredient.class);
				db.addIngredient(ingredient.name);
				
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
