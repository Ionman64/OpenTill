use rocket_contrib::databases::diesel;

use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use utils as app;

use chrono::{NaiveDateTime, Utc};
use schema::*;
use serde::{Deserialize, Serialize};
use models::Database::DatabaseConnection;

use rocket::Outcome;
use rocket::http::Status;
use rocket::request::{self, Request, FromRequest};

use config;

use rocket::outcome::IntoOutcome;

#[table_name = "users"]
#[derive(Queryable, Insertable, Serialize, Deserialize)]
pub struct User {
    pub id: String,
    pub name: String,
    pub telephone: String,
    pub email: String,
    #[serde(skip_serializing)]
    pub password_hash: String,
    pub code: String,
    pub updated: NaiveDateTime,
    pub created: NaiveDateTime,
    pub deleted: bool,
}

impl<'a, 'r> FromRequest<'a, 'r> for User {
    type Error = std::convert::Infallible;

    fn from_request(request: &'a Request<'r>) -> request::Outcome<User, Self::Error> {
        let db = match request.guard::<DatabaseConnection>().succeeded() {
            Some(x) => x,
            None => {
                panic!("");
            }
        };
        request.cookies()
            .get_private(config::AUTH_COOKIE_NAME)
            .and_then(|cookie| User::find_by_id(cookie.value(), &db))
            .or_forward(())
    }
}

impl User {
    pub fn new(name: String, telephone: String, email: String, password_hash: String) -> User {
        User {
            id: app::uuid4().to_string(),
            name: name,
            telephone: telephone,
            email: email,
            password_hash: password_hash,
            code: app::generate_user_code(),
            updated: Utc::now().naive_utc(),
            created: Utc::now().naive_utc(),
            deleted: false,
        }
    }
    pub fn find_by_id(id: &str, conn: &SqliteConnection) -> Option<User> {
        match users::table.find(id).get_result::<User>(conn) {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                warn!("{}", x);
                None
            }
        }
    }
    pub fn find_by_code(code: String, conn: &SqliteConnection) -> Option<User> {
        match users::table.filter(users::code.eq(code)).get_result::<User>(conn)
        {
            Ok(x) => Some(x),
            Err(diesel::NotFound) => None,
            Err(x) => {
                warn!("{}", x);
                None
            }
        }
    }
    pub fn find_by_username_and_password(email: String, password: String, conn: &SqliteConnection) -> Option<User> {
        match users::table.filter(users::email.eq(&email)).filter(users::password_hash.eq(&app::hash_password(&password))).get_result::<User>(conn)
        {
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
            String::from(app::hash_password("changeme")),
        )
    }
    pub fn get_all(conn: &SqliteConnection) -> Option<Vec<User>> {
        match users::table.load(conn) {
            Ok(x) => Some(x),
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn insert(&self, conn: &SqliteConnection) -> Option<usize> {
        match diesel::insert_into(users::table).values(self).execute(conn) {
            Ok(x) => Some(x),
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
    pub fn delete(user_id: &str, conn: &SqliteConnection) -> Option<usize> {
        //Assets are never deleted
        use schema::users::dsl::*;
        match diesel::update(users.filter(id.eq(user_id)))
            .set(deleted.eq(true))
            .execute(conn)
        {
            Ok(x) => Some(x),
            Err(x) => {
                error!("{}", x);
                None
            }
        }
    }
}

#[derive(Deserialize)]
pub struct NewUser {
    pub name: String,
    pub telephone: String,
    pub email: String,
    pub password: String,
}
