extern crate open_till;
#[macro_use]
extern crate log;

extern crate diesel;

use std::io;
use open_till::utils as app;
use open_till::config as config;
use open_till::format as formatter;
use open_till::models as models;
use std::path::{Path, PathBuf};
use diesel::prelude::*;
use open_till::schema::products::dsl::*;

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

fn main() {
    //Sets up the file system (e.g. all the folders needed for the program)
    app::setup_file_system();
    //Setup Fern Logger
    setup_logger().expect("Cannot Setup Logger");
    
    //Print Sexy Logo
    println!("{}", app::print_logo_ascii());
    println!("{}", formatter::format_currency(2.45, formatter::Currency::GBP).unwrap());
    println!("{}", app::uuid4());
    println!("{}", app::get_home_dir().to_str().unwrap());
    println!("{}", app::get_app_dir().to_str().unwrap());
    //println!("{}", app::hash_password(String::from("Hello World")).unwrap());

    let product = models::Product::new(String::from("Meat and Potato Pie"), String::from("909090909"), 100);
    match insert_into_database(product) {
        0=>{println!("Could not insert into database")},
        _=>{}
    };
    let product = find_product_by_barcode(String::from("909090909"));
    println!("Name => {}", product.name);
    read_database();
    
    //println!("{}", app::generate_qr_code_as_string(String::from("Hello World")));

    if config::AUTO_DOWNLOAD_UPDATES {
        match app::download_update_file() {
            Ok(x) => {
                if x {
                    info!("Downloaded new version");
                }
                else {
                    trace!("No new version available");
                }
            },
            Err(_) => warn!("Could not complete update procedure")
        }
    }
    info!("System started successfully");
}

pub fn read_database() {
    let connection = app::establish_connection();
    let results = products.limit(5).load::<models::Product>(&connection).expect("Error loading posts");

    println!("Displaying {} posts", results.len());
    for post in results {
        println!("{}", post.name);
        println!("----------\n");
        println!("{}", post.barcode);
    }
}

pub fn insert_into_database(product: models::Product) -> usize {
    let conn = app::establish_connection();
    match diesel::insert_into(products).values(&product).execute(&conn) {
        Ok(x) => x,
        Err(x) => 0
    }
}

pub fn find_product_by_barcode(code: String) -> models::Product {
    let conn = app::establish_connection();
    let product = products.filter(barcode.eq(code)).first::<models::Product>(&conn).expect("Could not connect to database");

    return product;
}