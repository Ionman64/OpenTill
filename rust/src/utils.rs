//Utility Functions

use config;
use uuid::Uuid;
use chrono::{NaiveDateTime, Timelike, Datelike};
use std::path::{Path, PathBuf};
use std::fs::File;
use std::env;
use std::io;
use std::str;
use std::fs;
use std::result;
use qrcodegen::QrCode;
use qrcodegen::QrCodeEcc;
use qrcodegen::QrSegment;
use models::Product;
use zip::write::ZipWriter;
use std::io::Write;
use blake2::{Blake2b, Digest};
use diesel::{SqliteConnection, Connection};
use dotenv::*;

pub fn uuid4() -> Uuid {
    Uuid::new_v4()
}

pub fn print_logo_ascii() -> String {
    return  String::from(r###"
   ___                     _____ _ _ _ 
  / _ \ _ __   ___ _ __   |_   _(_) | |
 | | | | '_ \ / _ \ '_ \    | | | | | |
 | |_| | |_) |  __/ | | |   | | | | | |
  \___/| .__/ \___|_| |_|   |_| |_|_|_| v2.0 [Powered By Rust]
       |_|                             
    "###);
}

/// Returns the timestamp of the first monday BEFORE a given timestamp
///
/// # Arguments
///
/// * `timestamp` - An i64 timestamp that represents a date.
pub fn get_monday_timestamp(timestamp: i64) -> i64 {
    const SECONDS_PER_DAY:i64 = 86400;
    let naive_date = NaiveDateTime::from_timestamp(timestamp, 0);
    let seconds_from_midnight = naive_date.num_seconds_from_midnight() as i64;
    let days_from_monday = naive_date.weekday().num_days_from_monday() as i64;
    naive_date.timestamp() - (days_from_monday * SECONDS_PER_DAY) - seconds_from_midnight
}

pub fn download_update_file() -> Result<bool, &'static str> {
    let update_file_name = "update.sh";
    let mut resp = match reqwest::get("https://sh.rustup.rs") {
        Ok(x) => x,
        Err(_) => return Err("Could not contact server")
    };
    let mut out = match File::create(get_app_dir().join(config::UPDATES_HOME).join(update_file_name)) {
        Ok(x) => x,
        Err(_) => return Err("Found Update, but could not create update file")
    };
    match io::copy(&mut resp, &mut out) {
        Ok(x) => x,
        Err(_) => return Err("Update file created, but cannot write to the file")
    };
    Ok(true)
}

pub fn get_home_dir() -> PathBuf {
    match dirs::home_dir() {
        Some(x) => x,
        None => panic!("Cannot find home directory")
    }
}

pub fn get_app_dir() -> PathBuf {
    get_home_dir().join(config::APP_HOME).to_path_buf()
}

pub fn get_app_temp() -> PathBuf {
    get_app_dir().join(config::TEMP_HOME).to_path_buf()
}

pub fn character_count(str_line: &String, matching_character: char) -> u32 {
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
    let folders = vec!{config::LOG_HOME, config::UPDATES_HOME, config::TEMP_HOME};
    for folder in folders {
        let project_path = Path::new(&home_dir).join(String::from(folder));
        if !project_path.exists() {
            print!("Creating path {}...", &project_path.to_str().unwrap());
            match fs::create_dir_all(&project_path) {
                Err(_) => panic!("ERROR (could not create {})", &project_path.to_str().unwrap()),
                Ok(_) => {}
            }
        }
    }
}

pub fn generate_qr_code_as_svg(content: String) -> String {
    let qr = QrCode::encode_text(&content, QrCodeEcc::Medium).unwrap();
    return qr.to_svg_string(4);
}

