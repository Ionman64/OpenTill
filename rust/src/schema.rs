table! {
    configurations (config_key) {
        config_key -> Text,
        config_value -> Text,
    }
}

table! {
    departments (id) {
        id -> Text,
        name -> Text,
        short_hand -> Text,
        comments -> Nullable<Text>,
        colour -> Text,
        order_num -> Integer,
        created -> Timestamp,
        updated -> Timestamp,
        deleted -> Bool,
    }
}

table! {
    cases (id) {
        id -> Text,
        barcode -> Text,
        product_barcode -> Text,
        units -> Integer,
        created -> Timestamp,
        updated -> Timestamp,
        deleted -> Bool,
    }
}

table! {
    store_products (id) {
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
    global_products (id) {
        id -> Text,
        name -> Text,
        barcode -> Text,
        updated -> Timestamp,
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
        created -> Timestamp,
        updated -> Timestamp,
        deleted -> Bool,
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
        created -> Timestamp,
        updated -> Timestamp,
        deleted -> Bool,
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

table! {
    transactions (id) {
        id -> Text,
        started -> Timestamp,
        ended -> Nullable<Timestamp>,
        total -> Integer,
        cashier -> Text,
        money_given -> Integer,
        card -> Integer,
        cashback -> Integer,
        payee -> Text,
        transaction_type -> Text,
    }
}

allow_tables_to_appear_in_same_query!(
    configurations,
    departments,
    global_products,
    store_products,
    servers,
    suppliers,
    users,
    versions,
    cases,
    transactions
);
