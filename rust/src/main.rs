#![feature(proc_macro_hygiene, decl_macro)]

#[macro_use]
extern crate rocket;
extern crate rocket_contrib;

#[macro_use]
extern crate log;

extern crate diesel;
extern crate open_till;

use open_till::config;
use open_till::models;
use open_till::network_broadcast::{listen, send};
use open_till::utils as app;
use rocket_contrib::json::Json;
use rocket_contrib::serve::StaticFiles;
use rocket_contrib::templates::Template;
use std::path::Path;

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

    rocket::ignite()
        .mount("/", StaticFiles::from(app::get_web_dir()))
        .mount("/api", routes![index, login, details])
        .attach(Template::fairing())
        .launch();
    info!("System started successfully");
}
