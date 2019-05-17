#![feature(proc_macro_hygiene, decl_macro)]

#[macro_use] 
extern crate rocket;
extern crate rocket_contrib;

#[macro_use]
extern crate log;

extern crate diesel;
extern crate open_till;

use std::io;
use open_till::utils as app;
use open_till::config as config;
use open_till::format as formatter;
use open_till::models as models;
use std::path::{Path, PathBuf};
use rocket_contrib::serve::StaticFiles;
use rocket::response::NamedFile;

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
        .chain(fern::log_file(&Path::new(&app::get_app_dir()).join(String::from(config::LOG_HOME)).join(&String::from(format!("{}.log", chrono::Local::now().format("%Y-%m-%d")))).to_str().unwrap())?)
        .apply()?;
    Ok(())
}

#[post("/login")]
fn login() -> String {
    String::new()
}

fn main() {
    setup_logger().expect("Cannot Setup Logger"); //Setup Fern Logger
    app::show_notification("OpenTill Started", "Hello");
    app::setup_file_system(); //Sets up the file system (e.g. all the folders needed for the program)
    app::download_update_file();
    if config::AUTO_DOWNLOAD_UPDATES {
        app::check_for_updates();
    }
    println!("{}", app::logo_ascii()); //Print Sexy Logo
    app::printpdf();
    

    rocket::ignite().mount("/", StaticFiles::from(app::get_web_dir())).mount("/api", routes![login]).launch();
    info!("System started successfully");
}
