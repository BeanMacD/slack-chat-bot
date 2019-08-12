package knowbot.dao;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;

public class MongoConnection {

	public void connectToDB(String jsonSession) {
		MongoClient mongo = null;

		// connecting to mongodb
		try {
			mongo = new MongoClient("52.232.43.104", 27017);
		} catch (UnknownHostException e) {
			System.out.println("DATABASE DIDNT CONNECT ");
			e.printStackTrace();
		}

		System.out.println("Off to get monged ???");
		DB database = mongo.getDB("knowbot");
		DBCollection collection = database.getCollection("session");

		DBObject dbObject = (DBObject) JSON.parse(jsonSession);

		collection.insert(dbObject);
		System.out.println("She's gone off to get monged");
		mongo.close();

	}

}