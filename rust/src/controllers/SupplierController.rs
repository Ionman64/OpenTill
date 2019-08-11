use rocket_contrib::json::Json;
use diesel::{Insertable, Queryable, SqliteConnection};
use rocket::http::Status;

use responses::CustomResponse::CustomResponse;

use serde::{Deserialize, Serialize};

use models::Database::DatabaseConnection;

use models::Supplier::{NewSupplier, Supplier};

#[get("/")]
pub fn get_all(
    conn: DatabaseConnection,
) -> Result<Json<Vec<Supplier>>, rocket::response::status::Custom<&'static str>> {
    match Supplier::get_all(&conn) {
        Some(x) => {
            return Ok(Json(
                x.into_iter().filter(|supplier| !supplier.deleted).collect(),
            ));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not read suppliers",
            ));
        }
    };
}

#[get("/<id>")]
pub fn get(
    conn: DatabaseConnection,
    id: String,
) -> Result<Json<Supplier>, rocket::response::status::Custom<&'static str>> {
    match Supplier::find_by_id(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(x));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::NotFound,
                "Could not get supplier",
            ));
        }
    };
}

#[post("/", format = "application/json", data = "<new_supplier>")]
pub fn insert(
    conn: DatabaseConnection,
    new_supplier: Json<NewSupplier>,
) -> Result<Json<Supplier>, rocket::response::status::Custom<&'static str>> {
    let supplier = Supplier::new(
        new_supplier.0.name,
        new_supplier.0.telephone,
        new_supplier.0.website,
        new_supplier.0.email,
    );
    match supplier.insert(&conn) {
        Some(_x) => {
            return Ok(Json(supplier));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not create supplier",
            ));
        }
    }
}

#[delete("/<id>")]
pub fn delete(
    conn: DatabaseConnection,
    id: String,
) -> Result<Json<CustomResponse>, rocket::response::status::Custom<&'static str>> {
    match Supplier::delete(id.as_str(), &conn) {
        Some(_x) => {
            return Ok(Json(CustomResponse::success()));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not create supplier",
            ));
        }
    }
}
