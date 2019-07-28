use rocket_contrib::json::Json;

use rocket::http::Status;
use models::Database::DatabaseConnection;
use responses::CustomResponse::CustomResponse;

use utils as app;

use models::User::{User, NewUser};

#[post("/login")]
fn login(conn:Database::DatabaseConnection, code:String) -> Result<Json<User>, rocket::response::status::Custom<&'static str>> {
    match User::find_by_code(code, &conn) {
        Some(x) => {
            return Ok(Json(x));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::NotFound, "Could not get user"));
        }
    };
}