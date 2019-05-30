use schema::*;

use diesel::prelude::*;
use diesel::{Insertable, Queryable};
use utils as app;

use chrono::{NaiveDateTime, Utc};
use serde::Deserialize;

use config;

#[table_name = "products"]
#[derive(Queryable, Insertable)]
pub struct Product {
    pub id: String,
    pub name: String,
    pub barcode: String,
    pub price: i32,
    pub department: String,
    pub supplier: String,
    #[column_name = "labelPrinted"]
    pub label_printed: bool,
    #[column_name = "isCase"]
    pub is_case: bool,
    pub updated: NaiveDateTime,
    pub created: NaiveDateTime,
    pub deleted: bool,
    pub max_stock: i32,
    pub current_stock: i32,
}

impl Product {
    pub fn new(name: String, barcode: String, price: i32) -> Product {
        Product {
            id: app::uuid4().to_string(),
            name: name,
            barcode: barcode,
            price: price,
            department: String::from(config::NO_DEPARTMENT_GUID),
            supplier: String::from(config::NO_SUPPLIER_GUID),
            label_printed: false,
            is_case: false,
            updated: Utc::now().naive_utc(),
            created: Utc::now().naive_utc(),
            deleted: false,
            max_stock: 0,
            current_stock: 0,
        }
    }
    pub fn default() -> Product {
        Product::new(String::from("Unknown Product"), String::from("01"), 0)
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
    pub fn find_by_id(id: &str) -> Option<Product> {
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
    pub fn find_by_barcode(code: &str) -> Option<Product> {
        let conn = app::establish_connection();
        match products::table
            .filter(products::barcode.eq(code))
            .first::<Product>(&conn)
        {
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
        1 == match diesel::replace_into(products::table)
            .values(self)
            .execute(&conn)
        {
            Ok(x) => x,
            Err(x) => {
                error!("{}", x);
                0
            }
        }
    }
}

#[table_name = "suppliers"]
#[derive(Queryable, Insertable)]
pub struct Supplier {
    pub id: String,
    pub name: String,
    pub telephone: String,
    pub website: String,
    pub email: String,
}

impl Supplier {
    pub fn new(name: String, telephone: String, website: String, email: String) -> Supplier {
        Supplier {
            id: app::uuid4().to_string(),
            name: name,
            telephone: telephone,
            website: website,
            email: email,
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
    pub fn find_by_id(id: &str) -> Option<Supplier> {
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
        match diesel::replace_into(suppliers::table)
            .values(self)
            .execute(&conn)
        {
            Ok(x) => x,
            Err(_x) => 0,
        }
    }
}

#[table_name = "users"]
#[derive(Queryable, Insertable)]
pub struct User {
    pub id: String,
    pub name: String,
    pub telephone: String,
    pub email: String,
    pub password_hash: String,
    pub code: String,
}

impl User {
    pub fn new(
        name: String,
        telephone: String,
        _website: String,
        email: String,
        password_hash: String,
        code: String,
    ) -> User {
        User {
            id: app::uuid4().to_string(),
            name: name,
            telephone: telephone,
            email: email,
            password_hash: password_hash,
            code: code,
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
        User::new(
            String::from("Unknown User"),
            String::from(""),
            String::from(""),
            String::from(""),
            app::hash_password(String::from("changeme")).unwrap(),
            app::generate_user_code(),
        )
    }
    pub fn save(&self) -> usize {
        let conn = app::establish_connection();
        match diesel::replace_into(users::table)
            .values(self)
            .execute(&conn)
        {
            Ok(x) => x,
            Err(_x) => 0,
        }
    }
}

#[derive(FromForm)]
pub struct LoginRequest {
    pub email: String,
    pub password: String,
}

#[table_name = "versions"]
#[derive(Deserialize, Debug, Queryable, Insertable)]
pub struct Version {
    pub id: String,
    #[serde(with = "date_serializer")]
    pub release_date: NaiveDateTime,
    pub major: i32,
    pub minor: i32,
}

impl Version {
    pub fn save(&self) -> usize {
        let conn = app::establish_connection();
        match diesel::replace_into(versions::table)
            .values(self)
            .execute(&conn)
        {
            Ok(x) => x,
            Err(_x) => 0,
        }
    }
}

#[derive(serde::Serialize, Deserialize)]
pub struct TemplateContent {
    pub logo: String,
    pub app_name: String,
    pub app_version_major: i32,
    pub app_version_minor: i32,
}

impl TemplateContent {
    pub fn new() -> TemplateContent {
        TemplateContent {
            logo: app::logo_ascii(),
            app_name: String::from(config::APP_NAME),
            app_version_major: config::APP_VERSION_MAJOR,
            app_version_minor: config::APP_VERSION_MINOR,
        }
    }
}

#[table_name = "servers"]
#[derive(serde::Serialize, Deserialize, Queryable, Insertable)]
pub struct Server {
    pub id: String,
    pub major: i32,
    pub minor: i32,
    pub ip_address: String,
}

impl Server {
    pub fn details() -> Server {
        Server {
            id: AppConfiguration::get(config::INSTANCE_GUID_KEY)
                .expect("Cannot find INSTANCE_GUID_KEY in configuration"),
            major: config::APP_VERSION_MAJOR,
            minor: config::APP_VERSION_MINOR,
            ip_address: String::from("[Unknown]"),
        }
    }
    pub fn get_all() -> Vec<Server> {
        let conn = app::establish_connection();
        match servers::table.load::<Server>(&conn) {
            Ok(x) => x,
            Err(x) => {
                warn!("{}", x);
                Vec::new()
            }
        }
    }
    pub fn save(&self) -> usize {
        let conn = app::establish_connection();
        match diesel::replace_into(servers::table)
            .values(self)
            .execute(&conn)
        {
            Ok(x) => x,
            Err(_x) => 0,
        }
    }
}

#[table_name = "configurations"]
#[derive(Deserialize, Debug, Queryable, Insertable)]
pub struct AppConfiguration {
    #[column_name = "config_key"]
    pub key: String,
    #[column_name = "config_value"]
    pub value: String,
}

impl AppConfiguration {
    pub fn new(key: &str, value: &str) -> AppConfiguration {
        AppConfiguration {
            key: String::from(key),
            value: String::from(value),
        }
    }
    pub fn get(key: &str) -> Option<String> {
        let conn = app::establish_connection();
        match configurations::table
            .find(key)
            .get_result::<AppConfiguration>(&conn)
        {
            Ok(x) => Some(x.value),
            Err(diesel::NotFound) => None,
            Err(x) => {
                warn!("{}", x);
                None
            }
        }
    }
    pub fn save(&self) -> usize {
        let conn = app::establish_connection();
        match diesel::replace_into(configurations::table)
            .values(self)
            .execute(&conn)
        {
            Ok(x) => x,
            Err(_x) => 0,
        }
    }
}

//Taken from https://earvinkayonga.com/posts/deserialize-date-in-rust/
mod date_serializer {
    use chrono::{DateTime, NaiveDateTime, Utc};
    use serde::{de::Error, Deserialize, Deserializer, Serialize, Serializer};

    fn time_to_json(t: NaiveDateTime) -> String {
        DateTime::<Utc>::from_utc(t, Utc).to_rfc3339()
    }

    pub fn serialize<S: Serializer>(
        time: &NaiveDateTime,
        serializer: S,
    ) -> Result<S::Ok, S::Error> {
        time_to_json(*time).serialize(serializer)
    }

    pub fn deserialize<'de, D: Deserializer<'de>>(
        deserializer: D,
    ) -> Result<NaiveDateTime, D::Error> {
        let time: String = Deserialize::deserialize(deserializer)?;
        Ok(NaiveDateTime::parse_from_str(&time, "%Y-%m-%d %H:%M").map_err(D::Error::custom)?)
    }
}
