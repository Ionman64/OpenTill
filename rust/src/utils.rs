//Utility Functions

use blake2::{Blake2b, Digest};
use chrono::{Datelike, NaiveDateTime, Timelike};
use config;
use diesel::{Connection, SqliteConnection};
use models::{
    Department::Department, GlobalProduct::GlobalProduct, Product::Product, User::User,
    Version::Version,
};
use qrcodegen::QrCode;
use qrcodegen::QrCodeEcc;
use rand::distributions::Alphanumeric;
use rand::Rng;
use std::env;
use std::fs::File;
use std::io::{Write};
use std::path::{Path, PathBuf};
use std::{fs, io, str, thread};
use uuid::Uuid;
use zip::ZipArchive;
use std::result::Result;

use configuration::AppConfiguration::AppConfiguration;

use printpdf::*;
use std::io::BufWriter;

use migrations;

pub fn uuid4() -> Uuid {
    Uuid::new_v4()
}

pub fn logo_ascii() -> String {
    return format!(
        r###"
   ___                     _____ _ _ _ 
  / _ \ _ __   ___ _ __   |_   _(_) | |
 | | | | '_ \ / _ \ '_ \    | | | | | |
 | |_| | |_) |  __/ | | |   | | | | | |
  \___/| .__/ \___|_| |_|   |_| |_|_|_| v{}.{} [Powered By Rust]
       |_|                             
    "###,
        config::APP_VERSION_MAJOR,
        config::APP_VERSION_MINOR
    );
}

/// Returns the timestamp of the first monday BEFORE a given timestamp
///
/// # Arguments
///
/// * `timestamp` - An i64 timestamp that represents a date.
pub fn get_monday_timestamp(timestamp: i64) -> i64 {
    const SECONDS_PER_DAY: i64 = 86400;
    let naive_date = NaiveDateTime::from_timestamp(timestamp, 0);
    let seconds_from_midnight = i64::from(naive_date.num_seconds_from_midnight());
    let days_from_monday = i64::from(naive_date.weekday().num_days_from_monday());
    naive_date.timestamp() - (days_from_monday * SECONDS_PER_DAY) - seconds_from_midnight
}

pub fn show_notification(title: &str, message: &str) {
    if config::NOTIFICATIONS_ON {
        notifica::notify(title, message);
    }
}

pub fn get_home_dir() -> PathBuf {
    match env::current_dir() {
        Ok(x) => x,
        Err(_x) => panic!("Cannot find home directory"),
    }
}

pub fn get_app_dir() -> PathBuf {
    get_home_dir().join(config::APP_HOME).to_path_buf()
}

pub fn get_app_temp() -> PathBuf {
    get_app_dir().join(config::TEMP_HOME).to_path_buf()
}

pub fn get_web_dir() -> PathBuf {
    get_app_dir().join(config::WEB_CONTENT_HOME).to_path_buf()
}

pub fn get_data_dir() -> PathBuf {
    get_app_dir().join(config::DATA_HOME).to_path_buf()
}

pub fn get_emails_dir() -> PathBuf {
    get_app_dir().join(config::EMAILS_HOME).to_path_buf()
}

pub fn character_count(str_line: &str, matching_character: char) -> u32 {
    let mut count: u32 = 0;
    for character in str_line.chars() {
        if character == matching_character {
            count += 1;
        }
    }
    return count;
}

pub fn setup_file_system() {
    let home_dir = get_app_dir();
    let folders = vec![
        config::LOG_HOME,
        config::UPDATES_HOME,
        config::TEMP_HOME,
        config::WEB_CONTENT_HOME,
        config::DATA_HOME,
    ];
    for folder in folders {
        let project_path = Path::new(&home_dir).join(String::from(folder));
        if !project_path.exists() {
            print!("Creating path {}...", &project_path.to_str().unwrap());
            match fs::create_dir_all(&project_path) {
                Err(_) => panic!(
                    "ERROR (could not create {})",
                    &project_path.to_str().unwrap()
                ),
                Ok(_) => {}
            }
        }
    }
}

pub fn remove_database() {
    let path = Path::new(&get_data_dir()).join(config::DATABASE_NAME);
    match std::fs::remove_file(path) {
        Ok(x) => x,
        Err(_x) => {
            warn!("Database does not exist and therefore it cannot be deleted");
        }
    }
}

