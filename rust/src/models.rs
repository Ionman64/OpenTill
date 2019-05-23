use schema::*;

use diesel::{Insertable, Queryable};
use diesel::prelude::*;
use utils as app;
use rocket::request::Form;
use serde::{Serialize, Deserialize};
use chrono::{DateTime, Duration, NaiveDate, NaiveDateTime, NaiveTime, TimeZone, Utc};

#[table_name="products"]
#[derive(Queryable,Insertable)]
pub struct Product {
    pub id: String,
    pub name: String,
    pub barcode: String,
    pub price: i32,
}

impl Product {
    pub fn new(name:String, barcode:String, price:i32) -> Product {
        Product {
            id: app::uuid4().to_string(),
            name: name,
            barcode:barcode,
            price:price
        }
    }
    pub fn get_all() -> Vec<Product> {
        let conn = app::establish_connection();
        match products::table.load::<Product>(&conn) {
            Ok(x) => x,
            Err(x) => {
                error!("{}", x);
                Vec::new()
            }
        }
    }
    pub fn find_by_id(id: &String) -> Option<Product> {
        let conn = app::establish_connection();
        match products::table.find(id).get_result::<Product>(&conn) {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                error!("{}", x);
                None
            } 
        }
    }
    pub fn find_by_barcode(code: &String) -> Option<Product> {
        let conn = app::establish_connection();
        match products::table.filter(products::barcode.eq(code)).first::<Product>(&conn) {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn save(&self) -> bool {
        let conn = app::establish_connection();
        1 == match diesel::replace_into(products::table).values(self).execute(&conn) {
            Ok(x) => x,
            Err(x) => {
                error!("{}", x);
                0
            }
        }
    }
}

#[table_name="suppliers"]
#[derive(Queryable,Insertable)]
pub struct Supplier {
    pub id: String,
    pub name: String,
    pub telephone: String,
    pub website: String,
    pub email: String
}

impl Supplier {
    pub fn new(name:String, telephone:String, website:String, email:String) -> Supplier {
        Supplier {
            id: app::uuid4().to_string(),
            name: name,
            telephone:telephone,
            website:website,
            email:email
        }
    }
    pub fn get_all() -> Vec<Supplier> {
        let conn = app::establish_connection();
        match suppliers::table.load::<Supplier>(&conn) {
            Ok(x) => x,
            Err(x) => {
                warn!("{}", x);
                Vec::new()
            }
        }
    }
    pub fn find_by_id(id: &String) -> Option<Supplier> {
        let conn = app::establish_connection();
        match suppliers::table.find(id).get_result::<Supplier>(&conn) {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                warn!("{}", x);
                None
            } 
        }
    }
    pub fn save(&self) -> usize {
        let conn = app::establish_connection();
        match diesel::replace_into(suppliers::table).values(self).execute(&conn) {
            Ok(x) => x,
            Err(x) => 0
        }
    }
}

#[table_name="users"]
#[derive(Queryable,Insertable)]
pub struct User {
    pub id: String,
    pub name: String,
    pub telephone: String,
    pub email: String,
    pub password_hash: String,
    pub code: String
}

impl User {
    pub fn new(name:String, telephone:String, website:String, email:String, password_hash:String, code:String) -> User {
        User {
            id: app::uuid4().to_string(),
            name: name,
            telephone:telephone,
            email:email,
            password_hash: password_hash,
            code: code
        }
    }
    pub fn find_by_id(id: &String) -> Option<User> {
        let conn = app::establish_connection();
        match users::table.find(id).get_result::<User>(&conn) {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                warn!("{}", x);
                None
            } 
        }
    }
    pub fn default() -> User {
        User::new(String::from("Unknown User"), String::from(""), String::from(""), String::from(""), app::hash_password(String::from("changeme")).unwrap(), app::generate_user_code())
    }
    pub fn save(&self) -> usize {
        let conn = app::establish_connection();
        match diesel::replace_into(users::table).values(self).execute(&conn) {
            Ok(x) => x,
            Err(x) => 0
        }
    }
}

#[derive(FromForm)]
pub struct LoginRequest {
    pub email: String,
    pub password: String
}

#[table_name="versions"]
#[derive(Deserialize,Debug,Queryable,Insertable)]
pub struct Version {
    pub release_sequence_num: i32,
    pub major: i32,
    pub minor: i32,
    pub release_date: NaiveDateTime
}