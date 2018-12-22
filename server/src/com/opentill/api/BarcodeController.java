package com.opentill.api;

import java.math.BigInteger;
import java.sql.SQLException;

import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.google.gson.Gson;
import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Utils;
import com.opentill.models.ProductModel;


@Path("barcode")
@Produces(MediaType.APPLICATION_JSON)
public class BarcodeController  {
	
	@GET
	public String find() {
		return "index";
	}
	
	@GET
	@Path("/{barcode}")
	public String create(@PathParam("barcode") long barcode) {
		try {
			Session session = DatabaseHandler.getDatabaseSession();
			Query query = session.createQuery(Utils.addTablePrefix("from Product where barcode = :barcode"));
			query.setParameter("barcode", barcode);
			ProductModel product = (ProductModel) query.getSingleResult();
			return new Gson().toJson(product);
		}
		catch (NoResultException e) {
			return "Not Found";
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			//DoNothing
		}
		return null;
	}
	
	@GET
	@Path("/{id}")
	public String create(@PathParam("id") String id) {
		try {
			Session session = DatabaseHandler.getDatabaseSession();
			Query query = session.createQuery(Utils.addTablePrefix("from Product where barcode = :id"));
			query.setParameter("id", id);
			ProductModel product = (ProductModel) query.getSingleResult();
			return new Gson().toJson(product);
		}
		catch (NoResultException e) {
			return "Not Found";
		} catch (SQLException ex) {
			Log.info(ex.toString());
		} finally {
			//DoNothing
		}
		return null;
	}

}