pub fn setup_database() -> bool {
    let path = Path::new(&get_data_dir()).join(config::DATABASE_NAME);
    if path.exists() {
        warn!("Check database and setup not implemented");
        return false;
    }
    info!("Missing database, creating it from scratch");
    let conn = establish_connection();
    migrations::database_setup(&conn);
    setup_default_configuration(&conn);
    download_products_file(&conn);
    info!("database setup!");
    true
}

pub fn setup_default_configuration(conn: &SqliteConnection) {
    //This should be run the first time the program is run
    AppConfiguration::new(config::INSTANCE_GUID_KEY, &uuid4().to_string()).save(conn);
    setup_admin(conn);
}

pub fn setup_admin(conn: &SqliteConnection) {
    let mut admin = User::new(
        String::from(config::DEFAULT_ADMIN_NAME),
        String::new(),
        String::from("admin@opentill.com"),
        hash_password("changeme"),
    );
    let department = Department::new(
        String::from("Department1"),
        String::from("Dep1"),
        String::from("ff9911"),
    );
    admin.code = String::from("1111"); //Override the set code for admin
    admin.insert(conn);
    department.insert(conn);
}

pub fn download_products_file(conn: &SqliteConnection) {
    let path = get_app_temp().join("products.zip");
    download_file(
        "https://www.goldstandardresearch.co.uk/products/products.zip",
        &path.as_path(),
    );
    let file = match File::open(path) {
        Ok(x) => x,
        Err(_x) => {
            error!("Could not open products zip");
            return;
        }
    };
    let mut zip = match ZipArchive::new(file) {
        Ok(x) => x,
        Err(x) => {
            error!("File is corrupted or empty, or file is not a valid .zip file");
            error!("{0}", x);
            return;
        }
    };
    if zip.len() == 0 {
        error!("Product zip file contains no files");
        return;
    }
    //Next match statement is a bit redundant
    let products_file = match zip.by_name(config::PRODUCTS_ZIP_PRODUCTS_FILENAME) {
        Ok(x) => x,
        Err(_x) => {
            error!(
                "Could not find {0} in zip file",
                config::PRODUCTS_ZIP_PRODUCTS_FILENAME
            );
            return;
        }
    };
    let mut rdr = csv::Reader::from_reader(products_file);
    let mut global_products: Vec<GlobalProduct> = Vec::new();
    const COLUMN_ID: usize = 0;
    const COLUMN_NAME: usize = 1;
    const COLUMN_BARCODE: usize = 3;
    const COLUMN_UPDATED: usize = 10;
    for (count, record) in rdr.records().enumerate() {
        if count % 5000 == 0 {
            match GlobalProduct::insert_many(conn, &global_products) {
                false => {
                    warn!("Could not insert some products in region {0}", count);
                }
                _ => {}
            };
            global_products.clear();
        }
        let unwrapped_record = match record {
            Ok(x) => x,
            Err(_x) => {
                warn!("Could not read record on line {0}", count);
                continue;
            }
        };
        let updated_string = match unwrapped_record.get(COLUMN_UPDATED) {
            Some(x) => x,
            None => {
                warn!("Cannot read updated");
                continue;
            }
        };
        let updated: i64 = updated_string.parse().unwrap();
        let gp = GlobalProduct {
            id: String::from(unwrapped_record.get(COLUMN_ID).unwrap()),
            name: String::from(unwrapped_record.get(COLUMN_NAME).unwrap()),
            barcode: String::from(unwrapped_record.get(COLUMN_BARCODE).unwrap()),
            updated: NaiveDateTime::from_timestamp(updated, 0),
        };
        global_products.push(gp);
    }
    match GlobalProduct::insert_many(conn, &global_products) {
        false => {
            warn!("Could not insert some products in last region");
        }
        true => {
            info!("Product(s) downloaded from server");
        }
    }
}

pub fn generate_qr_code_as_svg(content: String) -> String {
    let qr = QrCode::encode_text(&content, QrCodeEcc::Medium).unwrap();
    return qr.to_svg_string(4);
}

//Be careful here, because a very large file will cause a memory usage spike.
pub fn read_all_lines(file_path: PathBuf) -> String {
    use std::fs::read_to_string;
    match read_to_string(&file_path) {
        Ok(x) => x,
        Err(x) => {
            panic!("Couldn't open read file {}:{}", &file_path.as_os_str().to_str().unwrap(), x);
        }
    }
}