pub fn generate_lbx_from_product(product: &Product) -> Result<PathBuf, &'static str> {
    static LABEL_EXT: &str = ".lbx";
    static PLACEHOLDER_NAME: &str = "{:NAME}";
	static PLACEHOLDER_PRICE: &str =  "{:PRICE}";
	static PLACEHOLDER_DATE: &str = "{:DATE}"; //Not replaced yet
	static PLACEHOLDER_BARCODE: &str = "{:BARCODE}";
	static PLACEHOLDER_CHAR_LENGTH: &str = "{:CHAR_LENGTH}";

    let label_xml_string_with_date = String::from("<?xml version=\"1.0\" encoding=\"UTF-8\"?><pt:document xmlns:pt=\"http://schemas.brother.info/ptouch/2007/lbx/main\" xmlns:style=\"http://schemas.brother.info/ptouch/2007/lbx/style\" xmlns:text=\"http://schemas.brother.info/ptouch/2007/lbx/text\" xmlns:draw=\"http://schemas.brother.info/ptouch/2007/lbx/draw\" xmlns:image=\"http://schemas.brother.info/ptouch/2007/lbx/image\" xmlns:barcode=\"http://schemas.brother.info/ptouch/2007/lbx/barcode\" xmlns:database=\"http://schemas.brother.info/ptouch/2007/lbx/database\" xmlns:table=\"http://schemas.brother.info/ptouch/2007/lbx/table\" xmlns:cable=\"http://schemas.brother.info/ptouch/2007/lbx/cable\" version=\"1.5\" generator=\"P-touch Editor 5.2.001 Windows\"><pt:body currentSheet=\"Sheet 1\" direction=\"LTR\"><style:sheet name=\"Sheet 1\"><style:paper media=\"0\" width=\"175.7pt\" height=\"81.8pt\" marginLeft=\"4.3pt\" marginTop=\"8.4pt\" marginRight=\"4.4pt\" marginBottom=\"8.4pt\" orientation=\"portrait\" autoLength=\"false\" monochromeDisplay=\"true\" printColorDisplay=\"false\" printColorsID=\"0\" paperColor=\"#FFFFFF\" paperInk=\"#000000\" split=\"1\" format=\"274\" backgroundTheme=\"0\" printerID=\"14388\" printerName=\"Brother QL-800\"/><style:cutLine regularCut=\"0pt\" freeCut=\"\"/><style:backGround x=\"4.3pt\" y=\"8.4pt\" width=\"167.1pt\" height=\"65pt\" brushStyle=\"NULL\" brushId=\"0\" userPattern=\"NONE\" userPatternId=\"0\" color=\"#000000\" printColorNumber=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"/><pt:objects><table:table><pt:objectStyle x=\"4.3pt\" y=\"8.4pt\" width=\"167pt\" height=\"64.8pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"INSIDEFRAME\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"Table2\" ID=\"0\" lock=\"2\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><table:tableStyle row=\"3\" column=\"1\" autoSize=\"false\" keepSize=\"true\"/><table:gridPosition x=\"0pt 166.3pt\" y=\"0pt 21.4pt 42.8pt 64.1pt\"/><table:cells><table:cell addressX=\"1\" addressY=\"1\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><text:text><pt:objectStyle x=\"6pt\" y=\"10.1pt\" width=\"163.6pt\" height=\"18.7pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"NULL\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo><text:textControl control=\"FIXEDFRAME\" clipFrame=\"false\" aspectNormal=\"true\" shrink=\"true\" autoLF=\"true\" avoidImage=\"false\"/><text:textAlign horizontalAlignment=\"CENTER\" verticalAlignment=\"CENTER\" inLineAlignment=\"BASELINE\"/><text:textStyle vertical=\"false\" nullBlock=\"false\" charSpace=\"0\" lineSpace=\"0\" orgPoint=\"20pt\" combinedChars=\"true\"/><pt:data>{:NAME}</pt:data><text:stringItem charLen=\"{:CHAR_LENGTH}\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem></text:text><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell><table:cell addressX=\"1\" addressY=\"2\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><text:text><pt:objectStyle x=\"6pt\" y=\"31.5pt\" width=\"163.6pt\" height=\"18.7pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"NULL\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo><text:textControl control=\"FIXEDFRAME\" clipFrame=\"false\" aspectNormal=\"true\" shrink=\"true\" autoLF=\"true\" avoidImage=\"false\"/><text:textAlign horizontalAlignment=\"CENTER\" verticalAlignment=\"CENTER\" inLineAlignment=\"BASELINE\"/><text:textStyle vertical=\"false\" nullBlock=\"false\" charSpace=\"0\" lineSpace=\"0\" orgPoint=\"18.7pt\" combinedChars=\"false\"/><pt:data>{:PRICE} Use By {:DATE}</pt:data><text:stringItem charLen=\"1\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"1\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"3\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"11\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem><text:stringItem charLen=\"2\"><text:ptFontInfo><text:logFont name=\"Helsinki\" width=\"0\" italic=\"false\" weight=\"700\" charSet=\"0\" pitchAndFamily=\"34\"/><text:fontExt effect=\"NOEFFECT\" underline=\"0\" strikeout=\"0\" size=\"14.5pt\" orgSize=\"28.8pt\" textColor=\"#FF0000\" textPrintColorNumber=\"1\"/></text:ptFontInfo></text:stringItem></text:text><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell><table:cell addressX=\"1\" addressY=\"3\" spanX=\"1\" spanY=\"1\" backColor=\"#FFFFFF\" backPrintColorNumber=\"0\"><barcode:barcode><pt:objectStyle x=\"8pt\" y=\"52.9pt\" width=\"159.6pt\" height=\"18.6pt\" backColor=\"#FFFFFF\" backPrintColorNumber=\"1\" ropMode=\"COPYPEN\" angle=\"0\" anchor=\"TOPLEFT\" flip=\"NONE\"><pt:pen style=\"INSIDEFRAME\" widthX=\"0.5pt\" widthY=\"0.5pt\" color=\"#FFFFFF\" printColorNumber=\"1\"/><pt:brush style=\"NULL\" color=\"#FFFFFF\" printColorNumber=\"1\" id=\"0\"/><pt:expanded objectName=\"Bar Code3\" ID=\"0\" lock=\"0\" templateMergeTarget=\"LABELLIST\" templateMergeType=\"NONE\" templateMergeID=\"0\" linkStatus=\"NONE\" linkID=\"0\"/></pt:objectStyle><barcode:barcodeStyle protocol=\"CODE39\" lengths=\"48\" zeroFill=\"false\" barWidth=\"1.2pt\" barRatio=\"1:3\" humanReadable=\"true\" humanReadableAlignment=\"LEFT\" checkDigit=\"false\" autoLengths=\"true\" margin=\"true\" sameLengthBar=\"false\" bearerBar=\"false\"/><pt:data>{:BARCODE}</pt:data></barcode:barcode><pt:brush style=\"NULL\" color=\"#000000\" printColorNumber=\"1\" id=\"0\"/></table:cell></table:cells></table:table></pt:objects></style:sheet></pt:body></pt:document>");
    let prop_xml_string = String::from("<?xml version=\"1.0\" encoding=\"UTF-8\"?><meta:properties xmlns:meta=\"http://schemas.brother.info/ptouch/2007/lbx/meta\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\"><meta:appName>P-touch Editor</meta:appName><dc:title></dc:title><dc:subject></dc:subject><dc:creator>pete</dc:creator><meta:keyword></meta:keyword><dc:description></dc:description><meta:template></meta:template><dcterms:created>2018-01-25T12:24:21Z</dcterms:created><dcterms:modified>2018-09-06T16:19:22Z</dcterms:modified><meta:lastPrinted>2018-01-25T12:25:38Z</meta:lastPrinted><meta:modifiedBy>pete</meta:modifiedBy><meta:revision>2</meta:revision><meta:editTime>2</meta:editTime><meta:numPages>1</meta:numPages><meta:numWords>0</meta:numWords><meta:numChars>0</meta:numChars><meta:security>0</meta:security></meta:properties>");

    let options = zip::write::FileOptions::default().compression_method(zip::CompressionMethod::Deflated);
	
    let mut label_path = product.name.clone();
    label_path.push_str(LABEL_EXT);
    let file_path = get_app_temp().join(&label_path);
    let file = match std::fs::File::create(&file_path) {
        Ok(x) => x,
        Err(x) => return Err("Could not create file")
    };
    let mut zip = zip::ZipWriter::new(file);

    match zip.start_file("label.xml", options) {
        Ok(x) => {},
        Err(x) => {return Err("Could not start label.xml file to zip")}
    };

    match zip.write_all(&label_xml_string_with_date.replace(PLACEHOLDER_NAME, &product.name).replace(PLACEHOLDER_BARCODE, &product.barcode).replace(PLACEHOLDER_PRICE, &product.price.to_string()).replace(PLACEHOLDER_CHAR_LENGTH, &product.name.len().to_string()).into_bytes()) {
        Ok(x) => {},
        Err(x) => {return Err("Could not write context to label.xml file")}
    };

    match zip.start_file("prop.xml", options) {
        Ok(x) => {},
        Err(x) => {return Err("Could not start prop.xml in zip")}
    };

    match zip.write_all(&prop_xml_string.into_bytes()) {
        Ok(x) => {},
        Err(x) => {return Err("Could not write content to prop.xml file")}
    };

    match zip.finish() {
        Ok(x) => {},
        Err(x) => {return Err("Could not write complete zip write to disk")}
    }

    Ok(PathBuf::from(file_path))
}

pub fn hash_file(mut file: File) -> Result<String, &'static str> {
    let mut hasher = Blake2b::new();
    match io::copy(&mut file, &mut hasher) {
        Ok(x) => x,
        Err(x) => {return Err("Could not hash file returned by the function 'generate_lbx_from_product': {}")}
    };
    let hash = hasher.result();
    println!("{:x}", hash.clone());
    let s = match String::from_utf8(hash.as_slice().to_vec()) {
        Ok(v) => v,
        Err(e) => {
            println!("{}", e);
            return Err("Invalid UTF-8 sequence")
        }
    };
    Ok(s)
}

pub fn hash_password(m: String) -> Result<String, &'static str> {
    Ok(String::from_utf8(Blake2b::digest(m.as_bytes()).as_slice().to_vec()).unwrap())
}

pub fn establish_connection() -> SqliteConnection {
    dotenv().ok();

    let database_url = env::var("DATABASE_URL").expect("DATABASE_URL must be set");
    SqliteConnection::establish(&database_url).expect(&format!("Error connecting to {}", database_url))
}

