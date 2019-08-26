use rocket_contrib::json::Json;

use models::Database::DatabaseConnection;

use models::GlobalProduct::GlobalProduct;
use models::Product::Product;

use rocket::http::Status;

#[get("/barcode/<code>")]
pub fn barcode(conn: DatabaseConnection, code: String) -> Result<Json<Product>, rocket::response::status::NotFound<&'static str>> {
    match Product::find_by_barcode(code.as_str(), &conn) {
        Some(x) => {
            return Ok(Json(x));
        }
        None => match GlobalProduct::find_by_barcode(&conn, String::from(code)) {
            Some(x) => {
                let mapped_product = map_global_product_to_product(x);
                mapped_product.save(&conn);
                return Ok(Json(mapped_product));
            }
            None => {
                return Err(rocket::response::status::NotFound(""));;
            }
        },
    };
}
#[get("/search/<searchString>")]
pub fn search(conn: DatabaseConnection, searchString: String) -> Result<Json<Vec<Product>>, rocket::response::status::NotFound<&'static str>> {
    match Product::search(searchString, &conn) {
        Some(x) => {
            return Ok(Json(x));
        }
        None => {
            return Ok(Json(Vec::new()));
        }
    };
}

#[put("/<id>", format = "application/json", data = "<update_product>")]
pub fn update(conn: DatabaseConnection, id: String, update_product: Json<Product>) -> Result<Json<Product>, rocket::response::status::Custom<&'static str>> {
    let product = update_product.0;
    match product.save(&conn) {
        true => {
            return Ok(Json(product));
        },
        false => {
            return Err(rocket::response::status::Custom(Status::InternalServerError, "Could not update product"));
        }
    }
}

pub fn map_global_product_to_product(global_product: GlobalProduct) -> Product {
    let mut product: Product = Product::default();
    product.barcode = global_product.barcode;
    product.name = global_product.name;
    product.updated = global_product.updated;
    product.id = global_product.id;
    return product;
}