pub fn generate_lbx_from_product(product: &Product) -> Result<PathBuf, &'static str> {
    static LABEL_EXT: &str = ".lbx";
    static PLACEHOLDER_NAME: &str = "{:NAME}";
    static PLACEHOLDER_PRICE: &str = "{:PRICE}";
    static PLACEHOLDER_DATE: &str = "{:DATE}"; //Not replaced yet
    static PLACEHOLDER_BARCODE: &str = "{:BARCODE}";
    static PLACEHOLDER_CHAR_LENGTH: &str = "{:CHAR_LENGTH}";

    let label_xml_string_with_date = String::from("<?xml version=\"1.0\" encoding=\"UTF-8\"?><pt:document xmlns:pt=\"http://schemas.brother.info/ptouch/2007/lbx/main\" xmlns:style=\"http://schemas.brother.info/ptouch/2007/lbx/style\" xmlns:text=\"http://schemas.brother.info/ptouch/2007/lbx/text\" xmlns:draw=\"http://schemas.brother.info/ptouch/2007/lbx/draw\" xmlns:image=\"http://schemas.brother.info/ptouch/2007/lbx/image\" xmlns:barcode=\"http://schemas.brother.info/ptouch/2007/lbx/barcode\" xmlns:database=\"http://schemas.brother.info/ptouch/2007/lbx/database\" xmlns:table=\"http://schemas.brother.info/ptouch/2007/lbx/table\" xmlns:cable=\"http://schemas.brother.info/ptouch/2007/lbx/cable\" version=\"1.5\" generator=\"P-touch Editor 5.2.001 Windows\"><pt:body currentSheet=\"Sheet 1\" direction=\"LTR\"><style:sheet name=\"Sheet 1\"><style:paper media=\"0\" width=\"175.7pt\" height=\"81.8pt\" marginLeft=\"4.3pt\" marginTop=\"8.4pt\" marginRight=\"4.4pt\" marginBottom=\"8.4pt\" orientation=\"portrait\" autoLength=\"false\" monochromeDisplay=\"true\" printColorDisplay=\"false\" printColorsID=\"0\" paperColor=\"#FFFFFF\" paperInk=\"#000000\" split=\"1\" format=\"274\" backgroundTheme=\"0\" printerID=\"14388\" printerName=\"Brother QL-800\"/><style:cutLine regularCut=\"0pt\" freeCut=\"\"/><style:backGround x=\"4.3pt\" y=\"8.4pt\" width=\"167.1pt\" height=\"65pt\" brushStyle=\"NULL\" brushId=\"0\" userPattern=\"NONE\" userPatternId=\"0\" color=\"#000000\" printColorNumber=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"/><pt:objects><table:table><pt:objectStyle x=\"4.3pt\" y=\"8.4pt\" width=\"167pt\" height=\"64.8pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"INSIDEFRAME\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"Table2\" ID=\"0\" lock=\"2\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><table:tableStyle row=\"3\" column=\"1\" autoSize=\"false\" keepSize=\"true\"/><table:gridPosition x=\"0pt 166.3pt\" y=\"0pt 21.4pt 42.8pt 64.1pt\"/><table:cells><table:cell addressX=\"1\" addressY=\"1\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><text:text><pt:objectStyle x=\"6pt\" y=\"10.1pt\" width=\"163.6pt\" height=\"18.7pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"NULL\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo><text:textControl control=\"FIXEDFRAME\" clipFrame=\"false\" aspectNormal=\"true\" shrink=\"true\" autoLF=\"true\" avoidImage=\"false\"/><text:textAlign horizontalAlignment=\"CENTER\" verticalAlignment=\"CENTER\" inLineAlignment=\"BASELINE\"/><text:textStyle vertical=\"false\" nullBlock=\"false\" charSpace=\"0\" lineSpace=\"0\" orgPoint=\"20pt\" combinedChars=\"true\"/><pt:data>{:NAME}</pt:data><text:stringItem charLen=\"{:CHAR_LENGTH}\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem></text:text><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell><table:cell addressX=\"1\" addressY=\"2\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><text:text><pt:objectStyle x=\"6pt\" y=\"31.5pt\" width=\"163.6pt\" height=\"18.7pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"NULL\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo><text:textControl control=\"FIXEDFRAME\" clipFrame=\"false\" aspectNormal=\"true\" shrink=\"true\" autoLF=\"true\" avoidImage=\"false\"/><text:textAlign horizontalAlignment=\"CENTER\" verticalAlignment=\"CENTER\" inLineAlignment=\"BASELINE\"/><text:textStyle vertical=\"false\" nullBlock=\"false\" charSpace=\"0\" lineSpace=\"0\" orgPoint=\"18.7pt\" combinedChars=\"false\"/><pt:data>{:PRICE} Use By {:DATE}</pt:data><text:stringItem charLen=\"1\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"1\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"3\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"11\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"2\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem></text:text><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell><table:cell addressX=\"1\" addressY=\"3\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><barcode:barcode><pt:objectStyle x=\"8pt\" y=\"52.9pt\" width=\"159.6pt\" height=\"18.6pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"INSIDEFRAME\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"Bar Code3\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><barcode:barcodeStyle protocol=\"CODE39\" lengths=\"48\" zeroFill=\"false\" barWidth=\"1.2pt\" barRatio=\"1:3\" humanReadable=\"true\" humanReadableAlignment=\"LEFT\" checkDigit=\"false\" autoLengths=\"true\" margin=\"true\" sameLengthBar=\"false\" bearerBar=\"false\"/><pt:data>{:BARCODE}</pt:data></barcode:barcode><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell></table:cells></table:table></pt:objects></style:sheet></pt:body></pt:document>");
    let prop_xml_string = String::from("<?xml version=\"1.0\" encoding=\"UTF-8\"?><meta:properties xmlns:meta=\"http://schemas.brother.info/ptouch/2007/lbx/meta\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\"><meta:appName>P-touch Editor</meta:appName><dc:title></dc:title><dc:subject></dc:subject><dc:creator>pete</dc:creator><meta:keyword></meta:keyword><dc:description></dc:description><meta:template></meta:template><dcterms:created>2018-01-25T12:24:21Z</dcterms:created><dcterms:modified>2018-09-06T16:19:22Z</dcterms:modified><meta:lastPrinted>2018-01-25T12:25:38Z</meta:lastPrinted><meta:modifiedBy>pete</meta:modifiedBy><meta:revision>2</meta:revision><meta:editTime>2</meta:editTime><meta:numPages>1</meta:numPages><meta:numWords>0</meta:numWords><meta:numChars>0</meta:numChars><meta:security>0</meta:security></meta:properties>");

    let options =
        zip::write::FileOptions::default().compression_method(zip::CompressionMethod::Deflated);

    let mut label_path = product.name.clone();
    label_path.push_str(LABEL_EXT);
    let file_path = get_app_temp().join(&label_path);
    let file = match std::fs::File::create(&file_path) {
        Ok(x) => x,
        Err(_x) => return Err("Could not create file"),
    };
    let mut zip = zip::ZipWriter::new(file);

    match zip.start_file("label.xml", options) {
        Ok(_x) => {}
        Err(_x) => return Err("Could not start label.xml file to zip"),
    };

    match zip.write_all(
        &label_xml_string_with_date
            .replace(PLACEHOLDER_NAME, &product.name)
            .replace(PLACEHOLDER_BARCODE, &product.barcode)
            .replace(PLACEHOLDER_PRICE, &product.price.to_string())
            .replace(PLACEHOLDER_CHAR_LENGTH, &product.name.len().to_string())
            .into_bytes(),
    ) {
        Ok(_x) => {}
        Err(_x) => return Err("Could not write context to label.xml file"),
    };

    match zip.start_file("prop.xml", options) {
        Ok(_x) => {}
        Err(_x) => return Err("Could not start prop.xml in zip"),
    };

    match zip.write_all(&prop_xml_string.into_bytes()) {
        Ok(_x) => {}
        Err(_x) => return Err("Could not write content to prop.xml file"),
    };

    match zip.finish() {
        Ok(_x) => {}
        Err(_x) => return Err("Could not write complete zip write to disk"),
    }

    Ok(file_path)
}

