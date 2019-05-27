table! {
    products (id) {
        id -> Text,
        name -> Text,
        barcode -> Text,
        price -> Integer,
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
    products,
    suppliers,
    users,
    versions,
);
