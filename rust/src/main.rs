#![feature(proc_macro_hygiene, decl_macro)]

#[macro_use]
extern crate rocket;
#[macro_use]
extern crate rocket_contrib;
#[macro_use]
extern crate log;

extern crate open_till;

use rocket::config::{Config, Environment, Value};
use rocket_contrib::json::Json;
use rocket_contrib::serve::StaticFiles;
use rocket_contrib::templates::Template;
use rocket_contrib::databases::diesel;
use std::path::Path;
use diesel::SqliteConnection;
use rocket::response::status;
use rocket::http::Status;
use rocket::request::Form;
use open_till::models::NewDepartment;
use open_till::models;

use open_till::config;
use open_till::network_broadcast::{listen, send};
use open_till::utils as app;
use std::collections::HashMap;

fn setup_logger() -> Result<(), fern::InitError> {
    fern::Dispatch::new()
        .format(|out, message, record| {
            out.finish(format_args!(
                "{}[{}][{}] {}",
                chrono::Local::now().format("[%Y-%m-%d][%H:%M:%S]"),
                record.target(),
                record.level(),
                message
            ))
        })
        .level(log::LevelFilter::Debug)
        .chain(std::io::stdout())
        .chain(fern::log_file(
            &Path::new(&app::get_app_dir())
                .join(String::from(config::LOG_HOME))
                .join(&format!("{}.log", chrono::Local::now().format("%Y-%m-%d"))),
        )?)
        .apply()?;
    Ok(())
}

#[post("/login")]
fn login() -> String {
    String::new()
}

#[get("/servers")]
fn details() -> Json<Vec<models::Server>> {
    Json(models::Server::get_all())
}

#[get("/")]
fn index() -> Template {
    let context = models::TemplateContent::new();
    Template::render("index2", &context)
}
#[get("/dashboard")]
fn dashboard() -> Template {
    let context = models::TemplateContent::new();
    Template::render("dashboard", &context)
}

#[get("/barcode/<code>")]
fn barcode(conn: Db, code: String) -> Result<Json<models::Product>, rocket::response::status::NotFound<&'static str>> {
    match models::Product::find_by_barcode(code.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(x));
        },
        None => {
            return Err(rocket::response::status::NotFound(""));
        }
    };
}



fn main() {
    app::setup_file_system(); //Sets up the file system (e.g. all the folders needed for the program)
    setup_logger().expect("Cannot Setup Logger"); //Setup Fern Logger
    app::setup_database();
    app::setup_default_configuration();

    listen();
    let server_details = match serde_json::to_string(&models::Server::details()) {
        Ok(x) => x,
        Err(x) => {
            panic!("Could not serialize server {}", x);
        }
    };
    send(server_details);

    /*app::show_notification("OpenTill Started", "Hello");

    app::download_update_file();
    if config::AUTO_DOWNLOAD_UPDATES {
        app::check_for_updates();
    }
    println!("{}", app::logo_ascii()); //Print Sexy Logo
    //app::printpdf();*/

    let mut database_config = HashMap::new();
    let mut databases = HashMap::new();

    // This is the same as the following TOML:
    // my_db = { url = "database.sqlite" }
    database_config.insert("url", Value::from(app::get_data_dir().join(config::DATABASE_NAME).to_str().unwrap()));
    databases.insert("my_db", Value::from(database_config));

    let config = Config::build(Environment::Development)
        .extra("databases", databases)
        .finalize()
        .unwrap();

    rocket::custom(config)
        .mount("/", routes![index, dashboard])
        .mount("/", StaticFiles::from(app::get_web_dir()))
        .mount("/api", routes![login, details, barcode])
        .mount("/api/department", routes![get_all_departments, insert_department, get_department, delete_department])
        .mount("/api/user", routes![get_all_users, insert_user, get_user, delete_user])
        .mount("/api/supplier", routes![get_all_suppliers, insert_supplier, get_supplier, delete_supplier])
        .attach(Template::fairing())
        .attach(Db::fairing())
        .launch();
    info!("System started successfully");
}

//API METHODS BELOW

#[database("my_db")]
pub struct Db(SqliteConnection);

