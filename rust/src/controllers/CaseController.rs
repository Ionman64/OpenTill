use rocket_contrib::json::Json;

use models::Database::DatabaseConnection;
use responses::CustomResponse::CustomResponse;
use rocket::http::Status;

use models::Case::{Case, NewCase};

#[post("/", format = "application/json", data = "<new_case>")]
pub fn insert(
    conn: DatabaseConnection,
    new_case: Json<NewCase>,
) -> Result<Json<CustomResponse>, rocket::response::status::Custom<&'static str>> {
    let case = Case::new(
        new_case.0.barcode,
        new_case.0.product_barcode,
        new_case.0.units,
    );
    match case.insert(&conn) {
        Some(x) => {
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
