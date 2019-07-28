use rocket_contrib::json::Json;

use models::Database::DatabaseConnection;
use responses::CustomResponse::CustomResponse;
use rocket::http::Status;

use utils as app;

use models::User::{NewUser, User};

#[get("/")]
pub fn get_all(
    conn: DatabaseConnection,
) -> Result<Json<Vec<User>>, rocket::response::status::Custom<&'static str>> {
    match User::get_all(&conn) {
        Some(x) => {
            return Ok(Json(x.into_iter().filter(|user| !user.deleted).collect()));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not read users",
            ));
        }
    };
}

#[get("/<id>")]
pub fn get(
    conn: DatabaseConnection,
    id: String,
) -> Result<Json<User>, rocket::response::status::Custom<&'static str>> {
    match User::find_by_id(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(x));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::NotFound,
                "Could not get user",
            ));
        }
    };
}

#[post("/", format = "application/json", data = "<new_user>")]
pub fn insert(
    conn: DatabaseConnection,
    new_user: Json<NewUser>,
) -> Result<Json<User>, rocket::response::status::Custom<&'static str>> {
    let user = User::new(
        new_user.0.name,
        new_user.0.telephone,
        new_user.0.email,
        app::hash_password(new_user.0.password.as_str()),
    );
    match user.insert(&conn) {
        Some(_x) => {
            return Ok(Json(user));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not create user",
            ));
        }
    }
}

#[delete("/<id>")]
pub fn delete(
    conn: DatabaseConnection,
    id: String,
) -> Result<Json<CustomResponse>, rocket::response::status::Custom<&'static str>> {
    match User::delete(id.as_str(), &conn) {
        Some(_x) => {
            return Ok(Json(CustomResponse::success()));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not create user",
            ));
        }
    }
}
