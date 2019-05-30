table! {
    configurations (config_key) {
        config_key -> Text,
        config_value -> Text,
    }
}

table! {
    products (id) {
        id -> Text,
        name -> Text,
        barcode -> Text,
        price -> Integer,
        department -> Text,
        supplier -> Text,
        labelPrinted -> Bool,
        isCase -> Bool,
        updated -> Timestamp,
        created -> Timestamp,
        deleted -> Bool,
        max_stock -> Integer,
        current_stock -> Integer,
    }
}

table! {
    servers (id) {
        id -> Text,
        major -> Integer,
        minor -> Integer,
        ip_address -> Text,
    }
}

table! {
    suppliers (id) {
        id -> Text,
        name -> Text,
        telephone -> Text,
        website -> Text,
        email -> Text,
    }
}

table! {
    users (id) {
        id -> Text,
        name -> Text,
        telephone -> Text,
        email -> Text,
        password_hash -> Text,
        code -> Text,
    }
}

table! {
    versions (id) {
        id -> Text,
        release_date -> Timestamp,
        major -> Integer,
        minor -> Integer,
    }
}

allow_tables_to_appear_in_same_query!(
    configurations,
    products,
    servers,
    suppliers,
    users,
    versions,
);