pub fn hash_file(mut file: File) -> Result<String, &'static str> {
    let mut hasher = Blake2b::new();
    match io::copy(&mut file, &mut hasher) {
        Ok(x) => x,
        Err(_x) => {
            return Err(
                "Could not hash file returned by the function 'generate_lbx_from_product': {}",
            )
        }
    };
    let hash = hasher.result();
    println!("{:x}", hash);
    let s = match String::from_utf8(hash.as_slice().to_vec()) {
        Ok(v) => v,
        Err(e) => {
            println!("{}", e);
            return Err("Invalid UTF-8 sequence");
        }
    };
    Ok(s)
}

pub fn hash_password(m: &str) -> String {
    let mut hasher = Blake2b::new();
    hasher.input(m.as_bytes());
    let mut result = String::new();
    for byte in hasher.result().iter() {
        result.push_str(format!("{:x}", byte).as_str());
    }
    result.clone()
}

pub fn establish_connection() -> SqliteConnection {
    SqliteConnection::establish(&get_data_dir().join(config::DATABASE_NAME).to_str().unwrap())
        .expect(&format!(
            "Error connecting to {:?}",
            get_data_dir().join(config::DATABASE_NAME)
        ))
}

pub fn generate_user_code() -> String {
    rand::thread_rng()
        .sample_iter(&Alphanumeric)
        .take(config::USER_CODE_LENGTH)
        .collect::<String>()
}