#[get("/")]
pub fn get_all_departments(conn: Db) -> Result<Json<Vec<models::Department>>, rocket::response::status::Custom<&'static str>> {
    match models::Department::get_all(&conn) {
        Some(x) => {
            return Ok(Json(x.into_iter().filter(|department| !department.deleted).collect()));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not read departments"));
        }
    };
}

#[get("/<id>")]
pub fn get_department(conn: Db, id: String) -> Result<Json<models::Department>, rocket::response::status::Custom<&'static str>> {
    match models::Department::find_by_id(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(x));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::NotFound, "Could not get department"));
        }
    };
}

#[post("/", format = "application/json", data = "<new_department>")]
pub fn insert_department(conn: Db, new_department: Json<NewDepartment>) -> Result<Json<models::Department>, rocket::response::status::Custom<&'static str>> {
    let department = models::Department::new(new_department.0.name, new_department.0.short_hand, new_department.0.colour);
    match department.insert(&conn) {
        Some(x) => {
            return Ok(Json(department));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not create department"));
        }
    }   
}

#[delete("/<id>")]
pub fn delete_department(conn: Db, id: String) -> Result<Json<models::CustomResponse>, rocket::response::status::Custom<&'static str>> {
    match models::Department::delete(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(models::CustomResponse::success()));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not create department"));
        }
    }   
}

#[get("/")]
pub fn get_all_users(conn: Db) -> Result<Json<Vec<models::User>>, rocket::response::status::Custom<&'static str>> {
    match models::User::get_all(&conn) {
        Some(x) => {
            return Ok(Json(x.into_iter().filter(|user| !user.deleted).collect()));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not read users"));
        }
    };
}

#[get("/<id>")]
pub fn get_user(conn: Db, id: String) -> Result<Json<models::User>, rocket::response::status::Custom<&'static str>> {
    match models::User::find_by_id(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(x));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::NotFound, "Could not get user"));
        }
    };
}

#[post("/", format = "application/json", data = "<new_user>")]
pub fn insert_user(conn: Db, new_user: Json<models::NewUser>) -> Result<Json<models::User>, rocket::response::status::Custom<&'static str>> {
    let user = models::User::new(new_user.0.name, new_user.0.telephone, new_user.0.email, app::hash_password(new_user.0.password.as_str()));
    match user.insert(&conn) {
        Some(x) => {
            return Ok(Json(user));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not create user"));
        }
    }   
}

#[delete("/<id>")]
pub fn delete_user(conn: Db, id: String) -> Result<Json<models::CustomResponse>, rocket::response::status::Custom<&'static str>> {
    match models::User::delete(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(models::CustomResponse::success()));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not create user"));
        }
    }   
}

#[get("/")]
pub fn get_all_suppliers(conn: Db) -> Result<Json<Vec<models::Supplier>>, rocket::response::status::Custom<&'static str>> {
    match models::Supplier::get_all(&conn) {
        Some(x) => {
            return Ok(Json(x.into_iter().filter(|supplier| !supplier.deleted).collect()));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not read suppliers"));
        }
    };
}

#[get("/<id>")]
pub fn get_supplier(conn: Db, id: String) -> Result<Json<models::Supplier>, rocket::response::status::Custom<&'static str>> {
    match models::Supplier::find_by_id(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(x));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::NotFound, "Could not get supplier"));
        }
    };
}

#[post("/", format = "application/json", data = "<new_supplier>")]
pub fn insert_supplier(conn: Db, new_supplier: Json<models::NewSupplier>) -> Result<Json<models::Supplier>, rocket::response::status::Custom<&'static str>> {
    let supplier = models::Supplier::new(new_supplier.0.name, new_supplier.0.telephone, new_supplier.0.website, new_supplier.0.email);
    match supplier.insert(&conn) {
        Some(x) => {
            return Ok(Json(supplier));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not create supplier"));
        }
    }   
}

#[delete("/<id>")]
pub fn delete_supplier(conn: Db, id: String) -> Result<Json<models::CustomResponse>, rocket::response::status::Custom<&'static str>> {
    match models::Supplier::delete(id.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(models::CustomResponse::success()));
        },
        None => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not create supplier"));
        }
    }   
}