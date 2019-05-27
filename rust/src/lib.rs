#[macro_use] 
extern crate log;
extern crate fern;

#[macro_use] 
extern crate rocket;
extern crate rocket_contrib;

#[macro_use] 
extern crate diesel;
extern crate dotenv;
extern crate uuid;
extern crate chrono;
extern crate reqwest;
extern crate qrcodegen;
extern crate zip;
extern crate blake2;
extern crate rand;
extern crate serde;
extern crate notifica;
extern crate printpdf;
extern crate csv;

pub mod utils;
pub mod config;
pub mod format;
pub mod models;
pub mod schema;
pub mod listener;
pub mod sender;

#[cfg(test)]
mod tests {
    use utils::*;
    use models::*;
    use std::fs;
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
        let product = Product {id: String::from(uuid4().to_string()), name: String::from("test_item"), barcode: String::from("012344798232"), price: 42};
        let file_path = match generate_lbx_from_product(&product) {
            Ok(x) => x,
            Err(x) => panic!("Could not write file {}", x)
        };

        match fs::File::open(&file_path) {
            Ok(_) => {
                assert!(true);
            },
            Err(x) => {
                panic!("Could not find file returned by the function 'generate_lbx_from_product': {}", x);
            }
        };
    }
    #[test]
    fn save_product() {
        let barcode = String::from("12345678910");
        let product = Product::new(String::from("Test Product"), barcode.clone(), 100);
        product.save();

        let saved_product = match Product::find_by_barcode(&barcode) {
            Some(x) => x,
            None => {
                panic!("Product not found!");
            }  
        };
        assert_eq!(saved_product.barcode, barcode);
    }
    #[test]
    fn save_product_2() {
        let new_product_name = String::from("Updated Product");
        let new_product_price = 100;
        let barcode = String::from("10987654321");
        let mut product = Product::new(String::from("Test Product"), barcode.clone(), 100);
        product.save();
        product.name = new_product_name.clone();
        product.price = new_product_price;
        product.save();

        let saved_product = match Product::find_by_barcode(&barcode) {
            Some(x) => x,
            None => {
                panic!("Product not found!");
            }  
        };
        assert_eq!(saved_product.name, new_product_name);
        assert_eq!(saved_product.price, new_product_price);
    }
    #[test]
    fn save_user() {
        let mut user = User::default();
        let name = String::from("Steve");
        user.name = name.clone();
        user.save();

        let saved_user = match User::find_by_id(&user.id) {
            Some(x) => x,
            None => {
                panic!("Could not find user");
            }
        };

        assert_eq!(saved_user.name, name);
    }
}