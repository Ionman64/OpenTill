use serde::{Deserialize, Serialize};
use rocket::request::Form;

#[derive(Serialize, Deserialize)]
pub struct CodeAttempt {
    pub code: String
}

#[derive(Serialize, Deserialize, FromForm)]
pub struct LoginAttempt {
    pub email: String,
    pub password: String
}