fn download_file<'a>(online_location: &'a str, location: &'a Path,) -> Result<&'a Path, &'static str> {
    let mut resp = match reqwest::get(online_location) {
        Ok(x) => x,
        Err(x) => {
            error!("Error while downloading file {}", x);
            return Err("Could not contact server");
        }
    };
    let mut out = match File::create(location) {
        Ok(x) => x,
        Err(x) => {
            error!("{}", x);
            return Err("Found Update, but could not create update file");
        }
    };
    match io::copy(&mut resp, &mut out) {
        Ok(_) => {}
        Err(x) => {
            error!("{}", x);
            return Err("Update file created, but cannot write to the file");
        }
    };
    Ok(location)
}

pub fn download_update_file() {
    let conn = establish_connection();
    thread::spawn(move || {
        match reqwest::get("https://www.goldstandardresearch.co.uk/versions/versions.csv")
            .unwrap()
            .text()
        {
            Ok(resp) => {
                let mut rdr = csv::Reader::from_reader(resp.as_bytes());
                let mut updates_count = 0;

                for result in rdr.deserialize() {
                    let version: Version = match result {
                        Ok(x) => x,
                        Err(_x) => {
                            warn!("Could not read all version details from server");
                            continue;
                        }
                    };
                    version.save(&conn);
                    if version.major > config::APP_VERSION_MAJOR
                        || version.major == config::APP_VERSION_MAJOR
                            && version.minor > config::APP_VERSION_MINOR
                    {
                        updates_count += 1;
                    }
                }
                if updates_count > 0 {
                    println!("{} updates available", updates_count);
                } else {
                    println!("Running latest version");
                }
            }
            Err(x) => {
                error!("Error while downloading file {}", x);
                return Err("Could not contact server");
            }
        };
        Ok(())
    });
}

pub fn check_for_updates() {
    thread::spawn(move || {
        //This will download any updates in a seperate thread so the main program can start
        //updates will be installed afterwards
        let core_file_name = get_app_dir().join(config::UPDATES_HOME).join(format!(
            "{}-{}.exe",
            config::APP_VERSION_MAJOR,
            config::APP_VERSION_MINOR
        ));
        match download_file(
            &format!(
                "https://www.goldstandardresearch.co.uk/versions/{}.{}/main.exe",
                config::APP_VERSION_MAJOR,
                config::APP_VERSION_MINOR
            ),
            &core_file_name,
        ) {
            Ok(_) => {
                info!("Downloaded new version");
            }
            Err(x) => {
                error!("{}", x);
                return;
            }
        }
        let web_file_name = get_app_dir().join(config::UPDATES_HOME).join(format!(
            "{}-{}.zip",
            config::APP_VERSION_MAJOR,
            config::APP_VERSION_MINOR
        ));
        match download_file(
            &format!(
                "https://www.goldstandardresearch.co.uk/versions/{}.{}/content.zip",
                config::APP_VERSION_MAJOR,
                config::APP_VERSION_MINOR
            ),
            &web_file_name,
        ) {
            Ok(_x) => {
                info!("Downloaded new version");
            }
            Err(x) => {
                warn!("{}", x);
                return;
            }
        }
    });
}

pub fn printpdf() {
    let (doc, _page1, _layer1) =
        PdfDocument::new("PDF_Document_title", Mm(247.0), Mm(210.0), "Layer 1");
    let (_page2, _layer1) = doc.add_page(Mm(10.0), Mm(250.0), "Page 2, Layer 1");

    doc.save(&mut BufWriter::new(
        File::create(get_app_temp().join("test_working.pdf")).unwrap(),
    ))
    .unwrap();
}
