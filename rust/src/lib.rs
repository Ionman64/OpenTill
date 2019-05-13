#[macro_use] 
extern crate log;
extern crate fern;

#[macro_use] 
extern crate diesel;
extern crate dotenv;
extern crate uuid;
extern crate chrono;
extern crate reqwest;
extern crate qrcodegen;
extern crate zip;
extern crate blake2;

pub mod utils;
pub mod config;
pub mod format;
pub mod models;
pub mod schema;

#[cfg(test)]
mod tests {
    use utils::*;
    use models::*;
    use std::fs;
    #[test]
    fn it_works() {
        assert_eq!(2 + 2, 4);
    }
    #[test]
    fn uuid_length_test_1() {
        assert_eq!(36, String::from(uuid4().to_string()).len());
    }
    #[test]
    fn get_monday_timestamp_1() {
        let test_timestamp = 1555423748; //Tue, 16 Apr 2019 14:10:32
        let test_monday_timestamp = 1555286400; //Mon, 15 Apr 2019 00:00:00
        assert_eq!(test_monday_timestamp, get_monday_timestamp(test_timestamp));
    }
    #[test]
    fn get_monday_timestamp_2() {
        let test_timestamp = 1456747200; //Mon, 29 Feb 2016 12:00:00
        let test_monday_timestamp = 1456704000; //Mon, 29 Feb 2016 00:00:00
        assert_eq!(test_monday_timestamp, get_monday_timestamp(test_timestamp));
    }
    #[test]
    fn get_monday_timestamp_3() {
        let test_timestamp = 1556495999; //Sun, 28 Apr 2019 23:59:59
        let test_monday_timestamp = 1555891200; //Mon, 22 Apr 2019 00:00:00
        assert_eq!(test_monday_timestamp, get_monday_timestamp(test_timestamp))
    }
    #[test]
    fn character_count_test() {
        assert_eq!(2, character_count(&String::from(r#"C:\Users\Test"#), '\\'));
    }
    #[test]
    fn get_home_directory_test() {
        if character_count(&String::from(get_home_dir().to_str().unwrap()), '\\') > 0 {
            assert!(true);
        }
        assert!(false);
    }
    #[test]
    fn hash_password_test() {
        let password = String::from("Hello World");
        let hash = hash_password(password);
        assert_eq!(hash.unwrap(), "hello");
    }
    #[test]
    fn write_lbx_file_to_temp_directory() {
        let product = Product {name: String::from("test_item"), barcode: String::from("012344798232"), price: 0.42};
        let file_path = match generate_lbx_from_product(&product) {
            Ok(x) => x,
            Err(x) => panic!("Could not write file {}", x)
        };

        let mut file = match fs::File::open(&file_path) {
            Ok(x) => x,
            Err(x) => panic!("Could not find file returned by the function 'generate_lbx_from_product': {}", x)
        };

        let s = match hash_file(file) {
            Ok(x) => x,
            Err(x) => {
                println!("Could not hash file {}", x);
                panic!("Could not hash file");
            }
        };
        assert_eq!("b524bbb3972743972b6201987ba29f54b286990869109b5636b9c69aeef6a9f6bbf707c44e4b2a93557663cd55118a7eba34bfc97eb39f529aa1158ae67df3f8", s);
    }
}