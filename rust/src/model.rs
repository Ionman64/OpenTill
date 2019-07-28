use schema::*;

use diesel::prelude::*;
use diesel::{Insertable, Queryable, SqliteConnection};
use utils as app;

use chrono::{NaiveDateTime, Utc};
use serde::{Serialize, Deserialize};
use rocket::request::FromRequest;

use models::Department;


use config;








#[derive(Deserialize)]
pub struct NewUser {
    pub name: String,
    pub telephone: String,
    pub email: String,
    pub password: String,
}





#[derive(FromForm)]
pub struct LoginRequest {
    pub email: String,
    pub password: String,
}











