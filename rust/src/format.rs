//Module is designed for handling the formatting of currency

pub enum Currency {
    GBP,
    USD,
    EUR,
    SEK,
}

pub fn format_currency(amount: i64, currency:Currency) -> Result<String, &'static str> {
    let currency_setup = get_config_from_currency(currency);
    let formatted_amount = format_amount(amount, &currency_setup);
    if amount < 0 {
        let mut result = currency_setup.positive_format.clone();
        return Ok(result.replace("%s", &currency_setup.symbol.clone()).replace("%v", &formatted_amount));
    }
    else {
        let mut result = currency_setup.negative_format.clone();
        return Ok(result.replace("%s", &currency_setup.symbol.clone()).replace("%v", &formatted_amount));
    }
}

fn format_amount(amount: i64, currency_setup:&CurrencySetup) -> String {
    let mut result = String::new();
    let mut floor = 0;//amount / u64::pow(10, &currency_setup.precision);
    while floor > 1000 {
        result.push_str(&floor.to_string());
        result.push_str(&currency_setup.thousand);
        floor = floor / 1000;
    }
    result.push_str(&floor.to_string());
    result.push_str(&currency_setup.decimal);
    return result;
}

struct CurrencySetup {
    symbol: String,
    precision: u8,
    thousand: String,
    decimal: String,
    positive_format: String,
    negative_format: String
}

impl CurrencySetup {
    pub fn default() -> CurrencySetup {
        return CurrencySetup {
            symbol: String::from("£"),
            precision: 2,
            thousand: String::from(","),
            decimal: String::from("."),
            positive_format: String::from("%s%v"),
            negative_format: String::from("- %s%v")
        }
    }
    pub fn new(currency_symbol: String, precision:u8, thousand_sep: String, decimal_sep: String, positive_format:String, negative_format:String) -> CurrencySetup {
        return CurrencySetup {
            symbol: currency_symbol,
            precision: precision,
            thousand: thousand_sep,
            decimal: decimal_sep,
            positive_format: positive_format,
            negative_format:negative_format
        }
    }
}

fn get_config_from_currency(currency:Currency) -> CurrencySetup {
    return match currency {
        Currency::GBP => CurrencySetup::default(),
        Currency::USD => CurrencySetup::new(String::from("$"), 2, String::from(","), String::from("."), String::from("%s%v"), String::from("- %s%v")),
        Currency::EUR => CurrencySetup::new(String::from("€"), 2, String::from(","), String::from("."), String::from("%s%v"), String::from("- %s%v")),
        Currency::SEK => CurrencySetup::new(String::from("kr"), 2, String::from(","), String::from("."), String::from("%v %s"), String::from("- %v %s")),
        _ => panic!("Unsupported Currency")
    }
}