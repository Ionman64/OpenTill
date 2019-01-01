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
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.google.gson.Gson;
import com.opentill.database.DatabaseHandler;
import com.opentill.logging.Log;
import com.opentill.main.Config;
import com.opentill.main.Utils;
import com.opentill.models.ProductModel;
import com.opentill.models.SupplierModel;


@Path("supplier")
@Produces(MediaType.APPLICATION_JSON)
public class SupplierController  {
	
	@GET
	@Path("{id}")
	public String findById(@PathParam("id") String id) {
		try {
			Configuration configuration = new Configuration().addAnnotatedClass(SupplierModel.class).setProperties(Config.databaseProperties);
	        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
	        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	        
	        Session session = sessionFactory.openSession();
			SupplierModel supplier = session.byId( SupplierModel.class ).load(id);

			return new Gson().toJson(supplier);
		}
		catch (NoResultException e) {
			return "Not Found";
		}
	}
}
