/*package com.opentill.api;

import java.math.BigInteger;
import java.sql.SQLException;

import javax.persistence.NoResultException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
import com.opentill.models.UserModel;


@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class LoginController  {
	
	@POST
	@Path("login")
	public String findById(@FormParam("email") String email, @FormParam("password") String password) {
		try {
			//Subject currentUser = SecurityUtils.getSubject();
			//Log.log(currentUser.toString());
			Session session = DatabaseHandler.getDatabaseSession();
			Query query = session.createQuery(Utils.addTablePrefix("select user from User user where email=:email and passwordHash=:password"));
			query.setParameter("email", email);
			String hashPassword = Utils.hashPassword(password, "");
			query.setParameter("password", hashPassword);
			UserModel user = (UserModel) query.getSingleResult();
			Log.log(user.id);
			return "{\"success\":true}";
			//UserModel user = (UserModel) query.getSingleResult();
			//return new Gson().toJson(user);
		}
		catch (NoResultException e) {
			return "Not Found";
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}*/
