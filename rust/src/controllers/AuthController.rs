use rocket_contrib::json::Json;
use rocket_contrib::databases::diesel;
use rocket::http::Status;
use models::Database::DatabaseConnection;
use responses::CustomResponse::CustomResponse;

use utils as app;

use models::User::{User, NewUser};
use models::LoginAttempt::LoginAttempt;

#[post("/login", format = "application/json", data = "<login_attempt>")]
pub fn login(conn:DatabaseConnection, login_attempt:Json<LoginAttempt>) -> Result<Json<User>, rocket::response::status::Custom<&'static str>> {
    let code = login_attempt.0.code;
    match User::find_by_code(code, &conn) {
        Some(x) => {
            return Ok(Json(x));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::NotFound, "Could not find user"));
        }
    };
}