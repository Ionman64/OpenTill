#![feature(proc_macro_hygiene, decl_macro)]

#[macro_use]
extern crate rocket;
extern crate rocket_contrib;
#[macro_use]
extern crate log;

extern crate open_till;

use std::env;

use open_till::controllers::{
    CaseController, DepartmentController, IndexController, LanguageController, ProductController,
    ServerController, SupplierController, UserController, AuthController, TransactionController
};
use open_till::models::Database::DatabaseConnection;
use open_till::models::Server::Server;
use open_till::responses::CustomResponse::CustomResponse;
use rocket::config::{Config, Environment, Value};
use rocket_contrib::json::Json;
use rocket_contrib::serve::StaticFiles;
use rocket_contrib::templates::Template;
use std::path::Path;

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

fn main() {
    use open_till::template_manager;
    template_manager::process_templates_into_folder();
    for arg in env::args().skip(1) {
        match arg.as_str() {
            "-version" => {
                println!(
                    "v{}.{}",
                    config::APP_VERSION_MAJOR,
                    config::APP_VERSION_MINOR
                );
                return;
            }
            &_ => {
                continue;
            }
        }
    }
    println!("{}", app::logo_ascii());
    app::setup_file_system(); //Sets up the file system (e.g. all the folders needed for the program)
    setup_logger().expect("Cannot Setup Logger"); //Setup Fern Logger
    match config::DEVELOPMENT_MODE {
        config::ProgramMode::PRODUCTION => {}
        config::ProgramMode::DEVELOPMENT => {
            println!("Running in development mode");
        }
        config::ProgramMode::TESTING => {
            println!("Running in testing mode");
            app::remove_database();
        }
    }
    app::setup_database();
    listen();
    let conn = app::establish_connection();
    let server_details = match serde_json::to_string(&Server::details(&conn)) {
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
    //app::printpdf();*/

    let mut database_config = HashMap::new();
    let mut databases = HashMap::new();

    // This is the same as the following TOML:
    // my_db = { url = "database.sqlite" }
    database_config.insert(
        "url",
        Value::from(
            app::get_data_dir()
                .join(config::DATABASE_NAME)
                .to_str()
                .unwrap(),
        ),
    );
    databases.insert("my_db", Value::from(database_config));

    let config = Config::build(Environment::Development)
        .extra("databases", databases)
        .finalize()
        .unwrap();

    rocket::custom(config)
        .mount(
            "/",
            routes![IndexController::index, IndexController::dashboard],
        )
        .mount("/", StaticFiles::from(app::get_web_dir()))
        .mount("/heartbeat", routes![heartbeat])
        .mount("/api/product", routes![ProductController::barcode])
        .mount(
            "/api/department",
            routes![
                DepartmentController::get_all,
                DepartmentController::insert,
                DepartmentController::get,
                DepartmentController::delete
            ],
        )
        .mount(
            "/api/user",
            routes![
                UserController::get_all,
                UserController::insert,
                UserController::get,
                UserController::delete
            ],
        )
        .mount(
            "/api/supplier",
            routes![
                SupplierController::get_all,
                SupplierController::insert,
                SupplierController::get,
                SupplierController::delete
            ],
        )
        .mount("/api/auth",
            routes![
                AuthController::login
            ]
        )
        .mount("/api/transaction",
            routes![
                TransactionController::new
            ]
        )
        .mount("/api/case", routes![CaseController::insert])
        .mount("/api/language", routes![LanguageController::get_all])
        .mount("/api/server", routes![ServerController::get_all])
        .attach(Template::fairing())
        .attach(DatabaseConnection::fairing())
        .launch();
    info!("System started successfully");
}

//API METHODS BELOW

#[get("/heartbeat")]
pub fn heartbeat() -> Json<CustomResponse> {
    Json(CustomResponse::success())
}
