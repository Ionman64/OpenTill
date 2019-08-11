use rocket_contrib::json::Json;

use models::Database::DatabaseConnection;
use responses::CustomResponse::CustomResponse;
use rocket::http::Status;

use models::Department::{Department, NewDepartment, UpdateDepartment};

#[get("/")]
pub fn get_all(conn: DatabaseConnection) -> Result<Json<Vec<Department>>, rocket::response::status::Custom<&'static str>> {
    match Department::get_all(&conn) {
        Some(x) => {
            return Ok(Json(
                x.into_iter()
                    .filter(|department| !department.deleted)
                    .collect(),
            ));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not read departments",
            ));
        }
    };
}

#[get("/<id>")]
pub fn get(conn: DatabaseConnection, id: String) -> Result<Json<Department>, rocket::response::status::Custom<&'static str>> {
    match Department::find_by_id(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(x));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::NotFound,
                "Could not get department",
            ));
        }
    };
}

#[post("/", format = "application/json", data = "<new_department>")]
pub fn insert(
    conn: DatabaseConnection,
    new_department: Json<NewDepartment>,
) -> Result<Json<Department>, rocket::response::status::Custom<&'static str>> {
    let department = Department::new(
        new_department.0.name,
        new_department.0.short_hand,
        new_department.0.colour,
    );
    match department.insert(&conn) {
        Some(_x) => {
            return Ok(Json(department));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not create department",
            ));
        }
    }
}

/*#[put("/<id>", format = "application/json", data = "<update_department>")]
pub fn update(conn: DatabaseConnection, id: String, update_department: Json<UpdateDepartment>) -> Result<Json<Department>, rocket::response::status::Custom<&'static str>> {
    let department = Department::new(update_department.0.name, update_department.0.short_hand, update_department.0.colour);
    match department.insert(&conn) {
        Some(_x) => {
            return Ok(Json(department));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not update department"));
        }
    }
}*/

#[delete("/<id>")]
pub fn delete(
    conn: DatabaseConnection,
    id: String,
) -> Result<Json<CustomResponse>, rocket::response::status::Custom<&'static str>> {
    match Department::delete(id.as_str(), &conn) {
        Some(_x) => {
            return Ok(Json(CustomResponse::success()));
        }
        None => {
            return Err(rocket::response::status::Custom(
                Status::InternalServerError,
                "Could not create department",
            ));
        }
    }
}
